package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.MSounds;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class BikeEntity extends MyVehicleInventory implements GeoEntity, InventoryChangedListener, ILightAndSoundMount {
	private static final TrackedData<Boolean> CHEST = DataTracker.registerData(BikeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> LIGHT = DataTracker.registerData(BikeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(BikeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4 * 4, ItemStack.EMPTY);
	private int timeUntilMyRegen;
	private int lastSoundTick;

	public BikeEntity(EntityType<? extends BikeEntity> type, World level) {
		super(type, level);
		//this.ignoreCameraFrustum = true;
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
		this.dataTracker.startTracking(CHEST, false);
		this.dataTracker.startTracking(LIGHT, false);
		this.dataTracker.startTracking(COLOR, 0);

	}

	public boolean hasChest() {
		return this.dataTracker.get(CHEST);
	}

	public void setHasChest(boolean hasChest) {
		this.dataTracker.set(CHEST, hasChest);
	}

	@Override
	public int getColor() {
		return this.dataTracker.get(COLOR);
	}

	@Override
	public void setColor(int color) {
		this.dataTracker.set(COLOR, Math.max(0, Math.min(15, color)));
	}


	@Override
	protected void mobTick() {
		super.mobTick();
		if(!this.getWorld().isClient) {
			if(this.hasControllingPassenger()) {
				var ent = this.getControllingPassenger();
				if(ent instanceof PlayerEntity pl) {
					var comp = pl.getComponent(Components.PLAYER_DATA);
					comp.addCustomPose(PosesManager.RIDING_BIKE);
				}
			}
		}
		this.timeUntilMyRegen--;
		if(this.timeUntilMyRegen <= 0) {
			this.timeUntilMyRegen = 200;
			this.heal(1);
		}
	}

	@Override
	public void onDamaged(DamageSource damageSource) {
		super.onDamaged(damageSource);
		this.timeUntilMyRegen = 200;
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
		if(this.lastSoundTick >= 5)
			this.playSound(MSounds.BIKE_DRIVING, 0.1f, 1);
		this.lastSoundTick++;
		if(this.lastSoundTick > 5)
			this.lastSoundTick = 0;
		BlockSoundGroup blockSoundGroup = block.getSoundGroup();
		this.playSound(blockSoundGroup.getHitSound(), blockSoundGroup.getVolume() * 0.1F, blockSoundGroup.getPitch());
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
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}


	@Override
	public void writeStackData(NbtCompound nbt) {
		super.writeStackData(nbt);
		nbt.putBoolean("Light", this.isLightOn(this, null));

	}

	@Override
	public void readStackData(NbtCompound nbt) {
		super.readStackData(nbt);
		this.setHasLight(nbt.getBoolean("Light"));
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
		return true;
	}

	@Override
	boolean canHaveChest() {
		return true;
	}


	@Override
	public boolean isLightOn(@org.jetbrains.annotations.Nullable Entity owner, @org.jetbrains.annotations.Nullable ItemStack stack) {
		return this.dataTracker.get(LIGHT);
	}

	public void setHasLight(boolean light) {
		this.dataTracker.set(LIGHT, light);
	}

	@Override
	public SoundEvent getKlaxonSound() {
		return MSounds.BIKE_BELL;
	}

	@Override
	public Vec2f lightOffset() {
		return new Vec2f(1, 0.4f);
	}
}
