package com.diamssword.greenresurgence.containers.player.grids;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.items.AbstractBackpackItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class BagsGrid extends PlayerGrid {
    static final Identifier[] SLOT_TEXTURES = new Identifier[]{GreenResurgence.asRessource("item/bag_slot_indicator"), GreenResurgence.asRessource("item/satchel_l_slot_indicator"), GreenResurgence.asRessource("item/satchel_r_slot_indicator")};

    public final CustomPlayerInventory parent;

    public BagsGrid(String name, int width, int height) {
        super(name, width, height);
        parent = null;


    }

    public BagsGrid(CustomPlayerInventory parent, String name, Inventory inv, int width, int height) {
        super(name, inv, width, height, 0);
        this.parent = parent;
    }

    @Override
    public Slot createSlotFor(int index, int x, int y) {

        return new Slot(this.getInventory(), index, x, y) {
            @Override
            public void setStack(ItemStack stack) {
                if (inventory instanceof CustomPlayerInventory.PlayerLinkedInventory pl) {
                    if (!pl.player.getWorld().isClient() && !pl.player.isSilent()) {
                        pl.player.getWorld().playSound(null, pl.player.getX(), pl.player.getY(), pl.player.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, pl.player.getSoundCategory(), 1.0F, 1.0F);
                    }
                    super.setStack(stack);
                    if (parent != null) {
                        parent.clearCache();
                        if (!parent.getPlayer().getWorld().isClient) {
                            parent.InventoryScreenNeedRefresh = true;
                        }
                    }
                    //    pl.player.playerScreenHandler.updateToClient();

                } else
                    super.setStack(stack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof AbstractBackpackItem ab) {
                    return (ab.slot == AbstractBackpackItem.PackSlot.Backpack && index == 0) || (ab.slot == AbstractBackpackItem.PackSlot.Satchel && index > 0);
                }
                return false;
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SLOT_TEXTURES[index]);
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                ItemStack itemStack = this.getStack();
                return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
            }
        };
    }

    @Override
    public int getQuickSlotPriority(ItemStack item) {
        return item.getItem() instanceof AbstractBackpackItem ? 1000 : -10000;
    }
}