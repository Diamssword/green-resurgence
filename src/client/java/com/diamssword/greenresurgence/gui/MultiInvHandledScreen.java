package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import io.wispforest.owo.mixin.ui.SlotAccessor;
import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.util.pond.OwoSlotExtension;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MultiInvHandledScreen<R extends ParentComponent, S extends MultiInvScreenHandler> extends BaseUIModelHandledScreen<R, S> {
    private final Map<String,List<MySlotComponent>> slotComps=new HashMap<>();
    protected MultiInvHandledScreen(S handler, PlayerInventory inventory, Text title, Class<R> rootComponentClass, BaseUIModelScreen.DataSource source) {
        super(handler, inventory, Text.literal(""), rootComponentClass, source);
        this.handler.onReady(v->{
            if(this.uiAdapter.rootComponent instanceof BaseParentComponent r)
                findInvComps(r);
        });
    }

    protected MultiInvHandledScreen(S handler, PlayerInventory inventory, Text title, Class<R> rootComponentClass, Identifier modelId) {
        super(handler, inventory, title, rootComponentClass, modelId);
        this.handler.onReady(v->{
            if(this.uiAdapter.rootComponent instanceof BaseParentComponent r)
                findInvComps(r);
        });
    }
    @Override
    protected void init() {

        super.init();

        if(this.uiAdapter.rootComponent instanceof BaseParentComponent r && this.handler.isReady())
            findInvComps(r);
        //else
      //      System.err.println("RootComponent is not a BaseParentComponent, can't find inventories components!");


    }
    private void findInvComps(BaseParentComponent root)
    {
        root.children().forEach(c->{
            if(c instanceof InventoryComponent par)
            {
                if(!slotComps.containsKey(par.inventoryId))
                {

                    var slotL=this.handler.getSlotForInventory(par.inventoryId);
                    slotComps.put(par.inventoryId,slotL.stream().map(v->new MySlotComponent(v,this)).toList());

                }
                par.setSlots(slotComps.get(par.inventoryId), this.handler.getInventoryWidth(par.inventoryId),this.handler.getInventoryHeight(par.inventoryId));
            }
            else if(c instanceof BaseParentComponent c1)
                findInvComps(c1);
        });

    }

    public static class MySlotComponent extends BaseComponent {

        public final Slot slot;
        public final int baseX;
        public final int baseY;
        protected boolean didDraw = false;
        private final MultiInvHandledScreen screen;
        public MySlotComponent(Slot slot, MultiInvHandledScreen screen) {
            this.slot =slot;
            this.baseX=slot.x;
            this.baseY=slot.y;
            this.screen=screen;
        }

        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            this.didDraw = true;

            int[] scissor = new int[4];
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);

            ((OwoSlotExtension) this.slot).owo$setScissorArea(PositionedRectangle.of(
                    scissor[0], scissor[1], scissor[2], scissor[3]
            ));
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);

            ((OwoSlotExtension) this.slot).owo$setDisabledOverride(!this.didDraw);

            this.didDraw = false;
        }

        @Override
        public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            if (!this.slot.hasStack()) {
                super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
            }
        }

        @Override
        public boolean shouldDrawTooltip(double mouseX, double mouseY) {
            return super.shouldDrawTooltip(mouseX, mouseY);
        }

        @Override
        protected int determineHorizontalContentSize(Sizing sizing) {
            return 16;
        }

        @Override
        protected int determineVerticalContentSize(Sizing sizing) {
            return 16;
        }

        @Override
        public void updateX(int x) {
            super.updateX(x);
            ((SlotAccessor) this.slot).owo$setX(x - screen.x);
        }

        @Override
        public void updateY(int y) {
            super.updateY(y);
            ((SlotAccessor) this.slot).owo$setY(y - screen.y);
        }
    }
}
