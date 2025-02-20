package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.CraftingProvider;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrafterBlockEntity extends BlockEntity {

    private int progress=0;
    private SimpleRecipe current;
    private Optional<TerrainInstance> terrain=Optional.empty();
    private boolean canCraft=false;
    private CraftingProvider status;
    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(MBlockEntities.CRAFTER, pos, state);
    }
    public boolean craftRecipe(SimpleRecipe recipe, @Nullable PlayerEntity player)
    {
        return terrain.map(t->{
           status= new CraftingProvider().setForTerrain(terrain.get(),player);
           var st=status.getRecipeStatus(recipe,player);
           return st.canCraft;
        }).orElse(false);
    }
    public static void tick(World world, BlockPos pos, BlockState state, CrafterBlockEntity blockEntity) {
        if(world.getTime()%10==0)
        {
            var bl=world.getComponent(Components.BASE_LIST);
            blockEntity.terrain=bl.getTerrainAt(pos);

        }
        if(blockEntity.terrain.isPresent())
        {
            }
    }
    @Override
    public void writeNbt(NbtCompound nbt) {

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
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
