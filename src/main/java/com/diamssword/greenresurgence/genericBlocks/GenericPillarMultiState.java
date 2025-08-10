package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class GenericPillarMultiState extends GenericPillar {
	private IntProperty toggled;

	public GenericPillarMultiState(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings, props);
		this.setDefaultState(this.getDefaultState().with(getToggleProp(), 1));
	}

	public abstract int getPropCount();

	public IntProperty getToggleProp() {
		if (toggled == null)
			toggled = IntProperty.of("state", 1, getPropCount());
		return toggled;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(getToggleProp(), FACING, WATERLOGGED);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		super.onUse(state, world, pos, player, hand, hit);
		var v = state.get(getToggleProp());
		v++;
		if (v > getPropCount())
			v = 1;
		world.setBlockState(pos, state.with(getToggleProp(), v));
		world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + (v * 0.1f));
		world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
		return ActionResult.SUCCESS;
	}
}
