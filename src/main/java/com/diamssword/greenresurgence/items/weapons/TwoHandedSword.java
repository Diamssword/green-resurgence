package com.diamssword.greenresurgence.items.weapons;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class TwoHandedSword extends SwordItem implements ICustomPoseWeapon {

    private final boolean highHand;
    public TwoHandedSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings,boolean highHand) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.highHand=highHand;
    }

    @Override
    public boolean shouldRemoveOffHand() {
        return true;
    }

    @Override
    public String customPoseId() {
        return PosesManager.TWOHANDWIELD;
    }

    @Override
    public int customPoseMode() {
        return highHand?1:0;
    }
}
