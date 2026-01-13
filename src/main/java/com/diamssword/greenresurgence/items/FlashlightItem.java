package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.helpers.BatteryStorageHelper;
import com.diamssword.greenresurgence.items.helpers.ISimpleBatteryHolder;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.utils.IFlashLightProvider;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.Optional;

public class FlashlightItem extends Item implements ISimpleBatteryHolder, IFlashLightProvider {
	public final BatteryStorageHelper battery = new BatteryStorageHelper(2, BatteryTiers.BATTERY);

	public FlashlightItem(Settings settings) {
		super(settings);
	}

	@Override
	public BatteryStorageHelper getBatteryStorage() {
		return battery;
	}


	@Override
	public boolean isLightOn(Entity owner, ItemStack stack) {
		return stack != null && stack.hasNbt() && stack.hasNbt() && stack.getNbt().getBoolean("activated");
	}

	@Override
	public Vec2f lightOffset() {
		return new Vec2f(-1f, 0f);
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
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(battery.onStackClicked(stack, slot, clickType, player))
			return true;
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		/*if(hand == Hand.MAIN_HAND) {
			if(user.getOffHandStack().getItem() instanceof ISimpleEnergyItemTiered) {
				return TypedActionResult.pass(user.getMainHandStack());
			}
		}*/

		var st = user.getStackInHand(hand);
		if(this.getStoredEnergy(st) > 0) {
			var comp = st.getOrCreateNbt();
			user.playSound(SoundEvents.BLOCK_LEVER_CLICK, 0.5f, (float) (1f + Math.random()));
			comp.putBoolean("activated", !comp.getBoolean("activated"));
			st.setNbt(comp);
			user.getItemCooldownManager().set(this, 5);
			return TypedActionResult.consume(st);
		}
		return TypedActionResult.fail(st);
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
		if(isLightOn(null, stack))
			return 0xff53ccea;
		else
			return 0xffC6C6C6;
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.of(this.battery.getTooltipData(stack));
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		setStoredEnergy(stack, this.getEnergyCapacity(stack));
		return stack;
	}
}
