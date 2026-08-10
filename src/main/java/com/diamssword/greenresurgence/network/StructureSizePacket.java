package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.helpers.IStructureProvider;
import com.diamssword.greenresurgence.structure.JigsawHelper;
import com.diamssword.greenresurgence.structure.StructureInfos;
import com.diamssword.greenresurgence.utils.CompatibilityWarper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.diamssword.greenresurgence.structure.StructureInfos.PLACER_ENTRY;

public class StructureSizePacket {
	public static Map<Identifier, Map<Direction, StructureResponse>> cache = new HashMap<>();

	public record StructureRequest(Identifier name, Direction dir, IStructureProvider.StructureType type) {}


	public record StructureResponse(Identifier name, BlockPos offset, Vec3i size, Direction dir, Map<BlockPos, BlockState> blocks) {}

	;

	public static void init() {
		Channels.MAIN.registerClientbound(StructureResponse.class, (msg, ctx) -> {
			StructureInfos.setStructureInfos(msg);

		});
		Channels.MAIN.registerServerbound(StructureRequest.class, (msg, ctx) -> {

			if(ctx.player().interactionManager.getGameMode() == GameMode.CREATIVE) {
				if(msg.type == IStructureProvider.StructureType.jigsaw) {
					StructureResponse rep = null;
					var map = cache.get(msg.name);
					if(map != null) {
						var m = map.get(msg.dir);
						if(m != null) {
							rep = m;
						}
					}
					if(rep == null) {
						rep = loadJigSaw((ServerWorld) ctx.player().getWorld(), ctx.player().getBlockPos(), msg.dir, msg.name);
						var m1 = cache.computeIfAbsent(msg.name, (v) -> new HashMap<>());
						m1.put(msg.dir, rep);
					}
					if(rep != null) {
						Channels.MAIN.serverHandle(ctx.player()).send(rep);
					}
				} else {
					StructureTemplateManager structureManager = ((ServerWorld) ctx.player().getWorld()).getStructureTemplateManager();

					Optional<StructureTemplate> structure2 = structureManager.getTemplate(msg.name);
					if(structure2.isPresent()) {
						StructureTemplate temp = structure2.get();
						BlockPos p = BlockPos.ORIGIN;
						int[] off = StructureInfos.getOffsetSide(msg.dir, msg.type == IStructureProvider.StructureType.centered);
						p = p.add(off[0] * (temp.getSize().getX() / 2), 0, off[1] * (temp.getSize().getZ() / 2));
						Channels.MAIN.serverHandle(ctx.player()).send(new StructureResponse(msg.name, p, temp.getSize(), msg.dir, new HashMap<>()));

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
		StructureAccessor structureAccessor = world.getStructureAccessor();
		StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
		Random random = world.getRandom();
		Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);

		Optional<Structure.StructurePosition> optional = JigsawHelper.generate(context, registryEntry, Optional.of(PLACER_ENTRY), 32, blockPos, false, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(!optional.isPresent())
			optional = JigsawHelper.generate(context, registryEntry, Optional.empty(), 32, blockPos, true, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(optional.isPresent()) {

			StructurePiecesCollector structurePiecesCollector = optional.get().generate();
			BlockPos off = null;
			BlockBox box = null;
			Optional<Pair<HashMap<BlockPos, BlockState>, ServerWorld>> fakeWorld = Optional.empty();
			if(GreenResurgence.CONFIG.serverOptions.enableAdvancedStructurePreview())
				fakeWorld = CompatibilityWarper.getCreateSimulatedWorld.apply(world);
			for(StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
				if((structurePiece instanceof PoolStructurePiece poolStructurePiece)) {
					BlockBox box1 = poolStructurePiece.getBoundingBox();
					try {
						fakeWorld.ifPresent(hashMapServerWorldPair -> poolStructurePiece.generate(hashMapServerWorldPair.getRight(), structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, false));
					} catch(Exception e) {
						e.printStackTrace();
					}

					var list = poolStructurePiece.getPoolElement().getStructureBlockInfos(structureTemplateManager, pos, StructureInfos.getRotation(dir), random);
					if(box == null || (box1.getBlockCountY() + box1.getBlockCountZ() + box1.getBlockCountX() > box.getBlockCountY() + box.getBlockCountZ() + box.getBlockCountX())) {
						box = box1;
						Optional<StructureTemplate.StructureBlockInfo> start = list.stream().filter(i -> i.nbt().getString("name").equals(PLACER_ENTRY.toString())).findFirst();

						off = pos;
						if(start.isPresent()) {
							off = start.get().pos();
						}
					}

				}
			}
			var map = new HashMap<BlockPos, BlockState>();
			fakeWorld.ifPresent(l -> {
				l.getLeft().forEach((a, b) -> {
					map.put(a.subtract(pos), b);
				});
			});

			return new StructureResponse(structureName, off.subtract(pos), box.getDimensions(), dir, keepBoundingBoxBorder(map, box.offset(-pos.getX(), -pos.getY(), -pos.getZ()), 3));
		}
		return null;
	}

	public static Map<BlockPos, BlockState> removeHiddenBlocks(Map<BlockPos, BlockState> blocks) {
		Map<BlockPos, BlockState> result = new HashMap<>();

		for(Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
			BlockPos pos = entry.getKey();

			boolean exposed = false;

			for(Direction direction : Direction.values()) {
				if(!blocks.containsKey(pos.offset(direction))) {
					exposed = true;
					break;
				}
			}

			if(exposed) {
				result.put(pos, entry.getValue());
			}
		}

		return result;
	}

	public static Map<BlockPos, BlockState> keepBoundingBoxBorder(
			Map<BlockPos, BlockState> blocks,
			BlockBox box,
			int thickness) {

		Map<BlockPos, BlockState> result = new HashMap<>();

		int minX = box.getMinX();
		int minY = box.getMinY();
		int minZ = box.getMinZ();

		int maxX = box.getMaxX();
		int maxY = box.getMaxY();
		int maxZ = box.getMaxZ();

		for(var entry : blocks.entrySet()) {
			BlockPos pos = entry.getKey();

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			boolean keep = false;

			boolean keepZ = z - minZ < thickness || maxZ - z < thickness;
			boolean keepY = y - minY < thickness || maxY - y < thickness;
			if(x == minX || x == maxX) {
				keep |= keepY;
				keep |= keepZ;
			}

			// Y faces
			boolean keepX = x - minX < thickness || maxX - x < thickness;
			if(y < minY + thickness || y == maxY) {
				keep |= keepX;
				keep |= keepZ;
			}

			// Z faces
			if(z == minZ || z == maxZ) {
				keep |= keepX;
				keep |= keepY;
			}

			if(keep) {
				result.put(pos, entry.getValue());
			}
		}
		return removeHiddenBlocks(result);
	}

	public static void serializer(PacketByteBuf write, BlockState val) {
		write.writeNbt(NbtHelper.fromBlockState(val));

	}

	public static BlockState unserializer(PacketByteBuf read) {
		var nb = read.readNbt();
		if(nb != null)
			return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nb);
		return Blocks.AIR.getDefaultState();
	}
}
