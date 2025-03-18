package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.containers.SlotedSimpleInventory;
import com.diamssword.greenresurgence.entities.BackpackEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractBackpackItem extends Item {
    public final PackSlot slot;
    public AbstractBackpackItem(PackSlot slot,Settings settings) {
        super(settings.maxCount(1));
        this.slot=slot;
    }
    public static enum PackSlot{
        Backpack,
        Satchel
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            var inv=getInventory(stack);
            if(!inv.isEmpty())
            {
                tooltip.add(Text.literal("Contient :").formatted(Formatting.LIGHT_PURPLE));
                for (int i = 0; i < 4; i++) {
                    var st=inv.getStack(i);
                    if(!st.isEmpty())
                        tooltip.add(st.getName());
                }
                tooltip.add(Text.literal("..."));

            }
            else
                tooltip.add(Text.literal("Vide").formatted(Formatting.LIGHT_PURPLE));
    }

    public abstract int inventoryWidth(ItemStack stack);
    public abstract int inventoryHeight(ItemStack stack);
    public int inventorySize(ItemStack stack)
    {
        return this.inventoryWidth(stack)*this.inventoryHeight(stack);
    }
    public Inventory getInventory(ItemStack stack)
    {
        var inv= new SlotedSimpleInventory(inventorySize(stack));
        var tag=stack.getNbt();
        if(tag !=null && tag.contains("inventory"))
            inv.readNbtList(tag.getList("inventory", NbtElement.COMPOUND_TYPE));
        inv.addListener(in->{
            var tag1=stack.getOrCreateNbt();
            tag1.put("inventory",inv.toNbtList());
        });
        return inv;
    }
    public boolean isInventoryEmpty(ItemStack stack)
    {
        if(stack.hasNbt())
        {
            var tag=stack.getNbt();
            if(tag.contains("inventory"))
            {
                return getInventory(stack).isEmpty();
            }
        }
        return true;
    }
    public void bagSlotTick(ItemStack stack, World world, Entity entity, int slot) {

    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            if(!world.isClient && entity instanceof PlayerEntity pl)
            {
                if(!pl.isCreative() && !pl.isSpectator() && !isInventoryEmpty(stack))
                    BackpackEntity.dropItemBackpack(pl,stack.copyAndEmpty());
            }

    }
}
