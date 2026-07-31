package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.entities.FlamePuddleEntity;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.EquipmentEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class EquipmentFlameThrower extends EquipmentGasZoneDamage {
	public EquipmentFlameThrower(String category, String subCategory, Map<String, EffectLevel> baseEffects, boolean emissive) {
		super(category, subCategory, baseEffects, emissive);
	}

	@Override
	public float getBoxDistance(LivingEntity user, ItemStack stack) {

		var mod = user.getItemUseTime() % 5;
		return super.getBoxDistance(user, stack) + mod;
	}

	@Override
	public void attackEntityClient(LivingEntity user, ItemStack stack, Entity target) {
		//	super.attackEntityClient(user, stack, target);
	}

	@Override
	public boolean attackEntity(LivingEntity user, ItemStack stack, Entity target) {
		var val = getEquipmentStack(stack).getEffects().getOrDefault(EquipmentEffects.ATTACK_DAMAGE, new EffectLevel(0f)).getLevel();
		target.setFireTicks((int) (val * 20));
		return !target.isFireImmune();
	}

	@Override
	public void playUsingSound(LivingEntity user, ItemStack stack, boolean firstTick) {
		super.playUsingSound(user, stack, firstTick);
	}

	@Override
	public void performActionOnArea(ItemStack stack, LivingEntity user, Box b) {
		if(user.getWorld().isClient) {
			var vec = user.getRotationVec(1.0F);
			for(int i = 0; i < 10; i++) {
				var x = b.minX + (user.getRandom().nextDouble() * b.getXLength());
				var y = b.minY + (user.getRandom().nextDouble() * b.getYLength());
				var z = b.minZ + (user.getRandom().nextDouble() * b.getZLength());
				if(i % 2 == 0)
					user.getWorld().addParticle(ParticleTypes.FLAME, x, y, z, vec.x, vec.y, vec.z);
				else
					user.getWorld().addParticle(ParticleTypes.SMALL_FLAME, x, y, z, -0.1f + (user.getRandom().nextDouble() * 0.2f), (user.getRandom().nextDouble() * 0.1f), -0.1f + (user.getRandom().nextDouble() * 0.2f));
			}
		} else if(user.age % 20 == 0 && user.getItemUseTime() % 5 > 1) {
			var arr = new FlamePuddleEntity(user.getWorld(), b.getCenter().x, b.getCenter().y, b.getCenter().z);
			user.getWorld().spawnEntity(arr);
		}
	}

	@Override
	public Box createSelectionBox(Vec3d center, LivingEntity user, ItemStack stack) {
		double halfSize = 0.8;

		return new Box(
				center.x - halfSize,
				center.y - halfSize,
				center.z - halfSize,
				center.x + halfSize,
				center.y + halfSize,
				center.z + halfSize
		);
	}
}
