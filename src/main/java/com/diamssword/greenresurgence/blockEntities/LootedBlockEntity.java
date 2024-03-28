package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.systems.LootableLogic;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class LootedBlockEntity extends BlockEntity {
    public static final int MAX=5;
    public int durability=MAX;
    public int cooldown=200;
    public BlockState block;
    public LootedBlockEntity( BlockPos pos, BlockState state) {
        super(MBlocks.LOOTED_BE, pos, state);
    }
    public BlockState getRealBlock()
    {
        return this.block !=null?this.block: Blocks.AIR.getDefaultState();
    }
    public void setRealBlock(BlockState state)
    {
        this.block=state;
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }
    public void setCooldown(int cooldown)
    {
        this.cooldown=cooldown;
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }
    public void restoreDurability()
    {
        this.cooldown=200;
        if(this.durability<MAX)
        {
            this.durability++;
            this.markDirty();
            this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
        }
        else
        {
            this.world.setBlockState(pos,this.getRealBlock());
        }


    }
    public void attackBlock(ServerPlayerEntity player)
    {

        if(this.durability>0)
        {
            LootableLogic.giveLoot(player,pos,getRealBlock());
            getWorld().playSound(null,this.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS,1f,1f+(float)Math.random());
            getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(getRealBlock()));
            this.durability--;
            this.cooldown=200;
            this.markDirty();
            this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
        }
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        nbt.putInt("durability", durability);
        nbt.putInt("cooldown", cooldown);
        if(block ==null)
            block=Blocks.AIR.getDefaultState();
        nbt.put("block",NbtHelper.fromBlockState(block));
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        durability = nbt.getInt("durability");
        cooldown = nbt.getInt("cooldown");
        block=NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(),nbt.getCompound("block"));
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, LootedBlockEntity t) {
        if(world.getTime()%20==0)
        {
            if(t.cooldown==0)
            {
                t.restoreDurability();
            }
            else
                t.setCooldown(t.cooldown-1);

        }
    }
}
