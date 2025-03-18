package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.entities.BackpackEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackPackItem extends AbstractBackpackItem implements GeoItem {
    private int w,h;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    public static Supplier<Object> ProviderFunction;
    public BackPackItem(Settings settings,int w, int h) {
        super(PackSlot.Backpack, settings);
        this.w=w;
        this.h=h;
    }

    @Override
    public int inventoryWidth(ItemStack stack) {
        return w;
    }

    @Override
    public int inventoryHeight(ItemStack stack) {
        return h;
    }
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(ProviderFunction.get());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
