package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.systems.CableNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectorBlockEntity extends BlockEntity {
	public List<BlockPos> connections = new ArrayList<>();
	public BlockPos basePos;
	public Direction baseDir;

	public ConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}


	public void addConnection(BlockPos from) {
		this.connections.add(from);
		markUpdate();
	}

	private void markUpdate() {
		this.markDirty();
		if (this.world instanceof ServerWorld sw)
			sw.getChunkManager().markForUpdate(pos);
	}

	public void clearConnection(BlockPos from) {
		this.connections.remove(from);
		markUpdate();
	}

	public void clearConnections() {
		this.connections.clear();
		markUpdate();
	}

	public void markRemoved() {
		super.markRemoved();
	}

	public void onBreak() {
		if (this.world != null && !this.world.isClient)
			this.connections.forEach(c -> {
				BlockState st = this.world.getBlockState(c);
				if (st.getBlock() instanceof ConnectorBlock) {
					ConnectorBlockEntity te = ((ConnectorBlock) st.getBlock()).getBlockEntity(c, this.world);
					if (te != null && !te.isRemoved())
						te.clearConnection(this.pos);
				}
			});
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putLongArray("connections", this.connections.stream().map(BlockPos::asLong).toList());
		if (this.basePos == null)
			this.basePos = this.pos.add(0, 0, 0);
		if (this.baseDir == null) {
			if (this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING)) {
				this.baseDir = this.getCachedState().get(Properties.HORIZONTAL_FACING);
			}

		}
		nbt.putLong("base", this.basePos.asLong());
		if (this.baseDir != null)
			nbt.putInt("baseDir", this.baseDir.getId());
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("base"))
			this.basePos = BlockPos.fromLong(nbt.getLong("base"));
		else {
			this.basePos = this.pos.add(0, 0, 0);
			this.markDirty();
		}
		if (nbt.contains("baseDir"))
			this.baseDir = Direction.byId(nbt.getInt("baseDir"));
		else if (this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING)) {
			this.baseDir = this.getCachedState().get(Properties.HORIZONTAL_FACING);
			this.markDirty();
		}

		this.connections = new ArrayList<>(Arrays.stream(nbt.getLongArray("connections")).mapToObj(BlockPos::fromLong).toList());
		if (this.world != null)
			calculateNewConnections();
		if (world != null) {
			if (world.isClient)
				loadClientCables();
			else
				markUpdate();
		}

	}

	public void loadClientCables() {
		this.connections.forEach(c -> {

			if (CableNetwork.clientCables.stream().noneMatch(p -> (p.getLeft().equals(this.pos) && p.getRight().equals(c)) || (p.getLeft().equals(c) && p.getRight().equals(this.pos))))
				CableNetwork.clientCables.add(new Pair<>(this.pos, c));
		});
	}

	public void unloadClientCables() {
		this.connections.forEach(c -> {
			CableNetwork.clientCables.removeAll(CableNetwork.clientCables.stream().filter(p -> (p.getLeft().equals(this.pos) || p.getRight().equals(this.pos))).toList());

		});
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

	private void calculateNewConnections() {
		if (!this.pos.equals(this.basePos)) {
			BlockPos off = this.pos.subtract(this.basePos);
			this.connections = new ArrayList<>(this.connections.stream().map(v -> this.pos.add(rotateConnections(v.add(off)))).toList());
			this.basePos = pos.add(0, 0, 0);

			if (this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING)) {
				this.baseDir = this.getCachedState().get(Properties.HORIZONTAL_FACING);
			}
			markUpdate();
		}
	}

	private BlockPos rotateConnections(BlockPos pos) {
		BlockPos v = pos.subtract(this.pos);
		if (this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING)) {
			Direction newDir = this.getCachedState().get(Properties.HORIZONTAL_FACING);
			if (this.baseDir.getOpposite() == newDir) {
				return new BlockPos(-v.getX(), v.getY(), -v.getZ());
			} else if (this.baseDir.rotateYClockwise() == newDir) {
				return new BlockPos(-v.getZ(), v.getY(), v.getX());
			} else if (this.baseDir.rotateYCounterclockwise() == newDir) {
				return new BlockPos(v.getZ(), v.getY(), -v.getX());
			}
		}
		return v;
	}
}
