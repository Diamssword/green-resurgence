package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MSounds;
import com.diamssword.greenresurgence.items.helpers.ISecondaryDurabilityBar;
import com.diamssword.greenresurgence.items.helpers.ISimpleGasCanisterHolder;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.GasStackBasedEquipment;
import com.diamssword.greenresurgence.systems.equipement.IUpgradableEquipment;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.Map;
import java.util.Optional;

public class EquipmentToolGas extends EquipmentTool implements ISecondaryDurabilityBar {
	private final boolean emissive;

	public EquipmentToolGas(String category, String subCategory, Map<String, EffectLevel> baseEffects, boolean emissive) {
		super(category, subCategory, baseEffects);
		this.emissive = emissive;
	}

	@Override
	public boolean isEmissive() {
		return emissive;
	}

	public long gasPassiveConsumption() {
		return 1;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(world instanceof ServerWorld sw) {
			GeoItem.getOrAssignId(stack, sw);
			if(isActivated(stack) && world.getTime() % 80 == 0) {
				if(!this.tryUseGas(stack, gasPassiveConsumption() * 80L)) {
					stack.getNbt().putBoolean("activated", false);
				}
			}
		}

	}

	public boolean isActivated(ItemStack stack) {
		return stack.hasNbt() && stack.getNbt().getBoolean("activated");
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		var st = user.getStackInHand(hand);
		if(this.getStoredGas(st) > 0) {
			var comp = st.getOrCreateNbt();
			world.playSound(null, user.getX(), user.getY(), user.getZ(), MSounds.BUTTON_CLICK, SoundCategory.PLAYERS, 1, 0.5f + world.random.nextFloat());
			comp.putBoolean("activated", !comp.getBoolean("activated"));
			st.setNbt(comp);
			user.getItemCooldownManager().set(this, 20);
			return TypedActionResult.consume(st);
		}
		return TypedActionResult.fail(st);
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		return this.getBattery(stack).map(v -> v.getLeft().getGasStorage().onStackClicked(v.getRight(), slot, clickType, player)).orElse(super.onStackClicked(stack, slot, clickType, player));
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return this.getBattery(stack).map(v -> v.getLeft().getGasStorage().getTooltipData(v.getRight()));
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}


	@Override
	public IUpgradableEquipment createEquipmentInstance(ItemStack stack) {
		return new GasStackBasedEquipment(category, subCategory, stack, getBaseUpgrades());
	}

	public GasStackBasedEquipment getEquipmentStack(ItemStack stack) {
		return (GasStackBasedEquipment) getEquipment(stack);
	}

	protected Optional<Pair<ISimpleGasCanisterHolder, ItemStack>> getBattery(ItemStack stack) {
		return getEquipmentStack(stack).getCanister();
	}

	public long getGasCapacity(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getGasCapacity(pair.getRight())).orElse(0L);
	}

	public long getStoredGas(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getStoredGasAmount(pair.getRight())).orElse(0L);
	}

	public boolean tryUseGas(ItemStack stack, long newAmount) {
		return getBattery(stack).map((pair) -> pair.getLeft().tryUseGas(pair.getRight(), newAmount)).orElse(false);
	}

	@Override
	public float getSecondDurabilityProgress(ItemStack stack) {
		return (this.getStoredGas(stack) / (float) this.getGasCapacity(stack));
	}

	@Override
	public int getSecondItemBarColor(ItemStack stack) {
		return this.getBattery(stack).map(b -> b.getLeft().getStoredGas(b.getRight()).color()).orElse(0xff53ccea);
	}
}
