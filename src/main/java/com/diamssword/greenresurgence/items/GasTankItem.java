package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.helpers.SimpleGasItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GasTankItem extends Item implements SimpleGasItem {
	private final int capacity;

	public GasTankItem(Settings settings, int capacity) {
		super(settings);
		this.capacity = capacity;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.literal("").append(getStoredGas(stack).getTranslation()).append(": " + getStoredGasAmount(stack) + "/" + getGasCapacity(stack)));
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {

		return (int) ((this.getStoredGasAmount(stack) / (float) this.getGasCapacity(stack)) * 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return getStoredGas(stack).color();
	}

	@Override
	public long getGasCapacity(ItemStack stack) {
		return capacity;
	}
}
