package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.blocks.DeployableLadderBlock;
import com.diamssword.greenresurgence.systems.CableNetwork;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployableLadderEntity extends BlockEntity {
    public static final long DEPLOYABLE_EXPIRE =30000; //604_800_000;
    private BlockState originalState;
    private long placedAt=0;
    private boolean shouldRemove = false;

    public DeployableLadderEntity(BlockPos pos, BlockState state) {
        super(MBlockEntities.DEPLOYABLE_LADDER, pos, state);
        placedAt=System.currentTimeMillis();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
       if(originalState!=null)
       {
           nbt.put("original",NbtHelper.fromBlockState(originalState));
       }
        nbt.putLong("placedAt", placedAt);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if(nbt.contains("original"))
            this.originalState=NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(),nbt.getCompound("original"));
        if(nbt.contains("placedAt"))
            placedAt=nbt.getLong("placedAt");

    }
    public BlockState getOriginalState() {
        return originalState;
    }

    public void setOriginalState(BlockState originalState) {
        this.originalState = originalState;
        this.markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, DeployableLadderEntity deployableLadderEntity) {
        if(!world.isClient && world.getTime()%4==0)
        {
                var p1=pos.down();
                var state1=world.getBlockState(p1);
                var state2=world.getBlockState(pos.up());
                if(!state.get(DeployableLadderBlock.MASTER) && state2.getBlock() != MBlocks.DEPLOYABLE_LADDER)
                {
                    if(!deployableLadderEntity.shouldRemove)
                        deployableLadderEntity.shouldRemove=true;
                    else {
                        var st = deployableLadderEntity.originalState;
                        if (st == null)
                            st = Blocks.AIR.getDefaultState();
                        world.setBlockState(pos, st);
                        BlockSoundGroup blockSoundGroup = MBlocks.DEPLOYABLE_LADDER.getDefaultState().getSoundGroup();
                        world.playSound(null, pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 4.0F, blockSoundGroup.getPitch() * 0.8F);
                        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(MBlocks.DEPLOYABLE_LADDER.getDefaultState()));
                    }
                }
                else if(state1.getBlock() != MBlocks.DEPLOYABLE_LADDER && !state1.isFullCube(world,p1) && !world.isWater(p1) &&p1.getY()>-63)
                {
                    world.setBlockState(p1, MBlocks.DEPLOYABLE_LADDER.getDefaultState().with(DeployableLadderBlock.FACING,state.get(DeployableLadderBlock.FACING)).with(DeployableLadderBlock.MASTER,false));
                    MBlocks.DEPLOYABLE_LADDER.getBlockEntity(p1,world).setOriginalState(state1);
                    BlockSoundGroup blockSoundGroup = MBlocks.DEPLOYABLE_LADDER.getDefaultState().getSoundGroup();
                    world.playSound(null, pos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 4.0F, blockSoundGroup.getPitch() * 0.8F);
                    world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(MBlocks.DEPLOYABLE_LADDER.getDefaultState()));
                }
            if(state.get(DeployableLadderBlock.MASTER) && System.currentTimeMillis() > deployableLadderEntity.placedAt+ DeployableLadderEntity.DEPLOYABLE_EXPIRE )
            {
                world.setBlockState(pos,state.with(DeployableLadderBlock.MASTER,false));
            }
        }
    }
}
