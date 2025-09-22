package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.containers.grids.GridContainerSyncer;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.containers.player.grids.PlayerGrid;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class CreativeMultiInvScreenHandler extends AbstractMultiInvScreenHandler<CreativeMultiInvScreenHandler> {
	private final SyncedProperty<GridContainerSyncer> props;
	protected IGridContainer[] inventories;
	protected final Map<String, List<Slot>> inventoriesMap = new HashMap<>();
	protected final Map<String, Integer[]> sizeMap = new HashMap<>();
	private final PlayerInventory playerinv;
	@Nullable
	private BlockPos inventoryPos;

	//This constructor gets called on the client when the server wants it to open the screenHandler,
	//The client will call the other constructor with an empty Inventory and the screenHandler will automatically
	//sync this empty inventory with the inventory on the server.
	public CreativeMultiInvScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(null, syncId);
		this.playerinv = playerInventory;
		props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer());
		props.observe(c -> {
			this.inventoryPos = props.get().inventoryPos;
			this.inventories = containersFromProps(c);
			for(IGridContainer inventory : inventories) {
				addSlotsFor(inventory);
			}
			ready = true;
			listeners.forEach(v -> v.accept(this));
		});
	}

	public PlayerInventory getPlayerInventory() { //TODO might need to remove this function and redo all the logics of item deplacement for multiples player inventories

		return playerinv;
	}

	public List<String> getInventoriesNames() {
		return inventoriesMap.keySet().stream().toList();
	}

	public IGridContainer[] containersFromProps(GridContainerSyncer prop) {
		return prop.getContainers();
	}

	public void forceReady() {
		ready = true;
		listeners.forEach(v -> v.accept(this));
	}

	public CreativeMultiInvScreenHandler(int syncId, PlayerInventory player, IGridContainer... inventories) {
		super(null, syncId);
		for(IGridContainer iGridContainer : inventories) {
			checkSize(iGridContainer.getInventory(), iGridContainer.getSize());
			iGridContainer.getInventory().onOpen(player.player);
		}
		ready = true;
		this.playerinv = player;
		var ls = new ArrayList<IGridContainer>();
		ls.add(new PlayerGrid("player", player, 9, 3, 9));
		ls.add(new PlayerGrid("hotbar", player, 9, 1));
		ls.addAll(Arrays.asList(inventories));
		this.inventories = ls.toArray(new IGridContainer[0]);
		//used to send the GridContainer information to the client
		this.props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer(inventoryPos, this.inventories));
		props.markDirty();
		for(IGridContainer inventory : this.inventories) {
			addSlotsFor(inventory);
		}
	}

	public CreativeMultiInvScreenHandler(int syncId, PlayerInventory player, boolean empty) {
		super(null, syncId);
		ready = true;
		this.playerinv = player;
		inventories = new IGridContainer[]{new PlayerGrid("player", player, 9, 3, 9), new PlayerGrid("hotbar", player, 9, 1)};
		this.props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer(inventoryPos, inventories));
		props.markDirty();
		for(IGridContainer inventory : this.inventories) {
			addSlotsFor(inventory);
		}
	}

	public CreativeMultiInvScreenHandler setPos(BlockPos pos) {
		this.inventoryPos = pos;
		this.props.get().inventoryPos = pos;
		this.props.markDirty();
		return this;
	}

	public BlockPos getPos() {
		return this.inventoryPos;
	}

	public abstract ScreenHandlerType<? extends CreativeMultiInvScreenHandler> type();

	@Override
	public ScreenHandlerType<?> getType() {
		return type();
	}

	private boolean ready = false;
	private final List<Consumer<CreativeMultiInvScreenHandler>> listeners = new ArrayList<>();

	/**
	 * @return if the containers have been received and are ready to display
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 *
	 * Called when the containers have been received and are ready to display
	 * You should check  isReady() before using the callback
	 *
	 * @param consumer a callback
	 */
	public void onReady(Consumer<CreativeMultiInvScreenHandler> consumer) {
		if(isReady()) {consumer.accept(this);} else {listeners.add(consumer);}
	}

	protected void addSlotsFor(IGridContainer container) {

		if(container.revert()) {

			for(int m = container.getHeight() - 1; m >= 0; --m) {
				for(int l = container.getWidth() - 1; l >= 0; --l) {
					Slot s = createSlot(container, container.getStartIndex() + l + m * container.getWidth(), l * 18, m * 18);
					this.addSlot(s);
					inventoriesMap.putIfAbsent(container.getName(), new ArrayList<>());
					inventoriesMap.get(container.getName()).add(s);
				}
			}
		} else {
			for(int m = 0; m < container.getHeight(); ++m) {
				for(int l = 0; l < container.getWidth(); ++l) {
					Slot s = createSlot(container, container.getStartIndex() + l + m * container.getWidth(), l * 18, m * 18);
					this.addSlot(s);
					inventoriesMap.putIfAbsent(container.getName(), new ArrayList<>());
					inventoriesMap.get(container.getName()).add(s);
				}
			}
		}

		sizeMap.put(container.getName(), new Integer[]{container.getWidth(), container.getHeight()});
	}

	protected Slot createSlot(IGridContainer container, int index, int x, int y) {
		return container.createSlotFor(index, x, y);
	}

	public List<Slot> getSlotForInventory(String name) {
		return inventoriesMap.getOrDefault(name, new ArrayList<>());
	}

	public String getInventoryForSlot(Slot s) {
		for(String id : this.inventoriesMap.keySet()) {
			if(this.inventoriesMap.get(id).contains(s)) {return id;}
		}
		return null;
	}

	public String getInventoryForSlot(int slotID) {
		for(String id : this.inventoriesMap.keySet()) {
			if(this.inventoriesMap.get(id).stream().anyMatch(s -> s.getIndex() == slotID)) {return id;}
		}
		return null;
	}

	public IGridContainer getInventory(String name) {
		for(IGridContainer inventory : this.inventories) {
			if(inventory.getName().equals(name)) {return inventory;}
		}
		return null;

	}

	public IGridContainer getContainerFor(int slot) {
		Slot s = this.slots.get(slot);
		for(String string : this.inventoriesMap.keySet()) {
			var b = this.inventoriesMap.get(string);
			if(b.contains(s)) {
				return getInventory(string);
			}
		}
		return null;
	}

	public int getInventoryWidth(String name) {
		return sizeMap.getOrDefault(name, new Integer[]{1, 1})[0];
	}

	public int getInventoryHeight(String name) {
		return sizeMap.getOrDefault(name, new Integer[]{1, 1})[1];
	}

	@Override
	public boolean canUse(PlayerEntity player) { //TODO might want to change the logic later
		for(IGridContainer inventory : this.inventories) {
			if(!inventory.getInventory().canPlayerUse(player)) {return false;}
		}
		return true;
	}

	/**
	 * A version that can handle the multiple inventories
	 *
	 * @param player
	 * @param containerID
	 */
	public boolean canUse(PlayerEntity player, String containerID) {
		var inv = getInventory(containerID);
		if(inv != null) {return inv.getInventory().canPlayerUse(player);}
		return false;
	}

	public int totalSize() {
		int s = 0;
		for(IGridContainer inventory : inventories) {
			s = s + inventory.getInventory().size();
		}
		return s;
	}

	// Shift + Player Inv Slot
	@Override
	public ItemStack quickMove(PlayerEntity player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if(slot != null && slot.hasStack()) {
			ItemStack originalStack = slot.getStack();
			newStack = originalStack.copy();
			var cont = getContainerFor(invSlot);
			if(cont != null && !cont.isPlayerContainer()) {
				if(!this.insertItem(cont, originalStack, true)) {
					return ItemStack.EMPTY;
				}
			} else if(!this.insertItem(cont, originalStack, false)) {
				return ItemStack.EMPTY;
			}

			if(originalStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}
		return newStack;
	}

	protected boolean insertItem(IGridContainer origin, ItemStack stack, boolean fromContainer) {

		boolean bl = false;
		List<IGridContainer> invs = Arrays.stream(this.inventories).filter(v -> v.isPlayerContainer() == fromContainer).toList();
		if(invs.isEmpty() && !fromContainer) {
			invs = Arrays.stream(this.inventories).filter(v -> v != origin).toList();
		}


		if(stack.isStackable()) {
			for(var inv : invs) {
				for(var slot : this.getSlotForInventory(inv.getName())) {
					var itemstack = slot.getStack();
					if(!itemstack.isEmpty() && ItemStack.canCombine(stack, itemstack)) {
						int j = itemstack.getCount() + stack.getCount();
						if(j <= stack.getMaxCount()) {
							stack.setCount(0);
							itemstack.setCount(j);
							slot.markDirty();
							bl = true;
						} else if(itemstack.getCount() < stack.getMaxCount()) {
							stack.decrement(stack.getMaxCount() - itemstack.getCount());
							itemstack.setCount(stack.getMaxCount());
							slot.markDirty();
							bl = true;
						}
					}
					if(itemstack.isEmpty() && slot.canInsert(stack)) {
						if(stack.getCount() > slot.getMaxItemCount()) {
							slot.setStack(stack.split(slot.getMaxItemCount()));
						} else {
							slot.setStack(stack.split(stack.getCount()));
							return true;
						}
						slot.markDirty();
						bl = true;
					}
					if(stack.isEmpty()) {break;}
				}
				if(stack.isEmpty()) {break;}
			}
		} else if(!stack.isEmpty()) {
			for(var inv : invs) {
				for(var slot : this.getSlotForInventory(inv.getName())) {
					var itemstack = slot.getStack();
					if(itemstack.isEmpty() && slot.canInsert(stack)) {
						if(stack.getCount() > slot.getMaxItemCount()) {
							slot.setStack(stack.split(slot.getMaxItemCount()));
						} else {
							slot.setStack(stack.split(stack.getCount()));
						}
						slot.markDirty();
						return true;
					}
				}
			}
		}
		return bl;
	}

}