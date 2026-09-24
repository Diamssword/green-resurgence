package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.containers.GenericContainer;
import com.diamssword.greenresurgence.containers.IOptionalInventory;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class MyVehicleInventory extends AnimalEntity implements Inventory, NamedScreenHandlerFactory, IOptionalInventory, ISPawnableVehicle {

	@org.jetbrains.annotations.Nullable
	private Identifier lootTableId;
	private long lootTableSeed;

	protected MyVehicleInventory(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	abstract DefaultedList<ItemStack> getInventory();

	abstract void resetInventory();

	abstract boolean canBeDyed();

	abstract boolean canHaveChest();

	public abstract int getColor();

	public abstract void setColor(int color);

	public void setColor(DyeColor color) {
		setColor(color.getId());
	}

	public abstract void setHasChest(boolean hasChest);

	public void setInventoryLootTableId(@Nullable Identifier lootTableId) {
		this.lootTableId = lootTableId;
	}

	private long getInventoryLootTableSeed() {
		return this.lootTableSeed;
	}

	private void removeChest() {
		ItemScatterer.spawn(this.getWorld(), this, this);
		this.clearInventory();
		this.setHasChest(false);

	}

	public ActionResult interactWithItem(PlayerEntity player, ItemStack stack, Hand hand) {
		if(canHaveChest() && stack.getItem() == Items.CHEST) {
			if(this.hasChest()) {
				removeChest();
				stack.increment(1);
			} else {
				this.setHasChest(true);
				stack.decrement(1);
			}
			player.swingHand(hand);

			return ActionResult.CONSUME;
		} else if(canBeDyed() && stack.getItem() instanceof DyeItem dy) {
			if(this.getColor() != dy.getColor().getId()) {
				this.setColor(dy.getColor());
				player.swingHand(hand);
				stack.decrement(1);
				return ActionResult.CONSUME;
			}

		}
		return ActionResult.PASS;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		var res = interactWithItem(player, player.getStackInHand(hand), hand);
		if(res != ActionResult.PASS)
			return res;
		else if(this.canAddPassenger(player) && !player.shouldCancelInteraction()) {
			player.startRiding(this);
			player.swingHand(hand);
			return ActionResult.CONSUME;
		} else if(hasChest()) {
			player.openHandledScreen(this);
			this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
			player.swingHand(hand);
			return !player.getWorld().isClient ? ActionResult.CONSUME : ActionResult.SUCCESS;

		}

		return ActionResult.PASS;
	}

	@Override
	protected @org.jetbrains.annotations.Nullable SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return this.getVehicleItemStack();
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
		this.dropStack(this.getVehicleItemStack());
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PLAYER_ATTACK_CRIT;
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

	protected ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		if(player.isCreative()) {
			return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, this, 1);
		}
		return new GenericContainer(syncId, player, new GridContainer("container", this, 4, 4));
	}

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

	}

	public boolean canPlayerAccess(PlayerEntity player) {

		return !this.isRemoved() && this.getPos().isInRange(player.getPos(), ReachEntityAttributes.getReachDistance(player, 8.0));
	}


	@Override
	public int getXpToDrop() {
		return 0;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {

		return this.isRemoved() || this.isInvulnerable() && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer() || damageSource.isIn(DamageTypeTags.IS_FIRE) || damageSource.isIn(DamageTypeTags.IS_DROWNING) || damageSource.isIn(DamageTypeTags.IS_FREEZING) || damageSource.isOf(DamageTypes.WITHER) || damageSource.isOf(DamageTypes.MAGIC) || damageSource.isOf(DamageTypes.CACTUS);
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		writeStackData(nbt);
		if(this.hasChest()) {
			this.writeInventoryToNbt(nbt);
		}
	}

	@Override
	public void writeStackData(NbtCompound nbt) {
		if(this.canHaveChest())
			nbt.putBoolean("Chested", this.hasChest());
		if(this.canBeDyed())
			nbt.putInt("Color", this.getColor());

	}

	@Override
	public void readStackData(NbtCompound nbt) {
		if(this.canHaveChest())
			this.setHasChest(nbt.getBoolean("Chested"));
		if(this.canBeDyed())
			this.setColor(nbt.getInt("Color"));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.readStackData(nbt);
		if(this.hasChest()) {
			this.readInventoryFromNbt(nbt);
		}
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.getInventoryStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return this.removeInventoryStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeInventoryStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.setInventoryStack(slot, stack);
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return this.getInventoryStackReference(mappedIndex);
	}

}

