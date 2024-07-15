package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.crafting.Collection;
import com.diamssword.greenresurgence.systems.crafting.IRecipe;
import com.diamssword.greenresurgence.systems.crafting.IResource;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ButtonInventoryComponent extends BaseComponent {
    private final int slotSize=18;
    public static final Identifier SLOT_TEXTURE= GreenResurgence.asRessource("textures/gui/highlight.png");
    private final TextFieldWidget textField;
    private EventStream<RecipePicked> onPicked=RecipePicked.newPickStream();
    private int scroll=0;
    public Identifier collectionID;
    private IResource hovered;
    private Collection<IRecipe<IResource>,IResource> collection;
    private int columns=3;
    private List<IRecipe<IResource>> items=new ArrayList<>();
    private String lastResearch="";
    protected boolean blend = false;
    protected ButtonInventoryComponent(Sizing size,Identifier collectionID,Collection<IRecipe<IResource>,IResource> collection) {
        this.collectionID=collectionID;
        this.collection=collection;
        items=  this.collection.getRecipes(MinecraftClient.getInstance().player);
        this.sizing(size);
        textField= new TextFieldWidget(MinecraftClient.getInstance().textRenderer,1,1,this.width,10,Text.empty());
        textField.setFocused(true);
        textField.setEditable(true);
        textField.setDrawsBackground(false);


    }
    public void setCollection(Collection<IRecipe<IResource>,IResource> collection,Identifier id)
    {
        this.collection=collection;
        this.collectionID=id;
        refreshSearch();
    }
    public void refreshSearch()
    {
        PlayerEntity pl=MinecraftClient.getInstance().player;
        if(this.lastResearch.isBlank())
            this.items=this.collection.getRecipes(pl);
        else
            this.items= this.collection.getRecipes(pl).stream().filter(v->v.result(pl).getName().getString().toLowerCase().trim().contains(lastResearch)).toList();
    }
    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }
    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if(isOnTextField(mouseX,mouseY))
            return  textField.onMouseDrag(mouseX,mouseY,deltaX,deltaY,button);
        if(mouseX>this.width-10)
        {
            float perc=Math.min(Math.max((float)mouseY/this.width,0),1);
            this.scroll=(int) ((float)perc*(items.size()/columns))/2;
            return true;
        }
        return false;
    }
    @Override
    public void applySizing() {
            super.applySizing();
            this.textField.setWidth(this.width-12);
            this.columns=(width-10)/slotSize;

    }
    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        int x=(int) mouseX/slotSize;
        int y=(int) (mouseY-10)/slotSize;
        var h=(this.height-10)/slotSize;

        if(isOnTextField(mouseX,mouseY))
        {
           return textField.onMouseDown(mouseX,mouseY,button);
        }
        if(x<columns && y<h)
        {
          var d=  x+ y*columns;
          if(d<items.size())
          {
              UISounds.playButtonSound();
              onPicked.sink().onPicked(items.get(d),this.collection,this.collectionID);
              return true;
          }
        }
        return false;
    }
    public EventSource<RecipePicked> onRecipePicked() {
        return this.onPicked.source();
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {

        RenderSystem.enableDepthTest();
        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);

        var h=(this.height-10)/slotSize;
         hovered = null;
        context.fill(1,1,this.width-10,this.height-1,0xffd6a56f);
        context.fill(this.width-10,1,this.width,this.height-1,0xff966e42);
        int i=0;
        int j=0;
        hovered=null;
        for(int i1=scroll*columns;i1<Math.min((scroll*columns)+(h*columns),items.size());i1++)
        {
            IResource it=items.get(i1).result(MinecraftClient.getInstance().player);
            if(it.drawAsItem())
            {
                var w1=1+(i*slotSize);
                var h1=10+(j*slotSize);
                if(mouseX >=this.x+w1 && mouseX<=this.x+w1+slotSize && mouseY>=this.y+h1 && mouseY<=this.y+h1+slotSize)
                {
                    hovered=it;
                    context.drawTexture(SLOT_TEXTURE,w1,h1,0,0,slotSize,slotSize,32,32 );
                }
                //context.fill(w1,h1,w1+slotSize,h1+slotSize,0xffb57631);
                context.drawItem(it.asItem(),w1+1,h1+1);
                i++;
                if(i>=this.columns) {
                    j++;
                    i=0;
                }
            }
        }
        context.drawTexture(SLOT_TEXTURE,this.width-9, (int) (1+((this.height-13)*this.getScrollPercent())),18,0,8,11,32,32 );
        context.drawRectOutline(0,0,this.width,this.height,0xFFb38552);
        context.drawLine(this.width-10,1,this.width-10,this.height,1,Color.ofArgb(0xFFb38552));
        context.drawLine(0,10,this.width-10,10,1,Color.ofArgb(0xFFb38552));
        textField.render(context,mouseX,mouseY,delta);

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        matrices.pop();
    }
    public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(context,mouseX,mouseY,partialTicks,delta);
        if(hovered!=null) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, mouseX, mouseY, Arrays.asList(TooltipComponent.of(hovered.getName().asOrderedText())));
        }
    }
    public float getScrollPercent()
    {
        if(items.isEmpty())
            return 0;
        float f=(scroll*columns);
        return Math.min((f*2)/items.size(),1);
    }
    public void scroll(int value) {
            var perc=getScrollPercent();
            if(value<0 && perc<1)
            {
                scroll=scroll+1;
            }
            else if(value>0)
            {
                scroll=Math.max(0,scroll-1);
            }

    }
    private boolean isOnTextField(double mouseX, double mouseY)
    {
        return mouseY<10 && mouseX<this.width-10;
    }
    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        this.scroll((int)amount);
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
    public static ButtonInventoryComponent parse(Element element) {
        UIParsing.expectAttributes(element, "collection");
        var invId =UIParsing.parseIdentifier(element.getAttributeNode("collection"));
        var r=Recipes.get(invId);
        if(r==null)
            r= new Collection();
        return new ButtonInventoryComponent(Sizing.fill(100),invId,r);
    }
    public static interface RecipePicked{
        boolean onPicked(IRecipe<IResource> picked,Collection<IRecipe<IResource>,IResource> collection,Identifier collectionID);
        static EventStream<RecipePicked> newPickStream() {
            return new EventStream<>(subscribers -> (IRecipe<IResource> picked,Collection<IRecipe<IResource>,IResource> collection,Identifier collectionID) -> {
                var anyTriggered = false;
                for (var subscriber : subscribers) {
                    anyTriggered |= subscriber.onPicked(picked,collection,collectionID);
                }
                return anyTriggered;
            });
        }
    }

}
