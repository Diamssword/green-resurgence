package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.utils.DamageHandling;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ThrownWeaponEntity extends PersistentProjectileEntity {
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.BYTE);
	private boolean dealtDamage;

	public ThrownWeaponEntity(EntityType<? extends ThrownWeaponEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownWeaponEntity(World world, LivingEntity owner, ItemStack stack, int loyalty) {
		super(MEntities.THROWN_WEAPON, owner, world);
		this.dataTracker.set(STACK, stack.copy());
		this.dataTracker.set(LOYALTY, (byte) Math.max(0, Math.min(loyalty, 10)));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LOYALTY, (byte) 0);
		this.dataTracker.startTracking(STACK, new ItemStack(Items.STONE));
	}

	@Override
	public void tick() {
		if(this.inGroundTime > 4) {
			this.dealtDamage = true;
		}

		Entity entity = this.getOwner();
		int i = this.dataTracker.get(LOYALTY);
		if(i > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
			if(!this.isOwnerAlive()) {
				if(!this.getWorld().isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			} else {
				this.setNoClip(true);
				Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
				this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * i, this.getZ());
				if(this.getWorld().isClient) {
					this.lastRenderY = this.getY();
				}

				double d = 0.05 * i;
				this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));

			}
		}

		super.tick();
	}

	private boolean isOwnerAlive() {
		Entity entity = this.getOwner();
		return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
	}

	@Override
	public ItemStack asItemStack() {
		return this.dataTracker.get(STACK).copy();
	}


	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		var stack = asItemStack();
		Entity entity2 = this.getOwner();
		if(stack.getItem() instanceof IEquipementItem eq && entity2 instanceof LivingEntity le) {
			DamageHandling.distantAttackWithTool(this, le, entity, this.asItemStack(), eq, this::discard);
			this.dataTracker.set(STACK, stack);
			this.dealtDamage = true;
		} else {


			float f = 8.0F;
			if(entity instanceof LivingEntity livingEntity) {
				f += EnchantmentHelper.getAttackDamage(this.asItemStack(), livingEntity.getGroup());
			}

			DamageSource damageSource = this.getDamageSources().trident(this, (Entity) (entity2 == null ? this : entity2));
			this.dealtDamage = true;
			if(entity.damage(damageSource, f)) {
				if(entity.getType() == EntityType.ENDERMAN) {
					return;
				}

				if(entity instanceof LivingEntity livingEntity2) {
					if(entity2 instanceof LivingEntity) {
						EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
						EnchantmentHelper.onTargetDamaged((LivingEntity) entity2, livingEntity2);
					}
					this.onHit(livingEntity2);
				}
			}
		}
		SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
		this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
		this.playSound(soundEvent, 1.0f, 1.0F);
	}

	@Override
	protected boolean tryPickup(PlayerEntity player) {
		return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
	}

	@Override
	protected SoundEvent getHitSound() {
		return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if(this.isOwner(player) || this.getOwner() == null) {
			super.onPlayerCollision(player);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if(nbt.contains("Weapon", NbtElement.COMPOUND_TYPE)) {
			this.dataTracker.set(STACK, ItemStack.fromNbt(nbt.getCompound("Weapon")));
			;
		}

		this.dealtDamage = nbt.getBoolean("DealtDamage");
		this.dataTracker.set(LOYALTY, nbt.getByte("Loyalty"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Weapon", dataTracker.get(STACK).writeNbt(new NbtCompound()));
		nbt.putBoolean("DealtDamage", this.dealtDamage);
		nbt.putByte("loyalty", dataTracker.get(LOYALTY));
	}

	@Override
	public void age() {
		int i = this.dataTracker.get(LOYALTY);
		if(this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
			super.age();
		}
	}

	@Override
	protected float getDragInWater() {
		return 0.99F;
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}
}
