package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.blockEntities.CrafterBlockEntity;
import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.gui.components.RecipDisplayComponent;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.systems.crafting.CraftExtraControl;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CrafterScreen extends PlayerBasedGui<CrafterBlock.ScreenHandler> {
	private CrafterBlockEntity tile = null;

	public CrafterScreen(CrafterBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/crafter");
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		ButtonInventoryComponent<?> ls = rootComponent.childById(ButtonInventoryComponent.class, "list");
		var disp = rootComponent.childById(RecipDisplayComponent.class, "display");
		var searchB = rootComponent.childById(TextBoxComponent.class, "craftlist_search");
		if(searchB != null && ls != null) {
			searchB.setPlaceholder(Text.translatable("gui.green_resurgence.generic.search").formatted(Formatting.GRAY));
			ls.bindSearchField(searchB);
		}
		rootComponent.childById(ButtonComponent.class, "craft").onPress(v -> {
			var r = disp.getRecipe();
			if(r != null) {
				if(tile != null && (disp.getStatus() == null || disp.getStatus().canCraft))
					tile.tryCraft(r.getId(), new CraftExtraControl(Screen.hasShiftDown(), Screen.hasControlDown()), this.client.player);
			}
		});
		if(ls != null)
			ls.onRecipePicked().subscribe((v, a) -> {
				if(v instanceof SimpleRecipe r) {
					if(disp != null) {
						disp.setRecipe(r);
						if(tile != null)
							tile.requestStatus(r.getId(), this.client.player, disp::setCraftingStatus);
					}
				}
				return true;
			});
		this.handler.onReady(un -> {
			tile = ClientGuiPacket.getTile(CrafterBlockEntity.class, handler.getPos());
			if(tile != null && ls != null) {
				ls.setCollection(tile.getCollection());
				Recipes.get(ls.collectionID).ifPresent(v -> {
					var ls1 = v.getRecipes(this.client.player);
					if(!ls1.isEmpty()) {
						var r = ls1.get(0);
						disp.setRecipe(r);
						if(tile != null)
							tile.requestStatus(r.getId(), this.client.player, disp::setCraftingStatus);
					}
				});
			}
		});
	}


	@Override
	protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {

	}

}