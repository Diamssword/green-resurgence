package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class OmniBlockFull extends OmniBlock {
	public static final EnumProperty<Direction> ORIENTATION = Properties.HORIZONTAL_FACING;
	private final GenericBlockSet.Transparency transparency;
	private final boolean noHitbox;
	private final VoxelShape[] boxes;
	private final float damage;
	private final boolean isChair;
	private final float chairLvl;

	public OmniBlockFull(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, Direction.NORTH).with(ORIENTATION, Direction.NORTH));
		this.transparency = props.transparency;
		this.noHitbox = !props.solid;
		this.boxes = GenericPillar.CompileRotatedVoxels(props.hitbox.shape, false, props.hitbox.needRotate, props.hitbox.horizontal);
		this.damage = props.damage;
		this.isChair = props.isSeat;
		this.chairLvl = props.seatLevel;
	}

	public OmniBlockFull(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(TYPE, Direction.NORTH).with(ORIENTATION, Direction.NORTH));
		this.transparency = GenericBlockSet.Transparency.UNDEFINED;
		this.noHitbox = false;
		this.boxes = GenericPillar.CompileRotatedVoxels(GenericBlockSet.HitBox.FULL.shape, false, false, false);
		this.damage = 0;
		this.isChair = false;
		this.chairLvl = 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TYPE, ORIENTATION);
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(TYPE, ctx.getSide().getOpposite()).with(ORIENTATION, ctx.getPlayerLookDirection());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(TYPE, rotation.rotate(state.get(TYPE))).with(ORIENTATION, rotation.rotate(state.get(ORIENTATION)));
	}

}

