package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.blocks.SideShelfBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GenericConnectedPillar extends GlazedTerracottaBlock implements IChairable {
	public static final EnumProperty<SideShelfBlock.Model> MODEL = SideShelfBlock.MODEL;
	private final GenericBlockSet.Transparency transparency;
	private final VoxelShape[] boxes;
	private final boolean noHitbox;
	private final float damage;
	private final boolean isChair;
	private final float chairLvl;

	public GenericConnectedPillar(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		boxes = GenericPillar.CompileRotatedVoxels(props.hitbox.shape, true, props.hitbox.needRotate, props.hitbox.horizontal);
		this.transparency = props.transparency;
		this.noHitbox = !props.solid;
		this.damage = props.damage;
		this.isChair = props.isSeat;
		this.chairLvl = props.seatLevel;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(MODEL, getModelFor(ctx.getBlockPos(), ctx.getWorld(), ctx.getHorizontalPlayerFacing().getOpposite()));
	}

	private SideShelfBlock.Model getModelFor(BlockPos pos, WorldAccess world, Direction facing) {
		var axis = facing.getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
		var left = pos;
		var right = pos;
		if (facing == Direction.SOUTH) {
			left = pos.east();
			right = pos.west();
		} else if (facing == Direction.NORTH) {
			left = pos.west();
			right = pos.east();
		} else if (facing == Direction.EAST) {
			left = pos.north();
			right = pos.south();
		} else {
			left = pos.south();
			right = pos.north();
		}

		var st1 = world.getBlockState(left);
		var bleft = st1.getBlock() == this && st1.get(FACING) == facing;
		st1 = world.getBlockState(right);
		var bright = st1.getBlock() == this && st1.get(FACING) == facing;
		return bleft && bright ? SideShelfBlock.Model.CENTER : (bleft ? SideShelfBlock.Model.LEFT : (bright ? SideShelfBlock.Model.RIGHT : SideShelfBlock.Model.SINGLE));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (direction.getHorizontal() > -1) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(MODEL, getModelFor(pos, world, state.get(FACING)));
		}
		return state;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MODEL);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noHitbox)
			return VoxelShapes.empty();
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return boxes[state.get(FACING).getId()];
	}

	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		if (transparency == GenericBlockSet.Transparency.UNDEFINED || transparency == GenericBlockSet.Transparency.OPAQUE)
			return super.getAmbientOcclusionLightLevel(state, world, pos);
		return 1.0F;
	}

	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (damage > 0)
			entity.damage(world.getDamageSources().cactus(), damage);
	}

	@Override
	public float sittingHeight() {
		return chairLvl;
	}

	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && canUse()) {
			this.sit(player, pos);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public boolean canUse() {
		return isChair;
	}

}
