package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.systems.lootables.IAdvancedLootableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LootableShelfEntity extends BlockEntity implements IAdvancedLootableBlock {
	private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
	private int selectedIndex = 0;
	private int selectedCount = 0;
	private boolean isOff = false;
	private long lastBreak = 0;

	public LootableShelfEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.remove("item");
		nbt.putInt("selected", selectedIndex);
		nbt.putInt("selectedCount", selectedCount);
		nbt.putBoolean("isOff", isOff);
		Inventories.writeNbt(nbt, items);
	}

	@Override
	public boolean canBeInteracted() {
		return !isOff && !getItem().isEmpty();
	}

	public void lootBlock(PlayerEntity pl) {
		if (!isOff) {
			var st = getItem().copy();
			if (selectedCount == 0)
				selectedCount = (int) (1 + (Math.random() * st.getCount()));
			st.setCount(selectedCount);
			this.isOff = true;
			this.lastBreak = System.currentTimeMillis();

			if (!pl.giveItemStack(st))
				pl.dropStack(st);
			getWorld().playSound(null, this.pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.3f, 0.5f + (float) Math.random());
			this.saveAndUpdate();
		}

	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	protected void saveAndUpdate() {
		this.markDirty();
		if (this.world instanceof ServerWorld sw)
			sw.getChunkManager().markForUpdate(pos);
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, LootableShelfEntity t) {
		if (world.getTime() % 100 == 0 && t.isOff && !world.isClient) {
			if (System.currentTimeMillis() > t.lastBreak + GreenResurgence.CONFIG.serverOptions.cooldowns.respawnShelvesLootInSec() * 1000L) {

				t.isOff = false;
				for (int i = 0; i < 100; i++) {
					t.selectedIndex = (int) (Math.random() * t.items.size());
					t.selectedCount = (int) (1 + (Math.random() * t.items.get(t.selectedIndex).getCount()));
					if (!t.getItem().isEmpty())
						break;
				}
				t.saveAndUpdate();
			}

		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		Inventories.readNbt(nbt, items);
		selectedIndex = Math.min(nbt.getInt("selected"), items.size());
		selectedCount = nbt.getInt("selectedCount");
		isOff = nbt.getBoolean("isOff");
		if (selectedIndex != 0 && getItem().isEmpty())
			selectedIndex = 0;
	}

	public ItemStack getItem() {
		if (this.isOff)
			return ItemStack.EMPTY;
		return this.items.get(selectedIndex).copyWithCount(selectedCount);
	}

	public GridContainer getContainer() {
		SimpleInventory inv = new SimpleInventory(this.items.toArray(new ItemStack[0]));
		inv.addListener((c) -> {
			for (int i = 0; i < c.size(); i++) {
				this.items.set(i, c.getStack(i));
				this.selectedCount = (int) (1 + (Math.random() * this.items.get(this.selectedIndex).getCount()));
			}
			this.saveAndUpdate();
		});
		return new GridContainer("container", inv, 9, 1);
	}
}