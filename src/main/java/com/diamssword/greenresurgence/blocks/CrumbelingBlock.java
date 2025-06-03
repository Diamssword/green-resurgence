package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.CrumbelingBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrumbelingBlock extends ModBlockEntity<CrumbelingBlockEntity> {

	public CrumbelingBlock(Settings settings) {
		super(settings);
		this.setTickerFactory((w, s) -> w.isClient ? null : CrumbelingBlockEntity::tick);
	}

	@Override
	public Class<CrumbelingBlockEntity> getBlockEntityClass() {
		return CrumbelingBlockEntity.class;
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
		return 1f;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

		CrumbelingBlockEntity et = getBlockEntity(pos, world);
		if (et != null && et.getDisplayBlock() != null) {
			return et.getDisplayBlock().getOutlineShape(world, pos, context);
		}

		return VoxelShapes.fullCube();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.isCreative() && !world.isClient) {
			var item = player.getMainHandStack();
			if (item.getItem() instanceof BlockItem be) {
				var block = be.getBlock();
				if (block == this)
					return ActionResult.FAIL;
				var te = getBlockEntity(pos, world);
				if (te.getRealBlock().getBlock() == block) {
					var data = block.getStateManager().getProperties().stream().filter(v -> v.getValues().stream().findAny().get() instanceof Direction).findAny();
					if (data.isPresent()) {
						var curr = te.getRealBlock();
						BlockState blockState = cycle(curr, data.get(), false);
						te.setRealBlock(blockState);
					}
				} else {
					var ctx = new ItemPlacementContext(player, hand, item, hit);
					getBlockEntity(pos, world).setRealBlock(block.getPlacementState(ctx));
				}

				return ActionResult.CONSUME;
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}

	private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
		return state.with(property, cycle(property.getValues(), state.get(property), inverse));
	}

	private static <T> T cycle(Iterable<T> elements, @Nullable T current, boolean inverse) {
		return inverse ? Util.previous(elements, current) : Util.next(elements, current);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		CrumbelingBlockEntity et = getBlockEntity(pos, world);
		if (et != null && et.getDisplayBlock() != null) {
			return getBlockEntity(pos, world).getDisplayBlock().getCollisionShape(world, pos, context).offset(0, -0.01, 0);
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		CrumbelingBlockEntity et = getBlockEntity(pos, world);
		if (et != null && et.getDisplayBlock() != null) {
			return getBlockEntity(pos, world).getDisplayBlock().getSidesShape(world, pos);
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		CrumbelingBlockEntity et = getBlockEntity(pos, world);
		if (et != null) {
			return et.getRealBlock().getCameraCollisionShape(world, pos, context);
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		this.changeTilt(world, hit.getBlockPos());
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (isEntityAbove(state, world, pos, entity)) {
				this.changeTilt(world, pos);
			}
		}
	}

	private static boolean isEntityAbove(BlockState state, World world, BlockPos pos, Entity entity) {
		var shape = state.getCollisionShape(world, pos);
		return entity.isOnGround() && entity.getPos().y > shape.getMax(Direction.Axis.Y);
	}

	private void changeTilt(World world, BlockPos pos) {
		getBlockEntity(pos, world).triggerBlock();
	}

}
