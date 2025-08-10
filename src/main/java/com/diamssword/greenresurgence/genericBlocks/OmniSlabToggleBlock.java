package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class OmniSlabToggleBlock extends OmniSlabBlock {
	public static final EnumProperty<Direction> TYPE = Properties.FACING;
	public static final BooleanProperty TOGGLE = Properties.OPEN;

	public OmniSlabToggleBlock(Settings settings, boolean carpet, boolean passthrough) {
		super(settings, carpet, passthrough);
		this.setDefaultState(this.getDefaultState().with(TOGGLE, false).with(WATERLOGGED, false));
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TOGGLE, TYPE, WATERLOGGED);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		world.setBlockState(pos, state.with(TOGGLE, !state.get(TOGGLE)));
		boolean b = state.get(TOGGLE);
		world.playSound(player, pos, b ? SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE : SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
		world.emitGameEvent(player, b ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return ActionResult.SUCCESS;
	}

}

