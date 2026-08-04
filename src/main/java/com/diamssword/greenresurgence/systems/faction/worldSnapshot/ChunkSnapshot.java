package com.diamssword.greenresurgence.systems.faction.worldSnapshot;

import com.diamssword.greenresurgence.systems.Components;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChunkSnapshot implements Component {
	private final Chunk chunk;
	private final Map<BlockPos, BlockState> blocks = new HashMap<>();

	public ChunkSnapshot(Chunk chunk) {this.chunk = chunk;}

	public static ChunkSnapshot getSnapshotFor(World world, BlockPos pos) {
		return Components.CHUNK_SNAPSHOT.get(world.getChunk(pos));
	}

	public static ChunkSnapshot getSnapshotFor(World world, ChunkPos pos) {
		return Components.CHUNK_SNAPSHOT.get(world.getChunk(pos.x, pos.z));
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		blocks.clear();
		var ls = tag.getList("blocks", NbtElement.COMPOUND_TYPE);
		ls.forEach(t -> {
			var c = (NbtCompound) t;
			blocks.put(BlockPos.fromLong(c.getLong("pos")), NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), c.getCompound("state")));
		});
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		var ls = new NbtList();
		var nb = new NbtCompound();
		blocks.forEach((p, b) -> {
			nb.putLong("pos", p.asLong());
			nb.put("state", NbtHelper.fromBlockState(b));
			ls.add(nb);
		});
		tag.put("blocks", ls);
	}

	public void putBlockIfAbsent(BlockPos pos, BlockState state) {
		if(!blocks.containsKey(pos)) {
			blocks.put(pos, state);
			chunk.setNeedsSaving(true);
		}
	}

	public Optional<BlockState> getBlockAt(BlockPos pos) {
		return Optional.ofNullable(blocks.get(pos));
	}

	public Map<BlockPos, BlockState> getAllBlocks() {
		return new HashMap<>(blocks);
	}

	public BlockState getBlockAtOrAir(BlockPos pos) {
		return getBlockAt(pos).orElse(Blocks.AIR.getDefaultState());
	}

	public void removeBlock(BlockPos pos) {
		blocks.remove(pos);
		chunk.setNeedsSaving(true);
	}

	public void putBlock(BlockPos pos, BlockState state) {
		blocks.put(pos, state);
		chunk.setNeedsSaving(true);
	}

	public Chunk getChunk() {
		return chunk;
	}
}