package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.DeployableLadderEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

public abstract class ModBlockEntity<T extends BlockEntity> extends BlockWithEntity {
    private BiFunction<World,BlockState,BlockEntityTicker<T>> tickerFactory;
    protected BlockEntityType<T> type;
    public ModBlockEntity(Settings settings) {
        super(settings);
    }


    protected T createBlockEntity(BlockEntityType<T> type,BlockPos pos,BlockState state ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return getBlockEntityClass().getConstructor(BlockEntityType.class,BlockPos.class,BlockState.class).newInstance(type,pos,state);
    }
    public abstract Class<T> getBlockEntityClass();
    public void registerFromExternalType(BlockEntityType<?> type)
    {
        this.type= (BlockEntityType<T>) type;
    }
    public BlockEntityType<T> registerEntityType(Block... blocks)
    {
        type= FabricBlockEntityTypeBuilder.create((p,s)-> {
            try {
                return createBlockEntity(type,p,s);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, blocks).build();
        return type;
    }
    public BlockEntityType<T> getEntityType(){

        return type;
    };
    public Identifier getCustomBlockEntityName()
    {
        return null;
    }
    protected void setTickerFactory(BiFunction<World,BlockState,BlockEntityTicker<T>> tickerFactory )
    {
        this.tickerFactory=tickerFactory;
    }
    @Override
    @Nullable
    public <T1 extends BlockEntity> BlockEntityTicker<T1> getTicker(World world, BlockState state, BlockEntityType<T1> type) {
        if(tickerFactory !=null) {
            var ticker = tickerFactory.apply(world, state);
            if(ticker !=null)
                return checkType(type, ticker);
        }
        return null;
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        try {
            return createBlockEntity(type,pos,state);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
      //  return getEntityType().instantiate(pos,state);
    }
    public T getBlockEntity(BlockPos pos, BlockView world)
    {
        return (T) world.getBlockEntity(pos);
    }
    @Nullable
    public <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<E> expectedType, BlockEntityTicker<? super T> ticker) {
        return expectedType == getEntityType() ? (BlockEntityTicker<A>) ticker : null;
    }
}
