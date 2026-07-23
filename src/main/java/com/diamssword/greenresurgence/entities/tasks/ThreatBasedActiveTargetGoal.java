package com.diamssword.greenresurgence.entities.tasks;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ThreatBasedActiveTargetGoal extends ActiveTargetGoal<PlayerEntity> {


	public ThreatBasedActiveTargetGoal(MobEntity mob, Class<PlayerEntity> targetClass, boolean checkVisibility) {
		super(mob, targetClass, checkVisibility);
	}

	public ThreatBasedActiveTargetGoal(MobEntity mob, Class<PlayerEntity> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
		super(mob, targetClass, checkVisibility, targetPredicate);
	}

	public ThreatBasedActiveTargetGoal(MobEntity mob, Class<PlayerEntity> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
		super(mob, targetClass, checkVisibility, checkCanNavigate);
	}

	public ThreatBasedActiveTargetGoal(MobEntity mob, Class<PlayerEntity> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
		super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	protected void findClosestTarget() {
		var ls = this.mob.getWorld().getPlayers(this.targetPredicate, this.mob, this.getSearchBox(this.getFollowRange()));
		double d = -1.0;
		double score = 0f;
		PlayerEntity livingEntity = null;
		for(PlayerEntity pl : ls) {
			var tr = pl.getAttributeValue(Attributes.THREAT_MULTIPLIER);
			if(livingEntity == null || tr > score) {
				livingEntity = pl;
				score = tr;
			} else {
				double e = pl.squaredDistanceTo(this.mob);
				if(d == -1.0 || e < d) {
					d = e;
					livingEntity = pl;
					score = tr;
				}
			}
		}
		this.targetEntity = livingEntity;
	}
}