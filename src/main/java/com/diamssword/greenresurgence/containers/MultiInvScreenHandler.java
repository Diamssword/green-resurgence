package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class MultiInvScreenHandler extends AbstractMultiInvScreenHandler<MultiInvScreenHandler> {
    private final SyncedProperty<GridContainerSyncer> props;
    private CustomPlayerInventory playerInventory;
    protected IGridContainer[] inventories;
    protected final Map<String, List<Slot>> inventoriesMap = new HashMap<>();
    protected final Map<String, Integer[]> sizeMap = new HashMap<>();

    @Nullable
    private BlockPos inventoryPos;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public MultiInvScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(null, syncId);
        props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer());
        props.observe(c -> {
            this.inventoryPos = props.get().inventoryPos;
            this.inventories = containersFromProps(c);
            for (IGridContainer inventory : inventories) {
                addSlotsFor(inventory);
            }
            ready = true;
            listeners.forEach(v -> v.accept(this));
        });
    }

    public PlayerInventory getPlayerInventory() { //TODO might need to remove this function and redo all the logics of item deplacement for multiples player inventories
        var pl = this.getInventory("player");
        if (pl instanceof PlayerInventory)
            return (PlayerInventory) pl;
        return null;
    }

    public List<String> getInventoriesNames() {
        return inventoriesMap.keySet().stream().toList();
    }

    public IGridContainer[] containersFromProps(GridContainerSyncer prop) {
        return prop.getContainers();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            ItemStack itemStack = this.getCursorStack();
            if (!itemStack.isEmpty()) {
                if (player.isAlive() && !((ServerPlayerEntity) player).isDisconnected()) {
                    playerInventory.setCursorStack(itemStack);
                } else {
                    player.dropItem(itemStack, false);
                }
                this.setCursorStack(ItemStack.EMPTY);
            }
        }
    }

    public MultiInvScreenHandler(int syncId, PlayerEntity player, IGridContainer... inventories) {
        super(null, syncId);
        for (IGridContainer iGridContainer : inventories) {
            checkSize(iGridContainer.getInventory(), iGridContainer.getSize());
            iGridContainer.getInventory().onOpen(player);
        }
        ready = true;
        playerInventory = player.getComponent(Components.PLAYER_INVENTORY).getInventory();
        this.setCursorStack(playerInventory.getAndClearCursorStack());
        var ls = playerInventory.getAsContainers();
        ls.addAll(Arrays.asList(inventories));
        this.inventories = ls.toArray(new IGridContainer[0]);
        //used to send the GridContainer information to the client
        this.props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer(inventoryPos, this.inventories));
        props.markDirty();
        for (IGridContainer inventory : this.inventories) {
            addSlotsFor(inventory);
        }
    }

    public MultiInvScreenHandler(int syncId, PlayerEntity player, boolean empty) {
        super(null, syncId);
        ready = true;
        playerInventory = player.getComponent(Components.PLAYER_INVENTORY).getInventory();
        this.setCursorStack(playerInventory.getAndClearCursorStack());
        inventories = playerInventory.getAsContainers().toArray(new IGridContainer[0]);
        this.props = this.createProperty(GridContainerSyncer.class, new GridContainerSyncer(inventoryPos, inventories));
        props.markDirty();
        for (IGridContainer inventory : this.inventories) {
            addSlotsFor(inventory);
        }
    }

    public MultiInvScreenHandler setPos(BlockPos pos) {
        this.inventoryPos = pos;
        this.props.get().inventoryPos = pos;
        this.props.markDirty();
        return this;
    }

    public BlockPos getPos() {
        return this.inventoryPos;
    }

    public abstract ScreenHandlerType<? extends MultiInvScreenHandler> type();

    @Override
    public ScreenHandlerType<?> getType() {
        return type();
    }

    private boolean ready = false;
    private final List<Consumer<MultiInvScreenHandler>> listeners = new ArrayList<>();

    /**
     * @return if the containers have been received and are ready to display
     */
    public boolean isReady() {
        return ready;
    }

    @Override
    public void onReady(Consumer<MultiInvScreenHandler> consumer) {
        if (isReady())
            consumer.accept(this);
        else
            listeners.add(consumer);
    }

    protected void addSlotsFor(IGridContainer container) {

        if (container.revert()) {

            for (int m = container.getHeight() - 1; m >= 0; --m) {
                for (int l = container.getWidth() - 1; l >= 0; --l) {
                    Slot s = createSlot(container, container.getStartIndex() + l + m * container.getWidth(), l * 18, m * 18);
                    this.addSlot(s);
                    inventoriesMap.putIfAbsent(container.getName(), new ArrayList<>());
                    inventoriesMap.get(container.getName()).add(s);
                }
            }
        } else {
            for (int m = 0; m < container.getHeight(); ++m) {
                for (int l = 0; l < container.getWidth(); ++l) {
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
        for (String id : this.inventoriesMap.keySet()) {
            if (this.inventoriesMap.get(id).contains(s))
                return id;
        }
        return null;
    }

    public String getInventoryForSlot(int slotID) {
        for (String id : this.inventoriesMap.keySet()) {
            if (this.inventoriesMap.get(id).stream().anyMatch(s -> s.getIndex() == slotID))
                return id;
        }
        return null;
    }

    public IGridContainer getInventory(String name) {
        for (IGridContainer inventory : this.inventories) {
            if (inventory.getName().equals(name))
                return inventory;
        }
        return null;

    }

    public IGridContainer getContainerFor(int slot) {
        Slot s = this.slots.get(slot);
        for (String string : this.inventoriesMap.keySet()) {
            var b = this.inventoriesMap.get(string);
            if (b.contains(s)) {
                return getInventory(string);
            }
        }
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) { //TODO might want to change the logic later
        for (IGridContainer inventory : this.inventories) {
            if (!inventory.getInventory().canPlayerUse(player))
                return false;
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
        if (inv != null)
            return inv.getInventory().canPlayerUse(player);
        return false;
    }

    public int totalSize() {
        int s = 0;
        for (IGridContainer inventory : inventories) {
            s = s + inventory.getInventory().size();
        }
        return s;
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            var cont = getContainerFor(invSlot);
            if (cont != null) {
                if (!this.insertItem(cont, originalStack, !cont.isPlayerContainer())) {
                    return ItemStack.EMPTY;
                }
            }
            if (originalStack.isEmpty()) {
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
        if (invs.isEmpty() && !fromContainer) {
            invs = Arrays.stream(this.inventories).filter(v -> v != origin).sorted((a, b) -> b.getQuickSlotPriority(stack) - a.getQuickSlotPriority(stack)).toList();
        }
        if (stack.isStackable()) {
            for (var inv : invs) {
                for (var slot : this.getSlotForInventory(inv.getName())) {
                    var itemstack = slot.getStack();
                    if (!itemstack.isEmpty() && ItemStack.canCombine(stack, itemstack)) {
                        int j = itemstack.getCount() + stack.getCount();
                        if (j <= stack.getMaxCount()) {
                            stack.setCount(0);
                            itemstack.setCount(j);
                            slot.markDirty();
                            bl = true;
                        } else if (itemstack.getCount() < stack.getMaxCount()) {
                            stack.decrement(stack.getMaxCount() - itemstack.getCount());
                            itemstack.setCount(stack.getMaxCount());
                            slot.markDirty();
                            bl = true;
                        }
                    }
                    if (itemstack.isEmpty() && slot.canInsert(stack)) {
                        if (stack.getCount() > slot.getMaxItemCount()) {
                            slot.setStack(stack.split(slot.getMaxItemCount()));
                        } else {
                            slot.setStack(stack.split(stack.getCount()));
                            return true;
                        }
                        slot.markDirty();
                        bl = true;
                    }
                    if (stack.isEmpty())
                        break;
                }
                if (stack.isEmpty())
                    break;
            }
        } else if (!stack.isEmpty()) {
            for (var inv : invs) {
                for (var slot : this.getSlotForInventory(inv.getName())) {
                    var itemstack = slot.getStack();
                    if (itemstack.isEmpty() && slot.canInsert(stack)) {
                        if (stack.getCount() > slot.getMaxItemCount()) {
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