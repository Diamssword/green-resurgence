package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Optional;

public class GeneratorBlockEntity extends BlockEntity {

    private int burntime=0;
    public final int rfGen;
    private Optional<TerrainInstance> terrain=Optional.empty();
    public GeneratorBlockEntity(BlockPos pos, BlockState state,int rfPerTick) {
        super(MBlockEntities.GENERATOR, pos, state);
        rfGen=rfPerTick;
    }
    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(MBlockEntities.GENERATOR, pos, state);
        rfGen=64;
    }
    public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
        if(world.getTime()%10==0)
        {
            var bl=world.getComponent(Components.BASE_LIST);
            blockEntity.terrain=bl.getTerrainAt(pos);

        }
        if(blockEntity.terrain.isPresent())
        {

            if(blockEntity.burntime<=0) {
                var inv=InventoryStorage.of(blockEntity.terrain.get().storage,null);
                try (Transaction t1 = Transaction.openOuter()) {
                    var ext=inv.extract(ItemVariant.of(Items.COAL),1,t1);
                    if(ext>0) {
                        blockEntity.burntime=200;
                        t1.commit();
                        blockEntity.markDirty();
                    }
                }

            }
            if(blockEntity.burntime>0) {
                blockEntity.burntime--;
                try (Transaction t1 = Transaction.openOuter()) {
                    blockEntity.terrain.get().energyStorage.insert(blockEntity.rfGen, t1);
                    t1.commit();
                }
                blockEntity.markDirty();
            }
        }
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("fuel",burntime);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        burntime =nbt.getInt("fuel");
        super.readNbt(nbt);
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
}
