package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.DeployableMachineBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class DeployableMachineBlock extends ModBlockEntity<DeployableMachineBlockEntity> {

	public DeployableMachineBlock(Settings settings) {
		super(settings);
		this.setTickerFactory((w, s) -> w.isClient ? DeployableMachineBlockEntity::tickClient : DeployableMachineBlockEntity::tick);
	}

	@Override
	public Class<DeployableMachineBlockEntity> getBlockEntityClass() {
		return DeployableMachineBlockEntity.class;
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 0f;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

		DeployableMachineBlockEntity et = getBlockEntity(pos, world);
		if(et != null && et.getDisplayBlock() != null) {
			return et.getDisplayBlock().getOutlineShape(world, pos, context);
		}

		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		DeployableMachineBlockEntity et = getBlockEntity(pos, world);
		if(et != null && et.getDisplayBlock() != null) {
			return getBlockEntity(pos, world).getDisplayBlock().getCollisionShape(world, pos, context).offset(0, -0.01, 0);
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		DeployableMachineBlockEntity et = getBlockEntity(pos, world);
		if(et != null && et.getDisplayBlock() != null) {
			return getBlockEntity(pos, world).getDisplayBlock().getSidesShape(world, pos);
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		DeployableMachineBlockEntity et = getBlockEntity(pos, world);
		if(et != null) {
			return et.getDisplayBlock().getCameraCollisionShape(world, pos, context);
		}
		return VoxelShapes.fullCube();
	}

}
