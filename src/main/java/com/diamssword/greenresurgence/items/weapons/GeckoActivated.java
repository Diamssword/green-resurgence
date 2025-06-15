package com.diamssword.greenresurgence.items.weapons;

import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GeckoActivated extends ActivatedSword implements GeoItem, SimpleEnergyItemTiered {
	public static final RawAnimation POWERED_ANIM = RawAnimation.begin().thenLoop("powered");
	public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	public static BiFunction<String, Boolean, Object> ProviderFunction;
	private final boolean emissive;

	public GeckoActivated(ToolMaterial toolMaterial, int offDamage, float offSpeed, float onDamage, float onSpeed, boolean emissive, Settings settings) {
		super(toolMaterial, offDamage, offSpeed, onDamage, onSpeed, settings);
		this.emissive = emissive;
	}


	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(ProviderFunction.apply(Registries.ITEM.getId(this).getPath(), emissive));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (world instanceof ServerWorld sw) {
			GeoItem.getOrAssignId(stack, sw);
			if (stack.getNbt().getBoolean("activated") && world.getTime() % 80 == 0) {
				var v = Math.max(this.getStoredEnergy(stack) - (BatteryTiers.BATTERY.recommendeDischargeRate() * 80L), 0);
				this.setStoredEnergy(stack, v);
				if (v <= 0)
					stack.getNbt().putBoolean("activated", false);
			}
		}

	}

	@Override
	public boolean isNbtSynced() {
		return false;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			if (user.getOffHandStack().getItem() instanceof SimpleEnergyItemTiered) {
				return TypedActionResult.pass(user.getMainHandStack());
			}
		}

		var st = user.getStackInHand(hand);
		if (this.getStoredEnergy(st) > 0) {
			var comp = st.getOrCreateNbt();
			comp.putBoolean("activated", !comp.getBoolean("activated"));
			st.setNbt(comp);
			user.getItemCooldownManager().set(this, 20);
			return TypedActionResult.consume(st);
		}
		return TypedActionResult.fail(st);
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return this.renderProvider;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		var cont = new AnimationController<>(this, 1, state -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.
			var st = state.getData(DataTickets.ITEMSTACK);
			if (st != null && st.getOrCreateNbt().getBoolean("activated")) {
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
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}


	@Override
	public long getEnergyCapacity(ItemStack stack) {
		return BatteryTiers.BATTERY.capacity;
	}

	@Override
	public long getEnergyMaxInput(ItemStack stack) {
		return BatteryTiers.BATTERY.maxIO;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return 0;
	}

	@Override
	public BatteryTiers getBatteryTier() {
		return BatteryTiers.BATTERY;
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
