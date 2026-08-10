package com.diamssword.greenresurgence.entities.deployable;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.systems.deployable.AbstractDeployableInstance;
import com.diamssword.greenresurgence.systems.deployable.DeployableRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeployableEntity extends Entity {
	@Nullable
	private AbstractDeployableInstance deployable;
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(DeployableEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<String> DEPLOYABLE = DataTracker.registerData(DeployableEntity.class, TrackedDataHandlerRegistry.STRING);

	public DeployableEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public DeployableEntity(String deployableID, ItemStack placer, World world, Vec3d pos) {
		super(MEntities.DEPLOYABLE, world);
		this.setStack(placer);
		this.setDeployable(deployableID);
		this.setPosition(pos);
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	protected Box calculateBoundingBox() {
		if(deployable != null) {
			var box = deployable.getMainBox();

			return box.offset(this.getPos());

		}
		return super.calculateBoundingBox();
	}

	public EntityDimensions getDimensions(EntityPose pose) {
		if(deployable != null) {
			var box = deployable.getMainBox();
			return EntityDimensions.fixed((float) box.getXLength(), (float) box.getYLength());

		}
		return super.getDimensions(pose);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
		this.getDataTracker().startTracking(DEPLOYABLE, "");
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("Item");
		this.setStack(ItemStack.fromNbt(nbtCompound));
		this.setDeployable(nbt.getString("Deployable"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if(!this.getStack().isEmpty())
			nbt.put("Item", this.getStack().writeNbt(new NbtCompound()));
		if(this.getDeployableId() != null) {
			nbt.putString("Deployable", this.getDeployableId());
		}
	}

	@Override
	public void setYaw(float yaw) {
		super.setYaw(yaw);
		this.setBoundingBox(calculateBoundingBox());
	}

	@Override
	public boolean isCollidable() {
		if(deployable != null)
			return deployable.isCollidable();
		return super.isCollidable();
	}


	public ItemStack getStack() {
		return this.getDataTracker().get(STACK);
	}

	/**
	 * Sets the item stack contained in this item entity to {@code stack}.
	 */
	public void setStack(ItemStack stack) {
		this.getDataTracker().set(STACK, stack);
	}

	public String getDeployableId() {
		return this.getDataTracker().get(DEPLOYABLE);
	}

	/**
	 * Sets the item stack contained in this item entity to {@code stack}.
	 */
	public void setDeployable(String id) {
		this.getDataTracker().set(DEPLOYABLE, id);
		this.deployable = DeployableRegistry.instantiate(getDeployableId(), getStack()).orElse(null);
		if(deployable != null)
			deployable.setEntity(this);
	}

	public @Nullable AbstractDeployableInstance getDeployable() {
		return deployable;
	}

	@Override
	public void tick() {
		if(!getWorld().isClient) {
			if(deployable == null)
				this.discard();
		}
	}

	@Override
	public boolean handleAttack(Entity attacker) {
		if(attacker instanceof PlayerEntity pl) {
			if(pl.isSneaking())
				this.discard();
		}

		return false;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		this.deployable = DeployableRegistry.instantiate(getDeployableId(), getStack()).orElse(null);
		if(deployable != null)
			deployable.setEntity(this);
		calculateDimensions();
		this.setBoundingBox(calculateBoundingBox());
	}
}
