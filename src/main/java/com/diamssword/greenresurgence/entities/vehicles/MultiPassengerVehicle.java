package com.diamssword.greenresurgence.entities.vehicles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public abstract class MultiPassengerVehicle extends VehicleWithInventory {
	private static final TrackedData<NbtCompound> CONTROLLER_POS = DataTracker.registerData(MultiPassengerVehicle.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	protected final Map<Integer, LivingEntity> passengers = new HashMap<>();

	protected MultiPassengerVehicle(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CONTROLLER_POS, new NbtCompound());
	}

	public void syncPassengers() {
		var t1 = new NbtCompound();
		passengers.forEach((k, v) -> {
			t1.putUuid(k + "", v.getUuid());
		});
		this.dataTracker.set(CONTROLLER_POS, t1);
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if(CONTROLLER_POS.equals(data)) {
			var tag = this.dataTracker.get(CONTROLLER_POS);
			passengers.clear();
			for(String key : tag.getKeys()) {
				try {
					var p = Integer.parseInt(key);
					for(Entity entity : getPassengerList()) {
						if(entity instanceof LivingEntity lv && entity.getUuid().equals(tag.getUuid(key))) {
							passengers.put(p, lv);
							break;
						}
					}
				} catch(Exception e) {}
			}

		}
	}

	/**
	 * determine offset from the center of the vehicle for the passenger at i position,+X is front of the vehicle.
	 *
	 * @param passengerIndex
	 * @return
	 */
	protected abstract Vec3d getPassengerOffset(int passengerIndex);

	@Override
	public double getMountedHeightOffset() {
		return getPassengerOffset(0).y;
	}

	public OptionalInt getIndexOfPassenger(Entity entity) {
		for(Integer i : passengers.keySet()) {
			if(passengers.get(i) == entity)
				return OptionalInt.of(i);
		}
		return OptionalInt.empty();
	}


	@Override
	public void stopRiding() {
		super.stopRiding();
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		var p = getIndexOfPassenger(passenger);
		p.ifPresent(passengers::remove);
		if(!passenger.getWorld().isClient)
			syncPassengers();
	}

	@Override
	public @Nullable LivingEntity getControllingPassenger() {
		return passengers.get(0);
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		if(passenger instanceof LivingEntity p) {
			passengers.putIfAbsent(0, p);
			if(!passenger.getWorld().isClient)
				syncPassengers();
		}

	}

	public boolean hasPassenger(int index) {
		var p = passengers.get(index);
		return p != null;
	}

	/**
	 * will eject current passenger at the same index
	 */
	protected void setPassengerIndex(LivingEntity passenger, int index) {
		//	super.addPassenger(passenger);
		getIndexOfPassenger(passenger).ifPresent(passengers::remove);
		var old = passengers.get(index);

		if(old != null && old != passenger)
			old.dismountVehicle();
		passengers.put(index, passenger);
		if(!passenger.getWorld().isClient)
			syncPassengers();


	}

	/**
	 * @param player  the player interacting with the vehicle
	 * @param zHitPos the hit position already rotated to match vehicle front or back,negative if behind center
	 * @return the seat index to put the player on based on interaction hit.
	 */
	public abstract int getPassengerIndexForInteraction(PlayerEntity player, double zHitPos);

	abstract public int getMaxPassengers();

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers();
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		if(this.hasPassenger(passenger)) {
			int i = this.getIndexOfPassenger(passenger).orElse(-1);
			Vec3d vec3d = this.getPassengerOffset(i).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			positionUpdater.accept(passenger, this.getX() + vec3d.x, this.getY() + vec3d.y + passenger.getHeightOffset(), this.getZ() + vec3d.z);
			//passenger.setYaw(passenger.getYaw() + this.yawVelocity);
			//passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
			//	this.copyEntityData(passenger);

		}
	}


	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		var res = interactWithItem(player, player.getStackInHand(hand), hand);
		if(res != ActionResult.PASS)
			return res;
		if(!this.getWorld().isClient) {
			float entityYaw = (float) Math.toRadians(-this.getYaw());

			double localZ = hitPos.x * Math.sin(entityYaw) + hitPos.z * Math.cos(entityYaw);
			int index = getPassengerIndexForInteraction(player, localZ);
			if(player.shouldCancelInteraction() && hasChest()) {
				player.openHandledScreen(this);
				this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
				player.swingHand(hand);
				return !player.getWorld().isClient ? ActionResult.CONSUME : ActionResult.SUCCESS;
			} else if(!hasPassenger(index)) {
				if(player.startRiding(this)) {
					setPassengerIndex(player, index);
				}
			}
			return ActionResult.PASS;
		} else {
			return ActionResult.SUCCESS;
		}

	}

}
