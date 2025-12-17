package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class BikeEntity extends AnimalEntity implements GeoEntity {
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public BikeEntity(EntityType<? extends BikeEntity> type, World level) {
		super(type, level);
		this.ignoreCameraFrustum = true;
	}

	public BikeEntity(EntityType<? extends BikeEntity> type, World level, double x, double y, double z) {
		this(type, level);
		this.setPosition(x, y, z);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
		this.dropStack(new ItemStack(this.asItem()));
	}

	// Let the player ride the entity
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if(!this.hasPassengers()) {
			player.startRiding(this);

			return super.interactMob(player, hand);
		} else
			player.startRiding(this);


		return super.interactMob(player, hand);
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if(!this.getWorld().isClient) {
			if(this.hasControllingPassenger()) {
				var ent = this.getControllingPassenger();
				if(ent instanceof PlayerEntity pl) {
					var comp = pl.getComponent(Components.PLAYER_DATA);
					if(!PosesManager.RIDING_BIKE.equals(comp.getCustomPoseID()))
						comp.setCustomPose(PosesManager.RIDING_BIKE);
				}
			}
		}
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);

		if(this.getWorld().isClient) {
			float turnSpeed = 4f; // higher = faster turning
			float newYaw = MathHelper.lerpAngleDegrees(0.1f * turnSpeed, this.getYaw(), controllingPlayer.getYaw());
			setYaw(newYaw);
			//	setPitch(controllingPlayer.getPitch() * 0.5f);
			setRotation(getYaw(), getPitch());

			this.bodyYaw = this.getYaw();
			this.headYaw = this.bodyYaw;
		}
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
				.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1F)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 0.0);
	}

	@Override
	public int getXpToDrop() {
		return 0;
	}

	@Override
	public boolean shouldSpawnSprintingParticles() {
		return this.getVelocity().length() > 0.2f && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.75;
	}

	@Override
	protected void spawnSprintingParticles() {
		BlockPos blockPos = this.getLandingPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		if(blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			Vec3d vec3d = this.getVelocity();
			BlockPos blockPos2 = this.getBlockPos();
			double d = this.getX() + (this.random.nextDouble() - 0.5) * 0.2;
			double e = this.getZ() + (this.random.nextDouble() - 0.5) * 0.2;
			if(blockPos2.getX() != blockPos.getX()) {
				d = MathHelper.clamp(d, (double) blockPos.getX(), blockPos.getX() + 1.0);
			}

			if(blockPos2.getZ() != blockPos.getZ()) {
				e = MathHelper.clamp(e, (double) blockPos.getZ(), blockPos.getZ() + 1.0);
			}

			this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, this.getY() + 0.1, e, vec3d.x * -4.0, 1.5, vec3d.z * -4.0);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.isRemoved() || this.isInvulnerable() && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer() || damageSource.isIn(DamageTypeTags.IS_FIRE) || damageSource.isIn(DamageTypeTags.IS_DROWNING) || damageSource.isIn(DamageTypeTags.IS_FREEZING) || damageSource.isOf(DamageTypes.WITHER) || damageSource.isOf(DamageTypes.MAGIC) || damageSource.isOf(DamageTypes.CACTUS);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState block) {
		this.playSound(SoundEvents.ENTITY_MINECART_RIDING, 0.1f, (float) (1.5 + (Math.random() * 0.5F)));
	}

	// Apply player-controlled movement
	@Override
	public void travel(Vec3d pos) {
		if(this.isAlive()) {
			if(this.hasPassengers()) {
				LivingEntity passenger = (LivingEntity) getControllingPassenger();

				float x = passenger.sidewaysSpeed * 0.25F;
				float z = passenger.forwardSpeed;

				if(z <= 0)
					z *= 0.25f;
				this.setMovementSpeed(0.3f);

				super.travel(new Vec3d(x, pos.y, z));

			} else
				super.travel(pos);
		}
	}

	// Get the controlling passenger
	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return getFirstPassenger() instanceof LivingEntity entity ? entity : null;
	}

	@Override
	public boolean isLogicalSideForUpdatingMovement() {
		return super.isLogicalSideForUpdatingMovement();
		//	return true;
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}

	public float bikeRodDir() {
		if(!this.hasControllingPassenger()) return 0;
		if(this.prevYaw == this.getYaw())
			return 0;
		return this.prevYaw - this.getYaw();
	}

	public boolean isMoving() {
		if(!this.hasControllingPassenger()) return false;
		Vec3d delta = this.getPos().subtract(this.prevX, this.prevY, this.prevZ);
		return delta.horizontalLength() > 0.05f;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 2, state -> {
			if(this.isMoving()) {
				return state.setAndContinue(DefaultAnimations.DRIVE);
			} else {

				return state.setAndContinue(DefaultAnimations.IDLE);
			}
		}));
	}

	public Item asItem() {
		return MItems.BIKE;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(this.asItem());
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}
}
