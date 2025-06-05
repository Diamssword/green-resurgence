package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.GameRules;

public class HealthManager {
    private final float shieldHealAmount = 0.5f;
    private final float lifeHealAmount = 0.5f;
    private int shieldTickTimer;
    private int lifeTickTimer;
    private float shieldAmount = 20f;

    public HealthManager() {

    }

    public void update(PlayerEntity player) {
        boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl) {
            this.shieldTickTimer++;
            if (this.shieldTickTimer >= 10) {
                float f = Math.min(this.shieldHealAmount, 6.0F);
                healShield(f / 6.0F);
                Components.PLAYER_DATA.sync(player);
                this.shieldTickTimer = 0;
            }
        } else {
            this.shieldTickTimer = 0;
        }
    }

    public float attackShield(float amount, PlayerEntity owner) {
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
        if (nbt.contains("foodLevel", NbtElement.NUMBER_TYPE)) {
            this.shieldTickTimer = nbt.getInt("shieldTickTimer");
            this.lifeTickTimer = nbt.getInt("lifeTickTimer");
            this.shieldAmount = nbt.getFloat("shieldAmount");

        }
    }

    public float getShieldAmount() {
        return shieldAmount;
    }

    public void setShieldAmount(float shieldAmount) {
        this.shieldAmount = shieldAmount;
    }

    public void healShield(float amount) {
        this.shieldAmount = Math.min(shieldAmount + amount, 20f);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("shieldTickTimer", this.shieldTickTimer);
        nbt.putInt("lifeTickTimer", this.lifeTickTimer);
        nbt.putFloat("shieldAmount", this.shieldAmount);
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
