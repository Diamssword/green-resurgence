package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.ElectricStackBasedEquipment;
import com.diamssword.greenresurgence.systems.equipement.IUpgradableEquipment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentToolElectric extends EquipmentTool implements SimpleEnergyItemTiered {
	public static final RawAnimation POWERED_ANIM = RawAnimation.begin().thenLoop("powered");
	public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private final boolean emissive;

	public EquipmentToolElectric(String category, String subCategory, boolean emissive) {
		super(category, subCategory);
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
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

		return true;
	}

	@Override
	public Map<String, EffectLevel> getBaseUpgrades() {
		return new HashMap<>();
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		var cont = new AnimationController<>(this, 1, state -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.
			var st = state.getData(DataTickets.ITEMSTACK);
			if(st != null && st.getOrCreateNbt().getBoolean("activated")) {
				state.getController().setAnimation(POWERED_ANIM);
			} else
				state.getController().setAnimation(IDLE_ANIM);

			// Play the animation if the full set is being worn, otherwise stop
			return PlayState.CONTINUE;
		});
		cont.setParticleKeyframeHandler(event -> {
		});
		controllers.add(cont);
	}

	@Override
	public IUpgradableEquipment getEquipment(ItemStack stack) {
		return new ElectricStackBasedEquipment(category, subCategory, stack);
	}

	public ElectricStackBasedEquipment getEquipmentStack(ItemStack stack) {
		return (ElectricStackBasedEquipment) getEquipment(stack);
	}

	protected Optional<Pair<SimpleEnergyItemTiered, ItemStack>> getBattery(ItemStack stack) {
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

	@Override
	public int getItemBarColor(ItemStack stack) {
		return 0xff53ccea;
	}

	@Override
	public long getStoredEnergy(ItemStack stack) {
		return getBattery(stack).map((pair) -> pair.getLeft().getStoredEnergy(pair.getRight())).orElse(0L);
	}

	@Override
	public void setStoredEnergy(ItemStack stack, long newAmount) {
		getBattery(stack).ifPresent((pair) -> pair.getLeft().setStoredEnergy(pair.getRight(), newAmount));
	}
}
