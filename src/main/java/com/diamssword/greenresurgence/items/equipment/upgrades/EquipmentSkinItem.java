package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EquipmentSkinItem extends EquipmentUpgradeItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public static Function<Boolean, Object> ProviderFunction;
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public EquipmentSkinItem() {
		super("", Equipments.P_SKIN, 0);
	}

	public String getSkin(ItemStack stack) {
		if(stack.hasNbt()) {
			return stack.getNbt().getString("skin");
		}
		return "";
	}

	@Override
	public boolean canBeApplied(IEquipmentDef equipment, ItemStack stack) {

		return true;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player) {
		return null;
	}

	@Override
	public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, InteractType interaction, HitResult context) {

	}

	@Override
	public Text getName(ItemStack stack) {
		String skin = getSkin(stack);
		if(skin.isEmpty()) {
			skin = "empty";
		}
		return Text.translatable(this.getTranslationKey(stack)).append(Text.literal(" (")).append(Text.translatable(Util.createTranslationKey("skin", GreenResurgence.asRessource(skin)))).append(Text.literal(")"));
	}

	@Override
	public void onTick(Entity parent) {

	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(ProviderFunction.apply(false));
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return renderProvider;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
