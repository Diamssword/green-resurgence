package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.IFlashLightProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;

public class FlashlightItem extends Item implements SimpleEnergyItemTiered, IFlashLightProvider {
	public FlashlightItem(Settings settings) {
		super(settings);
	}

	@Override
	public BatteryTiers getBatteryTier(ItemStack var1) {
		return BatteryTiers.BATTERY;
	}

	@Override
	public boolean isOn(ItemStack stack) {
		return stack.hasNbt() && stack.getNbt().getBoolean("activated");
	}


	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(world instanceof ServerWorld sw) {
			GeoItem.getOrAssignId(stack, sw);
			if(stack.getNbt().getBoolean("activated") && world.getTime() % 80 == 0) {
				var v = Math.max(this.getStoredEnergy(stack) - (BatteryTiers.BATTERY.recommendedDischargeRate() * 80L), 0);
				this.setStoredEnergy(stack, v);
				if(v <= 0)
					stack.getNbt().putBoolean("activated", false);
			}
		}

	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(hand == Hand.MAIN_HAND) {
			if(user.getOffHandStack().getItem() instanceof SimpleEnergyItemTiered) {
				return TypedActionResult.pass(user.getMainHandStack());
			}
		}

		var st = user.getStackInHand(hand);
		if(this.getStoredEnergy(st) > 0) {
			var comp = st.getOrCreateNbt();
			comp.putBoolean("activated", !comp.getBoolean("activated"));
			st.setNbt(comp);
			user.getItemCooldownManager().set(this, 20);
			return TypedActionResult.consume(st);
		}
		return TypedActionResult.fail(st);
	}

	@Override
	public long getEnergyCapacity(ItemStack itemStack) {
		return BatteryTiers.BATTERY.capacity;
	}

	@Override
	public long getEnergyMaxInput(ItemStack itemStack) {
		return BatteryTiers.BATTERY.maxIO;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack itemStack) {
		return 0;
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

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		setStoredEnergy(stack, this.getEnergyCapacity(stack));
		return stack;
	}
}
