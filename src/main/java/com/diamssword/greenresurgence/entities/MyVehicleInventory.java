package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.containers.IOptionalInventory;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class MyVehicleInventory extends AnimalEntity implements Inventory, NamedScreenHandlerFactory, IOptionalInventory {

	@org.jetbrains.annotations.Nullable
	private Identifier lootTableId;
	private long lootTableSeed;

	protected MyVehicleInventory(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	abstract DefaultedList<ItemStack> getInventory();

	abstract void resetInventory();

	public void setInventoryLootTableId(@Nullable Identifier lootTableId) {
		this.lootTableId = lootTableId;
	}

	private long getInventoryLootTableSeed() {
		return this.lootTableSeed;
	}

	@Nullable
	Identifier getInventoryLootTableId() {
		return this.lootTableId;
	}

	@Override
	public void onClose(PlayerEntity player) {
		this.getWorld().emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of(player));
	}

	@Override
	public boolean hasInventory(PlayerEntity player) {
		return this.hasChest();
	}

	abstract public boolean hasChest();

	public void setLootTableSeed(long lootTableSeed) {
		this.lootTableSeed = lootTableSeed;
	}

	public boolean isEmpty() {
		return this.isInventoryEmpty();
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		if(!this.hasChest())
			return null;
		if(this.getInventoryLootTableId() != null && player.isSpectator()) {
			return null;
		} else {
			this.generateInventoryLoot(playerInventory.player);
			return getScreenHandler(syncId, playerInventory, player);
		}
	}

	abstract protected ScreenHandler getScreenHandler(int SyncID, PlayerInventory playerInventory, PlayerEntity player);

	public void writeInventoryToNbt(NbtCompound nbt) {

		if(this.getInventoryLootTableId() != null) {
			nbt.putString("LootTable", this.getInventoryLootTableId().toString());
			if(this.getInventoryLootTableSeed() != 0L) {
				nbt.putLong("LootTableSeed", this.getInventoryLootTableSeed());
			}
		} else {
			Inventories.writeNbt(nbt, this.getInventory());
		}

	}

	public void readInventoryFromNbt(NbtCompound nbt) {
		this.resetInventory();
		if(nbt.contains("LootTable", 8)) {
			this.setInventoryLootTableId(new Identifier(nbt.getString("LootTable")));
			this.setLootTableSeed(nbt.getLong("LootTableSeed"));
		} else {
			Inventories.readNbt(nbt, this.getInventory());
		}

	}

	public void onBroken(DamageSource source, World world, Entity vehicle) {
		if(world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemScatterer.spawn(world, vehicle, this);
			if(!world.isClient) {
				Entity entity = source.getSource();
				if(entity != null && entity.getType() == EntityType.PLAYER) {
					PiglinBrain.onGuardedBlockInteracted((PlayerEntity) entity, true);
				}
			}

		}
	}

	public ActionResult open(PlayerEntity player) {
		player.openHandledScreen(this);
		return !player.getWorld().isClient ? ActionResult.CONSUME : ActionResult.SUCCESS;
	}

	public void generateInventoryLoot(@Nullable PlayerEntity player) {
		MinecraftServer minecraftServer = this.getWorld().getServer();
		if(this.getInventoryLootTableId() != null && minecraftServer != null) {
			LootTable lootTable = minecraftServer.getLootManager().getLootTable(this.getInventoryLootTableId());
			if(player != null) {
				Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity) player, this.getInventoryLootTableId());
			}

			this.setInventoryLootTableId((Identifier) null);
			LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld) this.getWorld())).add(LootContextParameters.ORIGIN, this.getPos());
			if(player != null) {
				builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
			}

			lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST), this.getInventoryLootTableSeed());
		}

	}

	public void clearInventory() {
		this.generateInventoryLoot((PlayerEntity) null);
		this.getInventory().clear();
	}

	public boolean isInventoryEmpty() {
		for(ItemStack itemStack : this.getInventory()) {
			if(!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public ItemStack removeInventoryStack(int slot) {
		this.generateInventoryLoot((PlayerEntity) null);
		ItemStack itemStack = (ItemStack) this.getInventory().get(slot);
		if(itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.getInventory().set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	public ItemStack getInventoryStack(int slot) {
		this.generateInventoryLoot((PlayerEntity) null);
		return (ItemStack) this.getInventory().get(slot);
	}

	public ItemStack removeInventoryStack(int slot, int amount) {
		this.generateInventoryLoot((PlayerEntity) null);
		return Inventories.splitStack(this.getInventory(), slot, amount);
	}

	public void setInventoryStack(int slot, ItemStack stack) {
		this.generateInventoryLoot((PlayerEntity) null);
		this.getInventory().set(slot, stack);
		if(!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

	}

	public StackReference getInventoryStackReference(final int slot) {
		return slot >= 0 && slot < this.size() ? new StackReference() {
			public ItemStack get() {
				return MyVehicleInventory.this.getInventoryStack(slot);
			}

			public boolean set(ItemStack stack) {
				MyVehicleInventory.this.setInventoryStack(slot, stack);
				return true;
			}
		} : StackReference.EMPTY;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.canPlayerAccess(player);
	}

	@Override
	public void clear() {
		this.clearInventory();
	}

	@Override
	public int size() {
		return this.hasChest() ? getInventory().size() : 0;
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if(!this.getWorld().isClient && reason.shouldDestroy()) {
			ItemScatterer.spawn(this.getWorld(), this, this);
		}
		super.remove(reason);
	}

	@Override
	public void openInventory(PlayerEntity player) {
		player.openHandledScreen(this);
		if(!player.getWorld().isClient) {
			this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
			PiglinBrain.onGuardedBlockInteracted(player, true);
		}
	}

	public boolean canPlayerAccess(PlayerEntity player) {

		return !this.isRemoved() && this.getPos().isInRange(player.getPos(), ReachEntityAttributes.getReachDistance(player, 8.0));
	}
}

