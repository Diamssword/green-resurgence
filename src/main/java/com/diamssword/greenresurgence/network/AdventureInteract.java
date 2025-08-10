package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.IAdvancedLootableBlock;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.HashMap;
import java.util.Map;

public class AdventureInteract {
	static Map<PlayerEntity, Long> cooldowns = new HashMap<>();

	public record BlockInteract(BlockPos pos) {
	}

	public record AllowedList(Identifier[] blocks, Identifier[] items) {
	}

	public static void init() {
		Channels.MAIN.registerClientbound(AllowedList.class, (msg, ctx) -> {
			BaseInteractions.allowedBlocks.clear();
			BaseInteractions.allowedItems.clear();
			for (Identifier block : msg.blocks) {
				BaseInteractions.allowedBlocks.add(Registries.BLOCK.get(block));
			}
			for (Identifier block : msg.items) {
				BaseInteractions.allowedItems.add(Registries.ITEM.get(block));
			}
		});
		Channels.MAIN.registerServerbound(BlockInteract.class, (msg, ctx) -> {

			if (ctx.player().interactionManager.getGameMode().isSurvivalLike() && checkCooldown(ctx.player())) {
				ItemStack st = ctx.player().getMainHandStack();
				BlockState state = ctx.player().getWorld().getBlockState(msg.pos);
				if (state.getBlock() == MBlocks.LOOTED_BLOCK) {
					LootedBlockEntity ent = MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos, ctx.player().getWorld());
					if (st != null && LootableLogic.meetBreakRequirement(st, ent.getRealBlock(), ctx.player())) {
						setCooldown(ctx.player());
						ent.attackBlock(ctx.player());
						Lootables.loader.getTable(ent.getRealBlock().getBlock()).ifPresent(l -> {
							if (l.getConnected() != null) {
								for (var dir : Direction.values()) {
									if (dir.getAxis() != Direction.Axis.Y) {
										interactAdjacentBlock(ctx.player(), msg.pos.offset(dir), ctx.player().getWorld(), l.getConnected());
									}
								}
							}
						});
					}
				} else if (state.hasBlockEntity() && ctx.player().getWorld().getBlockEntity(msg.pos) instanceof IAdvancedLootableBlock res) {
					if (res.canBeInteracted()) {
						setCooldown(ctx.player());
						res.lootBlock(ctx.player());
					}
				} else if (st != null && LootableLogic.meetBreakRequirement(st, state, ctx.player())) {
					ctx.player().getWorld().setBlockState(msg.pos, MBlocks.LOOTED_BLOCK.getDefaultState());
					var te = MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos, ctx.player().getWorld());
					te.setRealBlock(state);
					setCooldown(ctx.player());
					te.lastBreak = System.currentTimeMillis();
					te.markDirty();
					LootableLogic.giveLoot(ctx.player(), msg.pos, state);
					ctx.player().getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, msg.pos, Block.getRawIdFromState(state));
					ctx.player().getWorld().playSound(null, msg.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1f + (float) Math.random());
					Lootables.loader.getTable(state.getBlock()).ifPresent(l -> {
						if (l.getConnected() != null) {
							for (var dir : Direction.values()) {
								if (dir.getAxis() != Direction.Axis.Y) {
									interactAdjacentBlock(ctx.player(), msg.pos.offset(dir), ctx.player().getWorld(), l.getConnected());
								}
							}
						}
					});
				}
			}
		});
	}

	private static void interactAdjacentBlock(ServerPlayerEntity player, BlockPos pos, World world, Block block) {
		BlockState state = player.getWorld().getBlockState(pos);
		if (state.getBlock() == MBlocks.LOOTED_BLOCK) {
			LootedBlockEntity ent = MBlocks.LOOTED_BLOCK.getBlockEntity(pos, world);
			ent.attackBlock(player);
		} else if (state.getBlock() == block) {
			player.getWorld().setBlockState(pos, MBlocks.LOOTED_BLOCK.getDefaultState());
			var te = MBlocks.LOOTED_BLOCK.getBlockEntity(pos, world);
			te.setRealBlock(state);
			setCooldown(player);
			te.lastBreak = System.currentTimeMillis();
			te.markDirty();
			LootableLogic.giveLoot(player, pos, state);
			world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));

		}
	}

	private static boolean checkCooldown(PlayerEntity player) {
		if (cooldowns.containsKey(player)) {
			return player.getWorld().getTime() > cooldowns.get(player) + 10;
		}
		return true;
	}

	private static void setCooldown(PlayerEntity player) {
		cooldowns.put(player, player.getWorld().getTime());
	}
}
