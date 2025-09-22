package com.diamssword.greenresurgence.items;

import net.minecraft.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StackBasedGeckoItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public static Function<Boolean, Object> ProviderFunction;
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	private final boolean emissive;

	public StackBasedGeckoItem(Settings settings, boolean emissive) {
		super(settings);
		this.emissive = emissive;
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(ProviderFunction.apply(emissive));
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
