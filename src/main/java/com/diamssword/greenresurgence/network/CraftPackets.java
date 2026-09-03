package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.*;
import net.minecraft.util.math.BlockPos;

public class CraftPackets {

	public record RequestCraft(BlockPos pos, ComposedIdentifier recipe, CraftExtraControl control) {}

	public record RequestPlayerCraft(SimpleRecipe recipe) {}

	public record RequestCraftStatus(Integer index, BlockPos pos, ComposedIdentifier recipe) {}

	public record SendCraftStatus(Integer index, BlockPos pos, CraftingResult recipe) {}


	public static void init() {
		Channels.MAIN.registerClientbound(SendCraftStatus.class, (msg, ctx) -> {
			if(GreenResurgence.clientHelper.getPlayer().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {
				ct.receiveStatus(msg.index, msg.recipe);
			}

		});
		Channels.MAIN.registerServerbound(RequestCraft.class, (msg, ctx) -> {
			if(ctx.player().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {

				var r = ct.tryCraft(msg.recipe, msg.control, ctx.player());

			}
		});
		Channels.MAIN.registerServerbound(RequestPlayerCraft.class, (msg, ctx) -> {
			if(msg.recipe.getId().getCollection().equals(GreenResurgence.asRessource("player"))) {
				var pli = ctx.player().getComponent(Components.PLAYER_INVENTORY);
				pli.getCrafterProvider().craftRecipe(msg.recipe, ctx.player());
			}
		});
		Channels.MAIN.registerServerbound(RequestCraftStatus.class, (msg, ctx) -> {
			if(ctx.player().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {
				ct.handleStatusRequest(msg.index, msg.recipe, ctx.player());


			}
		});
	}


}
