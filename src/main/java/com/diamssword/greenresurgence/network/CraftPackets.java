package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.CraftingResult;
import com.diamssword.greenresurgence.systems.crafting.ICraftingTile;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CraftPackets {

	public record RequestCraft(BlockPos pos, Identifier recipe) {}

	public record RequestPlayerCraft(SimpleRecipe recipe) {}

	public record RequestCraftStatus(Integer index, BlockPos pos, Identifier recipe) {}

	public record SendCraftStatus(Integer index, BlockPos pos, CraftingResult recipe) {}

	public static void init() {
		Channels.MAIN.registerClientbound(SendCraftStatus.class, (msg, ctx) -> {
			if(GreenResurgence.clientHelper.getPlayer().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {
				ct.receiveStatus(msg.index, msg.recipe);
			}

		});
		Channels.MAIN.registerServerbound(RequestCraft.class, (msg, ctx) -> {
			if(ctx.player().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {

				var r = ct.tryCraft(msg.recipe, ctx.player());

			}
		});
		Channels.MAIN.registerServerbound(RequestPlayerCraft.class, (msg, ctx) -> {
			var pli = ctx.player().getComponent(Components.PLAYER_INVENTORY);
			pli.getCrafterProvider().craftRecipe(msg.recipe, ctx.player());
		});
		Channels.MAIN.registerServerbound(RequestCraftStatus.class, (msg, ctx) -> {
			if(ctx.player().getWorld().getBlockEntity(msg.pos) instanceof ICraftingTile<?> ct) {
				ct.handleStatusRequest(msg.index, msg.recipe, ctx.player());


			}
		});
	}


	public static void sendCraftRequest(Identifier recipeId, BlockPos pos) {
		Channels.MAIN.clientHandle().send(new RequestCraft(pos, recipeId));
	}

}
