package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class MultiInvScreenHandler extends ScreenHandler {
    private SyncedProperty<Props> props;
    private IGridContainer[] inventories;
    private final Map<String, List<Slot>> inventoriesMap=new HashMap<>();
    private final Map<String, Integer[]> sizeMap=new HashMap<>();
    private final GridContainer playerGrid;
    private final GridContainer hotbarGrid;
    @Nullable
    private BlockPos inventoryPos;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public MultiInvScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(null,syncId);
        playerGrid=new GridContainer("player",playerInventory,9,3,9);
        hotbarGrid=new GridContainer("hotbar",playerInventory,9,1);
        addSlotsFor(playerGrid);
        addSlotsFor(hotbarGrid);
       props= this.createProperty(Props.class,new Props());
       props.observe(c->{
           this.inventoryPos=props.get().inventoryPos;
           this.inventories=props.get().getContainers();
           for (IGridContainer inventory : inventories) {
               addSlotsFor(inventory);
           }
           ready=true;
           listeners.forEach(v->v.accept(this));
       });
    }
    public void forceReady()
    {
        ready=true;
        listeners.forEach(v->v.accept(this));
    }
    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public MultiInvScreenHandler(int syncId, PlayerInventory playerInventory, IGridContainer... inventories) {
        super(null, syncId);
        for (IGridContainer iGridContainer : inventories) {
            checkSize(iGridContainer.getInventory(), iGridContainer.getSize());
            iGridContainer.getInventory().onOpen(playerInventory.player);
        }
        ready=true;
        this.inventories=inventories;
        //used to send the GridContainer information to the client
        this.props=this.createProperty(Props.class,new Props(inventoryPos,inventories));
        props.markDirty();
        playerGrid=new GridContainer("player",playerInventory,9,3,9);
        hotbarGrid=new GridContainer("hotbar",playerInventory,9,1);
        addSlotsFor(playerGrid);
        addSlotsFor(hotbarGrid);
        for (IGridContainer inventory : inventories) {
            addSlotsFor(inventory);
        }

 
    }
    public MultiInvScreenHandler(int syncId, PlayerInventory playerInventory,boolean empty) {
        super(null, syncId);
        ready=true;
        this.inventories=new IGridContainer[0];
        //used to send the GridContainer information to the client
        this.props=this.createProperty(Props.class,new Props(inventoryPos,inventories));
        props.markDirty();
        playerGrid=new GridContainer("player",playerInventory,9,3,9);
        hotbarGrid=new GridContainer("hotbar",playerInventory,9,1);
        addSlotsFor(playerGrid);
        addSlotsFor(hotbarGrid);
    }
    public void setPos(BlockPos pos)
    {
        this.inventoryPos=pos;
        this.props.get().inventoryPos=pos;
        this.props.markDirty();
    }
    public BlockPos getPos()
    {
        return this.inventoryPos;
    }
    public abstract ScreenHandlerType<? extends MultiInvScreenHandler> type();
    @Override
    public ScreenHandlerType<?> getType() {
        return type();
    }
    private boolean ready=false;
    private final List<Consumer<MultiInvScreenHandler>> listeners=new ArrayList<>();

    /**
     * @return if the containers have been received and are ready to display
     */
    public boolean isReady()
    {
        return ready;
    }

    /**
     *
     * Called when the containers have been received and are ready to display
     * You should check  isReady() before using the callback
     * @param consumer a callback
     */
    public void onReady(Consumer<MultiInvScreenHandler> consumer)
    {
        listeners.add(consumer);
    }
    private void addSlotsFor(IGridContainer container)
    {
        for (int m = 0; m < container.getHeight(); ++m) {
            for (int l = 0; l < container.getWidth(); ++l) {
                Slot s=new Slot(container.getInventory(), container.getStartIndex()+ l + m * container.getWidth(), l * 18, m * 18);
                this.addSlot(s);
                inventoriesMap.putIfAbsent(container.getName(),new ArrayList<>());
                inventoriesMap.get(container.getName()).add(s);
            }
        }
        sizeMap.put(container.getName(),new Integer[]{container.getWidth(),container.getHeight()});
    }
   public List<Slot> getSlotForInventory(String name)
    {
        return inventoriesMap.getOrDefault(name,new ArrayList<>());
    }
    public String getInventoryForSlot(Slot s)
    {
        for (String id : this.inventoriesMap.keySet()) {
            if(this.inventoriesMap.get(id).contains(s))
                return id;
        }
        return null;
    }
    public IGridContainer getInventory(String name)
    {
        if(name.equals("hotbar") )
            return hotbarGrid;
        else if(name.equals("player") )
            return playerGrid;
        for (IGridContainer inventory : this.inventories) {
            if(inventory.getName().equals(name))
                return inventory;
        }
        return null;

    }
    public IGridContainer getContainerFor(int slot)
    {
        Slot s=this.slots.get(slot);
        for (String string : this.inventoriesMap.keySet()) {
            var b=this.inventoriesMap.get(string);
            if(b.contains(s)) {
                return  getInventory(string);
            }
        }
        return null;
    }
    public int getInventoryWidth(String name)
    {
        return sizeMap.getOrDefault(name,new Integer[]{1,1})[0];
    }
    public int getInventoryHeight(String name)
    {
        return sizeMap.getOrDefault(name,new Integer[]{1,1})[1];
    }
    @Override
    public boolean canUse(PlayerEntity player) { //TODO might want to change the logic later
        for (IGridContainer inventory : this.inventories) {
            if(!inventory.getInventory().canPlayerUse(player))
                return false;
        }
        return true;
    }

    /**
     * A version that can handle the multiple inventories
     * @param player
     * @param containerID
     */
    public boolean canUse(PlayerEntity player,String containerID) {
        var inv= getInventory(containerID);
        if(inv !=null)
            return inv.getInventory().canPlayerUse(player);
        return false;
    }
    public int totalSize()
    {
        int s=0;
        for (IGridContainer inventory : inventories) {
            s=s+inventory.getInventory().size();
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
            var cont=getContainerFor(invSlot);
            if (cont != null && cont != playerGrid && cont!=hotbarGrid) {
                if (!this.insertItem(originalStack,  true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack,false)) {
                return ItemStack.EMPTY;
            }
 
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    protected boolean insertItem(ItemStack stack, boolean fromContainer) {
        boolean bl = false;
        List<IGridContainer> invs=new ArrayList<>();
        if(fromContainer)
        {
            invs.add(this.getInventory("player"));
            invs.add(this.getInventory("hotbar"));
        }
        else
            Collections.addAll(invs,this.inventories);


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
        }
        else if (!stack.isEmpty()) {
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
    public static class Props {
        public int count;
        public String[] names;
        public int[] sizes;
        public BlockPos inventoryPos;
        public Props(@Nullable BlockPos inventoryPos, IGridContainer... containers)
        {
            this.inventoryPos=inventoryPos;
            count=containers.length;
            List<Integer> ls=new ArrayList<>();
            List<String> ls1=new ArrayList<>();
            for (IGridContainer container : containers) {
                ls1.add(container.getName());
                ls.add(container.getWidth());
                ls.add(container.getHeight());
            }
            names=ls1.toArray(new String[0]);
            sizes=new int[ls.size()];
            for (int i = 0; i < ls.size(); i++) {
                sizes[i]=ls.get(i);
            }
        }
        public Props()
        {
            count=0;
            names=new String[0];
            sizes=new int[0];
        }
        public IGridContainer[] getContainers()
        {
            IGridContainer[] res=new IGridContainer[count];
            for(int i=0;i<count;i++)
            {
                res[i]=new GridContainer(names[i],sizes[i*2],sizes[(i*2)+1]);
            }
            return res;
        }
        public static void serializer(PacketByteBuf write, Props val)
        {
            if(val.inventoryPos==null)
                val.inventoryPos=BlockPos.ORIGIN;
            write.writeInt(val.count);
            write.writeBlockPos(val.inventoryPos);
            write.writeIntArray(val.sizes);
            for (String name : val.names) {
                write.writeString(name);
            }

        }
        public static Props unserializer(PacketByteBuf read)
        {
            Props p=new Props();
            p.count=read.readInt();
            p.inventoryPos=read.readBlockPos();
            p.sizes=read.readIntArray();
            p.names=new String[p.count];
            for (int i=0;i<p.count;i++)
            {
                p.names[i]=read.readString();
            }
            return  p;
        }
    }
}