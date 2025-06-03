package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.gui.components.InventorySearchableComponent;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionTerrainStorage;
import com.mojang.datafixers.util.Pair;
import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FactionStorageScreen extends PlayerBasedGui<FactionTerrainStorage.ScreenHandler> {

	public FactionStorageScreen(FactionTerrainStorage.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/faction_global_storage");

	}

	private static InventorySearchableComponent storage;

	@Override
	protected void findInvComps(BaseParentComponent root) {
		root.children().forEach(c -> {
			if (c instanceof InventoryComponent par) {
				invsComps.put(par.inventoryId, par);
				var inv = this.handler.getInventory(par.inventoryId);
				if (inv != null)
					par.setSize(inv.getWidth(), inv.getHeight());
				else
					par.hidden(true);
			} else if (c instanceof InventorySearchableComponent par) {
				storage = par;
				var inv = this.handler.getInventory(par.inventoryId);
				if (inv != null) {
					par.setSize(6, 6);
					par.setInventory(this.handler.getSlotForInventory(par.inventoryId));
					this.handler.onSlotAdded(() -> {
						par.setInventory(this.handler.getSlotForInventory(par.inventoryId));
					});

				}
			} else if (c instanceof BaseParentComponent c1)
				findInvComps(c1);
		});
	}

	protected void drawSlotList(List<Slot> slots, String id, DrawContext context, int mouseX, int mouseY) {
		for (Slot slot : slots) {

			if (slot.isEnabled()) {

				this.drawSlot(context, slot, id);
			}
			if (!this.isPointOverSlot(slot, mouseX, mouseY) || !slot.isEnabled()) continue;
			this.focusedSlot = slot;
			var pos = getSlotPosition(slot, id);
			if (!this.focusedSlot.canBeHighlighted()) continue;
			MultiInvHandledScreen.drawSlotHighlight(context, pos.getFirst(), pos.getSecond(), 0);
		}
	}

	@Override
	protected void drawSlots(DrawContext context, int mouseX, int mouseY, float delta) {
		for (String id : invsComps.keySet()) {
			drawSlotList(this.handler.getSlotForInventory(id), id, context, mouseX, mouseY);
		}
		drawSlotList(storage.getDisplayedSlots(), "storage", context, mouseX, mouseY);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		if (storage != null) {
			if (storage.isInBoundingBox(mouseX, mouseY))
				return false;
		}
		return super.isClickOutsideBounds(mouseX, mouseY, left, top, button);
	}

	@Override
	protected Slot getSlotAt(double x, double y) {
		if (storage != null) {

			for (Slot slot : storage.getDisplayedSlots()) {
				if (this.isPointOverSlot(slot, x, y)) {
					return slot;
				}


			}

		}
		for (int i = 0; i < this.handler.slots.size(); ++i) {
			Slot slot = this.handler.slots.get(i);
			if (!this.isPointOverSlot(slot, x, y) || !slot.isEnabled()) continue;
			return slot;
		}

		return null;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {

		if (slot != null) {
			slotId = slot.id;
		}
		if (slot instanceof InventorySearchableComponent.FalseSlot) {
			if (this.handler.getCursorStack().isEmpty())
				return;
			slotId = this.handler.getSlotForInventory("storage").get(0).id;
		}
		this.client.interactionManager.clickSlot(this.handler.syncId, slotId, button, actionType, this.client.player);
	}

	@Override
	protected boolean isPointOverSlot(Slot slot, double pointX, double pointY) {
		String id = this.handler.getInventoryForSlot(slot);
		if (id == null)
			id = "storage";
		var pos = getSlotPosition(slot, id);
		return this.isPointWithinBounds(pos.getFirst(), pos.getSecond(), 16, 16, pointX, pointY);

	}

	@Override
	public Pair<Integer, Integer> getSlotPosition(Slot s, String inventory) {
		if ("storage".equals(inventory) && storage != null) {
			List<Slot> slots = storage.getDisplayedSlots();
			var i = slots.indexOf(s);
			if (i >= 0)
				return new Pair<>(((i % storage.getSlotsSize().getLeft()) * 18) + storage.x() - this.x, ((i / storage.getSlotsSize().getLeft()) * 18) + storage.y() - this.y + 20);
			return new Pair<>(0, 0);
		} else
			return super.getSlotPosition(s, inventory);
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
	}

	@Override
	protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
		//if(window!=null)
		//   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
	}

	@Override
	protected void drawSlot(DrawContext context, Slot slot, String inventory) {
		Pair pair;
		var pos = getSlotPosition(slot, inventory);
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
			if (ScreenHandler.canInsertItemIntoSlot(slot, itemStack2, true) && this.handler.canInsertIntoSlot(slot)) {
				bl = true;
				int k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
				int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
				int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2) + l;
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
			Sprite sprite = this.client.getSpriteAtlas((Identifier) pair.getFirst()).apply((Identifier) pair.getSecond());
			context.drawSprite(pos.getFirst(), pos.getSecond(), 0, 16, 16, sprite);
			bl2 = true;
		}
		if (!bl2) {
			if (bl) {
				context.fill(pos.getFirst(), pos.getSecond(), pos.getFirst() + 16, pos.getSecond() + 16, -2130706433);
			}
			context.drawItem(itemStack, pos.getFirst(), pos.getSecond(), pos.getFirst() + pos.getSecond() * this.backgroundWidth);
			if (string != null)
				context.drawItemInSlot(this.textRenderer, itemStack, pos.getFirst(), pos.getSecond(), string);
			else if (!itemStack.isEmpty() && itemStack.getCount() > 1)
				RessourceGuiHelper.drawRessourceExtra(context, UniversalResource.fromItemOpti(itemStack), pos.getFirst(), pos.getSecond(), 0, 16777215);
		}
		context.getMatrices().pop();
	}

	public void drawItemInSlot(DrawContext ctx, ItemStack stack, int x, int y, @Nullable String countOverride) {
		if (!stack.isEmpty()) {
			ctx.getMatrices().push();
			if (stack.getCount() != 1 || countOverride != null) {
				String string = countOverride == null ? RessourceGuiHelper.getCountDisplay(stack.getCount(), false) : countOverride;
				ctx.getMatrices().translate(0.0F, 0.0F, 200.0F);
				ctx.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, 16777215, true);
			}

			int k;
			int l;
			if (stack.isItemBarVisible()) {
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				k = x + 2;
				l = y + 13;
				ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
				ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
			}

			ClientPlayerEntity clientPlayerEntity = this.client.player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());
			if (f > 0.0F) {
				k = y + MathHelper.floor(16.0F * (1.0F - f));
				l = k + MathHelper.ceil(16.0F * f);
				ctx.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
			}
			ctx.getMatrices().pop();
		}
	}
}