package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.collection.DefaultedList;
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

public class NewCaddieEntity extends MultiPassenger implements GeoEntity, InventoryChangedListener {
	private static final TrackedData<Boolean> CHEST = DataTracker.registerData(NewCaddieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4 * 4, ItemStack.EMPTY);
	private int timeUntilMyRegen;
	private int ticksUnderwater;
	private double lastHeight;
	private double downhillSpeedBonus = 0.0;

	public NewCaddieEntity(EntityType<? extends NewCaddieEntity> type, World level) {
		super(type, level);
		//this.ignoreCameraFrustum = true;
	}

	public NewCaddieEntity(EntityType<? extends NewCaddieEntity> type, World level, double x, double y, double z) {
		this(type, level);
		this.setPosition(x, y, z);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CHEST, false);

	}

	@Override
	protected Vec3d getPassengerOffset(int passengerIndex) {
		return passengerIndex == 0 ? new Vec3d(-1.1f, 0.4f, 0) : new Vec3d(0, 0.4f, 0);
	}


	@Override
	public boolean isPushable() {
		return true;
	}

	public boolean hasChest() {
		return this.dataTracker.get(CHEST);
	}

	public void setHasChest(boolean hasChest) {
		this.dataTracker.set(CHEST, hasChest);
		if(hasChest) {
			var p = passengers.get(1);
			if(p != null)
				p.dismountVehicle();
		}
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if(!this.getWorld().isClient) {
			if(this.hasControllingPassenger()) {
				var ent = this.getControllingPassenger();
				if(ent instanceof PlayerEntity pl) {
					var comp = pl.getComponent(Components.PLAYER_DATA);
					comp.addCustomPose(PosesManager.PUSHINGCART);

				}
			}
			if(isSubmergedInWater()) {
				this.ticksUnderwater++;
			} else
				this.ticksUnderwater = 0;
			if(this.ticksUnderwater > 60) {
				removeAllPassengers();
			}
		}
		this.timeUntilMyRegen--;
		if(this.timeUntilMyRegen <= 0) {
			this.timeUntilMyRegen = 200;
			this.heal(1);
		}
		this.bodyYaw = this.getYaw();
		this.headYaw = this.bodyYaw;
	}

	@Override
	public void onDamaged(DamageSource damageSource) {
		super.onDamaged(damageSource);
		this.timeUntilMyRegen = 200;
	}


	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
				.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1F)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 0.0);
	}

	@Override
	public boolean shouldSpawnSprintingParticles() {
		return this.getVelocity().length() > 0.3f && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
	}


	@Override
	protected void spawnSprintingParticles() {
		BlockPos blockPos = this.getLandingPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);

		if(blockState.getRenderType() == BlockRenderType.INVISIBLE)
			return;

		Vec3d velocity = this.getVelocity();
		float yaw = this.getYaw() * ((float) Math.PI / 180F);
		Vec3d forward = new Vec3d(-MathHelper.sin(yaw), 0, MathHelper.cos(yaw));
		Vec3d right = new Vec3d(forward.z, 0, -forward.x);
		double trailSpacing = 0.45;
		double trailBack = -0.3;

		Vec3d center = this.getPos()
				.add(forward.multiply(trailBack))
				.add(0, 0.1, 0);
		Vec3d leftPos = center.add(right.multiply(-trailSpacing));
		Vec3d rightPos = center.add(right.multiply(trailSpacing));
		spawnTrailParticle(blockState, leftPos, velocity);
		spawnTrailParticle(blockState, rightPos, velocity);
	}

	private void spawnTrailParticle(BlockState blockState, Vec3d pos, Vec3d velocity) {
		this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), pos.x, pos.y, pos.z, velocity.x * -4.0, 1.5, velocity.z * -4.0);
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		if(this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
			return 0;
		} else {
			StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.JUMP_BOOST);
			float f = statusEffectInstance == null ? 0.0F : statusEffectInstance.getAmplifier() + 1;
			return MathHelper.ceil((fallDistance - 10.0F - f) * (damageMultiplier / 5f));
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState block) {

		BlockSoundGroup blockSoundGroup = block.getSoundGroup();
		this.playSound(blockSoundGroup.getHitSound(), blockSoundGroup.getVolume() * 0.1F, blockSoundGroup.getPitch());
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		if(getWorld().isClient) {

			float vehicleYaw = getYaw();

			// Maximum amount the player can look away from the vehicle.
			float maxHeadOffset = 75.0f;

			/*
			 * Player's actual yaw is what the camera uses.
			 * Keep it within the allowed range around the vehicle.
			 */
			float playerYaw = controllingPlayer.getYaw();

			float offset = MathHelper.wrapDegrees(
					playerYaw - vehicleYaw
			);

			if(offset > maxHeadOffset) {
				playerYaw = vehicleYaw + maxHeadOffset;
			} else if(offset < -maxHeadOffset) {
				playerYaw = vehicleYaw - maxHeadOffset;
			}

			controllingPlayer.setYaw(playerYaw);

			// Body is always the vehicle.
			controllingPlayer.bodyYaw = vehicleYaw;
			controllingPlayer.prevBodyYaw = vehicleYaw;
			setRotation(getYaw(), getPitch());
			this.bodyYaw = this.getYaw();
			this.headYaw = this.bodyYaw;
		}
		super.tickControlled(controllingPlayer, movementInput);
	}

	@Override
	public void travel(Vec3d movementInput) {
		if(!this.isAlive() || !this.hasPassengers()) {
			super.travel(movementInput);
			return;
		}

		LivingEntity passenger = getControllingPassenger();

		if(passenger == null) {
			super.travel(movementInput);
			return;
		}

		// =========================================================
		// INPUT
		// =========================================================

		float forward = passenger.forwardSpeed;
		float steering = passenger.sidewaysSpeed;

		Vec3d velocity = getVelocity();

		// =========================================================
		// SPEED SETTINGS
		// =========================================================

		double maxSpeed = 3;
		double maxReverseSpeed = maxSpeed * 0.5;

		// Acceleration from a complete stop.
		// Lower = slower acceleration.
		double baseAcceleration = 0.012;

		// Drag while coasting.
		// Higher = more inertia.
		double coastDrag = 0.885;

		// Drag while actively accelerating.
		// Very small amount of drag prevents infinite acceleration.
		double accelerationDrag = 0.995;

		// =========================================================
		// SLOPE
		// =========================================================

		double height = getY();

		// Positive = going downhill
		// Negative = going uphill
		double slopeChange = lastHeight - height;

		lastHeight = height;

		// How much speed the downhill has built up.
		// Only accumulate when actually descending.
		if(slopeChange > 0.001) {

			downhillSpeedBonus += slopeChange * 0.5;

		} else if(slopeChange < -0.001) {

			// Going uphill gradually loses the downhill bonus.
			downhillSpeedBonus += slopeChange * 0.25;

		} else {

			// Flat ground slowly removes the bonus.
			downhillSpeedBonus *= 0.995;
		}

		// Never negative
		downhillSpeedBonus = Math.max(0.0, downhillSpeedBonus);

		// Maximum possible speed = 10x normal speed
		double downhillMaxSpeed = maxSpeed * 10.0;

		// Current effective maximum speed
		double effectiveMaxSpeed =
				Math.min(
						maxSpeed + downhillSpeedBonus,
						downhillMaxSpeed
				);

		// =========================================================
		// STEERING
		// =========================================================

		if(Math.abs(steering) > 0.01f) {

			double horizontalSpeed = Math.sqrt(
					velocity.x * velocity.x +
							velocity.z * velocity.z
			);

			float turnSpeed = Math.max(
					(float) Math.min(horizontalSpeed * 8.0, 4.0),
					2.0f
			);

			setYaw(getYaw() - steering * turnSpeed);
		}

		// =========================================================
		// FORWARD DIRECTION
		// =========================================================

		Vec3d forwardVector = Vec3d.fromPolar(0, getYaw())
				.normalize();

		// Current speed in the direction the vehicle is facing.
		double forwardSpeed = velocity.dotProduct(forwardVector);

		// =========================================================
		// FORWARD
		// =========================================================

		if(forward > 0) {

			double acceleration = baseAcceleration;

			// Going downhill gives additional acceleration.
			if(slopeChange > 0.001) {
				acceleration += baseAcceleration * slopeChange * 2.0;
			}

			// Going uphill reduces acceleration.
			if(slopeChange < -0.001) {
				acceleration += baseAcceleration * slopeChange * 2.0;
			}

			acceleration = Math.max(acceleration, 0.001);

			// Slow down acceleration as we approach the current speed limit.
			double speedRatio = Math.max(
					0.0,
					Math.min(forwardSpeed / effectiveMaxSpeed, 1.0)
			);

			double speedFactor = 1.0 - speedRatio;

			acceleration *= speedFactor;

			if(forwardSpeed < effectiveMaxSpeed) {
				velocity = velocity.add(
						forwardVector.multiply(acceleration)
				);
			}
		}

		// =========================================================
		// REVERSE
		// =========================================================

		if(forward < 0) {

			Vec3d backwardVector = forwardVector.multiply(-1);

			double reverseSpeed = -forwardSpeed;

			/*
			 * Reverse acceleration is half of forward acceleration.
			 */
			double reverseAcceleration = baseAcceleration * 0.5;

			double reverseSpeedRatio = Math.max(
					0.0,
					Math.min(reverseSpeed / maxReverseSpeed, 1.0)
			);

			double reverseSpeedFactor = 1.0 - reverseSpeedRatio;

			reverseAcceleration *= reverseSpeedFactor;

			if(reverseSpeed < maxReverseSpeed) {
				velocity = velocity.add(
						backwardVector.multiply(reverseAcceleration)
				);
			}
		}

		// =========================================================
		// DRAG
		// =========================================================

		double drag;

		if(Math.abs(forward) < 0.01f) {
			// No throttle -> stronger deceleration.
			drag = coastDrag;
		} else {
			// Throttle -> weaker drag.
			drag = accelerationDrag;
		}

		velocity = new Vec3d(
				velocity.x * drag,
				velocity.y,
				velocity.z * drag
		);

		// =========================================================
		// HARD SPEED LIMIT
		// =========================================================

		/*
		 * Only enforce the normal limit when going uphill/flat.
		 *
		 * Downhill:
		 * the vehicle is allowed to exceed maxSpeed.
		 */
		double horizontalSpeed = Math.sqrt(
				velocity.x * velocity.x +
						velocity.z * velocity.z
		);

		if(horizontalSpeed > effectiveMaxSpeed) {

			double scale = effectiveMaxSpeed / horizontalSpeed;

			velocity = new Vec3d(
					velocity.x * scale,
					velocity.y,
					velocity.z * scale
			);
		}
		/*
		 * Reverse speed limit.
		 */
		if(forward < 0 && -forwardSpeed > maxReverseSpeed) {

			double scale = maxReverseSpeed / (-forwardSpeed);

			velocity = new Vec3d(
					velocity.x * scale,
					velocity.y,
					velocity.z * scale
			);
		}

		// =========================================================
		// GRAVITY
		// =========================================================
		if(!hasNoGravity() && !isOnGround()) {

			velocity = velocity.add(
					0.0,
					-0.1,
					0.0
			);
		}

		// Prevent tiny downward velocity from accumulating
		// while standing on the ground.
		if(isOnGround() && velocity.y < 0) {

			velocity = new Vec3d(
					velocity.x,
					0,
					velocity.z
			);
		}

		// =========================================================
		// MOVE
		// =========================================================

		setVelocity(velocity);

		move(MovementType.SELF, velocity);
	}

	@Override
	public int getPassengerIndexForInteraction(PlayerEntity player, double zPos) {
		if(hasChest())
			return 0;
		var i = zPos < -0.3f ? 0 : 1;
		if(hasPassenger(i))
			return i == 0 ? 1 : 0;
		return i;
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
		float deltaYaw = MathHelper.wrapDegrees(this.getYaw() - this.prevYaw);
		super.updatePassengerPosition(passenger, positionUpdater);
		if(getIndexOfPassenger(passenger).orElse(1) != 0) {
			passenger.setYaw(passenger.getYaw() + deltaYaw);
			passenger.setHeadYaw(passenger.getHeadYaw() + deltaYaw);
			passenger.setBodyYaw(this.getYaw());
		}

	}

	@Override
	public int getMaxPassengers() {
		return hasChest() ? 1 : 2;
	}

	@Override
	public boolean isLogicalSideForUpdatingMovement() {
		return super.isLogicalSideForUpdatingMovement();
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
			// Handle the sound keyframe that is part of our animation json
		}));
	}

	public Item asItem() {
		return MItems.CADDIE;
	}


	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}


	@Override
	public void writeStackData(NbtCompound nbt) {
		super.writeStackData(nbt);

	}

	@Override
	public void readStackData(NbtCompound nbt) {
		super.readStackData(nbt);
	}

	@Override
	public void onInventoryChanged(Inventory sender) {

	}


	@Override
	public void markDirty() {

	}

	@Override
	public DefaultedList<ItemStack> getInventory() {
		return this.inventory;
	}

	@Override
	public void resetInventory() {
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
	}

	@Override
	boolean canBeDyed() {
		return false;
	}

	@Override
	boolean canHaveChest() {
		return true;
	}

	@Override
	public int getColor() {
		return 0;
	}

	@Override
	public void setColor(int color) {

	}

}
