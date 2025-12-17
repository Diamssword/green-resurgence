package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TwoPassengerVehicle extends Entity implements GeoEntity {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(TwoPassengerVehicle.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CONTROLLER_POS = DataTracker.registerData(TwoPassengerVehicle.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(TwoPassengerVehicle.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(TwoPassengerVehicle.class, TrackedDataHandlerRegistry.FLOAT);
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
	private float ticksUnderwater;
	private float yawVelocity;
	private float forward;
	private double lastHeight;
	private double heightSpeedCap;
	private int field_7708;
	private double x;
	private double y;
	private double z;
	private double boatYaw;
	private double boatPitch;
	private boolean pressingLeft;
	private boolean pressingRight;
	private boolean pressingForward;
	private boolean pressingBack;
	private double waterLevel;
	private float nearbySlipperiness;
	private BoatEntity.Location location;
	private BoatEntity.Location lastLocation;
	private double fallVelocity;

	public TwoPassengerVehicle(EntityType<? extends TwoPassengerVehicle> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
		this.setStepHeight(0.5f);
	}

	public TwoPassengerVehicle(EntityType<? extends TwoPassengerVehicle> entityType, World world, double x, double y, double z) {
		this(entityType, world);
		this.setPosition(x, y, z);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height;
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
		this.dataTracker.startTracking(CONTROLLER_POS, 1);

	}

	@Override
	public boolean collidesWith(Entity other) {
		return canCollide(this, other);
	}

	public static boolean canCollide(Entity entity, Entity other) {
		return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
	}

	@Override
	public boolean isCollidable() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
		return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.4;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if(this.isInvulnerableTo(source)) {
			return false;
		} else if(!this.getWorld().isClient && !this.isRemoved()) {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			this.scheduleVelocityUpdate();
			this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
			if(bl || this.getDamageWobbleStrength() > 40.0F) {
				if(!bl && this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
					this.dropItems(source);
				}

				this.discard();
			}

			return true;
		} else {
			return true;
		}
	}

	protected void dropItems(DamageSource source) {
		this.dropItem(this.asItem());
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if(entity instanceof BoatEntity) {
			if(entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
				super.pushAwayFrom(entity);
			}
		} else if(entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.pushAwayFrom(entity);
		}
	}

	public Item asItem() {
		return MItems.CADDIE;
	}

	@Override
	public void animateDamage(float yaw) {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.boatYaw = (double) yaw;
		this.boatPitch = (double) pitch;
		this.field_7708 = 10;
	}

	@Override
	public Direction getMovementDirection() {
		return this.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public void tick() {
		this.lastLocation = this.location;
		this.location = this.checkLocation();
		if(this.location != BoatEntity.Location.UNDER_WATER && this.location != BoatEntity.Location.UNDER_FLOWING_WATER) {
			this.ticksUnderwater = 0.0F;
		} else {
			this.ticksUnderwater++;
		}
		if(!this.getWorld().isClient) {
			var d = this.dataTracker.get(CONTROLLER_POS);
			if(!this.hasPassengers())
				this.dataTracker.set(CONTROLLER_POS, 1);
			if(d < this.getPassengerList().size()) {
				var ent = this.getPassengerList().get(d);
				if(ent instanceof PlayerEntity pl) {
					var comp = pl.getComponent(Components.PLAYER_DATA);
					if(!PosesManager.PUSHINGCART.equals(comp.getCustomPoseID()))
						comp.setCustomPose(PosesManager.PUSHINGCART);
				}
			} else if(this.getFirstPassenger() instanceof PlayerEntity pl) {
				var comp = pl.getComponent(Components.PLAYER_DATA);
				if(PosesManager.PUSHINGCART.equals(comp.getCustomPoseID()))
					comp.setCustomPose(null);
			}
		}
		if(!this.getWorld().isClient && this.ticksUnderwater >= 60.0F) {
			this.removeAllPassengers();
		}

		if(this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if(this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		super.tick();
		this.updatePositionAndRotation();
		if(this.isLogicalSideForUpdatingMovement()) {

			var hei = this.getPos().y;
			if(hei < lastHeight)
				heightSpeedCap += 0.2;
			else
				heightSpeedCap = Math.max(0, heightSpeedCap - 0.1f);
			lastHeight = hei;

			this.updateVelocity();
			if(this.getWorld().isClient) {
				this.updatePaddles();
			}

			this.move(MovementType.SELF, this.getVelocity());
		} else {
			this.setVelocity(Vec3d.ZERO);
		}

		this.checkBlockCollision();
		List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicates.canBePushedBy(this));
		if(!list.isEmpty()) {
			for(Entity value : list) {
				if(!value.hasPassenger(this)) {
					this.pushAwayFrom(value);
				}
			}
		}
	}

	private void updatePositionAndRotation() {
		if(this.isLogicalSideForUpdatingMovement()) {
			this.field_7708 = 0;
			this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
		}

		if(this.field_7708 > 0) {
			double d = this.getX() + (this.x - this.getX()) / (double) this.field_7708;
			double e = this.getY() + (this.y - this.getY()) / (double) this.field_7708;
			double f = this.getZ() + (this.z - this.getZ()) / (double) this.field_7708;
			double g = MathHelper.wrapDegrees(this.boatYaw - (double) this.getYaw());
			this.setYaw(this.getYaw() + (float) g / (float) this.field_7708);
			this.setPitch(this.getPitch() + (float) (this.boatPitch - (double) this.getPitch()) / (float) this.field_7708);
			this.field_7708--;
			this.setPosition(d, e, f);
			this.setRotation(this.getYaw(), this.getPitch());
		}
	}

	private BoatEntity.Location checkLocation() {
		BoatEntity.Location location = this.getUnderWaterLocation();
		if(location != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return location;
		} else if(this.checkBoatInWater()) {
			return BoatEntity.Location.IN_WATER;
		} else {
			float f = this.getNearbySlipperiness();
			if(f > 0.0F) {
				this.nearbySlipperiness = f;
				return BoatEntity.Location.ON_LAND;
			} else {
				return BoatEntity.Location.IN_AIR;
			}
		}
	}

	public float getWaterHeightBelow() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(box.maxY - this.fallVelocity);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		label39:
		for(int o = k; o < l; o++) {
			float f = 0.0F;

			for(int p = i; p < j; p++) {
				for(int q = m; q < n; q++) {
					mutable.set(p, o, q);
					FluidState fluidState = this.getWorld().getFluidState(mutable);
					if(fluidState.isIn(FluidTags.WATER)) {
						f = Math.max(f, fluidState.getHeight(this.getWorld(), mutable));
					}

					if(f >= 1.0F) {
						continue label39;
					}
				}
			}

			if(f < 1.0F) {
				return (float) mutable.getY() + f;
			}
		}

		return (float) (l + 1);
	}

	public float getNearbySlipperiness() {
		Box box = this.getBoundingBox();
		Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
		int i = MathHelper.floor(box2.minX) - 1;
		int j = MathHelper.ceil(box2.maxX) + 1;
		int k = MathHelper.floor(box2.minY) - 1;
		int l = MathHelper.ceil(box2.maxY) + 1;
		int m = MathHelper.floor(box2.minZ) - 1;
		int n = MathHelper.ceil(box2.maxZ) + 1;
		VoxelShape voxelShape = VoxelShapes.cuboid(box2);
		float f = 0.0F;
		int o = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for(int p = i; p < j; p++) {
			for(int q = m; q < n; q++) {
				int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
				if(r != 2) {
					for(int s = k; s < l; s++) {
						if(r <= 0 || s != k && s != l - 1) {
							mutable.set(p, s, q);
							BlockState blockState = this.getWorld().getBlockState(mutable);
							if(!(blockState.getBlock() instanceof LilyPadBlock)
									&& VoxelShapes.matchesAnywhere(
									blockState.getCollisionShape(this.getWorld(), mutable).offset((double) p, (double) s, (double) q), voxelShape, BooleanBiFunction.AND
							)) {
								f += blockState.getBlock().getSlipperiness();
								o++;
							}
						}
					}
				}
			}
		}

		return f / (float) o;
	}

	private boolean checkBoatInWater() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.minY + 0.001);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;
		this.waterLevel = -Double.MAX_VALUE;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for(int o = i; o < j; o++) {
			for(int p = k; p < l; p++) {
				for(int q = m; q < n; q++) {
					mutable.set(o, p, q);
					FluidState fluidState = this.getWorld().getFluidState(mutable);
					if(fluidState.isIn(FluidTags.WATER)) {
						float f = (float) p + fluidState.getHeight(this.getWorld(), mutable);
						this.waterLevel = Math.max((double) f, this.waterLevel);
						bl |= box.minY < (double) f;
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private BoatEntity.Location getUnderWaterLocation() {
		Box box = this.getBoundingBox();
		double d = box.maxY + 0.001;
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(d);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for(int o = i; o < j; o++) {
			for(int p = k; p < l; p++) {
				for(int q = m; q < n; q++) {
					mutable.set(o, p, q);
					FluidState fluidState = this.getWorld().getFluidState(mutable);
					if(fluidState.isIn(FluidTags.WATER) && d < (double) ((float) mutable.getY() + fluidState.getHeight(this.getWorld(), mutable))) {
						if(!fluidState.isStill()) {
							return BoatEntity.Location.UNDER_FLOWING_WATER;
						}

						bl = true;
					}
				}
			}
		}

		return bl ? BoatEntity.Location.UNDER_WATER : null;
	}

	private void updateVelocity() {
		double e = this.hasNoGravity() ? 0.0 : -0.04F;
		double f = 0.0;
		float velocityDecay = 0.05F;
		if(this.lastLocation == BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.ON_LAND) {
			this.waterLevel = this.getBodyY(1.0);
			this.setPosition(this.getX(), (double) (this.getWaterHeightBelow() - this.getHeight()) + 0.101, this.getZ());
			this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
			this.fallVelocity = 0.0;
			this.location = BoatEntity.Location.IN_WATER;
		} else {
			if(this.location == BoatEntity.Location.IN_WATER) {
				f = 0.01F;//(this.waterLevel - this.getY()) / (double) this.getHeight();
				velocityDecay = 0.45F;
			} else if(this.location == BoatEntity.Location.UNDER_FLOWING_WATER) {
				e = -7.0E-4;
				velocityDecay = 0.9F;
			} else if(this.location == BoatEntity.Location.UNDER_WATER) {
				f = 0.01F;
				velocityDecay = 0.45F;
			} else if(this.location == BoatEntity.Location.IN_AIR) {
				velocityDecay = this.pressingForward ? 1f : Math.min(0.90f, this.nearbySlipperiness * 1.5f);
			} else if(this.location == BoatEntity.Location.ON_LAND) {
				velocityDecay = this.pressingForward ? 1f : Math.min(0.90f, this.nearbySlipperiness * 1.5f);//Math.min(0.99f,this.nearbySlipperiness*1.5f);
				if(this.getControllingPassenger() instanceof PlayerEntity) {
					this.nearbySlipperiness /= 2.0F;
				}
			}

			Vec3d vec3d = this.getVelocity();
			this.setVelocity(
					MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * this.forward,
					vec3d.y + (e * Math.min(1 + heightSpeedCap, 5)),
					MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * this.forward
			);
			//this.setVelocity(MathHelper.clamp(vec3d.x * (double) velocityDecay, -bound, bound), vec3d.y + e, MathHelper.clamp(vec3d.z * (double) velocityDecay, -bound, bound));
			this.yawVelocity = (this.yawVelocity * velocityDecay) / 2f;
			this.forward = (this.forward * velocityDecay);
			if(f > 0.0) {
				Vec3d vec3d2 = this.getVelocity();
				this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75, vec3d2.z);
			}
		}
	}

	private void updatePaddles() {
		if(this.hasPassengers()) {
			float f = 0.0F;
			if(this.pressingLeft) {
				this.yawVelocity -= 3;
			}

			if(this.pressingRight) {
				this.yawVelocity += 3;
			}

			if(this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
				//	f += 0.2F;
			}
			var cap = 1.8f + Math.min(heightSpeedCap, 30f);
			this.setYaw(this.getYaw() + this.yawVelocity);
			if(this.pressingForward && this.forward < cap) {
				f += 0.01F;
				this.forward += 0.02f;
			} else if(this.pressingBack && this.forward > -(cap * 0.8f)) {
				f -= 0.01F;
				this.forward -= 0.02f;
			} //else if(this.forward > 0)
			//	this.forward = Math.max(0, this.forward - 0.1f);
			//else if(this.forward < 0)
			//	this.forward = Math.min(0, this.forward + 0.1f);
			if(this.forward != 0) {
			/*	this.setVelocity(
						MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * this.forward,
						0.0,
						MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * this.forward
				);*/
			}
			/*this.setVelocity(
					this.getVelocity()
							.add(
									MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * f,
									0.0,
									MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * f
							)
			);*/
		}
	}

	protected float getPassengerHorizontalOffset() {
		return 0.0F;
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		if(this.hasPassenger(passenger)) {
			float f = this.getPassengerHorizontalOffset();
			float g = (float) ((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());
			int i = this.getPassengerList().indexOf(passenger);
			if(i == dataTracker.get(CONTROLLER_POS))
				f = -1F;


			Vec3d vec3d = new Vec3d((double) f, 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			positionUpdater.accept(passenger, this.getX() + vec3d.x, this.getY() + (double) g, this.getZ() + vec3d.z);
			passenger.setYaw(passenger.getYaw() + this.yawVelocity);
			passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
			this.copyEntityData(passenger);
			if(passenger instanceof AnimalEntity && this.getPassengerList().size() == this.getMaxPassengers()) {
				int j = passenger.getId() % 2 == 0 ? 90 : 270;
				passenger.setBodyYaw(((AnimalEntity) passenger).bodyYaw + (float) j);
				passenger.setHeadYaw(passenger.getHeadYaw() + (float) j);
			}
		}
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Vec3d vec3d = getPassengerDismountOffset((double) (this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO), (double) passenger.getWidth(), passenger.getYaw());
		double d = this.getX() + vec3d.x;
		double e = this.getZ() + vec3d.z;
		BlockPos blockPos = BlockPos.ofFloored(d, this.getBoundingBox().maxY, e);
		BlockPos blockPos2 = blockPos.down();
		if(!this.getWorld().isWater(blockPos2)) {
			List<Vec3d> list = Lists.<Vec3d>newArrayList();
			double f = this.getWorld().getDismountHeight(blockPos);
			if(Dismounting.canDismountInBlock(f)) {
				list.add(new Vec3d(d, (double) blockPos.getY() + f, e));
			}

			double g = this.getWorld().getDismountHeight(blockPos2);
			if(Dismounting.canDismountInBlock(g)) {
				list.add(new Vec3d(d, (double) blockPos2.getY() + g, e));
			}

			for(EntityPose entityPose : passenger.getPoses()) {
				for(Vec3d vec3d2 : list) {
					if(Dismounting.canPlaceEntityAt(this.getWorld(), vec3d2, passenger, entityPose)) {
						passenger.setPose(entityPose);
						return vec3d2;
					}
				}
			}
		}

		return super.updatePassengerForDismount(passenger);
	}

	protected void copyEntityData(Entity entity) {
		entity.setBodyYaw(this.getYaw());
		float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
		float g = MathHelper.clamp(f, -105.0F, 105.0F);
		entity.prevYaw += g - f;
		entity.setYaw(entity.getYaw() + g - f);
		entity.setHeadYaw(entity.getYaw());
	}

	@Override
	public void onPassengerLookAround(Entity passenger) {
		this.copyEntityData(passenger);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		if(player.shouldCancelInteraction())
			return ActionResult.PASS;
		if(this.ticksUnderwater < 60.0F) {
			if(!this.getWorld().isClient) {
				float entityYaw = (float) Math.toRadians(-this.getYaw());

				double localZ = hitPos.x * Math.sin(entityYaw) + hitPos.z * Math.cos(entityYaw);
				if(player.startRiding(this)) {
					if(localZ < -0.3f) {
						var curr = this.dataTracker.get(CONTROLLER_POS);
						var ls = this.getPassengerList();
						if(ls.size() < 2)
							this.dataTracker.set(CONTROLLER_POS, 0);
						else {
							if(curr != 0)
								this.dataTracker.set(CONTROLLER_POS, 1);
						}
					}
					return ActionResult.CONSUME;
				}
				return ActionResult.PASS;
			} else {
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		return super.interact(player, hand);
	}

	@Override
	protected void removePassenger(Entity passenger) {

		var ind = this.getPassengerList().indexOf(passenger);
		if(!passenger.getWorld().isClient) {
			var cont = dataTracker.get(CONTROLLER_POS);
			if(ind == 0) {
				if(cont == 1)
					dataTracker.set(CONTROLLER_POS, 0);
				else
					dataTracker.set(CONTROLLER_POS, 1);
			}

		}
		super.removePassenger(passenger);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
		this.fallVelocity = this.getVelocity().y;
		if(!this.hasVehicle()) {
			if(onGround) {
				if(this.fallDistance > 3.0F) {

					this.handleFallDamage(this.fallDistance, 1.0F, this.getDamageSources().fall());
					if(this.fallDistance > 25.0F) {
						if(!this.getWorld().isClient && !this.isRemoved()) {
							this.kill();
							if(this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
								this.dropItem(this.asItem());
							}
						}
					}
					if(this.location != BoatEntity.Location.ON_LAND) {
						this.onLanding();
					}


				}

				this.onLanding();
			} else if(!this.getWorld().getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
				this.fallDistance -= (float) heightDifference;
			}
		}
	}

	public void setDamageWobbleStrength(float wobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, wobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}


	public void setDamageWobbleSide(int side) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers() && !this.isSubmergedIn(FluidTags.WATER);
	}

	protected int getMaxPassengers() {
		return 2;
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		var pos = this.dataTracker.get(CONTROLLER_POS);
		if(pos == 0)
			return this.getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : null;
		return this.getSecondPassenger() instanceof LivingEntity livingEntity ? livingEntity : null;
	}

	@Nullable
	public Entity getSecondPassenger() {
		return this.getPassengerList().size() < 2 ? null : this.getPassengerList().get(1);
	}

	public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
		this.pressingLeft = pressingLeft;
		this.pressingRight = pressingRight;
		this.pressingForward = pressingForward;
		this.pressingBack = pressingBack;
	}

	@Override
	public boolean isSubmergedInWater() {
		return this.location == BoatEntity.Location.UNDER_WATER || this.location == BoatEntity.Location.UNDER_FLOWING_WATER;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(this.asItem());
	}

	public boolean isMoving() {
		return this.getVelocity().length() > 0.1f;
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

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}
}
