package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OmniBlock extends Block implements IChairable {
	public static final EnumProperty<Direction> TYPE = Properties.FACING;
	private final GenericBlockSet.Transparency transparency;
	private final boolean noHitbox;
	private final VoxelShape[] boxes;
	private final float damage;
	private final boolean isChair;
	private final float chairLvl;

	public OmniBlock(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, Direction.NORTH));
		this.transparency = props.transparency;
		this.noHitbox = !props.solid;
		this.boxes = GenericPillar.CompileRotatedVoxels(props.hitbox.shape, false, props.hitbox.needRotate, props.hitbox.horizontal);
		this.damage = props.damage;
		this.isChair = props.isSeat;
		this.chairLvl = props.seatLevel;
	}

	public OmniBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, Direction.NORTH));
		this.transparency = GenericBlockSet.Transparency.UNDEFINED;
		this.noHitbox = false;
		this.boxes = GenericPillar.CompileRotatedVoxels(GenericBlockSet.HitBox.FULL.shape, false, false, false);
		this.damage = 0;
		this.isChair = false;
		this.chairLvl = 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TYPE);
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState().with(TYPE, ctx.getPlayerLookDirection());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(TYPE, rotation.rotate(state.get(TYPE)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(TYPE)));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noHitbox)
			return VoxelShapes.empty();
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return boxes[state.get(TYPE).getId()];
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

	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && canUse()) {
			this.sit(player, pos);
			return ActionResult.SUCCESS;
		} else
			return ActionResult.PASS;
	}

	@Override
	public float sittingHeight() {
		return chairLvl;
	}

	@Override
	public boolean canUse() {
		return isChair;
	}
}

