package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.cosmetics.ClothingModel;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClothInventoryComponent extends BaseComponent {
    private final static int clothW=30;
    private final static int clothH=50;
    private final static int scrollBarW=10;
    private final static int topW=20;
    protected final EntityRenderDispatcher dispatcher;
    protected final VertexConsumerProvider.Immediate entityBuffers;
    public static final Identifier SEARCH_TEXTURE=GreenResurgence.asRessource("textures/gui/search.png");
    private final TextFieldWidget textField;
    private final EventStream<ClothPicked> onPicked= ClothPicked.newPickStream();
    private final EventStream<ClothPicked> onHovered= ClothPicked.newPickStream();
    private final ScrollPart scroller;
    private Layer hovered;
    private int columns=3;
    private int lines=3;
    private List<ClothingLoader.Cloth> cloths =new ArrayList<>();
    private List<ClothingLoader.Cloth> equippeds =new ArrayList<>();

    private List<ClothingLoader.Cloth> filtered=new ArrayList<>();
    private List<Layer> displayed=new ArrayList<>();
    private String lastResearch="";
    private double lastMouseX=0;
    private double lastMouseY=0;

    protected ClothInventoryComponent(Sizing size) {

        this.sizing(size);
        textField= new TextFieldWidget(MinecraftClient.getInstance().textRenderer,1,1,this.width,10,Text.empty());
        textField.setFocused(true);
        textField.setEditable(true);
        textField.setDrawsBackground(false);
        final var client = MinecraftClient.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.getBufferBuilders().getEntityVertexConsumers();
        this.scroller=new ScrollPart(this.width-10,topW,10,this.height-topW,1);
        this.scroller.onScroll(this::onScroll);

    }
    private void onScroll(int lines)
    {
        refreshClothList();
    }
    public void setEquipped(List<ClothingLoader.Cloth> objects)
    {
        equippeds=objects;
    }
    public void setSelection(List<ClothingLoader.Cloth> objects)
    {
        this.scroller.scroll=0;
        this.cloths =objects;
        this.textField.setText("");
        this.lastResearch="";
        refreshSearch();
    }
    private static  class Layer{
        public ClothingModel<AbstractClientPlayerEntity> model;
        public ClothingModel<AbstractClientPlayerEntity> model1;
        public ClothingLoader.Cloth cloth;
        public Layer(ClothingLoader.Cloth cloth)
        {
            this.model=new ClothingModel<>(false,0,false);
            this.model1=new ClothingModel<>(false,1,true);
            this.cloth=cloth;
        }
    }
    private int getClothListWidth()
    {
        return this.columns*clothW;
    }
    private int getClothListHeight()
    {
        return this.lines*clothH;
    }
    public void refreshSearch()
    {
        if(this.lastResearch.isBlank())
            this.filtered=this.cloths;
        else
            this.filtered= this.cloths.stream().filter(v->v.name().toLowerCase().trim().contains(lastResearch)).toList();

        this.scroller.lines=Math.max(1,(this.filtered.size()/columns)-lines+1);

        refreshClothList();
    }
    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }
    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if(isOnTextField(mouseX,mouseY))
            return  textField.onMouseDrag(mouseX,mouseY,deltaX,deltaY,button);
        if(hovered==null)
        {
            return this.scroller.mouseDrag(mouseY);
        }
        return false;
    }
    @Override
    public void applySizing() {
            super.applySizing();
            this.textField.setWidth(this.width-12);

            this.columns=(this.width-scrollBarW)/clothW;
            this.lines=(this.height-topW)/clothH;
            this.scroller.x=this.width-10;
            this.scroller.y=topW;
            this.scroller.width=10;
            this.scroller.height=this.height-topW;
            this.refreshSearch();
    }
    @Override
    public CursorStyle cursorStyle() {
        if(this.hovered!=null || scroller.isOver(lastMouseX,lastMouseY))
            return CursorStyle.HAND;
        if(this.isOnTextField(lastMouseX,lastMouseY))
            return CursorStyle.TEXT;
        return super.cursorStyle();
    }
    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);

        if(isOnTextField(mouseX,mouseY))
        {
           return textField.onMouseDown(mouseX,mouseY,button);
        }
        if(hovered!=null) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            onPicked.sink().onPicked(hovered.cloth);
        }
        return false;
    }
    public EventSource<ClothPicked> onClothPicked() {
        return this.onPicked.source();
    }
    public EventSource<ClothPicked> onClothHovered() {
        return this.onHovered.source();
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {

        this.lastMouseX=mouseX-x;
        this.lastMouseY=mouseY-y;
        RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        context.drawTexture(SEARCH_TEXTURE,0,0,10, (int) (topW*0.75f),0,0,10,16,64,64);
        context.drawTexture(SEARCH_TEXTURE,10,0,this.width-20, (int) (topW*0.75f),10,0,44,16,64,64);
        context.drawTexture(SEARCH_TEXTURE,width-10,0,10, (int) (topW*0.75f),54,0,10,16,64,64);
        int i=0;
        int j=0;
        var oldHov=hovered;
        hovered=null;
        this.scroller.draw(context);
        textField.render(context,mouseX,mouseY,delta);
        for (Layer layer : this.displayed) {
            var sX=2+(i*clothW);
            var sY=2+topW+(j*clothH);
            if(equippeds.contains(layer.cloth))
                context.setShaderColor(0.8f,1f,0.8f,1);
            Panels.drawOverlay(context,sX,sY,clothW-2,clothH-2);
            context.setShaderColor(1,1,1,1);
            drawClothing(context,layer,mouseX,partialTicks,i,j);
            if(mouseX>=this.x+sX && mouseX<this.x+sX+clothW-2 && mouseY>=this.y+sY && mouseY<this.y+sY+clothH-2)
                hovered=layer;
            i++;
            if(i>=this.columns) {
                j++;
                i=0;
            }
        }
        if(hovered!=oldHov)
            onHovered.sink().onPicked(hovered==null?null:hovered.cloth);
        RenderSystem.disableBlend();
        matrices.pop();
    }
    public void drawClothing(OwoUIDrawContext context,Layer cloth, int mouseX,float partialTicks,int x,int y) {
        var matrices = context.getMatrices();
        matrices.push();
        float scale=20;
        matrices.translate((x*clothW)-4, topW+18+(y*clothH), 100);
           matrices.scale(scale, scale,scale);
            matrices.translate(1, 0, 0);
            float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180+(yRotation * .6f)));

     //   matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10));
        var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
        dispatcher.owo$setCounterRotate(true);
        dispatcher.owo$setShowNametag(false);
        RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
        this.dispatcher.setRenderShadows(false);
            renderLayer(cloth,matrices,this.entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        this.dispatcher.setRenderShadows(true);
        this.entityBuffers.draw();
        DiffuseLighting.enableGuiDepthLighting();

        matrices.pop();

        dispatcher.owo$setCounterRotate(false);
        dispatcher.owo$setShowNametag(true);
    }
    public void renderLayer(Layer layer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        //PlayerEntityRenderer r= (PlayerEntityRenderer) this.dispatcher.getRenderer(entity);

        //r.getModel().copyBipedStateTo(layer.model);

        layer.model.child=false;
        layer.model1.child=false;
        //layer.model.animateModel(entity,limbAngle,limbDistance,tickDelta);
        //layer.model.setAngles(entity,limbAngle,limbDistance,animationProgress,entity.prevHeadYaw,entity.prevPitch);
        //matrices.scale(charWidth,charHeight,charWidth);
        var pack= OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
        layer.model.render(matrices,vertexConsumers.getBuffer(layer.model.getLayer(GreenResurgence.asRessource("textures/cloth/"+layer.cloth.layer()+"/"+layer.cloth.id()+".png"))),light, pack,1,1,1,1);
        layer.model1.render(matrices,vertexConsumers.getBuffer(layer.model1.getLayer(GreenResurgence.asRessource("textures/cloth/"+layer.cloth.layer()+"/"+layer.cloth.id()+".png"))),light,pack,1,1,1,1);

    }
    public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(context,mouseX,mouseY,partialTicks,delta);
        if(hovered!=null) {
            var ls=new ArrayList<Text>();
            ls.add(Text.literal(hovered.cloth.name()));
            ls.add(Text.translatable(GreenResurgence.ID+".wardrobe.collection."+hovered.cloth.collection()).formatted(Formatting.BLUE));
            ls.add(Text.translatable(GreenResurgence.ID+".wardrobe.layer."+hovered.cloth.layer().toString()).formatted(Formatting.GRAY,Formatting.ITALIC));
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, mouseX, mouseY, ls.stream().map(v->TooltipComponent.of(v.asOrderedText())).toList());
        }
    }

    private void refreshClothList()
    {
        this.displayed=new ArrayList<>();

        for(int i1=scroller.scroll*columns;i1<Math.min((scroller.scroll*columns)+(lines*columns),filtered.size());i1++)
        {
            displayed.add(new Layer(filtered.get(i1)));
        }
        
    }
    private boolean isOnTextField(double mouseX, double mouseY)
    {
        return mouseY<10 && mouseX<this.width-10;
    }
    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        this.scroller.scroll((int)amount);
        if(isOnTextField(mouseX,mouseY))
        {
            return textField.onMouseScroll(mouseX,mouseY,amount);
        }
        super.onMouseScroll(mouseX, mouseY, amount);
        return true;

    }
    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
          var v=this.textField.onKeyPress(keyCode,scanCode,modifiers);
        if(!textField.getText().trim().equals(lastResearch))
        {
            lastResearch=textField.getText().toLowerCase().trim();
            refreshSearch();
        }
        return v;
    }
    @Override
    public boolean onCharTyped(char chr, int modifiers) {
            var v= this.textField.charTyped(chr,modifiers);
        if(!textField.getText().trim().equals(lastResearch))
        {
            lastResearch=textField.getText().toLowerCase().trim();
            refreshSearch();
        }
        return v;
       // return super.onCharTyped(chr,modifiers);
    }
    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model,element,children);

    }
    public static ClothInventoryComponent parse(Element element) {

        return new ClothInventoryComponent(Sizing.fill(100));
    }
    public static interface ClothPicked{
        void onPicked(ClothingLoader.Cloth cloth);
        static EventStream<ClothPicked> newPickStream() {
            return new EventStream<>(subscribers -> (ClothingLoader.Cloth cloth) -> {
                for (var subscriber : subscribers) {
                   subscriber.onPicked(cloth);
                }
            });
        }
    }

}
