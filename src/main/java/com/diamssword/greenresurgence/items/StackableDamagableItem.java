package com.diamssword.greenresurgence.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class StackableDamagableItem extends Item {
	private final int maxDamage;

	public StackableDamagableItem(Settings settings, int maxDamage) {
		super(settings);
		this.maxDamage = maxDamage;
	}

	public int getCustomMaxDamage() {
		return maxDamage;
	}

	public static int getDamage(ItemStack stack) {
		return stack.getNbt() == null ? 0 : stack.getNbt().getInt("CustomDamage");
	}

	public static void setDamage(ItemStack stack, int damage) {
		stack.getOrCreateNbt().putInt("CustomDamage", damage);
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.getDamage() > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13.0F - getDamage(stack) * 13.0F / this.getCustomMaxDamage());
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) this.getCustomMaxDamage() - getDamage(stack)) / this.getCustomMaxDamage());
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
}
