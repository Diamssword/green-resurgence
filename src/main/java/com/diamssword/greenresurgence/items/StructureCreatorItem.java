package com.diamssword.greenresurgence.items;



import com.diamssword.greenresurgence.structure.JigsawHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;


public class StructureCreatorItem extends Item {
    public final static Identifier PLACER_ENTRY=new Identifier("build:placer_entry");
    private final Identifier structureName;
    private final boolean centered;
    private final boolean isJigsaw;
    public StructureCreatorItem(Settings properties, Identifier structurename, boolean centered,boolean isJigsaw) {
        super(properties);
        this.isJigsaw=isJigsaw;
        this.centered=centered;
        this.structureName= structurename;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        if (context.getWorld().isClient||context.getPlayer()==null) return ActionResult.PASS;
        BlockPos pos1=context.getBlockPos().add(0,1,0);
            if (structureName==null) return ActionResult.CONSUME;

            boolean res=loadStructure((ServerWorld) context.getWorld(),structureName,pos1,context.getHorizontalPlayerFacing(),context.getPlayer().isSneaking());
            return res?ActionResult.SUCCESS:ActionResult.FAIL;
    }
    public boolean loadStructure(ServerWorld serverLevel,Identifier structureName,BlockPos blockPos,Direction facing,boolean mirror) {
        if (structureName != null) {
            if(isJigsaw)
            {
                return loadJigSaw(serverLevel,blockPos,facing,mirror);
            }
            StructureTemplateManager structureManager = serverLevel.getStructureTemplateManager();
            Optional<StructureTemplate> structure2;
            try {
                structure2 = structureManager.getTemplate(structureName);
                return this.place(serverLevel, structure2.get(),blockPos,facing,mirror);
            } catch (InvalidIdentifierException var6) {
                return false;
            }
        } else { return false;}
    }
    public boolean loadJigSaw(ServerWorld world, BlockPos pos, Direction dir,boolean mirror)
    {
        //BlockPos blockPos = pos.offset(dir);
        BlockPos blockPos = pos.offset(Direction.UP);
        Registry<StructurePool> registry = world.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        RegistryKey<StructurePool> ent=RegistryKey.of(RegistryKeys.TEMPLATE_POOL, this.structureName);
        RegistryEntry.Reference<StructurePool> registryEntry = registry.entryOf(ent);
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        Random random = world.getRandom();
        Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);
        Optional<Structure.StructurePosition> optional = JigsawHelper.generate(context, registryEntry, Optional.of(PLACER_ENTRY), 7, blockPos, false, Optional.empty(), 128,getRotation(dir));
        if(!optional.isPresent())
            optional = JigsawHelper.generate(context, registryEntry, Optional.empty(), 7, blockPos, true, Optional.empty(), 128,getRotation(dir));
        if (optional.isPresent()) {
            StructurePiecesCollector structurePiecesCollector = optional.get().generate();
            for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
                if (!(structurePiece instanceof PoolStructurePiece poolStructurePiece)) continue;
                poolStructurePiece.generate((StructureWorldAccess)world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, false);
            }
            return true;
        }
        return false;
    }
    public boolean place(ServerWorld serverLevel, StructureTemplate structure, BlockPos blockPos,Direction facing,boolean mirror) {
        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(BlockMirror.values()[mirror?1:0]).setRotation(getRotation(facing));
        int[] off=getOffsetSide(facing);
        BlockPos p1=blockPos.add(off[0]*(structure.getSize().getX()/2),0,off[1]*(structure.getSize().getZ()/2));
        structure.place(serverLevel,p1, p1, structurePlacementData, serverLevel.getRandom(),2);
        return true;

    }
    public BlockRotation getRotation(Direction facing)
    {
        switch (facing)
        {
            case DOWN, NORTH, UP -> {
                return BlockRotation.CLOCKWISE_180;
            }
            case SOUTH -> {
                return BlockRotation.NONE;
            }
            case WEST -> {
                return BlockRotation.CLOCKWISE_90;
            }
            case EAST -> {
                return BlockRotation.COUNTERCLOCKWISE_90;
            }
        }
        return BlockRotation.NONE;
    }
    public int[] getOffsetSide(Direction facing)
    {
        switch (facing)
        {
            case DOWN, NORTH, UP -> {
                return new int[]{1,centered?1:0};
            }
            case SOUTH -> {
                return new int[]{-1,centered?-1:0};
            }
            case WEST -> {
                return new int[]{centered?1:0,-1};
            }
            case EAST -> {
                return new int[]{centered?-1:0,1};
            }
        }
        return new int[]{-1,-1};
    }
}