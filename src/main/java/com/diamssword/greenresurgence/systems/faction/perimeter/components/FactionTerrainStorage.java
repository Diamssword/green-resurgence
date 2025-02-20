package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.blockEntities.GenericStorageBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FactionTerrainStorage implements NamedScreenHandlerFactory, Inventory {
    private final List<BlockPos> inventoriesPos=new ArrayList<>();
    private final Map<BlockPos,Inventory> inventoriesCache=new HashMap<>();
    private final FormattedInventory formated = new FormattedInventory(this);

    public void toNBT(NbtCompound tag)
    {
        List<Long> ls=new ArrayList<>();
        inventoriesPos.forEach(v->ls.add(v.asLong()));
        tag.putLongArray("inventories",ls);
    }
    public void fromNBT(NbtCompound tag, World w)
    {
        inventoriesPos.clear();
        inventoriesCache.clear();
        var ls=tag.getLongArray("inventories");
        for (long l : ls) {
            inventoriesPos.add(BlockPos.fromLong(l));
        }
        for (BlockPos p1 : inventoriesPos) {

            var te=w.getBlockEntity(p1);
            if(te instanceof Inventory in)
            {
                inventoriesCache.put(p1,in);
                if(in instanceof GenericStorageBlockEntity in1)
                    in1.addListener(l-> formated.refresh());
            }
        }
        formated.refresh();
    }
    public void addIfMissing(BlockPos pos, Inventory inventory)
    {
        if(!inventoriesPos.contains(pos))
        {
            inventoriesPos.add(pos);
            inventoriesCache.put(pos,inventory);
            if(inventory instanceof GenericStorageBlockEntity in1)
                in1.addListener(l-> formated.refresh());
        }
    }
    public void removeInventory(BlockPos pos)
    {
        if(inventoriesPos.contains(pos))
        {
            inventoriesPos.remove(pos);
            inventoriesCache.remove(pos);
        }
    }
    @Override
    public Text getDisplayName() {
        return Text.literal("Base");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ScreenHandler(syncId, playerInventory, new GridContainer("storage",formated,formated.size(),1));
    }

    @Override
    public int size() {
        int size=0;
        for (Inventory value : inventoriesCache.values()) {
            size+=value.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Inventory value : inventoriesCache.values()) {
            if(!value.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        var p=findInventory(slot);
        if(p!=null)
        {
            return p.getRight().getStack(p.getLeft());
        }
        return ItemStack.EMPTY;
    }
    private Pair<Integer,Inventory> findInventory(int slot)
    {
        for (Inventory value : inventoriesCache.values()) {
            if(slot<value.size())
            {
               return new Pair<>(slot,value);
            }
           slot-=value.size();
        }
        return null;
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        var p=findInventory(slot);
        if(p!=null)
        {
            return p.getRight().removeStack(p.getLeft(),amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        var p=findInventory(slot);
        if(p!=null)
        {
            return p.getRight().removeStack(p.getLeft());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        var p=findInventory(slot);
        if(p!=null)
        {
            p.getRight().setStack(p.getLeft(),stack);
        }
    }

    @Override
    public void markDirty() {
        inventoriesCache.forEach((k,v)->v.markDirty());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }
    public static class ScreenHandler extends MultiInvScreenHandler {
        public ScreenHandler(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public ScreenHandler( int syncId, PlayerInventory playerInventory, IGridContainer... containers) {
            super( syncId, playerInventory, containers);
            ((FormattedInventory)containers[0].getInventory()).addListener(l->{
                if(l.size()>this.getSlotForInventory("storage").size())
                {
                    var c=createSlot(this.getInventory("storage"),l.size()-1,0,0);
                    this.inventoriesMap.get("storage").add(c);

                    this.addSlot(c);
                    for (Slot storage : this.inventoriesMap.get("storage")) {
                        storage.setStackNoCallbacks(l.getStack(storage.getIndex()));
                    }
                    if(playerInventory.player instanceof ServerPlayerEntity sp) {
                        sp.networkHandler.sendPacket(new InventoryS2CPacket(this.syncId, ScreenHandler.this.nextRevision(), ScreenHandler.this.getStacks(), ItemStack.EMPTY));

                    }
                }
            });

        }
        @Override
        public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
            var bl=false;
            if(stacks.size()>this.slots.size())
            {
                for(int i=this.slots.size();i<stacks.size();i++)
                {
                    var c=createSlot(this.getInventory("storage"),this.inventoriesMap.get("storage").size(),i,i);
                    this.addSlot(c);
                    this.inventoriesMap.get("storage").add(c);
                    bl=true;

                }
            }
            super.updateSlotStacks(revision,stacks,cursorStack);
            if(bl && handler !=null)
                handler.run();
        }
        @Override
        public IGridContainer[] containersFromProps(Props prop)
        {
            var d=new FormattedInventory(new SimpleInventory(prop.sizes[0]*prop.sizes[1]));
            return new IGridContainer[]{new GridContainer("storage",d,prop.sizes[0],prop.sizes[1])};
        }
        private Runnable handler;
        public void onSlotAdded(Runnable handler)
        {
            this.handler=handler;
        }
        @Override
        public ScreenHandlerType<ScreenHandler> type() {
            return Containers.FAC_STORAGE;
        }
        @Override
        public void setStackInSlot(int slot, int revision, ItemStack stack) {
                super.setStackInSlot(slot,revision,stack);
        }
        @Override
        protected void addSlotsFor(IGridContainer container)
        {
        /*    if(container.getName().equals("storage"))
            {
                var size=((FormattedInventory)container.getInventory()).parent.size();
                for (int m = 0; m < size; ++m) {
                        Slot s=createSlot(container, container.getStartIndex()+ m, m, m);
                        this.addSlot(s);
                        inventoriesMap.putIfAbsent(container.getName(),new ArrayList<>());
                        inventoriesMap.get(container.getName()).add(s);
                }
            }
            else*/
                super.addSlotsFor(container);
        }
        @Override
        protected Slot createSlot(IGridContainer container,int index,int x,int y)
        {
            if(container.getInventory() instanceof FormattedInventory)
            return new FormattedInventory.MySlot((FormattedInventory) container.getInventory(), index, x , y);
            else
                return super.createSlot(container,index,x,y);
        }
        @Override
        public ItemStack quickMove(PlayerEntity player, int invSlot) {
            Slot slot = this.slots.get(invSlot);
            if (slot != null && slot.hasStack()) {
                ItemStack originalStack=slot.takeStack(slot.getStack().getMaxCount());
                var cont=getContainerFor(invSlot);
                if (cont != null) {
                    this.insertItem(originalStack, cont != playerGrid && cont != hotbarGrid);
                    if (!originalStack.isEmpty())
                        slot.insertStack(originalStack);
                    else {
                        slot.setStack(ItemStack.EMPTY);
                    }
                }
            }
            return ItemStack.EMPTY;
        }
        @Override
        protected boolean insertItem(ItemStack stack, boolean fromContainer) {
            if(fromContainer)
                return super.insertItem(stack, true);
            var inv=this.getInventory("storage");
            if(inv.getInventory() instanceof FormattedInventory fi)
            {
                if(fi.canInsert(stack))
                {
                    var i=fi.inserStack(stack);
                    stack.setCount(i);
                    return true;
                }

            }
            return false;
        }
        private boolean handleSlotClick(PlayerEntity player, ClickType clickType, Slot slot, ItemStack stack, ItemStack cursorStack) {
            FeatureSet featureSet = player.getWorld().getEnabledFeatures();
            if (cursorStack.isItemEnabled(featureSet) && cursorStack.onStackClicked(slot, clickType, player)) {
                return true;
            } else {
                return stack.isItemEnabled(featureSet) && stack.onClicked(cursorStack, slot, clickType, player, this.getCursorStackReference());
            }
        }
        private StackReference getCursorStackReference() {
            return new StackReference() {
                public ItemStack get() {
                    return ScreenHandler.this.getCursorStack();
                }

                public boolean set(ItemStack stack) {
                    ScreenHandler.this.setCursorStack(stack);
                    return true;
                }
            };
        }
        public boolean customSlotCLick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
            if (actionType == SlotActionType.QUICK_CRAFT) {
                return false;
            }
                if(actionType==SlotActionType.PICKUP )
                {
                    if (slotIndex < 0) {
                        return false;
                    }
                    ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                    var slot = (Slot)this.slots.get(slotIndex);
                    var itemStack = slot.getStack();
                    itemStack.setCount(Math.min(itemStack.getCount(),itemStack.getMaxCount()));
                    ItemStack itemStack4 = this.getCursorStack();
                    player.onPickupSlotClick(itemStack4, slot.getStack(), clickType);
                    if (!this.handleSlotClick(player, clickType, slot, itemStack, itemStack4)) {
                        if (itemStack.isEmpty()) {
                            if (!itemStack4.isEmpty()) {
                                var o = clickType == ClickType.LEFT ? itemStack4.getCount() : 1;
                                this.setCursorStack(slot.insertStack(itemStack4, o));
                            }
                        } else if (slot.canTakeItems(player)) {
                            if (itemStack4.isEmpty()) {
                                var o = clickType == ClickType.LEFT ? itemStack.getCount() : (itemStack.getCount() + 1) / 2;
                                Optional<ItemStack> optional = slot.tryTakeStackRange(o, Integer.MAX_VALUE, player);
                                optional.ifPresent((stack) -> {
                                    this.setCursorStack(stack);
                                    slot.onTakeItem(player, stack);
                                });
                            } else if (slot.canInsert(itemStack4)) {
                                var o = clickType == ClickType.LEFT ? itemStack4.getCount() : 1;
                                this.setCursorStack(slot.insertStack(itemStack4, o));
                             /*   if (ItemStack.canCombine(itemStack, itemStack4)) {
                                   var o = clickType == ClickType.LEFT ? itemStack4.getCount() : 1;
                                    this.setCursorStack(slot.insertStack(itemStack4, o));
                                } else if (itemStack4.getCount() <= slot.getMaxItemCount(itemStack4)) {
                                    var st1=slot.takeStack(itemStack.getCount());
                                    this.setCursorStack(st1);
                                    slot.insertStack(itemStack4);
                                }
                                */
                            } else if (ItemStack.canCombine(itemStack, itemStack4)) {
                                Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack.getCount(), itemStack4.getMaxCount() - itemStack4.getCount(), player);
                                optional2.ifPresent((stack) -> {
                                    itemStack4.increment(stack.getCount());
                                    slot.onTakeItem(player, stack);
                                });
                            }
                        }
                    }
                    slot.markDirty();
                    return true;
                }
                else if (actionType == SlotActionType.SWAP) {
                    var slot3 = (Slot)this.slots.get(slotIndex);
                    var itemStack2 = getPlayerInventory().getStack(button);
                    var itemStack = slot3.getStack();
                    itemStack.setCount(Math.min(itemStack.getCount(),itemStack.getMaxCount()));
                    if (!itemStack2.isEmpty() || !itemStack.isEmpty()) {
                        if (itemStack2.isEmpty()) {
                            if (slot3.canTakeItems(player)) {
                                var st1=slot3.takeStack(itemStack.getCount());
                                this.getPlayerInventory().setStack(button, st1);
                                slot3.onTakeItem(player, st1);
                            }
                        } else if (itemStack.isEmpty()) {
                            if (slot3.canInsert(itemStack2)) {
                                var p = slot3.getMaxItemCount(itemStack2);
                                if (itemStack2.getCount() > p) {
                                    slot3.insertStack(itemStack2.split(p));
                                } else {
                                    this.getPlayerInventory().setStack(button, ItemStack.EMPTY);
                                    slot3.insertStack(itemStack2);
                                }
                            }
                        } else if (slot3.canTakeItems(player) && slot3.canInsert(itemStack2)) {
                            var p = slot3.getMaxItemCount(itemStack2);
                            if (itemStack2.getCount() > p) {
                                slot3.insertStack(itemStack2.split(p));
                                var i1=slot3.takeStack(itemStack.getCount());
                                slot3.onTakeItem(player, i1);
                                if (! this.getPlayerInventory().insertStack(i1)) {
                                    player.dropItem(itemStack, true);
                                }
                            } else {
                                var i1=slot3.takeStack(itemStack.getCount());
                                slot3.onTakeItem(player, i1);
                                getPlayerInventory().setStack(button, i1);
                                slot3.insertStack(itemStack2);
                            }
                        }
                    }
                    return true;
                }
            return false;
        }
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

        try {
            if(!customSlotCLick(slotIndex,button,actionType,player))
                super.onSlotClick(slotIndex,button,actionType,player);

        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> {
                return this.type() != null ? Registries.SCREEN_HANDLER.getId(this.type()).toString() : "<no type>";
            });
            crashReportSection.add("Menu Class", () -> this.getClass().getCanonicalName());
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotIndex);
            crashReportSection.add("Button", button);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    }
}