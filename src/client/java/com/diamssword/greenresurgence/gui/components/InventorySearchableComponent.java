package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FormattedInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventorySearchableComponent extends BaseComponent {
    public static final Identifier SLOT_TEXTURE= GreenResurgence.asRessource("textures/gui/slots/slot.png");
    public final String inventoryId;
    public final String name;
    private final TextFieldWidget textField;
    protected AnimatableProperty<PositionedRectangle> visibleArea;
    private int regionWidth=18;
    private int regionHeight=18;
    private int slotsWidth=2;
    private int slotsHeight=2;
    protected boolean blend = false;
    private String lastResearch="";
    private List<Slot> displayedSlot=new ArrayList<>();
    private List<Slot> filteredSlots=new ArrayList<>();
    private List<Slot> inventory=new ArrayList<>();
    private final ScrollPart scroller;
    protected InventorySearchableComponent(String inventoryId, int width, int height, String name) {
        this.inventoryId=inventoryId;
        this.name=name;
        this.scroller=new ScrollPart(this.width-10,20,10,this.height-20,1);
        this.scroller.onScroll(this::onScroll);
        textField= new TextFieldWidget(MinecraftClient.getInstance().textRenderer,1,9,this.width,10,Text.empty());
        textField.setFocused(true);
        textField.setEditable(true);
        textField.setDrawsBackground(false);

        this.setSize(width,height);

    }

    public Pair<Integer,Integer> getSlotsSize()
    {
        return new Pair<>(slotsWidth,slotsHeight);
    }
    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }
    private boolean isOnTextField(double mouseX, double mouseY)
    {
        return mouseY<10 && mouseX<this.width-10;
    }
    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if(isOnTextField(mouseX,mouseY))
            return  textField.onMouseDrag(mouseX,mouseY,deltaX,deltaY,button);
        if(scroller.isOver(mouseX,mouseY))
        {
            return this.scroller.mouseDrag(mouseY);
        }
        return false;
    }
    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);

        if(isOnTextField(mouseX,mouseY))
        {
            return textField.onMouseDown(mouseX,mouseY,button);
        }
        return false;
    }
    @Override
    public void applySizing() {
        super.applySizing();
        this.textField.setWidth(this.width);
        this.scroller.x=this.width-10;
        this.scroller.y=20;
        this.scroller.width=10;
        this.scroller.height=this.height-20;
        this.refreshSlots();
    }
    private void onScroll(int lines)
    {
        refreshSlots();
    }
    public void setInventory(List<Slot> inv)
    {
        this.inventory=inv;
        this.lastResearch="";
        this.textField.setText("");

        this.refreshSearch();
    }
    private void refreshSlots()
    {
        this.displayedSlot=new ArrayList<>();

        for(int i1=scroller.scroll*slotsWidth;i1<Math.min((scroller.scroll*slotsWidth)+(this.slotsHeight*slotsWidth),filteredSlots.size());i1++)
        {
            displayedSlot.add(filteredSlots.get(i1));
        }

        while (displayedSlot.size()<slotsWidth*slotsHeight && !displayedSlot.isEmpty())
            displayedSlot.add(new FalseSlot(displayedSlot.size()+1,0,0));

    }
    public List<Slot> getDisplayedSlots()
    {
        return this.displayedSlot;
    }
    private void refreshSearch()
    {
        if(this.lastResearch.isBlank())
            this.filteredSlots=this.inventory;
        else
            this.filteredSlots= this.inventory.stream().filter(v->v.getStack().getName().getString().toLowerCase().trim().contains(lastResearch)).toList();
        this.scroller.lines=Math.max(1,(this.filteredSlots.size()/slotsWidth)-slotsHeight+1);
        refreshSlots();
    }
    public void setSize(int width,int height)
    {
        this.slotsWidth=width;
        this.slotsHeight=height;
        this.regionWidth=10+width*18;
        this.regionHeight=20+height*18;
        this.visibleArea = AnimatableProperty.of(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));
        this.applySizing();
        if(this.parent!=null)
            this.parent.inflate(this.parent.fullSize());
    }
    protected InventorySearchableComponent(String inventoryId, int width, int height) {
        this(inventoryId,width,height,inventoryId);
    }
    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
      return this.regionWidth;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.regionHeight;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.visibleArea.update(delta);
        textField.update(delta,mouseX,mouseY);

    }
    public Text getInventoryName()
    {
        if(this.name.equals("disabled"))
            return null;
        if(this.inventoryId.equals("player"))
            return Text.translatable("container.inventory");
        else if(this.inventoryId.equals("hotbar") || this.name.isEmpty())
            return null;
        return Text.translatable(GreenResurgence.ID+".container."+this.name);
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
        matrices.scale(this.width / (float) this.regionWidth, this.height / (float) this.regionHeight, 0);

        var visibleArea = this.visibleArea.get();

        int bottomEdge = Math.min(visibleArea.y() + visibleArea.height(), regionHeight);
        int rightEdge = Math.min(visibleArea.x() + visibleArea.width(), regionWidth);
        Text name=getInventoryName();
        if(name!=null)
            context.drawText(name,visibleArea.x(),visibleArea.y(),0.9f,0xffffff);

        context.drawTexture(SLOT_TEXTURE,
                visibleArea.x()-1,
                visibleArea.y()+19,
                rightEdge - visibleArea.x()-10,
                bottomEdge - visibleArea.y()-20,
                visibleArea.x(),
                visibleArea.y(),
                rightEdge - visibleArea.x()-10,
                bottomEdge - visibleArea.y()-20,
                18,18
        );

        if (this.blend) {
            RenderSystem.disableBlend();
        }
        context.drawTexture(SLOT_TEXTURE,textField.getX()-2,textField.getY()-1,2, textField.getHeight(),0,0,2,18,18,18);
        context.drawTexture(SLOT_TEXTURE,textField.getX(),textField.getY()-1,textField.getWidth()-4, textField.getHeight(),2,0,14,18,18,18);
        context.drawTexture(SLOT_TEXTURE,textField.getX()+textField.getWidth()-4,textField.getY()-1,2, textField.getHeight(),16,0,2,18,18,18);
        this.scroller.draw(context);


        textField.render(context,mouseX,mouseY,delta);
        matrices.pop();
    }

    public InventorySearchableComponent visibleArea(PositionedRectangle visibleArea) {
        this.visibleArea.set(visibleArea);
        return this;
    }


    public InventorySearchableComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public boolean blend() {
        return this.blend;
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
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

        if (children.containsKey("visible-area")) {
            var areaChildren = UIParsing.childElements(children.get("visible-area"));

            int x = 0, y = 0, width = this.regionWidth, height = this.regionHeight;
            if (areaChildren.containsKey("x")) {
                x = UIParsing.parseSignedInt(areaChildren.get("x"));
            }

            if (areaChildren.containsKey("y")) {
                y = UIParsing.parseSignedInt(areaChildren.get("y"));
            }

            if (areaChildren.containsKey("width")) {
                width = UIParsing.parseSignedInt(areaChildren.get("width"));
            }

            if (areaChildren.containsKey("height")) {
                height = UIParsing.parseSignedInt(areaChildren.get("height"));
            }

            this.visibleArea(PositionedRectangle.of(x, y, width, height));
        }
    }
    public class FalseSlot extends Slot{
        public FalseSlot( int index, int x, int y) {
            super(new SimpleInventory(ItemStack.EMPTY), index, x, y);
        }
        public ItemStack insertStack(ItemStack stack) {
            return InventorySearchableComponent.this.inventory.get(0).insertStack(stack);
        }
        public ItemStack insertStack(ItemStack stack, int count) {
            return InventorySearchableComponent.this.inventory.get(0).insertStack(stack,count);
        }
        public boolean canInsert(ItemStack stack) {
            return InventorySearchableComponent.this.inventory.get(0).canInsert(stack);
        }
    }
    public static InventorySearchableComponent parse(Element element) {
        UIParsing.expectAttributes(element, "id");
        UIParsing.expectAttributes(element, "width");
        UIParsing.expectAttributes(element, "height");
        var invId =element.getAttributeNode("id").getValue();
        var w=UIParsing.parseUnsignedInt(element.getAttributeNode("width"));
        var h=UIParsing.parseUnsignedInt(element.getAttributeNode("height"));
        if (element.hasAttribute("name")) {
            return new InventorySearchableComponent(invId,w,h,element.getAttributeNode("name").getValue());
        }
        return new InventorySearchableComponent(invId,w,h);
    }
}
