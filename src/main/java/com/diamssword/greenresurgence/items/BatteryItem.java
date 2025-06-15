package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public class BatteryItem extends MaterialItem implements SimpleEnergyItemTiered {
	private final BatteryTiers type;
	private final float scaler;

	public BatteryItem(Settings settings, int tier, String id, String material, BatteryTiers type, float scaler) {
		super(settings, tier, id, material);
		this.type = type;
		this.scaler = scaler;
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		setStoredEnergy(stack, type.capacity(scaler));
		return stack;
	}

	@Override
	public long getEnergyCapacity(ItemStack stack) {
		return type.capacity(scaler);
	}

	@Override
	public long getEnergyMaxInput(ItemStack stack) {
		return type.maxIO(scaler);
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return type.maxIO(scaler);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		var stack = user.getStackInHand(hand);

		if (hand == Hand.OFF_HAND && this.getStoredEnergy(stack) > 0) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.fail(stack);

	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		if (this.getStoredEnergy(stack) > 0)
			return 72000;
		return 0;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (user instanceof PlayerEntity pl && pl.getActiveHand() == Hand.OFF_HAND) {
			var otherStack = user.getStackInHand(Hand.MAIN_HAND);
			if (otherStack.getItem() instanceof SimpleEnergyItemTiered rei) {
				var storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(pl, Hand.OFF_HAND));
				var recp = EnergyStorage.ITEM.find(otherStack, ContainerItemContext.ofPlayerHand(pl, Hand.MAIN_HAND));
				if (storage == null || !storage.supportsExtraction() || recp == null || !recp.supportsInsertion()) {
					return;
				}

				// Utilise un contexte de transaction propre
				try (Transaction ctx = Transaction.openOuter()) {
					var max = rei.getEnergyMaxInput(otherStack);
					max = Math.min(max, recp.getCapacity() - recp.getAmount());
					if (max > 0) {
						long extracted = storage.extract(max, ctx);

						//recp.insert(extracted, ctx);
						if (extracted > 0) {
							recp.insert(extracted, ctx);
							ctx.commit(); // Applique la transaction
						}
					}
				}
			}
		}
	}

	@Override
	public BatteryTiers getBatteryTier() {
		return type;
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {

		return (int) ((this.getStoredEnergy(stack) / (float) this.getEnergyCapacity(stack)) * 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return 0xff53ccea;
	}
}
