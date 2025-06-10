package com.diamssword.greenresurgence.containers.player.grids;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class OffHandGrid extends PlayerGrid {
    public OffHandGrid(String name, Inventory inv, int width, int height) {
        super(name, inv, width, height);
    }

    public OffHandGrid(String name, int width, int height) {
        super(name, width, height);
    }

    public OffHandGrid(String name, Inventory inv, int width, int height, int index) {
        super(name, inv, width, height, index);
    }

    @Override
    public Slot createSlotFor(int index, int x, int y) {
        var i = index < 36 ? index : index - 36;
        return new Slot(this.getInventory(), index, x, y) {
            @Override
            public void setStack(ItemStack stack) {
                Equipment equipment = Equipment.fromStack(stack);
                if (inventory instanceof PlayerInventory pl) {
                    if (equipment != null) {
                        pl.player.onEquipStack(EquipmentSlot.OFFHAND, this.getStack(), stack);
                    }
                    super.setStack(stack);
                    pl.player.playerScreenHandler.updateToClient();

                } else
                    super.setStack(stack);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
            }
        };
    }

    @Override
    public int getQuickSlotPriority(ItemStack item) {
        return MobEntity.getPreferredEquipmentSlot(item) == EquipmentSlot.OFFHAND ? 1000 : -100;
    }
}
