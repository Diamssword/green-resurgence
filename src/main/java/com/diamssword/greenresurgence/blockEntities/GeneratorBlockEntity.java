package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity {

	private int burntime = 0;
	public final int rfGen;
	private FactionZone terrain;

	public GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int rfPerTick) {
		super(type, pos, state);
		rfGen = rfPerTick;
	}

	public GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		this(type, pos, state, 64);
	}

	public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
		if (world.getTime() % 10 == 0) {
			var bl = world.getComponent(Components.BASE_LIST);
			blockEntity.terrain = bl.getTerrainAt(pos).orElseGet(() -> null);

		}
		if (blockEntity.terrain != null) {

			if (blockEntity.burntime <= 0) {
				var inv = InventoryStorage.of(blockEntity.terrain.getOwner().storage, null);
				try (Transaction t1 = Transaction.openOuter()) {
					var ext = inv.extract(ItemVariant.of(Items.COAL), 1, t1);
					if (ext > 0) {
						blockEntity.burntime = 200;
						t1.commit();
						blockEntity.markDirty();
					}
				}

			}
			if (blockEntity.burntime > 0) {
				blockEntity.burntime--;
				try (Transaction t1 = Transaction.openOuter()) {
					blockEntity.terrain.getOwner().energyStorage.insert(blockEntity.rfGen, t1);
					t1.commit();
				}
				blockEntity.markDirty();
			}
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("fuel", burntime);
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		burntime = nbt.getInt("fuel");
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
