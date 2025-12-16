package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.items.IStructureProvider;
import com.diamssword.greenresurgence.structure.JigsawHelper;
import com.diamssword.greenresurgence.structure.StructureInfos;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;

import static com.diamssword.greenresurgence.structure.StructureInfos.PLACER_ENTRY;

public class StructureSizePacket {
	public record StructureRequest(Identifier name, Direction dir, IStructureProvider.StructureType type) {}

	;

	public record StructureResponse(Identifier name, BlockPos offset, Vec3i size, Direction dir) {}

	;

	public static void init() {
		Channels.MAIN.registerClientbound(StructureResponse.class, (msg, ctx) -> {
			StructureInfos.setStructureInfos(msg);

		});
		Channels.MAIN.registerServerbound(StructureRequest.class, (msg, ctx) -> {

			if(ctx.player().interactionManager.getGameMode() == GameMode.CREATIVE) {
				if(msg.type == IStructureProvider.StructureType.jigsaw) {
					StructureResponse structureResponse = loadJigSaw((ServerWorld) ctx.player().getWorld(), ctx.player().getBlockPos(), msg.dir, msg.name);
					if(structureResponse != null) {

						Channels.MAIN.serverHandle(ctx.player()).send(structureResponse);
					}
				} else {
					StructureTemplateManager structureManager = ((ServerWorld) ctx.player().getWorld()).getStructureTemplateManager();

					Optional<StructureTemplate> structure2 = structureManager.getTemplate(msg.name);
					if(structure2.isPresent()) {
						StructureTemplate temp = structure2.get();
						BlockPos p = BlockPos.ORIGIN;
						int[] off = StructureInfos.getOffsetSide(msg.dir, msg.type == IStructureProvider.StructureType.centered);
						p = p.add(off[0] * (temp.getSize().getX() / 2), 0, off[1] * (temp.getSize().getZ() / 2));
						Channels.MAIN.serverHandle(ctx.player()).send(new StructureResponse(msg.name, p, temp.getSize(), msg.dir));

					}
				}
			}
		});
	}

	public static StructureResponse loadJigSaw(ServerWorld world, BlockPos pos, Direction dir, Identifier structureName) {

		BlockPos blockPos = pos.offset(Direction.UP);
		Registry<StructurePool> registry = world.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
		RegistryKey<StructurePool> ent = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, structureName);
		RegistryEntry.Reference<StructurePool> registryEntry = registry.entryOf(ent);
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
		StructureAccessor structureAccessor = world.getStructureAccessor();
		Random random = world.getRandom();
		Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);
		Optional<Structure.StructurePosition> optional = JigsawHelper.generate(context, registryEntry, Optional.of(PLACER_ENTRY), 7, blockPos, false, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(!optional.isPresent())
			optional = JigsawHelper.generate(context, registryEntry, Optional.empty(), 7, blockPos, true, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = optional.get().generate();
			for(StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
				if((structurePiece instanceof PoolStructurePiece poolStructurePiece)) {
					BlockBox box = poolStructurePiece.getBoundingBox();
					Optional<StructureTemplate.StructureBlockInfo> start = poolStructurePiece.getPoolElement().getStructureBlockInfos(structureTemplateManager, pos, StructureInfos.getRotation(dir), random).stream().filter(i -> i.nbt().getString("name").equals(PLACER_ENTRY.toString())).findFirst();
					BlockPos off = pos;
					if(start.isPresent()) {
						off = start.get().pos();
					}
					return new StructureResponse(structureName, off.subtract(pos), box.getDimensions(), dir);
				}
			}
		}
		return null;
	}
}
