package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MParticles;
import com.diamssword.greenresurgence.MSounds;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.EquipmentEffects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;

import java.util.List;
import java.util.Map;

public class EquipmentElectricZoneDamage extends EquipmentToolElectric {
	public static final RegistryKey<DamageType> CUTTING_WEAPON_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GreenResurgence.asRessource("cutting"));

	public EquipmentElectricZoneDamage(String category, String subCategory, Map<String, EffectLevel> baseEffects, boolean emissive) {

		super(category, subCategory, baseEffects, emissive);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if(remainingUseTicks > 71995) {
			var comp = stack.getOrCreateNbt();
			if(user instanceof PlayerEntity pl)
				pl.getItemCooldownManager().set(this, getWarmUpSpeed(user, stack));
			world.playSound(null, user.getX(), user.getY(), user.getZ(), MSounds.BUTTON_CLICK, SoundCategory.PLAYERS, 1, 0.5f + world.random.nextFloat());
			comp.putBoolean("activated", !comp.getBoolean("activated"));
		}
		if(world.isClient) {
			stack.getNbt().remove("isUsed");
			stack.getNbt().remove("isStarting");
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if(world.isClient) {
			stack.getNbt().remove("isUsed");
			stack.getNbt().remove("isStarting");
		}
		return super.finishUsing(stack, world, user);
	}

	public float getBoxDistance(LivingEntity user, ItemStack stack) {
		return getEquipmentStack(stack).getEffects().getOrDefault(EquipmentEffects.ATTACK_RANGE, new EffectLevel(0f)).getLevel();
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		return ArrayListMultimap.create();
	}

	public Box createSelectionBox(Vec3d center, LivingEntity user, ItemStack stack) {
		double halfSize = 0.5;
		return new Box(
				center.x - halfSize,
				center.y - 0.2 - halfSize,
				center.z - halfSize,
				center.x + halfSize,
				center.y - 0.2 + halfSize,
				center.z + halfSize
		);
	}

	public void attackEntityClient(LivingEntity user, ItemStack stack, Entity target) {
		var random = target.getWorld().random;
		for(int i = 0; i < 12; i++) {
			Vec3d dir = new Vec3d(
					random.nextGaussian() * 0.03,
					random.nextDouble() * 0.03,
					random.nextGaussian() * 0.03
			);

			target.getWorld().addParticle(
					MParticles.BLOOD,
					target.getX(),
					target.getBodyY(0.6),
					target.getZ(),
					dir.x,
					dir.y,
					dir.z
			);
		}
	}

	public boolean attackEntity(LivingEntity user, ItemStack stack, Entity target) {
		var val = getEquipmentStack(stack).getEffects().getOrDefault(EquipmentEffects.ATTACK_DAMAGE, new EffectLevel(0f)).getLevel();
		return target.damage(user.getDamageSources().create(CUTTING_WEAPON_DAMAGE, user), val + 1);

	}

	public float getEnergyUseOnTarget(LivingEntity user, ItemStack stack, Entity target) {

		var val = getEquipmentStack(stack).getEffects().getOrDefault(EquipmentEffects.ELECTRIC_EFFICIENCY, new EffectLevel(100f)).getLevel();

		return 100 / (val / 100f);
	}

	public int getWarmUpSpeed(LivingEntity user, ItemStack stack) {
		var val = getEquipmentStack(stack).getEffects().getOrDefault(EquipmentEffects.ELECTRIC_WARMUP_SPEED, new EffectLevel(100f)).getLevel();
		return (int) (50 / (val / 100f));
	}

	public void performActionOnArea(ItemStack stack, LivingEntity user, Box selectionBox) {

	}

	public float makeHurtBox(ItemStack stack, LivingEntity user) {
		float used = 0;
		Vec3d eyePos = user.getEyePos();
		Vec3d center = eyePos.add(user.getRotationVec(1.0F).multiply(getBoxDistance(user, stack)));

		var hit = user.getWorld().raycast(new RaycastContext(eyePos, center, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
		var canSee = false;
		if(hit.getType() == HitResult.Type.BLOCK) {
			var st = user.getWorld().getBlockState(hit.getBlockPos());
			canSee = st.getCollisionShape(user.getWorld(), hit.getBlockPos()).isEmpty();
		} else
			canSee = true;
		if(canSee) {
			var box = createSelectionBox(center, user, stack);
			performActionOnArea(stack, user, box);
			List<Entity> entities = user.getWorld().getOtherEntities(user, box, Entity::isAlive);

			for(Entity entity : entities) {
				if(!user.getWorld().isClient) {
					if(attackEntity(user, stack, entity)) {
						used += getEnergyUseOnTarget(user, stack, entity);
					}
				} else
					attackEntityClient(user, stack, entity);
				//	System.out.println("Found: " + entity.getName().getString());
			}
		}
		return used;
	}

	public void playUsingSound(LivingEntity user, ItemStack stack, boolean firstTick) {

	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if(world.isClient)
			stack.getNbt().putBoolean("isStarting", true);
		var l = 72000 - (getWarmUpSpeed(user, stack) * 0.2);
		if(remainingUseTicks <= l) {
			if(!world.isClient) {
				playUsingSound(user, stack, remainingUseTicks == (int) l);
			}
			var used = makeHurtBox(stack, user);
			if(world.isClient) {
				stack.getNbt().putBoolean("isStarting", false);
				stack.getNbt().putBoolean("isUsed", true);
			}
			if(user.age % 15 == 0)
				user.swingHand(user.getActiveHand());
			if(!user.getWorld().isClient && used > 0) {
				var v = Math.max(this.getStoredEnergy(stack) - used, 0);
				this.setStoredEnergy(stack, (long) v);
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		var st = user.getStackInHand(hand);
		var comp = st.getOrCreateNbt();
		if(!comp.getBoolean("activated") && this.getStoredEnergy(st) > 0) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), MSounds.BUTTON_CLICK, SoundCategory.PLAYERS, 1, 0.5f + world.random.nextFloat());
			comp.putBoolean("activated", !comp.getBoolean("activated"));
			st.setNbt(comp);
			user.getItemCooldownManager().set(this, getWarmUpSpeed(user, st));
			return TypedActionResult.consume(st);
		} else if(this.getStoredEnergy(st) > 0) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(st);
		} else
			return TypedActionResult.fail(st);
	}
}
