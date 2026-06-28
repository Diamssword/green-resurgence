package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blocks.ClaimBlock;
import com.diamssword.greenresurgence.blocks.NanoGeneratorBlock;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class ClaimBlockPlacerItem extends Item {
	public ClaimBlockPlacerItem(Settings properties) {
		super(properties);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.of("Permet de creer un campement autour de l'emplacement du générateur"));
	}


	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.ANY);
		if(hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else {
			if(hitResult.getType() == HitResult.Type.BLOCK) {
				useOnBlock(new ItemUsageContext(user, hand, hitResult));
			} else {
				return TypedActionResult.pass(itemStack);
			}
		}
		return TypedActionResult.pass(itemStack);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		var pos = context.getBlockPos().offset(context.getSide());
		if(isBlockReplacable(context.getWorld(), context.getBlockPos()))
			pos = context.getBlockPos();
		var dir = context.getHorizontalPlayerFacing().getOpposite();
		if(!context.getWorld().isClient && isSpaceFree(context.getWorld(), pos, dir)) {
			var guilds = context.getWorld().getComponent(Components.BASE_LIST);
			if(!guilds.doBoxIntersectWithOtherFaction(context.getPlayer().getUuid(), new BlockBox(pos).expand(4))) {
				var succ = false;
				var currg = guilds.getForPlayer(context.getPlayer().getUuid(), false);
				if(currg.isEmpty()) {
					currg = Optional.of(FactionGuild.createForPlayer(context.getPlayer(), pos, 4));
					succ = guilds.addGuild(currg.get());
				} else {
					if(currg.get().getTerrainAt(pos).isEmpty()) {
						currg.get().addZone(pos, 4, context.getWorld());
						succ = true;
					} else
						context.getPlayer().sendMessage(Text.literal("Cette zone est déja protégée par un de vos générateur"));
				}
				if(succ) {
					placeBig(context.getWorld(), pos, dir, context.getPlayer(), currg.get());
					context.getStack().decrement(1);
					return ActionResult.CONSUME;
				}
			} else
				context.getPlayer().sendMessage(Text.literal("Un autre générateur interfére avec cette zone"));
			return ActionResult.FAIL;
		}
		return ActionResult.FAIL;
	}

	private boolean isBlockReplacable(WorldAccess world, BlockPos pos) {
		return world.getBlockState(pos).isReplaceable();
	}

	private boolean isSpaceFree(WorldAccess world, BlockPos pos, Direction dir) {
		boolean center = isBlockReplacable(world, pos) && isBlockReplacable(world, pos.up()) && isBlockReplacable(world, pos.up(2)) && isBlockReplacable(world, pos.up(3));
		if(center) {
			var right = dir.rotateYCounterclockwise();
			var left = dir.rotateYClockwise();
			return center && isBlockReplacable(world, pos.add(left.getVector())) && isBlockReplacable(world, pos.add(right.getVector())) && isBlockReplacable(world, pos.add(right.getVector()).up());
		}
		return false;
	}

	private void placeBig(World world, BlockPos pos, Direction dir, PlayerEntity player, FactionGuild faction) {
		world.playSound(player, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, (BlockSoundGroup.AMETHYST_BLOCK.getVolume() + 1.0F) / 2.0F, BlockSoundGroup.AMETHYST_BLOCK.getPitch() * 0.8F);
		world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(player, MBlocks.NANOTEK_GENERATOR_RELAY.getDefaultState()));

		world.setBlockState(pos, MBlocks.NANOTEK_GENERATOR_COMPUTER.getDefaultState().with(NanoGeneratorBlock.FACING, dir));
		world.setBlockState(pos.up(), MBlocks.NANOTEK_GENERATOR_RELAY.getDefaultState().with(ClaimBlock.FACING, dir));
		MBlocks.NANOTEK_GENERATOR_RELAY.getBlockEntity(pos.up(), world).setFaction(faction);
		world.setBlockState(pos.up(2), MBlocks.NANOTEK_GENERATOR_PILLAR.getDefaultState().with(NanoGeneratorBlock.FACING, dir));
		world.setBlockState(pos.up(3), MBlocks.NANOTEK_GENERATOR_BIG_ANTENNA.getDefaultState().with(NanoGeneratorBlock.FACING, dir));
		var right = dir.rotateYCounterclockwise();
		var left = dir.rotateYClockwise();
		world.setBlockState(pos.add(left.getVector()), MBlocks.NANOTEK_GENERATOR_CANISTER.getDefaultState().with(NanoGeneratorBlock.FACING, dir));
		world.setBlockState(pos.add(right.getVector()), MBlocks.NANOTEK_GENERATOR_SERVER.getDefaultState().with(NanoGeneratorBlock.FACING, dir));
		world.setBlockState(pos.add(right.getVector()).up(), MBlocks.NANOTEK_GENERATOR_SMALL_ANTENNA.getDefaultState().with(NanoGeneratorBlock.FACING, dir));

	}
}