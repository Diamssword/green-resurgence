package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LootedBlock extends ModBlockEntity<LootedBlockEntity> {

    public LootedBlock(Settings settings) {
        super(settings);
        this.setTickerFactory((w,s)->w.isClient?null:LootedBlockEntity::tick);
    }

    @Override
    public Class<LootedBlockEntity> getBlockEntityClass() {
        return LootedBlockEntity.class;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.INVISIBLE;
    }
    /*
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient?null: LootedBlock.checkType(type, MBlockEntities.LOOTED_BLOCk, LootedBlockEntity::tick);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LootedBlockEntity(pos,state);
    }
    public LootedBlockEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (LootedBlockEntity) world.getBlockEntity(pos);
    }*/
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1f;
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        LootedBlockEntity et=getBlockEntity(pos,world);
        if(et!=null && et.getDisplayBlock() !=null)
        {
            return et.getDisplayBlock().getOutlineShape(world,pos,context);
        }

        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        LootedBlockEntity et=getBlockEntity(pos,world);
        if(et!=null && et.getDisplayBlock() !=null)
        {
            return getBlockEntity(pos,world).getDisplayBlock().getCollisionShape(world,pos,context);
        }
        return VoxelShapes.fullCube();
    }
    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        LootedBlockEntity et=getBlockEntity(pos,world);
        if(et!=null && et.getDisplayBlock() !=null)
        {
            return getBlockEntity(pos,world).getDisplayBlock().getSidesShape(world,pos);
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        LootedBlockEntity et=getBlockEntity(pos,world);
        if(et!=null)
        {
            if(et.durability<=0)
                return VoxelShapes.empty();
            return getBlockEntity(pos,world).getRealBlock().getCameraCollisionShape(world,pos,context);
        }
        return VoxelShapes.fullCube();
    }
    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
