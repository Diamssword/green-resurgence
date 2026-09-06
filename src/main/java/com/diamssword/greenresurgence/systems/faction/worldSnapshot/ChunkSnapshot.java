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
	private final Map<BlockPos, NbtCompound> tiles = new HashMap<>();

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
		tiles.clear();
		var ls = tag.getList("blocks", NbtElement.COMPOUND_TYPE);
		ls.forEach(t -> {
			var c = (NbtCompound) t;
			var p = BlockPos.fromLong(c.getLong("pos"));
			blocks.put(p, NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), c.getCompound("state")));
			if(c.contains("tile")) {
				tiles.put(p, c.getCompound("tile"));
			}
		});
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		var ls = new NbtList();
		var nb = new NbtCompound();
		blocks.forEach((p, b) -> {
			nb.putLong("pos", p.asLong());
			nb.put("state", NbtHelper.fromBlockState(b));
			var tile = tiles.get(p);
			if(tile != null)
				nb.put("tile", tile);
			ls.add(nb);
		});
		tag.put("blocks", ls);
	}

	public void putBlockIfAbsent(BlockPos pos, BlockState state) {
		putBlockIfAbsent(pos, state, false);
	}

	public void putBlockIfAbsent(BlockPos pos, BlockState state, boolean destructive) {
		if(!blocks.containsKey(pos)) {
			blocks.put(pos, state);
			if(state.hasBlockEntity()) {
				var p = this.chunk.getBlockEntity(pos);
				tiles.put(pos, p.createNbt());
				if(destructive)
					p.readNbt(new NbtCompound());
			}
			chunk.setNeedsSaving(true);
		}
	}

	public Optional<NbtCompound> getTilesDataAt(BlockPos pos) {
		return Optional.ofNullable(tiles.get(pos));
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
		tiles.remove(pos);
		chunk.setNeedsSaving(true);
	}

	public void putBlock(BlockPos pos, BlockState state, boolean destructive) {
		blocks.put(pos, state);
		if(state.hasBlockEntity()) {
			var p = chunk.getBlockEntity(pos);
			tiles.put(pos, p.createNbt());
			if(destructive)
				p.readNbt(new NbtCompound());
		}
		chunk.setNeedsSaving(true);
	}

	public Chunk getChunk() {
		return chunk;
	}
}