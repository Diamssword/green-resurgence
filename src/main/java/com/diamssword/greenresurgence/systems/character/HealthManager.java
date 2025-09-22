package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.attributs.AttributeModifiers;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.GameRules;

public class HealthManager {
	private final float shieldHealAmount = 1f;
	private int shieldTickTimer;
	private int energyTickTimer;
	private boolean energyBurnout;
	private double shieldAmount = 20;
	private double energyAmount = 100;
	public final PlayerEntity player;
	private int refreshTicks = 5;

	public HealthManager(PlayerEntity pl) {
		this.player = pl;
	}

	private void speedLogic() {
		if(player.isSprinting()) {
			this.energyTickTimer = 0;
			if(this.energyAmount > 0 && !energyBurnout) {
				energyAmount = Math.max(energyAmount - 1f, 0);
				markDirty();
			} else {
				player.setSprinting(false);
				this.energyBurnout = true;
			}
		} else {
			this.energyTickTimer++;
			if(this.energyTickTimer >= 5) {
				double f = 5.0 * getEnergyRateAmount();
				var old = this.energyAmount;
				if(player.isSneaking()) {f = f * 1.5;}
				this.energyAmount = Math.min(energyAmount + f, getMaxEnergyAmount());
				if(energyBurnout && energyAmount / getMaxEnergyAmount() >= 0.1) {energyBurnout = false;}
				if(old != this.energyAmount) {markDirty();}
				this.energyTickTimer = 0;
			}
		}
	}

	public boolean isEnergyBurnout() {
		return energyBurnout;
	}

	private void markDirty() {
		if(refreshTicks < 0) {refreshTicks = 20;}
	}

	public void update() {
		boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if(bl) {
			this.shieldTickTimer++;
			if(this.shieldTickTimer >= 10) {
				float f = Math.min(this.shieldHealAmount, 6.0F);
				var old = shieldAmount;
				healShield(f / 6.0F);
				if(old != shieldAmount) {markDirty();}
				this.shieldTickTimer = 0;
			}
		} else {
			this.shieldTickTimer = 0;
		}
		speedLogic();
		if(refreshTicks > -1) {refreshTicks--;}
		if(refreshTicks == 0 && !player.getWorld().isClient) {PlayerData.syncHUD(player);}


	}

	public void onRespawn(boolean wasAlive) {

		player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(50);
		player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(AttributeModifiers.BASE_SPEED_ID, GreenResurgence.ID + ".base_speed_modifier", 0.2, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		if(!wasAlive) {
			player.setHealth(player.getMaxHealth());
			this.energyAmount = this.getMaxEnergyAmount();
			this.shieldAmount = this.getMaxShieldAmount();
		}
	}

	public double attackShield(double amount, PlayerEntity owner) {
		if(owner.getWorld().isClient) {return 0;}
		var m = this.shieldAmount - amount;
		this.shieldAmount = Math.max(m, 0);
		this.shieldTickTimer = 0;
		PlayerData.syncHUD(owner);
		if(m < 0) {return m;}
		return 0;
	}

	public void readNbt(NbtCompound nbt) {
		this.shieldTickTimer = nbt.getInt("shieldTickTimer");
		this.shieldAmount = nbt.getDouble("shieldAmount");
		this.energyTickTimer = nbt.getInt("energyTickTimer");
		this.energyAmount = nbt.getDouble("energyAmount");
	}

	public double getShieldAmount() {
		return shieldAmount;
	}

	public double getHealthAmount() {
		return player.getHealth();
	}

	public double getMaxHealthAmount() {
		return player.getMaxHealth();
	}

	public double getEnergyAmount() {
		return energyAmount;
	}

	public double getMaxShieldAmount() {
		return player.getAttributeValue(Attributes.MAX_SHIELD);
	}

	public double getEnergyRateAmount() {
		return player.getAttributeValue(Attributes.ENERGY_RATE);
	}

	public double getMaxEnergyAmount() {
		return player.getAttributeValue(Attributes.MAX_ENERGY);
	}

	public void setShieldAmount(double shieldAmount) {
		this.shieldAmount = shieldAmount;
	}

	public void setEnergyAmount(double energyAmount) {
		this.energyAmount = energyAmount;
	}

	public void healShield(double amount) {
		this.shieldAmount = Math.min(shieldAmount + amount, getMaxShieldAmount());
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putInt("shieldTickTimer", this.shieldTickTimer);
		nbt.putDouble("shieldAmount", this.shieldAmount);
		nbt.putDouble("energyAmount", energyAmount);
		nbt.putDouble("energyTickTimer", this.energyTickTimer);
	}

	public static boolean damageByPassShield(DamageSource source) {
		return source.isOf(DamageTypes.CACTUS) || source.isOf(DamageTypes.FREEZE) ||
				source.isOf(DamageTypes.BAD_RESPAWN_POINT) || source.isOf(DamageTypes.DROWN) ||
				source.isOf(DamageTypes.CRAMMING) || source.isOf(DamageTypes.DRAGON_BREATH) ||
				source.isOf(DamageTypes.FALL) || source.isOf(DamageTypes.FLY_INTO_WALL) ||
				source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) ||
				source.isOf(DamageTypes.LAVA) || source.isOf(DamageTypes.WITHER) || source.isOf(DamageTypes.WITHER_SKULL);
	}
}
