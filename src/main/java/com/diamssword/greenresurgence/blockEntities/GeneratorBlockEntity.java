package com.diamssword.greenresurgence.blockEntities;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends AreaAwareBlockEntity {

	private int burntime = 0;
	public int rfGen;

	public GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int rfPerTick) {
		super(type, pos, state);
		rfGen = rfPerTick;
	}

	public GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		this(type, pos, state, 64);
	}

	public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
		blockEntity.checkForArea(false);
		if(blockEntity.burntime <= 0) {
			blockEntity.getArea().ifPresent(ar -> {
				var inv = InventoryStorage.of(ar.getStorage(), null);
				try(Transaction t1 = Transaction.openOuter()) {
					var ext = inv.extract(ItemVariant.of(Items.COAL), 1, t1);
					if(ext > 0) {
						blockEntity.burntime = 200;
						blockEntity.updateGrid();
						t1.commit();
						blockEntity.markDirty();
					}
				}
			});


		}
		if(blockEntity.burntime > 0) {
			blockEntity.burntime--;
			blockEntity.markDirty();
		} else
			blockEntity.updateGrid();

	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("fuel", burntime);
		nbt.putInt("gen", rfGen);
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		burntime = nbt.getInt("fuel");
		if(nbt.contains("gen"))
			rfGen = nbt.getInt("gen");
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

	@Override
	public int getIO() {
		return burntime > 0 ? rfGen : 0;
	}

	@Override
	public long getCapacity() {
		return rfGen * 2L;
	}
}
