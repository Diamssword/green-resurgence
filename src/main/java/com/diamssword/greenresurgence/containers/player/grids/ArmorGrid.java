package com.diamssword.greenresurgence.containers.player.grids;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ArmorGrid extends PlayerGrid {
    static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
            PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE
    };
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD,
    };


    public ArmorGrid(String name, int width, int height) {
        super(name, width, height);
    }

    public ArmorGrid(String name, Inventory inv, int width, int height) {
        super(name, inv, width, height, 0);
    }

    @Override
    public boolean revert() {
        return !(this.getInventory() instanceof SimpleInventory);
    }

    @Override
    public Slot createSlotFor(int index, int x, int y) {

        return new Slot(this.getInventory(), index, x, y) {
            @Override
            public void setStack(ItemStack stack) {
                Equipment equipment = Equipment.fromStack(stack);
                if (inventory instanceof CustomPlayerInventory.OffsetInventory pl1) {
                    if (pl1.parent instanceof PlayerInventory pl) {
                        if (equipment != null) {
                            pl.player.onEquipStack(EQUIPMENT_SLOT_ORDER[index], this.getStack(), stack);
                        }
                        super.setStack(stack);
                        pl.player.playerScreenHandler.updateToClient();
                    } else
                        super.setStack(stack);
                } else
                    super.setStack(stack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return EQUIPMENT_SLOT_ORDER[index] == MobEntity.getPreferredEquipmentSlot(stack);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[EQUIPMENT_SLOT_ORDER[index].getEntitySlotId()]);
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
        var a = MobEntity.getPreferredEquipmentSlot(item);
        if (a == null)
            return -1000;
        return a.isArmorSlot() ? 1000 : -1000;
    }
}