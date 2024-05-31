/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.parsing.ConfigureHotReloadScreen;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.util.UIErrorToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class MultiInvHandledScreen<T extends MultiInvScreenHandler,R extends ParentComponent> extends Screen implements ScreenHandlerProvider<T> {
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;
    protected final T handler;
    @Nullable
    protected Slot focusedSlot;
    @Nullable
    private Slot touchDragSlotStart;
    @Nullable
    private Slot touchDropOriginSlot;
    @Nullable
    private Slot touchHoveredSlot;
    @Nullable
    private Slot lastClickedSlot;
    protected int x;
    protected int y;
    private boolean touchIsRightClickDrag;
    private ItemStack touchDragStack = ItemStack.EMPTY;
    private int touchDropX;
    private int touchDropY;
    private long touchDropTime;
    private ItemStack touchDropReturningStack = ItemStack.EMPTY;
    private long touchDropTimer;
    protected final Set<Slot> cursorDragSlots = Sets.newHashSet();
    protected boolean cursorDragging;
    private int heldButtonType;
    private int heldButtonCode;
    private boolean cancelNextRelease;
    private int draggedStackRemainder;
    private long lastButtonClickTime;
    private int lastClickedButton;
    private boolean doubleClicking;
    private ItemStack quickMovingStack = ItemStack.EMPTY;
    /**
     * The UI model this screen is built upon, parsed from XML.
     * This is usually not relevant to subclasses, the UI adapter
     * inherited from {@link BaseOwoScreen} is more interesting
     */
    protected final UIModel model;
    protected final Class<R> rootComponentClass;

    protected final @Nullable Identifier modelId;
    /**
     * The UI adapter of this screen. This handles
     * all user input as well as setting up GL state for rendering
     * and managing component focus
     */
    protected OwoUIAdapter<R> uiAdapter = null;
    protected Map<String,InventoryComponent> invsComps=new HashMap<>();
    /**
     * Whether this screen has encountered an unrecoverable
     * error during its lifecycle and should thus close
     * itself on the next frame
     */
    protected boolean invalid = false;
    public MultiInvHandledScreen(T handler, PlayerInventory inventory, Class<R> rootComponentClass, BaseUIModelScreen.DataSource source) {
        super(Text.literal(""));
        this.handler = handler;
        this.cancelNextRelease = true;
        var providedModel = source.get();
        if (providedModel == null) {
            source.reportError();
            this.invalid = true;
        }

        this.rootComponentClass = rootComponentClass;
        this.model = providedModel;
        this.modelId = source instanceof BaseUIModelScreen.DataSource.AssetDataSource assetSource
                ? assetSource.assetPath()
                : null;
        this.handler.onReady(v->{
            if(this.uiAdapter !=null && this.uiAdapter.rootComponent instanceof BaseParentComponent r)
                findInvComps(r);
        });
    }
    protected MultiInvHandledScreen(T handler, PlayerInventory inventory, Class<R> rootComponentClass, Identifier modelId) {
        this(handler, inventory, rootComponentClass, BaseUIModelScreen.DataSource.asset(modelId));
        this.handler.onReady(v->{
            if(this.uiAdapter !=null && this.uiAdapter.rootComponent instanceof BaseParentComponent r)
                findInvComps(r);
        });
    }
    @Override
    protected void init() {
        invsComps.clear();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        if (this.invalid) return;

        // Check whether this screen was already initialized
        if (this.uiAdapter != null) {
            // If it was, only resize the adapter instead of recreating it - this preserves UI state
            this.uiAdapter.moveAndResize(0, 0, this.width, this.height);
            // Re-add it as a child to circumvent vanilla clearing them
            this.addDrawableChild(this.uiAdapter);
            if(this.uiAdapter.rootComponent instanceof BaseParentComponent r && this.handler.isReady())
                findInvComps(r);
        } else {
            try {
                this.uiAdapter = this.createAdapter();
                this.build(this.uiAdapter.rootComponent);
                this.uiAdapter.inflateAndMount();
                if(this.uiAdapter.rootComponent instanceof BaseParentComponent r && this.handler.isReady())
                    findInvComps(r);
            } catch (Exception error) {
                Owo.LOGGER.warn("Could not initialize owo screen", error);
                UIErrorToast.report(error);
                this.invalid = true;
            }
        }

    }
    private void findInvComps(BaseParentComponent root)
    {
        root.children().forEach(c->{
            if(c instanceof InventoryComponent par)
            {
                invsComps.put(par.inventoryId,par);
            }
            else if(c instanceof BaseParentComponent c1)
                findInvComps(c1);
        });

    }
    protected <C extends Component> @Nullable C component(Class<C> expectedClass, String id) {
        return this.uiAdapter.rootComponent.childById(expectedClass, id);
    }
    protected @NotNull OwoUIAdapter<R> createAdapter() {
        return this.model.createAdapter(rootComponentClass, this);
    }

    /**
     * Build the component hierarchy of this screen,
     * called after the adapter and root component have been
     * initialized by {@link #createAdapter()}
     *
     * @param rootComponent The root component created
     *                      in the previous initialization step
     */
    protected abstract void build(R rootComponent);
    public Pair<Integer,Integer> getSlotPosition(Slot s, String inventory)
    {
        var comp=invsComps.get(inventory);
        if(comp!=null)
            return new Pair<>(s.x+comp.x()-this.x,s.y+comp.y()-this.y+10);
        return  null;

    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.invalid) {
            this.close();
            return;
        }
        var context1 = OwoUIDrawContext.of(context);
        if (this.uiAdapter.enableInspector) {
            context1.getMatrices().translate(0, 0, 500);
            int i=0;
            for (String id : invsComps.keySet()) {
                List<Slot> slots=this.handler.getSlotForInventory(id);
                for (Slot slot : slots) {
                    if (slot.isEnabled()) {
                        var pos=getSlotPosition(slot,id);
                        context1.drawText(Text.literal("H:" +i),
                                this.x + pos.getFirst() + 15, this.y + pos.getSecond() + 9, .5f, 0x0096FF,
                                OwoUIDrawContext.TextAnchor.BOTTOM_RIGHT
                        );
                        context1.drawText(Text.literal("I:" + slot.getIndex()),
                                this.x + pos.getFirst() + 15, this.y + pos.getSecond() + 15, .5f, 0x5800FF,
                                OwoUIDrawContext.TextAnchor.BOTTOM_RIGHT
                        );
                        i++;
                    }
                }

            }
            context.getMatrices().translate(0, 0, -500);
        }
        ItemStack itemStack;
        int l;
        int i = this.x;
        int j = this.y;
        this.drawBackground(context, delta, mouseX, mouseY);
        RenderSystem.disableDepthTest();
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().translate(i, j, 0.0f);
        this.focusedSlot = null;
        for (String id : invsComps.keySet()) {
            List<Slot> slots=this.handler.getSlotForInventory(id);
            for (Slot slot : slots) {
                if (slot.isEnabled()) {
                    this.drawSlot(context, slot,id);
                }
                if (!this.isPointOverSlot(slot, mouseX, mouseY) || !slot.isEnabled()) continue;
                this.focusedSlot = slot;
                var pos=getSlotPosition(slot,id);
                if (!this.focusedSlot.canBeHighlighted()) continue;
                MultiInvHandledScreen.drawSlotHighlight(context, pos.getFirst(), pos.getSecond(), 0);
            }

        }
        this.drawForeground(context, mouseX, mouseY);
        ItemStack itemStack2 = itemStack = this.touchDragStack.isEmpty() ? this.handler.getCursorStack() : this.touchDragStack;
        if (!itemStack.isEmpty()) {
            int n = 8;
            l = this.touchDragStack.isEmpty() ? 8 : 16;
            String string = null;
            if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
                itemStack = itemStack.copyWithCount(MathHelper.ceil((float)((float)itemStack.getCount() / 2.0f)));
            } else if (this.cursorDragging && this.cursorDragSlots.size() > 1 && (itemStack = itemStack.copyWithCount(this.draggedStackRemainder)).isEmpty()) {
                string = Formatting.YELLOW + "0";
            }
            this.drawItem(context, itemStack, mouseX - i - 8, mouseY - j - l, string);
        }
        if (!this.touchDropReturningStack.isEmpty()) {
            float f = (float)(Util.getMeasuringTimeMs() - this.touchDropTime) / 100.0f;
            if (f >= 1.0f) {
                f = 1.0f;
                this.touchDropReturningStack = ItemStack.EMPTY;
            }
            l = this.touchDropOriginSlot.x - this.touchDropX;
            int m = this.touchDropOriginSlot.y - this.touchDropY;
            int o = this.touchDropX + (int)((float)l * f);
            int p = this.touchDropY + (int)((float)m * f);
            this.drawItem(context, this.touchDropReturningStack, o, p, null);
        }
        context.getMatrices().pop();
        RenderSystem.enableDepthTest();

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public static void drawSlotHighlight(DrawContext context, int x, int y, int z) {
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 16, -2130706433, -2130706433, z);
    }

    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack itemStack = this.focusedSlot.getStack();
            context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y);
        }
    }

    protected List<Text> getTooltipFromItem(ItemStack stack) {
        return MultiInvHandledScreen.getTooltipFromItem(this.client, stack);
    }

    private void drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText) {
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 232.0f);
        context.drawItem(stack, x, y);
        context.drawItemInSlot(this.textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
        context.getMatrices().pop();
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {

    }

    protected abstract void drawBackground(DrawContext var1, float var2, int var3, int var4);

    private void drawSlot(DrawContext context, Slot slot, String inventory) {

        Pair pair;
        var pos=getSlotPosition(slot,inventory);
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = this.handler.getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot((Slot)slot, (ItemStack)itemStack2, (boolean)true) && this.handler.canInsertIntoSlot(slot)) {
                bl = true;
                int k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
                int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
                int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, (int)this.heldButtonType, (ItemStack)itemStack2) + l;
                if (m > k) {
                    m = k;
                    string = Formatting.YELLOW.toString() + k;
                }
                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 100.0f);
        if (itemStack.isEmpty() && slot.isEnabled() && (pair = slot.getBackgroundSprite()) != null) {
            Sprite sprite = this.client.getSpriteAtlas((Identifier)pair.getFirst()).apply((Identifier)pair.getSecond());
            context.drawSprite(pos.getFirst(), pos.getSecond(), 0, 16, 16, sprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                context.fill(pos.getFirst(), pos.getSecond(), pos.getFirst()+ 16,pos.getSecond() + 16, -2130706433);
            }
            context.drawItem(itemStack, pos.getFirst(), pos.getSecond(), pos.getFirst() + pos.getSecond() * this.backgroundWidth);
            context.drawItemInSlot(this.textRenderer, itemStack, pos.getFirst(), pos.getSecond(), string);
        }
        context.getMatrices().pop();
    }

    private void calculateOffset() {
        ItemStack itemStack = this.handler.getCursorStack();
        if (itemStack.isEmpty() || !this.cursorDragging) {
            return;
        }
        if (this.heldButtonType == 2) {
            this.draggedStackRemainder = itemStack.getMaxCount();
            return;
        }
        this.draggedStackRemainder = itemStack.getCount();
        for (Slot slot : this.cursorDragSlots) {
            ItemStack itemStack2 = slot.getStack();
            int i = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
            int j = Math.min(itemStack.getMaxCount(), slot.getMaxItemCount(itemStack));
            int k = Math.min(ScreenHandler.calculateStackSize(this.cursorDragSlots, (int)this.heldButtonType, (ItemStack)itemStack) + i, j);
            this.draggedStackRemainder -= k - i;
        }
    }

    @Nullable
    private Slot getSlotAt(double x, double y) {
        for (int i = 0; i < ((ScreenHandler)this.handler).slots.size(); ++i) {
            Slot slot = (Slot)((ScreenHandler)this.handler).slots.get(i);
            if (!this.isPointOverSlot(slot, x, y) || !slot.isEnabled()) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        boolean bl = this.client.options.pickItemKey.matchesMouse(button) && this.client.interactionManager.hasCreativeInventory();
        Slot slot = this.getSlotAt(mouseX, mouseY);
        long l = Util.getMeasuringTimeMs();
        this.doubleClicking = this.lastClickedSlot == slot && l - this.lastButtonClickTime < 250L && this.lastClickedButton == button;
        this.cancelNextRelease = false;
        if (button == 0 || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || bl) {
            int i = this.x;
            int j = this.y;
            boolean bl2 = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
            int k = -1;
            if (slot != null) {
                k = slot.id;
            }
            if (bl2) {
                k = -999;
            }
            if (this.client.options.getTouchscreen().getValue().booleanValue() && bl2 && this.handler.getCursorStack().isEmpty()) {
                this.close();
                return true;
            }
            if (k != -1) {
                if (this.client.options.getTouchscreen().getValue().booleanValue()) {
                    if (slot != null && slot.hasStack()) {
                        this.touchDragSlotStart = slot;
                        this.touchDragStack = ItemStack.EMPTY;
                        this.touchIsRightClickDrag = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
                    } else {
                        this.touchDragSlotStart = null;
                    }
                } else if (!this.cursorDragging) {
                    if (this.handler.getCursorStack().isEmpty()) {
                        if (bl) {
                            this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                        } else {
                            boolean bl3 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT));
                            SlotActionType slotActionType = SlotActionType.PICKUP;
                            if (bl3) {
                                this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                slotActionType = SlotActionType.QUICK_MOVE;
                            } else if (k == -999) {
                                slotActionType = SlotActionType.THROW;
                            }
                            this.onMouseClick(slot, k, button, slotActionType);
                        }
                        this.cancelNextRelease = true;
                    } else {
                        this.cursorDragging = true;
                        this.heldButtonCode = button;
                        this.cursorDragSlots.clear();
                        if (button == 0) {
                            this.heldButtonType = 0;
                        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                            this.heldButtonType = 1;
                        } else if (bl) {
                            this.heldButtonType = 2;
                        }
                    }
                }
            }
        } else {
            this.onMouseClick(button);
        }
        this.lastClickedSlot = slot;
        this.lastButtonClickTime = l;
        this.lastClickedButton = button;
        return true;
    }

    private void onMouseClick(int button) {
        if (this.focusedSlot != null && this.handler.getCursorStack().isEmpty()) {
            if (this.client.options.swapHandsKey.matchesMouse(button)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesMouse(button)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
            }
        }
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        ItemStack itemStack = this.handler.getCursorStack();
        if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
            if (button == 0 || button == 1) {
                if (this.touchDragStack.isEmpty()) {
                    if (slot != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
                        this.touchDragStack = this.touchDragSlotStart.getStack().copy();
                    }
                } else if (this.touchDragStack.getCount() > 1 && slot != null && ScreenHandler.canInsertItemIntoSlot((Slot)slot, (ItemStack)this.touchDragStack, (boolean)false)) {
                    long l = Util.getMeasuringTimeMs();
                    if (this.touchHoveredSlot == slot) {
                        if (l - this.touchDropTimer > 500L) {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.PICKUP);
                            this.touchDropTimer = l + 750L;
                            this.touchDragStack.decrement(1);
                        }
                    } else {
                        this.touchHoveredSlot = slot;
                        this.touchDropTimer = l;
                    }
                }
            }
        } else if (this.cursorDragging && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && ScreenHandler.canInsertItemIntoSlot((Slot)slot, (ItemStack)itemStack, (boolean)true) && slot.canInsert(itemStack) && this.handler.canInsertIntoSlot(slot)) {
            this.cursorDragSlots.add(slot);
            this.calculateOffset();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.getSlotAt(mouseX, mouseY);
        int i = this.x;
        int j = this.y;
        boolean bl = this.isClickOutsideBounds(mouseX, mouseY, i, j, button);
        int k = GLFW.GLFW_KEY_UNKNOWN;
        if (slot != null) {
            k = slot.id;
        }
        if (bl) {
            k = -999;
        }
        if (this.doubleClicking && slot != null && button == 0 && this.handler.canInsertIntoSlot(ItemStack.EMPTY, slot)) {
            if (MultiInvHandledScreen.hasShiftDown()) {
                if (!this.quickMovingStack.isEmpty()) {
                    for (Slot slot2 : ((ScreenHandler)this.handler).slots) {
                        if (slot2 == null || !slot2.canTakeItems((PlayerEntity)this.client.player) || !slot2.hasStack() || slot2.inventory != slot.inventory || !ScreenHandler.canInsertItemIntoSlot((Slot)slot2, (ItemStack)this.quickMovingStack, (boolean)true)) continue;
                        this.onMouseClick(slot2, slot2.id, button, SlotActionType.QUICK_MOVE);
                    }
                }
            } else {
                this.onMouseClick(slot, k, button, SlotActionType.PICKUP_ALL);
            }
            this.doubleClicking = false;
            this.lastButtonClickTime = 0L;
        } else {
            if (this.cursorDragging && this.heldButtonCode != button) {
                this.cursorDragging = false;
                this.cursorDragSlots.clear();
                this.cancelNextRelease = true;
                return true;
            }
            if (this.cancelNextRelease) {
                this.cancelNextRelease = false;
                return true;
            }
            if (this.touchDragSlotStart != null && this.client.options.getTouchscreen().getValue().booleanValue()) {
                if (button == 0 || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    if (this.touchDragStack.isEmpty() && slot != this.touchDragSlotStart) {
                        this.touchDragStack = this.touchDragSlotStart.getStack();
                    }
                    boolean bl2 = ScreenHandler.canInsertItemIntoSlot((Slot)slot, (ItemStack)this.touchDragStack, (boolean)false);
                    if (k != GLFW.GLFW_KEY_UNKNOWN && !this.touchDragStack.isEmpty() && bl2) {
                        this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                        this.onMouseClick(slot, k, 0, SlotActionType.PICKUP);
                        if (this.handler.getCursorStack().isEmpty()) {
                            this.touchDropReturningStack = ItemStack.EMPTY;
                        } else {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.PICKUP);
                            this.touchDropX = MathHelper.floor((double)(mouseX - (double)i));
                            this.touchDropY = MathHelper.floor((double)(mouseY - (double)j));
                            this.touchDropOriginSlot = this.touchDragSlotStart;
                            this.touchDropReturningStack = this.touchDragStack;
                            this.touchDropTime = Util.getMeasuringTimeMs();
                        }
                    } else if (!this.touchDragStack.isEmpty()) {
                        this.touchDropX = MathHelper.floor((double)(mouseX - (double)i));
                        this.touchDropY = MathHelper.floor((double)(mouseY - (double)j));
                        this.touchDropOriginSlot = this.touchDragSlotStart;
                        this.touchDropReturningStack = this.touchDragStack;
                        this.touchDropTime = Util.getMeasuringTimeMs();
                    }
                    this.endTouchDrag();
                }
            } else if (this.cursorDragging && !this.cursorDragSlots.isEmpty()) {
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData((int)0, (int)this.heldButtonType), SlotActionType.QUICK_CRAFT);
                for (Slot slot2 : this.cursorDragSlots) {
                    this.onMouseClick(slot2, slot2.id, ScreenHandler.packQuickCraftData((int)1, (int)this.heldButtonType), SlotActionType.QUICK_CRAFT);
                }
                this.onMouseClick(null, -999, ScreenHandler.packQuickCraftData((int)2, (int)this.heldButtonType), SlotActionType.QUICK_CRAFT);
            } else if (!this.handler.getCursorStack().isEmpty()) {
                if (this.client.options.pickItemKey.matchesMouse(button)) {
                    this.onMouseClick(slot, k, button, SlotActionType.CLONE);
                } else {
                    boolean bl2;
                    boolean bl3 = bl2 = k != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT));
                    if (bl2) {
                        this.quickMovingStack = slot != null && slot.hasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                    }
                    this.onMouseClick(slot, k, button, bl2 ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP);
                }
            }
        }
        if (this.handler.getCursorStack().isEmpty()) {
            this.lastButtonClickTime = 0L;
        }
        this.cursorDragging = false;
        return true;
    }

    public void endTouchDrag() {
        this.touchDragStack = ItemStack.EMPTY;
        this.touchDragSlotStart = null;
    }

    private boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
        String id=this.handler.getInventoryForSlot(slot);
        if(id !=null && invsComps.containsKey(id))
        {
            var pos= getSlotPosition(slot,id);
            return this.isPointWithinBounds(pos.getFirst(),pos.getSecond(), 16, 16, pointX, pointY);
        }
        return  false;

    }

    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.x;
        int j = this.y;
        return (pointX -= (double)i) >= (double)(x - 1) && pointX < (double)(x + width + 1) && (pointY -= (double)j) >= (double)(y - 1) && pointY < (double)(y + height + 1);
    }

    /**
     * @see net.minecraft.screen.ScreenHandler#onSlotClick(int, int, net.minecraft.screen.slot.SlotActionType, net.minecraft.entity.player.PlayerEntity)
     */
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null) {
            slotId = slot.id;
        }
        this.client.interactionManager.clickSlot(((ScreenHandler)this.handler).syncId, slotId, button, actionType, this.client.player);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Owo.DEBUG && this.modelId != null && keyCode == GLFW.GLFW_KEY_F5 && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            this.client.setScreen(new ConfigureHotReloadScreen(this.modelId, this));
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.client.options.pickItemKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
            } else if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, MultiInvHandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
            }
        }
        return true;
    }

    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
            if (this.client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 40, SlotActionType.SWAP);
                return true;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) continue;
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, i, SlotActionType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.client.player == null) {
            return;
        }
        this.handler.onClosed((PlayerEntity)this.client.player);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!this.client.player.isAlive() || this.client.player.isRemoved()) {
            this.client.player.closeHandledScreen();
        } else {
            this.handledScreenTick();
        }
    }

    protected void handledScreenTick() {
    }

    @Override
    public T getScreenHandler() {
        return this.handler;
    }

    @Override
    public void close() {
        this.client.player.closeHandledScreen();
        super.close();
    }
}

