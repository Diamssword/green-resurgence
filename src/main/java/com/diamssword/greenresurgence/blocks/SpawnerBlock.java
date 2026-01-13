package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.blockEntities.SpawnerBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.CreativeMultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerBlock extends ModBlockEntity<SpawnerBlockEntity> {
	public SpawnerBlock(Settings settings) {
		super(settings);
		this.setTickerFactory((w, p) -> w.isClient ? null : SpawnerBlockEntity::serverTick);
	}

	@Override
	public Class<SpawnerBlockEntity> getBlockEntityClass() {
		return SpawnerBlockEntity.class;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient && player.isCreative()) {
			var te = SpawnerBlock.this.getBlockEntity(pos, world);
			Containers.createHandler(player, pos, (sync, inv, p1) -> new SpawnerBlock.ScreenHandler(sync, player, te.getContainer(), te.getCamoContainer()));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	public static class ScreenHandler extends CreativeMultiInvScreenHandler {

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player.getInventory(), inventories);
		}

		@Override
		public ScreenHandlerType<SpawnerBlock.ScreenHandler> type() {
			return Containers.SPAWNER_BLOCK;
		}

	}
}
