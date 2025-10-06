package com.diamssword.greenresurgence.items.weapons;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class GeckoActivatedTwoHand extends GeckoActivated implements ICustomPoseWeapon {


	public GeckoActivatedTwoHand(ToolMaterial toolMaterial, int offDamage, float offSpeed, float onDamage, float onSpeed, boolean emissive, Settings settings) {
		super(toolMaterial, offDamage, offSpeed, onDamage, onSpeed, emissive, settings);
	}

	@Override
	public boolean shouldRemoveOffHand() {
		return true;
	}

	@Override
	public String customPoseId(ItemStack stack) {
		return PosesManager.TWOHANDWIELD;
	}

	@Override
	public int customPoseMode(ItemStack stack) {
		return 2;
	}
}
