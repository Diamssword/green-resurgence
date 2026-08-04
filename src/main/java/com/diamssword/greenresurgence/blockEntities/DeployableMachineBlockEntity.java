package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.systems.multiblock.DeployingMachineInstance;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachines;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeployableMachineBlockEntity extends BlockEntity {
	private DeployingMachineInstance machine;
	private DeployingMachineInstance nextMachine;


	public DeployableMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public DeployingMachineInstance getMachine() {
		return machine;
	}

	public void setMachine(DeployingMachineInstance machine) {
		this.machine = machine;
		markChange();
	}

	/**
	 * if 'machine' is a deconstructing instance, this machine will be placed next
	 */
	public void setNextMachine(DeployingMachineInstance machine) {
		this.nextMachine = machine;
		markChange();
	}

	public BlockState getDisplayBlock() {
		return this.machine != null ? this.machine.getMainBlock() : Blocks.AIR.getDefaultState();
	}


	private void markChange() {
		this.markDirty();
		this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
	}


	@Override
	public void writeNbt(NbtCompound nbt) {
		if(machine != null) {
			nbt.putString("machine", machine.getParent().id);
			nbt.putInt("direction", machine.direction.getId());
			nbt.put("machineExtra", machine.getExtraDatas());
			nbt.putBoolean("deconstructing", machine.isDeconstructing());
		}
		if(nextMachine != null) {

			nbt.putString("nextMachine", nextMachine.getParent().id);
			nbt.put("newMachineExtra", nextMachine.getExtraDatas());
		}
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if(nbt.contains("machine") && nbt.contains("direction")) {
			DeployingMachines.instantiate(nbt.getString("machine"), pos, Direction.byId(nbt.getInt("direction"))).ifPresent(m -> {
				this.machine = m;
				this.machine.setExtraDatas(nbt.getCompound("machineExtra"));
				this.machine.setDeconstructing(nbt.getBoolean("deconstructing"));
			});
		}
		if(nbt.contains("nextMachine") && nbt.contains("direction")) {
			DeployingMachines.instantiate(nbt.getString("nextMachine"), pos, Direction.byId(nbt.getInt("direction"))).ifPresent(m -> {
				this.nextMachine = m;
				this.nextMachine.setExtraDatas(nbt.getCompound("machineExtra"));
			});
		}
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, DeployableMachineBlockEntity t) {
		if(t.machine != null) {
			t.machine.tick(world);
			if(t.machine.isFinished()) {
				t.machine.complete(world, t.machine.isDeconstructing() && t.nextMachine != null);
				if(t.nextMachine != null) {
					t.setMachine(t.nextMachine);
					t.setNextMachine(null);
				}
			}
		}
	}

	public static <T extends BlockEntity> void tickClient(World world, BlockPos pos, BlockState blockState, DeployableMachineBlockEntity t) {
		if(t.machine != null)
			t.machine.tick(world);

	}

}
