package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.equipement.EquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class EquipmentUpgradeItem extends Item implements EquipmentUpgrade {
	protected final String[] allowed;
	private final String slot;
	private final int durability;
	private final float weight;

	public EquipmentUpgradeItem(String allowed, String slot, float wheight) {
		this(allowed, slot, -1, wheight);
	}

	public EquipmentUpgradeItem(String allowed, String slot) {
		this(allowed, slot, -1, 1);
	}

	@Override
	public boolean isDamageable() {
		return durability > -1;
	}

	public int maxDurability() {
		return durability;
	}

	public EquipmentUpgradeItem(String allowed, String slot, int durability, float wheight) {
		super(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1));
		this.allowed = allowed.split(",");
		this.slot = slot;
		this.durability = durability;
		this.weight = wheight;
	}

	@Override
	public boolean canBeApplied(IEquipmentDef equipment, ItemStack stack) {
		for(String s : allowed) {
			var sp = s.split("/");
			if(sp[0].equals("*") || sp[0].equals(equipment.getEquipmentType())) {
				if(sp[1].equals("*") || sp[1].equals(equipment.getEquipmentSubtype())) return true;
			}
		}
		return false;
	}

	@Override
	public float damageWheight() {
		return this.weight;
	}

	@Override
	public String slot(IEquipmentDef equipment) {
		return slot;
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.getDamage() > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13.0F - (float) stack.getDamage() * 13.0F / (float) this.durability);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) this.durability - (float) stack.getDamage()) / (float) this.durability);
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

}
