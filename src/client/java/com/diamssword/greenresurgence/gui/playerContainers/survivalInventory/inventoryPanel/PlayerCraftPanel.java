package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.gui.components.ItemCooldownComponent;
import com.diamssword.greenresurgence.gui.components.hud.IconComponent;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.character.PlayerInventoryData;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.TimedCraftingProvider;
import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerCraftPanel extends InstancedSubPanel {


	public PlayerCraftPanel() {
		super("craft", "gui/subpanel/craft_icon", "survival/subpanel/craft", Instance::new);
	}

	@Override
	public int preferredWidth() {
		return 100;
	}

	@Override
	public int preferredHeight() {
		return 100;
	}

	private static class Instance implements SubPanelInstance {

		private PlayerInventoryData playerinv;
		private ClickableLayoutComponent last;

		@Override
		public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {
			var scroll = root.childById(ScrollContainer.class, "craftlist_body");
			if(scroll != null)
				scroll.verticalSizing(Sizing.fixed(root.height() - 25));
		}

		@Override
		public void build(FlowLayout rootComponent, PlayerBasedGui<?> gui) {

			var scroll = rootComponent.childById(ScrollContainer.class, "craftlist_body");
			scroll.verticalSizing(Sizing.fixed(rootComponent.height() - 25));
			var ls = rootComponent.childById(FlowLayout.class, "recipeMenu");
			var queue = rootComponent.childById(FlowLayout.class, "recipeProgress");
			var offHandFlow = rootComponent.childById(FlowLayout.class, "flowOffhand");
			var client = MinecraftClient.getInstance();
			var collection = Recipes.get(GreenResurgence.asRessource("player"));
			if(collection.isPresent()) {
				var recipes = collection.get().getRecipes(client.player);
				for(var rec : recipes) {
					ls.child(create(rec));
				}
			}
			if(offHandFlow != null) {
				offHandFlow.horizontalSizing(Sizing.fixed(48));
			}
			playerinv = client.player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
			playerinv.getCrafterProvider().onNewRecipeQueued(() -> redrawQueue(queue, playerinv.getCrafterProvider()));
			redrawQueue(queue, playerinv.getCrafterProvider());


		}

		private void redrawQueue(FlowLayout container, TimedCraftingProvider provider) {
			var client = MinecraftClient.getInstance();
			container.clearChildren();
			last = null;
			Map<Item, ItemStack> stacks = new HashMap<>();

			provider.getPendingCrafts().forEach(v -> {
				var r = v.recipe.result(client.player).getCurrentItem(client.getRenderTime());
				if(stacks.containsKey(r.getItem())) {
					var st = stacks.get(r.getItem());
					st.setCount(st.getCount() + r.getCount());
				} else
					stacks.put(r.getItem(), r);
			});
			for(var st : stacks.values()) {
				var k = addQueue(st);
				container.child(k);
				if(last == null)
					last = k;
			}
		}

		//TODO affiche le progress seulement dans la derniere instance crée
		@Override
		public void panelTick() {
			if(playerinv != null && last != null) {
				if(last.children().get(0) instanceof ItemCooldownComponent co)
					co.setCooldown(playerinv.getCrafterProvider().getCraftProgress());

			}
		}

		public ClickableLayoutComponent addQueue(ItemStack stack) {
			var cli = new ClickableLayoutComponent(Sizing.content(), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL);
			cli.surface(HoloPanels.panel(true, true));
			cli.surface2(HoloPanels.panel(true, true).and(Surface.outline(ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF))));
			cli.child(new ItemCooldownComponent(stack).showOverlay(true).setTooltipFromStack(true));
			return cli;
		}

		private static final Supplier<IconComponent> arrow_c = () -> (IconComponent) new IconComponent(GreenResurgence.asRessource("textures/gui/craft/arrow.png"), 0, 0, 32, 32, 32, 32).sizing(Sizing.fixed(12), Sizing.fixed(16));

		public ClickableLayoutComponent create(SimpleRecipe recipe) {
			var client = MinecraftClient.getInstance();
			var cli = new ClickableLayoutComponent(Sizing.content(), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL);
			cli.onPress((c) -> {
				Channels.MAIN.clientHandle().send(new CraftPackets.RequestPlayerCraft(recipe));
			});

			cli.cursorStyle(CursorStyle.HAND).sizing(Sizing.fill(100), Sizing.content());
			cli.alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
			cli.surface2(HoloPanels.panel(true, false).and(Surface.outline(ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF))));

			cli.gap(2).padding(Insets.of(2)).surface(HoloPanels.panel(true, false)).margins(Insets.vertical(1));
			var ings = recipe.ingredients(client.player);
			for(var ing : ings) {
				if(ing.getType().isItem)
					cli.child(Components.item(ing.getCurrentItem(client.getRenderTime())).showOverlay(true).setTooltipFromStack(true));

			}
			cli.child(arrow_c.get());

			var res = recipe.result(client.player);
			if(res.getType().isItem)
				cli.child(Components.item(res.getCurrentItem(client.getRenderTime())).showOverlay(true).setTooltipFromStack(true));
			return cli;
		}
	}

}