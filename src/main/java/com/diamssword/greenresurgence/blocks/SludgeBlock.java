package com.diamssword.greenresurgence.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SludgeBlock extends Block {
	public static final IntProperty LEVEL = Properties.LEVEL_1_8;
	public static final BooleanProperty BOTTOM = Properties.BOTTOM;
	public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);

	public SludgeBlock(Settings settings) {
		super(settings);

		this.setDefaultState((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(LEVEL, 8).with(BOTTOM, false));
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx);
	}

	public boolean shouldBeBottom(WorldAccess world, BlockPos pos, BlockState state) {
		if(!state.isAir() && state.isReplaceable()) {
			return true;
		}
		if(Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP)) {
			return false;
		}
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL, BOTTOM);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return true;
	}


	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		if(state.getBlock() == stateFrom.getBlock())
			return state.get(LEVEL) <= stateFrom.get(LEVEL);
		return super.isSideInvisible(state, stateFrom, direction);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean b = false;
		var level = state.get(LEVEL);
		var isBot = state.get(BOTTOM);
		if(level > 1) {
			for(Direction direction : Direction.Type.HORIZONTAL) {
				var accept = getClosestHeightBlock(world, isBot ? pos.down() : pos, direction);
				if(accept != null) {
					if(accept.getRight().getBlock() == this) {
						var l = accept.getRight().get(LEVEL);
						if(l < level - 1) {
							world.setBlockState(accept.getLeft(), accept.getRight().with(LEVEL, Math.max(level - 2, 1)).with(BOTTOM, shouldBeBottom(world, accept.getLeft().down(), world.getBlockState(accept.getLeft().down()))));
						}
					} else {
						var bt = shouldBeBottom(world, accept.getLeft().down(), accept.getRight());
						if(world.getBlockState(accept.getLeft()).isAir() && (bt || accept.getLeft().getY() <= pos.getY()))
							world.setBlockState(accept.getLeft(), this.getDefaultState().with(LEVEL, Math.max(level - 2, 1)).with(BOTTOM, bt));
					}
					b = true;
				}
			}
		}
		if(b || random.nextFloat() > 0.9f) {
			if(level - 1 < 1)
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			else
				world.setBlockState(pos, state.with(LEVEL, level - 1).with(BOTTOM, shouldBeBottom(world, pos.down(), world.getBlockState(pos.down()))));
			if(!world.isClient)
				world.playSound(null, pos, SoundEvents.ENTITY_STRIDER_STEP_LAVA, SoundCategory.BLOCKS);
		}
		world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5f - (isBot ? 1 : 0), pos.getZ() + 0.5f, 10, 0, 0, 0, 1);
	}

	private Pair<BlockPos, BlockState> getClosestHeightBlock(ServerWorld world, BlockPos origin, Direction direction) {
		var f = origin.offset(direction);
		for(int i = 0; i < 32; i++) {
			var v = f.add(0, -i, 0);
			var st = world.getBlockState(v);
			if(st.getBlock() == this) {
				return new Pair<>(v, st);
			} else if(!st.isAir()) {
				return new Pair<>(v.up(), st);
			}
		}


		return null;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		var lev = state.get(LEVEL);
		var b = state.get(BOTTOM);
		var l = pos.getY() + ((float) lev / 16f) + 0.2;
		if(b)
			l -= 0.5;
		if(entity.getY() <= l) {
			var d = entity.fallDistance;
			entity.slowMovement(state, new Vec3d(0.8, 0.5f, 0.8));
			if(lev < 7)
				entity.fallDistance = d;
		}
	}


	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
}
