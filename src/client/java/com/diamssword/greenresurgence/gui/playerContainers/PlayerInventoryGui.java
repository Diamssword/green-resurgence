package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.VanillaPlayerInvMokup;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.gui.components.ItemCooldownComponent;
import com.diamssword.greenresurgence.gui.components.hud.IconComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.character.PlayerInventoryData;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.TimedCraftingProvider;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerInventoryGui extends PlayerBasedGui<VanillaPlayerInvMokup> {
	private PlayerInventoryData playerinv;
	private ClickableLayoutComponent last;

	public PlayerInventoryGui(VanillaPlayerInvMokup handler, PlayerInventory inv, Text title) {
		super(handler, "survival/player_craft");

	}


	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var ls = rootComponent.childById(FlowLayout.class, "recipeMenu");
		var queue = rootComponent.childById(FlowLayout.class, "recipeProgress");
		var collection = Recipes.get(GreenResurgence.asRessource("player"));
		if (collection.isPresent()) {
			var recipes = collection.get().getRecipes(client.player);
			for (var rec : recipes) {
				ls.child(create(rec));
			}
		}
		playerinv = client.player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
		playerinv.getCrafterProvider().onNewRecipeQueued(() -> redrawQueue(queue, playerinv.getCrafterProvider()));
		redrawQueue(queue, playerinv.getCrafterProvider());
	}

	private void redrawQueue(FlowLayout container, TimedCraftingProvider provider) {
		container.clearChildren();
		last = null;
		Map<Item, ItemStack> stacks = new HashMap<>();

		provider.getPendingCrafts().forEach(v -> {
			var r = v.recipe.result(client.player).getCurrentItem(client.getRenderTime());
			if (stacks.containsKey(r.getItem())) {
				var st = stacks.get(r.getItem());
				st.setCount(st.getCount() + r.getCount());
			} else
				stacks.put(r.getItem(), r);
		});
		for (var st : stacks.values()) {
			var k = addQueue(st);
			container.child(k);
			if (last == null)
				last = k;
		}
	}

	private static final Supplier<IconComponent> arrow_c = () -> (IconComponent) new IconComponent(GreenResurgence.asRessource("textures/gui/craft/arrow.png"), 0, 0, 32, 32, 32, 32).sizing(Sizing.fixed(12), Sizing.fixed(16));

	public ClickableLayoutComponent create(SimpleRecipe recipe) {
		var cli = new ClickableLayoutComponent(Sizing.content(), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL);
		cli.onPress((c) -> {
			Channels.MAIN.clientHandle().send(new CraftPackets.RequestPlayerCraft(recipe));
		});

		cli.cursorStyle(CursorStyle.HAND).sizing(Sizing.fill(100), Sizing.content());
		cli.alignment(HorizontalAlignment.RIGHT, VerticalAlignment.CENTER);
		cli.surface2(Surface.VANILLA_TRANSLUCENT.and(Surface.outline(0xc4c9c3FF)));
		cli.gap(2).padding(Insets.of(2)).surface(Surface.VANILLA_TRANSLUCENT).margins(Insets.vertical(1));
		var ings = recipe.ingredients(client.player);
		for (var ing : ings) {
			if (ing.getType().isItem)
				cli.child(Components.item(ing.getCurrentItem(client.getRenderTime())).showOverlay(true).setTooltipFromStack(true));

		}
		cli.child(arrow_c.get());

		var res = recipe.result(client.player);
		if (res.getType().isItem)
			cli.child(Components.item(res.getCurrentItem(client.getRenderTime())).showOverlay(true).setTooltipFromStack(true));
		return cli;
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if (playerinv != null && last != null) {
			if (last.children().get(0) instanceof ItemCooldownComponent co)
				co.setCooldown(playerinv.getCrafterProvider().getCraftProgress());

		}
	}

	public ClickableLayoutComponent addQueue(ItemStack stack) {
		var cli = new ClickableLayoutComponent(Sizing.content(), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL);
		cli.child(new ItemCooldownComponent(stack).showOverlay(true).setTooltipFromStack(true));
		return cli;
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}
}
