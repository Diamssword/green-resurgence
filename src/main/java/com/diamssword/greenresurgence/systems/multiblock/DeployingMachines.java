package com.diamssword.greenresurgence.systems.multiblock;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blockEntities.DeployableMachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DeployingMachines {
	private static final Map<String, DeployingMachineDefinition> machines = new HashMap<>();


	public static String register(DeployingMachineDefinition def) {
		machines.put(def.id, def);
		return def.id;
	}

	public static Optional<DeployingMachineDefinition> get(String id) {
		return Optional.ofNullable(machines.get(id));
	}

	public static Optional<DeployingMachineInstance> instantiate(String id, BlockPos pos, Direction dir) {
		return get(id).map(d -> new DeployingMachineInstance(d, dir, pos));
	}

	public static DeployableMachineBlockEntity placeMachine(World world, BlockPos pos, DeployingMachineInstance machineInstance) {
		world.setBlockState(pos, MBlocks.DEPLOYABLE_MACHINE_BLOCK.getDefaultState());
		var te = MBlocks.DEPLOYABLE_MACHINE_BLOCK.getBlockEntity(pos, world);
		te.setMachine(machineInstance);
		return te;
	}

	public static final String GENERATOR_T1 = DeployingMachines.register(new DeployingMachineDefinition("generator_t1") {
		@Override
		public void onBlockDeployed(World world, BlockPos pos, BlockState state, NbtCompound extrasData) {
			if(state.getBlock() == MBlocks.NANOTEK_GENERATOR_RELAY && !world.isClient) {
				try {
					if(extrasData.contains("faction"))
						MBlocks.NANOTEK_GENERATOR_RELAY.getBlockEntity(pos, world).setFaction(extrasData.getUuid("faction"));
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onMachineDeconstructed(World world, BlockPos worldPos, NbtCompound extraDatas) {
			if(!extraDatas.getBoolean("isUpgrading"))
				world.spawnEntity(new ItemEntity(world, worldPos.getX() + 0.5d, worldPos.getY() + 0.5d, worldPos.getZ() + 0.5d, MItems.CLAIM_PLACER.getDefaultStack().copy()));
		}
	}.add(0, MBlocks.NANOTEK_GENERATOR_RELAY, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), 0)
			.add(1, MBlocks.NANOTEK_GENERATOR_BIG_ANTENNA, new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), 20));
	public static final String GENERATOR_T3 = DeployingMachines.register(new DeployingMachineDefinition("generator_t3") {
		@Override
		public void onBlockDeployed(World world, BlockPos pos, BlockState state, NbtCompound extrasData) {
			if(state.getBlock() == MBlocks.NANOTEK_GENERATOR_RELAY && !world.isClient) {
				try {
					var te = MBlocks.NANOTEK_GENERATOR_RELAY.getBlockEntity(pos, world);
					if(extrasData.contains("faction"))
						te.setFaction(extrasData.getUuid("faction"));
					te.setLevel(extrasData.getInt("level"));
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onMachineDeconstructed(World world, BlockPos worldPos, NbtCompound extraDatas) {
			if(!extraDatas.getBoolean("isUpgrading")) {
				world.spawnEntity(new ItemEntity(world, worldPos.getX() + 0.5d, worldPos.getY() + 0.5d, worldPos.getZ() + 0.5d, new ItemStack(Items.DIAMOND, 2)));
				world.spawnEntity(new ItemEntity(world, worldPos.getX() + 0.5d, worldPos.getY() + 0.5d, worldPos.getZ() + 0.5d, MItems.CLAIM_PLACER.getDefaultStack().copy()));
			}
		}
	}.add(0, MBlocks.NANOTEK_GENERATOR_COMPUTER, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), 0)
			.add(1, MBlocks.NANOTEK_GENERATOR_CANISTER, new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), 25)
			.add(1, MBlocks.NANOTEK_GENERATOR_SERVER, new BlockPos(0, 0, 0), new BlockPos(-1, 0, 0), 30)
			.add(1, MBlocks.NANOTEK_GENERATOR_RELAY, new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), 35)
			.add(2, MBlocks.NANOTEK_GENERATOR_PILLAR, new BlockPos(0, 0, 0), new BlockPos(0, 2, 0), 60)
			.add(2, MBlocks.NANOTEK_GENERATOR_BIG_ANTENNA, new BlockPos(0, 1, 0), new BlockPos(0, 3, 0), 60));
	public static final String GENERATOR_T2 = DeployingMachines.register(new DeployingMachineDefinition("generator_t2") {
		@Override
		public void onBlockDeployed(World world, BlockPos pos, BlockState state, NbtCompound extrasData) {
			if(state.getBlock() == MBlocks.NANOTEK_GENERATOR_RELAY && !world.isClient) {
				try {
					var te = MBlocks.NANOTEK_GENERATOR_RELAY.getBlockEntity(pos, world);
					if(extrasData.contains("faction"))
						te.setFaction(extrasData.getUuid("faction"));
					te.setLevel(extrasData.getInt("level"));
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onMachineDeconstructed(World world, BlockPos worldPos, NbtCompound extraDatas) {
			if(!extraDatas.getBoolean("isUpgrading")) {
				world.spawnEntity(new ItemEntity(world, worldPos.getX() + 0.5d, worldPos.getY() + 0.5d, worldPos.getZ() + 0.5d, new ItemStack(Items.DIAMOND, 1)));
				world.spawnEntity(new ItemEntity(world, worldPos.getX() + 0.5d, worldPos.getY() + 0.5d, worldPos.getZ() + 0.5d, MItems.CLAIM_PLACER.getDefaultStack().copy()));
			}
		}
	}.add(0, MBlocks.NANOTEK_GENERATOR_RELAY, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), 0)
			.add(2, MBlocks.NANOTEK_GENERATOR_PILLAR, new BlockPos(0, -1, 0), new BlockPos(0, 1, 0), 40)
			.add(2, MBlocks.NANOTEK_GENERATOR_BIG_ANTENNA, new BlockPos(0, 0, 0), new BlockPos(0, 2, 0), 40));

}
