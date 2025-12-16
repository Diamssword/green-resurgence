package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.ExtractOnlyGrid;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class LootedBlockEntity extends BlockEntity {
	public static final int MAX = 5;
	public int durability = MAX;
	public long lastBreak = 0;
	public SimpleInventory inventory;
	public BlockState block;
	public BlockState emptyBlock;

	public LootedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public BlockState getRealBlock() {
		return this.block != null ? this.block : Blocks.AIR.getDefaultState();
	}

	public BlockState getDisplayBlock() {
		if(emptyBlock == null && this.block != null) {emptyBlock = LootableLogic.copyStateProperties(block, Lootables.getEmptyBlock(block.getBlock()));}
		if(durability <= 0) {return emptyBlock;} else {return getRealBlock();}

	}

	public void setRealBlock(BlockState state) {
		this.block = state;
		this.emptyBlock = LootableLogic.copyStateProperties(state, Lootables.getEmptyBlock(block.getBlock()));
		boolean w = false;
		var db = getDisplayBlock();
		if(db.getProperties().contains(Properties.WATERLOGGED)) {
			w = db.get(Properties.WATERLOGGED);
		}
		this.world.setBlockState(this.pos, this.getWorld().getBlockState(this.pos).with(Properties.WATERLOGGED, w));
		this.markDirty();
		this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
	}

	public void restoreDurability() {
		this.world.setBlockState(pos, this.getRealBlock());
	}

	public void attackBlock(ServerPlayerEntity player) {

		if(this.durability > 0) {
			LootableLogic.giveLoot(player, pos, getRealBlock());
			getWorld().playSound(null, this.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1f + (float) Math.random());
			getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(getRealBlock()));
			this.durability--;
			if(this.durability == 0) {this.inventory = null;}
			this.lastBreak = System.currentTimeMillis();
			this.markDirty();
			this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);

		}
	}

	public void openInventory(ServerPlayerEntity player) {
		if(inventory == null)
			createInventory(player);
		Containers.createHandler(player, pos, (sync, inv, p1) -> new Container(sync, player, new ExtractOnlyGrid("loot", inventory, 3, 3)));

	}

	private void createInventory(ServerPlayerEntity player) {
		this.lastBreak = System.currentTimeMillis();

		inventory = new SimpleInventory(9);
		inventory.addListener(ls -> this.markDirty());
		LootableLogic.createLootInventory(player, pos, getRealBlock(), inventory);
		this.markDirty();
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		// Save the current value of the number to the nbt
		nbt.putInt("durability", durability);
		nbt.putLong("lastBreak", lastBreak);
		if(block == null) {block = Blocks.AIR.getDefaultState();}
		nbt.put("block", NbtHelper.fromBlockState(block));
		if(inventory != null) {
			nbt.put("inventory", inventory.toNbtList());
		}
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		durability = nbt.getInt("durability");
		lastBreak = nbt.getLong("lastBreak");
		block = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("block"));
		emptyBlock = LootableLogic.copyStateProperties(block, Lootables.getEmptyBlock(block.getBlock()));
		if(nbt.contains("inventory")) {
			inventory = new SimpleInventory(9);
			inventory.readNbtList(nbt.getList("inventory", NbtElement.COMPOUND_TYPE));
			inventory.addListener(ls -> this.markDirty());
		}
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
		if(world.getTime() % 100 == 0) {
			if(System.currentTimeMillis() > t.lastBreak + GreenResurgence.CONFIG.serverOptions.cooldowns.respawnLootedBlockInSec() * 1000L) {
				t.restoreDurability();
			}

		}
	}

	public static class Container extends MultiInvScreenHandler {

		public Container(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public Container(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player, inventories);
		}

		public Container(int syncId, PlayerEntity player, boolean empty) {
			super(syncId, player, empty);
		}

		@Override
		public ScreenHandlerType<Container> type() {
			return Containers.LOOTABLE_INV;
		}
	}

}
