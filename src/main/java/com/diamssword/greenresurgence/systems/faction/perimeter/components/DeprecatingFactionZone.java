package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.worldSnapshot.ChunkSnapshot;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeprecatingFactionZone extends FactionZone {
	private Map<ChunkPos, ChunkSnapshot> chunkSnaps1 = new HashMap<>();
	private List<ChunkPos> chunksPos = new ArrayList<>();
	public List<BlockPos> posToProcess = new ArrayList<>();
	public List<BlockPos> processedPos = new ArrayList<>();
	private ChunkSnapshot currentChunk;

	public DeprecatingFactionZone(FactionZone zone) {
		super(zone.getOwner(), zone.getBounds());
		calculateChunksPos();
	}

	public DeprecatingFactionZone(FactionGuild owner, NbtCompound tag) {
		super(owner, tag);
		for(long processed : tag.getLongArray("processed")) {
			processedPos.add(BlockPos.fromLong(processed));
		}
		calculateChunksPos();
	}

	public boolean tick(ServerWorld world) {
		if(currentChunk == null) {
			if(chunksPos.isEmpty()) {
				return true;
			}
			var pos = chunksPos.get(world.random.nextInt(chunksPos.size()));
			if(!world.isChunkLoaded(pos.x, pos.z))
				return false;
			chunksPos.remove(pos);
			currentChunk = ChunkSnapshot.getSnapshotFor(world, pos);
			posToProcess = new ArrayList<>(currentChunk.getAllBlocks().keySet().stream().toList());
			posToProcess.removeAll(processedPos);
		}
		if(posToProcess.isEmpty()) {
			currentChunk = null;
			return false;
		}

		var l = posToProcess.get(world.random.nextInt(posToProcess.size()));
		if(!world.isChunkLoaded(currentChunk.getChunk().getPos().x, currentChunk.getChunk().getPos().z))
			return false;

		var guilds = world.getComponent(Components.BASE_LIST);

		if(getBounds().contains(l) && guilds.getTerrainAt(l).isEmpty()) {
			posToProcess.remove(l);
			processedPos.add(l);
			currentChunk.getBlockAt(l).ifPresent(state -> {
				if(state.isAir()) {
					world.setBlockState(l, Blocks.SLIME_BLOCK.getDefaultState());
					world.updateNeighbors(l, Blocks.SLIME_BLOCK.getDefaultState().getBlock());
				} else {
					world.setBlockState(l, state);
					world.updateNeighbors(l, state.getBlock());
				}
				world.playSound(null, l.getX(), l.getY(), l.getZ(), SoundEvents.BLOCK_NYLIUM_BREAK, SoundCategory.BLOCKS, 0.5f, 0.8f + world.random.nextFloat() * 0.4f);
			});
		}
		if(posToProcess.isEmpty())
			currentChunk = null;
		return false;
	}


	public void calculateChunksPos() {
		chunksPos.clear();
		int minChunkX = getBounds().getMinX() >> 4;
		int maxChunkX = getBounds().getMaxX() >> 4;
		int minChunkZ = getBounds().getMinZ() >> 4;
		int maxChunkZ = getBounds().getMaxZ() >> 4;

		for(int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			for(int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				chunksPos.add(new ChunkPos(chunkX, chunkZ));
			}
		}
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.putLongArray("processed", this.processedPos.stream().map(BlockPos::asLong).toList());

	}
}
