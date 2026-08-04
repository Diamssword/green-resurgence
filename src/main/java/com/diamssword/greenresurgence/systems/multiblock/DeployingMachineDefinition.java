package com.diamssword.greenresurgence.systems.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Local coordinate are defined in relation to the 'main' block (the position at wich the machine is placed) and based on direction North
 * So +x is left the main block,
 * +z  is behind the main block
 *
 */
public abstract class DeployingMachineDefinition {

	public final String id;
	private boolean forceSpace = false;
	private int maxStep = 0;
	private Map<Integer, List<AnimatedBlockStateFactory>> steps = new HashMap<>();

	public DeployingMachineDefinition(String id) {
		this.id = id;
	}

	public abstract void onBlockDeployed(World world, BlockPos pos, BlockState state, NbtCompound extraDatas);

	public DeployingMachineDefinition add(int index, BlockState state, BlockPos from, BlockPos to, int animationTime) {
		BiFunction<DeployingMachineInstance, Direction, BlockState> fac = (a, b) -> state;
		for(Property<?> p : state.getProperties()) {
			if(p.getType() == Direction.class) {
				fac = (a, b) -> state.with((Property<Direction>) p, b);
				break;
			} else if(p.getType() == Direction.Axis.class) {

				fac = (a, b) -> state.with((Property<Direction.Axis>) p, b.getAxis());
				break;
			}
		}

		return add(index, fac, from, to, animationTime);
	}

	public DeployingMachineDefinition add(int index, Block block, BlockPos from, BlockPos to, int animationTime) {

		return add(index, block.getDefaultState(), from, to, animationTime);
	}

	public int getMaxStep() {
		return maxStep;
	}

	public Map<Integer, List<AnimatedBlockStateFactory>> getSteps() {
		return new HashMap<>(steps);
	}

	public DeployingMachineDefinition add(int index, BiFunction<DeployingMachineInstance, Direction, BlockState> blockStateGetter, BlockPos from, BlockPos to, int animationTime) {
		var ls = steps.computeIfAbsent(index, a -> new ArrayList<>());
		ls.add(new AnimatedBlockStateFactory(blockStateGetter, new BlockAnimation(from, to, animationTime)));
		if(index > maxStep) {
			maxStep = index;
		}
		return this;
	}

	public boolean isForceSpace() {
		return forceSpace;
	}

	public void setForceSpace(boolean forceSpace) {
		this.forceSpace = forceSpace;
	}

	public abstract void onMachineDeconstructed(World world, BlockPos worldPos, NbtCompound extraDatas);

	public record BlockAnimation(BlockPos from, BlockPos to, int tickLength) {
	}

	public record AnimatedBlockStateFactory(BiFunction<DeployingMachineInstance, Direction, BlockState> blockStateGetter, BlockAnimation animation) {}

}
