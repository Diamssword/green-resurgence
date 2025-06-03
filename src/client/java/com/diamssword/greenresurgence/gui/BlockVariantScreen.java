package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import com.diamssword.greenresurgence.systems.crafting.RecipeCollection;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;

public class BlockVariantScreen extends MultiInvHandledScreen<BlockVariantItem.Container, FlowLayout> {
	private boolean onWindow = false;
	private BlockVariantItem parent;

	public BlockVariantScreen(BlockVariantItem.Container handler, PlayerInventory inv, Text title) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("block_variant")));
		for (ItemStack handItem : MinecraftClient.getInstance().player.getHandItems()) {
			if (handItem.getItem() instanceof BlockVariantItem it) {
				this.parent = it;
				break;
			}
		}
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		if (this.parent != null) {
			ButtonInventoryComponent comp = rootComponent.childById(ButtonInventoryComponent.class, "main");
			var search = rootComponent.childById(TextBoxComponent.class, "search");
			search.setPlaceholder(Text.literal("Recherche"));
			this.setFocused(search);
			comp.bindSearchField(search);
			comp.focusGained().subscribe(v -> {
				this.setFocused(search);
			});
			var coll = new RecipeCollection(new Identifier("minecraft:void"));
			parent.getVariants().forEach(v -> coll.add(new SimpleRecipe(v).setID(v)));
			comp.onRecipePicked().subscribe((v, v1, v2) -> onPick(v));
			comp.setSorter(sorter());
			comp.setCollection(coll, GreenResurgence.asRessource("air"));
		}
	}

	@Override
	protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {

	}

	private Comparator<SimpleRecipe> sorter() {

		return (o1, o2) -> {
			String[] wordsA = o1.getId().getPath().split("_");
			String[] wordsB = o2.getId().getPath().split("_");

			int maxLength = Math.max(wordsA.length, wordsB.length);
			for (int i = 0; i < maxLength; i++) {
				String wordA = i < wordsA.length ? wordsA[i] : "";
				String wordB = i < wordsB.length ? wordsB[i] : "";

				int cmp = wordA.compareTo(wordB);
				if (cmp != 0) return cmp;
			}
			return 0;
		};
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 69) //desactive le 'e' qui ferme le gui
			return false;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private boolean onPick(SimpleRecipe re) {
		var st = re.result(client.player);
		if ((InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT))) {
			int i = this.handler.getPlayerInventory().getEmptySlot();
			if (i > -1) {
				this.handler.getPlayerInventory().setStack(i, st.asItem().copyWithCount(64));
				this.client.interactionManager.clickCreativeStack(st.asItem().copyWithCount(64), i);
			}

		} else {
			this.handler.setCursorStack(st.asItem());
		}
		onWindow = true;
		return true;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		if (slot != null) {
			slotId = slot.id;
		}
		if (slotId > -1 && !this.handler.getCursorStack().isEmpty()) {
			var d = this.handler.getCursorStack();
			this.handler.setCursorStack(slot.getStack());
			this.client.interactionManager.clickCreativeStack(d, slotId + 9);

		} else if (slotId > -1) {
			var d = slot.getStack();
			slot.setStack(this.handler.getCursorStack());
			this.handler.setCursorStack(d);

		} else if (!onWindow) {
			super.onMouseClick(slot, slotId, button, actionType);
		}
		onWindow = false;
		//   this.client.interactionManager.clickSlot(((net.minecraft.screen.ScreenHandler)this.handler).syncId, slotId, button, actionType, this.client.player);
	}
}