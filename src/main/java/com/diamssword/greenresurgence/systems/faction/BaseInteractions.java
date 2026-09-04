package com.diamssword.greenresurgence.systems.faction;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import com.diamssword.greenresurgence.events.PlaceBlockCallback;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionList;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.*;
import com.diamssword.greenresurgence.systems.faction.worldSnapshot.ChunkSnapshot;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class BaseInteractions {

	public static AllowedBlocksList allowedListSynced = new AllowedBlocksList(false);
	public static AllowedBlocksList allowedListRecipe = new AllowedBlocksList(true);


	public static void register() {
		// ServerTickEvents.START_WORLD_TICK.register(BaseInteractions::playerTick);
		AttackBlockCallback.EVENT.register(BaseInteractions::destroyBlock);
		// UseBlockCallback.EVENT.register(BaseInteractions::placeBlock);

		BaseEventCallBack.ENTER.register(BaseInteractions::onEnter);
		BaseEventCallBack.LEAVE.register(BaseInteractions::onLeave);
		BaseEventCallBack.PLAYER_TICK.register(BaseInteractions::onTickInZone);
		PlaceBlockCallback.EVENT.register(BaseInteractions::placeBlock);
	}

	private static void onTickInZone(ServerPlayerEntity serverPlayerEntity, FactionGuild factionGuild) {
		var dt = serverPlayerEntity.getComponent(Components.PLAYER_DATA);
		dt.healthManager.addContaminationMitigated(-0.1f);
	}

	public static boolean canBreak(World world, BlockPos pos, BlockState state) {

		return allowedListSynced.canBreakBlock(world, pos, state) || allowedListRecipe.canBreakBlock(world, pos, state);
	}

	public static boolean canPlace(World world, BlockPos pos, BlockState state) {

		return allowedListSynced.canPlaceBlock(world, pos, state) || allowedListRecipe.canPlaceBlock(world, pos, state);
	}

	private static ActionResult placeBlock(ItemPlacementContext ctx, BlockState state) {
		if(ctx.getPlayer() != null && ctx.getPlayer() instanceof ServerPlayerEntity pl) {
			if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL)) {
				FactionList list = ctx.getWorld().getComponent(Components.BASE_LIST);
				if(list.isAllowedAt(ctx.getBlockPos(), new FactionMember(pl), Perms.PLACE)) {
					if(canPlace(ctx.getWorld(), ctx.getBlockPos(), state)) {
						ChunkSnapshot.getSnapshotFor(ctx.getWorld(), ctx.getBlockPos()).putBlockIfAbsent(ctx.getBlockPos(), ctx.getWorld().getBlockState(ctx.getBlockPos()));
						var sp = SpecialPlacement.REGISTRY.get(state.getBlock());
						if(sp != null) {
							var terr = list.getTerrainAt(ctx.getBlockPos());
							if(terr.isPresent())
								return sp.onPlacement(ctx.getPlayer(), terr.get(), ctx.getBlockPos()) ? ActionResult.PASS : ActionResult.FAIL;
						}
						return ActionResult.PASS;
					} else
						return ActionResult.FAIL;
				} else
					return ActionResult.FAIL;
			}
		}
		return ActionResult.PASS;
	}

	public static void onEnter(ServerPlayerEntity player, FactionGuild base) {
		player.sendMessage(Text.translatable("message.green_resurgence.guild.zone.enter", base.getName()), true);
		if(base.needSurvival(new FactionMember(player))) {
			if(player.interactionManager.getGameMode().equals(GameMode.ADVENTURE))
				player.changeGameMode(GameMode.SURVIVAL);
			Channels.MAIN.serverHandle(player).send(CurrentZonePacket.from(base, player));
		}
		CurrentZonePacket.sendCreativeDebugZone(player);
	}

	public static boolean shouldOverlayBlock(World world, BlockPos pos, BlockState state) {
		return canBreak(world, pos, state);
	}

	public static void onLeave(ServerPlayerEntity player, FactionGuild base) {
		player.sendMessage(Text.translatable("message.green_resurgence.guild.zone.leave", base.getName()), true);
		if(player.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
			player.changeGameMode(GameMode.ADVENTURE);
	}

	public static ActionResult destroyBlock(PlayerEntity player, World w, Hand hand, BlockPos pos, Direction dir) {
		var m = System.currentTimeMillis();
		if(player instanceof ServerPlayerEntity pl) {
			if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL)) {

				FactionList list = w.getComponent(Components.BASE_LIST);
				if(list.isAllowedAt(pos, new FactionMember(pl), Perms.BREAK)) {
					var st = w.getBlockState(pos);
					if(canBreak(w, pos, st)) {
						var g = ChunkSnapshot.getSnapshotFor(w, pos);
						g.putBlockIfAbsent(pos, st);
						var sp = SpecialPlacement.REGISTRY.get(st.getBlock());
						if(sp != null) {
							var terr = list.getTerrainAt(pos);
							if(terr.isPresent())
								return sp.onBreak(player, terr.get(), pos) ? ActionResult.PASS : ActionResult.FAIL;
						}
						return ActionResult.PASS;
					}
				}
				return ActionResult.FAIL;
			}
		} else if(w.isClient && !player.isCreative()) {
			if(CurrentZonePacket.currentZone != null)
				for(FactionZone box : CurrentZonePacket.currentZone.zones) {
					if(box.getBounds().contains(pos) && canBreak(w, pos, w.getBlockState(pos)))
						return ActionResult.PASS;
				}
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}

	public static boolean canUseItemAt(PlayerEntity player, BlockPos pos, Hand hand) {
		var st = player.getStackInHand(hand);
		return allowedListSynced.canUseItem(player.getWorld(), pos, st) || allowedListRecipe.canUseItem(player.getWorld(), pos, st);
	}

	public static boolean canUseItem(PlayerEntity player, Hand hand) {
		return canUseItemAt(player, player.getBlockPos(), hand);
	}
}
