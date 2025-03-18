package com.diamssword.greenresurgence.containers.player.grids;

import com.diamssword.greenresurgence.containers.GridContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class PlayerGrid extends GridContainer {
        public PlayerGrid(String name, Inventory inv, int width, int height) {
            super(name, inv, width, height);
        }
        public PlayerGrid(String name, int width, int height) {
            super(name, width, height);
        }

        @Override
        public Slot createSlotFor(int index, int x, int y) {
            return new Slot(getInventory(),index,x,y){
                @Override
                public void setStack(ItemStack stack) {
                    this.setStackNoCallbacks(stack);
                    if(inventory instanceof  PlayerInventory pl)
                        pl.player.playerScreenHandler.updateToClient();
                }
            };
        }

        public PlayerGrid(String name, Inventory inv, int width, int height, int index) {
            super(name, inv, width, height, index);
        }
        @Override
        public boolean isPlayerContainer() {
            return true;
        }
    }