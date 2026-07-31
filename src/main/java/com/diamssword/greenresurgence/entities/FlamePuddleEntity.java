package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MEntities;
import com.google.common.collect.Maps;
import net.minecraft.entity.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class FlamePuddleEntity extends AreaEffectCloudEntity {
	private final Map<Entity, Integer> affectedEntities = Maps.<Entity, Integer>newHashMap();

	public FlamePuddleEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
		super(entityType, world);
	}

	public FlamePuddleEntity(World world, double x, double y, double z) {
		this(MEntities.FLAME_PUDDLE, world);
		this.setPosition(x, y, z);
		this.setParticleType(ParticleTypes.SMALL_FLAME);
		this.setRadius(0.8f);
		this.setRadiusGrowth(0.005f);
		this.setDuration(200);
		this.groundCollision = true;
		this.noClip = false;
	}

	@Override
	public void tick() {
		super.tick();
		if(!getWorld().isClient) {
			if(!hasNoGravity()) {
				move(MovementType.SELF, new Vec3d(0, -0.3f, 0));
			}
			if(this.age >= this.getWaitTime() + (this.getDuration() / 2) && getRadiusGrowth() > 0)
				this.setRadiusGrowth(-getRadiusGrowth());
			if(this.age % 5 == 0) {
				this.affectedEntities.entrySet().removeIf(entry -> this.age >= (Integer) entry.getValue());

				List<LivingEntity> list2 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
				if(!list2.isEmpty()) {
					for(LivingEntity livingEntity : list2) {
						if(!this.affectedEntities.containsKey(livingEntity) && livingEntity.isAffectedBySplashPotions()) {
							double q = livingEntity.getX() - this.getX();
							double r = livingEntity.getZ() - this.getZ();
							double s = q * q + r * r;
							var f = getRadius();
							if(s <= f * f) {
								this.affectedEntities.put(livingEntity, this.age + 20);

								livingEntity.setOnFireFor(2);
								if(this.getRadiusOnUse() != 0.0F) {
									f += this.getRadiusOnUse();
									if(f < 0.5F) {
										this.discard();
										return;
									}

									this.setRadius(f);
								}

								if(this.getDurationOnUse() != 0) {
									this.setDuration(this.getDuration() + this.getDurationOnUse());
									if(this.getDuration() <= 0) {
										this.discard();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
