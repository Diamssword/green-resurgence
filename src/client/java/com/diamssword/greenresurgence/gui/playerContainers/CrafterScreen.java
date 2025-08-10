package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.gui.components.RecipDisplayComponent;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CrafterScreen extends PlayerBasedGui<CrafterBlock.ScreenHandler> {
	public CrafterScreen(CrafterBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/crafter");

	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var ls = rootComponent.childById(ButtonInventoryComponent.class, "list");
		var disp = rootComponent.childById(RecipDisplayComponent.class, "display");
		rootComponent.childById(ButtonComponent.class, "craft").onPress(v -> {
			var r = disp.getRecipe();
			if (r != null) {
				if (disp.getStatus() == null || disp.getStatus().canCraft)
					CraftPackets.sendCraftRequest(r, this.handler.getPos());
			}
		});
		ls.onRecipePicked().subscribe((v, a, b) -> {
			disp.setRecipe(v);
			CraftPackets.requestStatus(v, this.handler.getPos(), disp::setCraftingStatus);
			return true;
		});
		this.handler.onReady(un -> {
			Recipes.get(ls.collectionID).ifPresent(v -> {
				var ls1 = v.getRecipes(this.client.player);
				if (!ls1.isEmpty()) {
					var r = ls1.get(0);
					disp.setRecipe(r);
					CraftPackets.requestStatus(r, this.handler.getPos(), disp::setCraftingStatus);
				}
			});
		});
	}

	@Override
	protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
		//if(window!=null)
		//   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
	}

}