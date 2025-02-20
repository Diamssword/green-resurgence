package com.diamssword.greenresurgence.systems.lootables;

import net.minecraft.entity.player.PlayerEntity;

public interface IAdvancedLootableBlock {
    public boolean canBeInteracted();
    public void lootBlock(PlayerEntity pl);
}
