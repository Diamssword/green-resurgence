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
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
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

public class UniversalPlacerItem extends Item implements IStructureProvider{
    public UniversalPlacerItem(Settings properties) {
        super(properties);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt() )
        {
            if(stack.getNbt().contains("pos"))
            {
                BlockPos p=BlockPos.fromLong(stack.getNbt().getLong("pos"));
                tooltip.add(Text.of(p.getX()+" "+p.getY()+" "+p.getZ()));
            }
            if(stack.getNbt().contains("dir"))
            {

                tooltip.add(Text.of(Direction.byId(stack.getNbt().getInt("dir")).toString()));
            }
        }
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient||context.getPlayer()==null) return ActionResult.PASS;
        BlockPos pos1=context.getBlockPos().add(0,1,0);

        NbtCompound tag=context.getStack().getOrCreateNbt();
        if(tag.contains("pos"))
        {
            BlockPos pos= BlockPos.fromLong(tag.getLong("pos"));
            if(pos.equals(context.getBlockPos())) {
                boolean res = loadStructure((ServerWorld) context.getWorld(), this.getStructureName(context.getStack(),context.getWorld()), pos1, context.getHorizontalPlayerFacing(),context.getStack());
                tag.remove("pos");
                tag.remove("dir");
                if(!res)
                {
                    context.getPlayer().sendMessage(Text.of("Impossible de charger "+ this.getStructureName(context.getStack(),context.getWorld())),true);
                }
                return res ? ActionResult.SUCCESS : ActionResult.FAIL;
            }
            else {
                tag.putLong("pos", context.getBlockPos().asLong());
                tag.putInt("dir",context.getHorizontalPlayerFacing().getId());
                return ActionResult.SUCCESS;
            }
        }
        else
        {
            tag.putLong("pos",context.getBlockPos().asLong());
            tag.putInt("dir",context.getHorizontalPlayerFacing().getId());
            return ActionResult.SUCCESS;
        }

    }
    public boolean loadStructure(ServerWorld serverLevel,Identifier structureName,BlockPos blockPos,Direction facing,ItemStack st) {
        if (structureName != null) {
         try {
             StructureType type = strutctureType(st, serverLevel);
             if(type==StructureType.jigsaw)
                return loadJigSaw(serverLevel, blockPos, facing,structureName);
             else
             {
                 StructureTemplateManager structureManager = serverLevel.getStructureTemplateManager();
                 Optional<StructureTemplate> structure2;
                 structure2 = structureManager.getTemplate(structureName);
                 return structure2.filter(structureTemplate -> this.place(serverLevel, structureTemplate, blockPos, facing, false,type==StructureType.centered)).isPresent();
             }
         }catch (Exception e) {
             return false;
         }
        }
        return false;
    }
    public boolean loadJigSaw(ServerWorld world, BlockPos pos, Direction dir,Identifier structure)
    {
        //BlockPos blockPos = pos.offset(dir);
        BlockPos blockPos = pos.offset(Direction.UP);
        Registry<StructurePool> registry = world.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        RegistryKey<StructurePool> ent=RegistryKey.of(RegistryKeys.TEMPLATE_POOL, structure);
        RegistryEntry.Reference<StructurePool> registryEntry = registry.entryOf(ent);
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        Random random = world.getRandom();
        Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);
        Optional<Structure.StructurePosition> optional = JigsawHelper.generate(context, registryEntry, Optional.of(StructureInfos.PLACER_ENTRY), 7, blockPos, false, Optional.empty(), 128,StructureInfos.getRotation(dir));
        if(optional.isEmpty())
            optional = JigsawHelper.generate(context, registryEntry, Optional.empty(), 7, blockPos, true, Optional.empty(), 128,StructureInfos.getRotation(dir));
        if (optional.isPresent()) {
            StructurePiecesCollector structurePiecesCollector = optional.get().generate();
            for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
                if (!(structurePiece instanceof PoolStructurePiece poolStructurePiece)) continue;
                poolStructurePiece.generate(world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, false);
            }
            return true;
        }
        return false;
    }
    public boolean place(ServerWorld serverLevel, StructureTemplate structure, BlockPos blockPos,Direction facing,boolean mirror,boolean centered) {
        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(BlockMirror.values()[mirror?1:0]).setRotation(StructureInfos.getRotation(facing));
        int[] off= StructureInfos.getOffsetSide(facing,centered);
        BlockPos p1=blockPos.add(off[0]*(structure.getSize().getX()/2),0,off[1]*(structure.getSize().getZ()/2));
        structure.place(serverLevel,p1, p1, structurePlacementData, serverLevel.getRandom(),2);
        return true;

    }
    @Override
    public BlockPos getPosition(ItemStack stack,World w) {
        NbtCompound tag=stack.getOrCreateNbt();
        if(tag.contains("pos"))
        {
            return  BlockPos.fromLong(tag.getLong("pos"));
        }
        return null;
    }

    @Override
    public Direction getDirection(ItemStack stack, World w) {
        NbtCompound tag=stack.getOrCreateNbt();
        if(tag.contains("dir"))
        {
            return Direction.byId(tag.getInt("dir"));
        }
        return Direction.NORTH;
    }
    @Override
    public Text getName(ItemStack st) {
        Identifier id=this.getStructureName(st,null);
        return Text.translatable(this.getTranslationKey()).append(Text.of("("+id.toString()+")"));
    }
    @Override
    public Identifier getStructureName(ItemStack stack, World w) {
        NbtCompound tag=stack.getOrCreateNbt();
        if(tag.contains("structure"))
        {
            return Optional.ofNullable(Identifier.tryParse(tag.getString("structure"))).orElse(new Identifier("empty"));
        }
        return new Identifier("empty");
    }

    @Override
    public StructureType strutctureType(ItemStack stack, World w) {
        NbtCompound tag=stack.getOrCreateNbt();
        if(tag.contains("type"))
        {
            return StructureType.getById(tag.getInt("type"));
        }
        return StructureType.normal;
    }


}
