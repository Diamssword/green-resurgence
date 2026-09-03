package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.blockEntities.StoneCutterBlockEntity;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.systems.crafting.ComposedIdentifier;
import com.diamssword.greenresurgence.systems.crafting.CraftExtraControl;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.IStoneCutterTypeRecipe;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StoneCutterScreen extends PlayerBasedGui<StoneCutterBlockEntity.ScreenHandler> {
	private StoneCutterBlockEntity tile = null;
	private IStoneCutterTypeRecipe cutter;
	private ButtonInventoryComponent.StoneCutterListComponent display;

	public StoneCutterScreen(StoneCutterBlockEntity.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/stonecutter");
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		display = rootComponent.childById(ButtonInventoryComponent.StoneCutterListComponent.class, "list");
		var searchB = rootComponent.childById(TextBoxComponent.class, "craftlist_search");
		if(searchB != null && display != null) {
			searchB.setPlaceholder(Text.translatable("gui.green_resurgence.generic.search").formatted(Formatting.GRAY));
			display.bindSearchField(searchB);
		}

		if(display != null)
			display.onRecipePicked().subscribe((v, a) -> {
				if(tile != null)
					tile.tryCraft(new ComposedIdentifier(tile.getCollection(), v.getID().toString()), new CraftExtraControl(Screen.hasShiftDown(), Screen.hasControlDown()), MinecraftClient.getInstance().player);
				return true;
			});
		this.handler.onReady(un -> {
			tile = ClientGuiPacket.getTile(StoneCutterBlockEntity.class, handler.getPos());
			if(tile != null && display != null) {
				display.setCollection(tile.getCollection());
				if(handler.getInventory("slot").getInventory() instanceof SimpleInventory inv) {
					this.inputListener(inv);
					inv.addListener(this::inputListener);
				}


			}
		});
	}

	private void inputListener(Inventory inv) {
		display.setInput(UniversalResource.fromItem(inv.getStack(0)));
	}

	@Override
	public void close() {
		super.close();
		if(tile != null)
			tile.getSlot().removeListener(this::inputListener);
	}

	@Override
	protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {

	}

}