package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MSounds;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.EquipmentElectricZoneDamage;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Map;

public class EquipmentToolElectricTwoHanded extends EquipmentElectricZoneDamage implements ICustomPoseWeapon {
	public static final RawAnimation SLASH_FP_ANIM = RawAnimation.begin().thenLoop("slash_fp");
	public static final RawAnimation LOAD_ANIM = RawAnimation.begin().thenPlayAndHold("load");

	public EquipmentToolElectricTwoHanded(String category, String subCategory, Map<String, EffectLevel> baseEffects, boolean emissive) {
		super(category, subCategory, baseEffects, emissive);
	}

	@Override
	public boolean shouldRemoveOffHand() {
		return true;
	}

	@Override
	public String customPoseId(ItemStack stack) {
		return PosesManager.TWOHANDWIELD;
	}

	@Override
	public int customPoseMode(ItemStack stack) {
		return 2;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

		super.inventoryTick(stack, world, entity, slot, selected);
		if(!world.isClient && isActivated(stack) && entity.age % 20 == 0) {
			var cat = entity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.NEUTRAL;
			world.playSound(null, entity.getX(), entity.getBodyY(0.6), entity.getZ(), MSounds.CHAINSAW_IDLE, cat, 1, 0.9f + (world.random.nextFloat() * 0.2f));
		}
	}

	@Override
	public void playUsingSound(LivingEntity user, ItemStack stack, boolean firstTick) {
		if(firstTick || user.age % 70 == 0) {
			var cat = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.NEUTRAL;
			user.getWorld().playSound(null, user.getX(), user.getBodyY(0.6), user.getZ(), MSounds.CHAINSAW_CUT, cat, 0.6f, 0.9f + (user.getWorld().random.nextFloat() * 0.2f));
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		var cont = new AnimationController<>(this, "idle", 1, state -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.


			var st = state.getData(DataTickets.ITEMSTACK);
			if(st != null && isActivated(st)) {

				state.getController().setAnimation(POWERED_ANIM);
			} else
				state.getController().setAnimation(IDLE_ANIM);

			return PlayState.CONTINUE;
		}).setSoundKeyframeHandler(event -> {

			//event.getKeyframeData().getSound()
		});
		var cont1 = new AnimationController<>(this, "slash", 1, state -> {

			var st = state.getData(DataTickets.ITEMSTACK);
			if(st != null && st.hasNbt()) {
				var perspective = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
				if(perspective == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
					if(st.getNbt().getBoolean("isUsed")) {

						state.getController().setAnimation(SLASH_FP_ANIM);
						return PlayState.CONTINUE;
					} else if(st.getNbt().getBoolean("isStarting")) {
						state.getController().setAnimation(LOAD_ANIM);
						return PlayState.CONTINUE;
					}
				}
			}

			return PlayState.STOP;
		});
		controllers.add(cont);
		controllers.add(cont1);
	}


	@Override
	public boolean isPerspectiveAware() {
		return true;
	}
}
