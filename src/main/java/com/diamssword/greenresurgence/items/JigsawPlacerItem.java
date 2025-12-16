package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.structure.JigsawHelper;
import com.diamssword.greenresurgence.structure.StructureInfos;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class JigsawPlacerItem extends Item implements IStructureProvider {
	private final Identifier structureName;

	public JigsawPlacerItem(Item.Settings properties, Identifier structurename) {
		super(properties);
		this.structureName = structurename;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if(stack.hasNbt()) {
			if(stack.getNbt().contains("pos")) {
				BlockPos p = BlockPos.fromLong(stack.getNbt().getLong("pos"));
				tooltip.add(Text.of(p.getX() + " " + p.getY() + " " + p.getZ()));
			}
			if(stack.getNbt().contains("dir")) {

				tooltip.add(Text.of(Direction.byId(stack.getNbt().getInt("dir")).toString()));
			}
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if(context.getWorld().isClient || context.getPlayer() == null) return ActionResult.PASS;
		if(structureName == null) return ActionResult.CONSUME;

		NbtCompound tag = context.getStack().getOrCreateNbt();
		if(tag.contains("pos")) {
			BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
			var dir = Direction.byId(tag.getInt("dir"));
			if(pos.equals(context.getBlockPos().offset(context.getSide()))) {
				boolean res = loadStructure((ServerWorld) context.getWorld(), structureName, pos, dir);
				tag.remove("pos");
				tag.remove("dir");
				if(!res) {
					context.getPlayer().sendMessage(Text.of("Impossible de charger " + this.structureName), true);
				}
				return res ? ActionResult.SUCCESS : ActionResult.FAIL;
			} else {
				tag.putLong("pos", context.getBlockPos().offset(context.getSide()).asLong());
				tag.putInt("dir", context.getHorizontalPlayerFacing().getId());
				return ActionResult.SUCCESS;
			}
		} else {
			tag.putLong("pos", context.getBlockPos().offset(context.getSide()).asLong());
			tag.putInt("dir", context.getHorizontalPlayerFacing().getId());
			return ActionResult.SUCCESS;
		}

	}

	public boolean loadStructure(ServerWorld serverLevel, Identifier structureName, BlockPos blockPos, Direction facing) {
		if(structureName != null) {
			try {
				return loadJigSaw(serverLevel, blockPos, facing);
			} catch(Exception e) {
				return false;
			}
		}
		return false;
	}

	public boolean loadJigSaw(ServerWorld world, BlockPos pos, Direction dir) {
		//BlockPos blockPos = pos.offset(dir);
		BlockPos blockPos = pos.offset(Direction.UP);
		Registry<StructurePool> registry = world.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
		RegistryKey<StructurePool> ent = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, this.structureName);
		RegistryEntry.Reference<StructurePool> registryEntry = registry.entryOf(ent);
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
		StructureAccessor structureAccessor = world.getStructureAccessor();
		Random random = world.getRandom();
		Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);
		Optional<Structure.StructurePosition> optional = JigsawHelper.generate(context, registryEntry, Optional.of(StructureInfos.PLACER_ENTRY), 7, blockPos, false, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(optional.isEmpty())
			optional = JigsawHelper.generate(context, registryEntry, Optional.empty(), 7, blockPos, true, Optional.empty(), 128, StructureInfos.getRotation(dir));
		if(optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = optional.get().generate();
			for(StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
				if(!(structurePiece instanceof PoolStructurePiece poolStructurePiece)) continue;
				poolStructurePiece.generate((StructureWorldAccess) world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, false);
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockPos getPosition(ItemStack stack, World w) {
		NbtCompound tag = stack.getOrCreateNbt();
		if(tag.contains("pos")) {
			return BlockPos.fromLong(tag.getLong("pos"));
		}
		return null;
	}

	@Override
	public Direction getDirection(ItemStack stack, World w) {
		NbtCompound tag = stack.getOrCreateNbt();
		if(tag.contains("dir")) {
			return Direction.byId(tag.getInt("dir"));
		}
		return Direction.NORTH;
	}

	@Override
	public Identifier getStructureName(ItemStack stack, World w) {
		return this.structureName;
	}

	@Override
	public IStructureProvider.StructureType strutctureType(ItemStack stack, World w) {
		return IStructureProvider.StructureType.jigsaw;
	}


}
