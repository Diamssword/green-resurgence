package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.blockEntities.StoneCutterBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneCutterBlock extends ModBlockEntity<StoneCutterBlockEntity> {
	public StoneCutterBlock() {
		super(Settings.create().sounds(BlockSoundGroup.METAL).strength(5).mapColor(MapColor.BLACK));
	}

	@Override
	protected StoneCutterBlockEntity createBlockEntity(BlockEntityType<StoneCutterBlockEntity> type, BlockPos pos, BlockState state) {
		return new StoneCutterBlockEntity(type, pos, state).setCollection(GreenResurgence.asRessource("stone_cutter"));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public Class<StoneCutterBlockEntity> getBlockEntityClass() {
		return StoneCutterBlockEntity.class;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(state.getBlock() != newState.getBlock()) {
			var blockEntity = getBlockEntity(pos, world);
			ItemScatterer.spawn(world, pos, blockEntity.getSlot());
			blockEntity.getSlot().clear();
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient) {
			Containers.createHandler(player, pos, (sync, inv, p1) -> new StoneCutterBlockEntity.ScreenHandler(sync, player, this.getBlockEntity(pos, world)).setPos(pos));
			return ActionResult.SUCCESS;
		}
		return ActionResult.SUCCESS;
	}

}
