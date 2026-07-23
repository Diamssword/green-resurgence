package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MSounds;
import com.diamssword.greenresurgence.items.helpers.ISecondaryDurabilityBar;
import com.diamssword.greenresurgence.items.helpers.ISimpleBatteryHolder;
import com.diamssword.greenresurgence.items.helpers.ISimpleEnergyItemTiered;
import com.diamssword.greenresurgence.items.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.ElectricStackBasedEquipment;
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
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Map;
import java.util.Optional;

public class EquipmentToolElectric extends EquipmentTool implements ISimpleEnergyItemTiered, ISecondaryDurabilityBar {
	public static final RawAnimation POWERED_ANIM = RawAnimation.begin().thenLoop("powered");
	public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private final boolean emissive;

	public EquipmentToolElectric(String category, String subCategory, Map<String, EffectLevel> baseEffects, boolean emissive) {
		super(category, subCategory, baseEffects);
		this.emissive = emissive;
	}

	@Override
	public boolean isEmissive() {
		return emissive;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(world instanceof ServerWorld sw) {
			GeoItem.getOrAssignId(stack, sw);
			if(isActivated(stack) && world.getTime() % 80 == 0) {
				var v = Math.max(this.getStoredEnergy(stack) - (BatteryTiers.BATTERY.recommendedDischargeRate() * 80L), 0);
				this.setStoredEnergy(stack, v);
				if(v <= 0)
					stack.getNbt().putBoolean("activated", false);
			}
		}

	}

	public boolean isActivated(ItemStack stack) {
		return stack.hasNbt() && stack.getNbt().getBoolean("activated");
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		var st = user.getStackInHand(hand);
		if(this.getStoredEnergy(st) > 0) {
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
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		var cont = new AnimationController<>(this, 1, state -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.
			var st = state.getData(DataTickets.ITEMSTACK);
			if(st != null && isActivated(st)) {
				state.getController().setAnimation(POWERED_ANIM);
			} else
				state.getController().setAnimation(IDLE_ANIM);

			return PlayState.CONTINUE;
		});
		cont.setParticleKeyframeHandler(event -> {
		});
		controllers.add(cont);
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		return this.getBattery(stack).map(v -> v.getLeft().getBatteryStorage().onStackClicked(v.getRight(), slot, clickType, player)).orElse(super.onStackClicked(stack, slot, clickType, player));
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return this.getBattery(stack).map(v -> v.getLeft().getBatteryStorage().getTooltipData(v.getRight()));
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}


	@Override
	public IUpgradableEquipment createEquipmentInstance(ItemStack stack) {
		return new ElectricStackBasedEquipment(category, subCategory, stack, getBaseUpgrades());
	}

	public ElectricStackBasedEquipment getEquipmentStack(ItemStack stack) {
		return (ElectricStackBasedEquipment) getEquipment(stack);
	}

	protected Optional<Pair<ISimpleBatteryHolder, ItemStack>> getBattery(ItemStack stack) {
		return getEquipmentStack(stack).getBattery();
	}

	@Override
	public long getEnergyCapacity(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getEnergyCapacity(pair.getRight())).orElse(0L);
	}

	@Override
	public long getEnergyMaxInput(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getEnergyMaxInput(pair.getRight())).orElse(0L);
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getEnergyMaxOutput(pair.getRight())).orElse(0L);
	}

	@Override
	public BatteryTiers getBatteryTier(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getBatteryTier(pair.getRight())).orElse(BatteryTiers.BATTERY);
	}

	/**
	 * @Override public int getItemBarColor(ItemStack stack) {
	 * return 0xff53ccea;
	 * }
	 **/
	@Override
	public long getStoredEnergy(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getStoredEnergy(pair.getRight())).orElse(0L);
	}

	@Override
	public void setStoredEnergy(ItemStack stack, long newAmount) {
		getBattery(stack).ifPresent((pair) -> pair.getLeft().setStoredEnergy(pair.getRight(), newAmount));
	}

	@Override
	public float getSecondDurabilityProgress(ItemStack stack) {
		return (this.getStoredEnergy(stack) / (float) this.getEnergyCapacity(stack));
	}

	@Override
	public int getSecondItemBarColor(ItemStack stack) {
		return 0xff53ccea;
	}
}
