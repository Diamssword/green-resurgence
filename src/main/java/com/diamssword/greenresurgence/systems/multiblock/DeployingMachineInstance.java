package com.diamssword.greenresurgence.systems.multiblock;

import com.diamssword.greenresurgence.utils.TriConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeployingMachineInstance {

	public final boolean forceSpace;
	public final int maxStep;
	public final Direction direction;
	private boolean deconstructing = false;
	public final BlockPos worldPos;
	private int step = 0;
	private int tickTime;
	private int goalTime;
	private final Map<Integer, List<AnimatedBlockState>> steps = new HashMap<>();
	private final DeployingMachineDefinition parent;
	private BlockState mainBlock;
	private NbtCompound extraDatas = new NbtCompound();
	private TriConsumer<World, BlockPos, NbtCompound> deconstructedListener;

	public DeployingMachineInstance(DeployingMachineDefinition def, Direction dir, BlockPos worldPos) {
		maxStep = def.getMaxStep();
		forceSpace = def.isForceSpace();
		direction = dir;
		this.worldPos = worldPos;
		parent = def;
		def.getSteps().forEach((k, v) -> {
			var l = steps.computeIfAbsent(k, a -> new ArrayList<>());

			v.forEach(v1 -> {
				var st = v1.blockStateGetter().apply(this, dir);
				if(v1.animation().to().equals(BlockPos.ORIGIN))
					mainBlock = st;
				l.add(new AnimatedBlockState(st, v1.animation()));
			});

		});
		if(mainBlock == null)
			mainBlock = Blocks.AIR.getDefaultState();
	}

	public void setDeconstructedListener(TriConsumer<World, BlockPos, NbtCompound> deconstructedListener) {
		this.deconstructedListener = deconstructedListener;
	}

	public boolean isDeconstructing() {
		return deconstructing;
	}

	public void setDeconstructing(boolean deconstructing) {
		this.deconstructing = deconstructing;
		if(deconstructing)
			this.step = maxStep + 1;
		else
			this.step = 0;
	}

	public DeployingMachineDefinition getParent() {
		return parent;
	}


	public BlockState getMainBlock() {
		return mainBlock;
	}

	public boolean canPlace(World world) {
		if(forceSpace)
			return true;
		for(List<AnimatedBlockState> value : steps.values()) {
			for(AnimatedBlockState an : value) {
				if(!isBlockReplacable(world, rotatedCoordinate(an.animation.to()).add(worldPos)))
					return false;
			}
		}
		return true;
	}

	private boolean isBlockReplacable(WorldAccess world, BlockPos pos) {
		return world.getBlockState(pos).isReplaceable();
	}

	public BlockPos rotatedCoordinate(BlockPos baseCoord) {
		return switch(direction) {
			case DOWN, UP, NORTH -> baseCoord;
			case EAST -> baseCoord.rotate(BlockRotation.CLOCKWISE_90);
			case WEST -> baseCoord.rotate(BlockRotation.COUNTERCLOCKWISE_90);
			case SOUTH -> baseCoord.rotate(BlockRotation.CLOCKWISE_180);
		};
	}

	public boolean isFinished() {
		if(isDeconstructing())
			return step < 0;
		return step > maxStep;
	}

	public List<AnimatedBlockState> getCurrentAnimated() {
		return steps.get(Math.max(0, step));

	}

	public int getCurrentTick() {
		return tickTime;
	}

	private void deconstructTick(World world) {
		if(goalTime <= 0) {
			step--;
			tickTime = 0;
			var ls = steps.get(step);
			if(ls != null) {
				for(AnimatedBlockState l : ls) {
					var t = l.animation.tickLength();
					if(t > goalTime)
						goalTime = t;
				}
			}
		} else {
			tickTime++;
			var ls = steps.get(step);
			if(ls != null) {
				for(AnimatedBlockState l : ls) {
					if(!l.animation.to().equals(BlockPos.ORIGIN)) {
						if(tickTime == 1)
							world.setBlockState(rotatedCoordinate(l.animation.to()).add(worldPos), Blocks.AIR.getDefaultState());
						else if(tickTime == l.animation.tickLength()) {
							var p = rotatedCoordinate(l.animation.to()).add(worldPos);
							world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.2f, 0.8f + world.random.nextFloat() * 0.6f);
						}
					}
				}
			}
			if(tickTime == goalTime)
				goalTime = 0;
		}
	}

	public void tick(World world) {
		if(isDeconstructing()) {
			deconstructTick(world);
			return;
		}
		if(goalTime <= 0) {
			step++;
			tickTime = 0;
			var ls = steps.get(step);
			if(ls != null) {
				for(AnimatedBlockState l : ls) {
					var t = l.animation.tickLength();
					if(t > goalTime)
						goalTime = t;
				}
			}
		} else {
			tickTime++;
			var ls = steps.get(step);
			if(ls != null) {
				for(AnimatedBlockState l : ls) {
					if(l.animation.tickLength() == tickTime && !l.animation.to().equals(BlockPos.ORIGIN)) {
						placeBlock(world, l);
					}
				}
			}
			if(tickTime == goalTime)
				goalTime = 0;
		}
	}

	public void placeBlock(World world, AnimatedBlockState block) {

		var p = rotatedCoordinate(block.animation.to()).add(worldPos);
		if(world.getBlockState(p).getBlock() != block.blockState.getBlock()) {
			world.breakBlock(p, true);
		}
		world.setBlockState(p, block.blockState);
		parent.onBlockDeployed(world, p, block.blockState, extraDatas);
		world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.2f, 0.8f + world.random.nextFloat() * 0.6f);
	}

	public NbtCompound getExtraDatas() {
		return extraDatas;
	}

	public void setExtraDatas(NbtCompound extraDatas) {
		this.extraDatas = extraDatas;
	}

	public void complete(World world, boolean upgradingNext) {
		if(isDeconstructing()) {
			if(upgradingNext) {
				this.getExtraDatas().putBoolean("isUpgrading", true);
			} else {
				world.setBlockState(worldPos, Blocks.AIR.getDefaultState());
			}
			parent.onMachineDeconstructed(world, worldPos, extraDatas);
			if(deconstructedListener != null)
				deconstructedListener.accept(world, worldPos, extraDatas);
		} else {
			world.setBlockState(worldPos, getMainBlock());
			parent.onBlockDeployed(world, worldPos, getMainBlock(), extraDatas);
		}
	}

	public record AnimatedBlockState(BlockState blockState, DeployingMachineDefinition.BlockAnimation animation) {}

	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("step", step);
		nbt.putInt("direction", direction.getId());
		NbtCompound stepsN = new NbtCompound();
		this.steps.forEach((k, v) -> {
			var lsN = new NbtList();
			v.forEach(v1 -> {
				var t1 = new NbtCompound();
				lsN.add(t1);
				t1.put("state", NbtHelper.fromBlockState(v1.blockState));
				t1.putLong("from", v1.animation.from().asLong());
				t1.putLong("to", v1.animation.to().asLong());
				t1.putInt("ticks", v1.animation.tickLength());
			});
			stepsN.put(k + "", lsN);
		});
		nbt.put("steps", stepsN);
	}


	private int stepsFromNBT(NbtCompound nbt) {
		var max = 0;
		steps.clear();
		for(String key : nbt.getKeys()) {
			var ls = nbt.getList(key, NbtElement.COMPOUND_TYPE);
			var lsR = new ArrayList<AnimatedBlockState>();
			ls.forEach(t -> {
				NbtCompound tag = (NbtCompound) t;
				lsR.add(new AnimatedBlockState(NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("state")),
						new DeployingMachineDefinition.BlockAnimation(BlockPos.fromLong(tag.getLong("from")), BlockPos.fromLong(tag.getLong("to")), nbt.getInt("ticks"))));
			});
			var k1 = Integer.parseInt(key);
			if(k1 > max)
				max = k1;
			steps.put(k1, lsR);
		}
		return max;
	}
}
