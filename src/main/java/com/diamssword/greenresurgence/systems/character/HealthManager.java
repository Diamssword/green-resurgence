package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.GameRules;

public class HealthManager {
    private final float shieldHealAmount = 1f;
    private final float energyHealAmount = 0.5f;
    private int shieldTickTimer;
    private int energyTickTimer;
    private double shieldAmount = 20;
    private double energyAmount = 100;
    public final PlayerEntity player;

    public HealthManager(PlayerEntity pl) {
        this.player = pl;
    }

    public void update() {
        var needSync = false;
        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl) {
            this.shieldTickTimer++;
            if (this.shieldTickTimer >= 10) {
                float f = Math.min(this.shieldHealAmount, 6.0F);
                healShield(f / 6.0F);
                needSync = true;
                this.shieldTickTimer = 0;
            }
        } else {
            this.shieldTickTimer = 0;
        }
        if (player.isSprinting()) {
            this.energyTickTimer = 0;
            if (this.energyAmount > 0) {
                energyAmount -= 0.5f;
                needSync = true;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1));
            }
        } else {
            this.energyTickTimer++;
            if (this.energyTickTimer >= 5) {
                float f = Math.min(this.energyHealAmount, 6.0F);
                this.energyAmount = Math.min(energyAmount + f, getMaxEnergyAmount());
                needSync = true;
                this.energyTickTimer = 0;
            }
        }
        if (needSync)
            Components.PLAYER_DATA.sync(player);

    }

    public double attackShield(double amount, PlayerEntity owner) {
        if (owner.getWorld().isClient)
            return 0;
        var m = this.shieldAmount - amount;
        this.shieldAmount = Math.max(m, 0);
        this.shieldTickTimer = 0;
        Components.PLAYER_DATA.sync(owner);
        if (m < 0)
            return m;
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

    public double getEnergyAmount() {
        return energyAmount;
    }

    public double getMaxShieldAmount() {
        return player.getAttributeValue(Attributes.MAX_SHIELD);
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
