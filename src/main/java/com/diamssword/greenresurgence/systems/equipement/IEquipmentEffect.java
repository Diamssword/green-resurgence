package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public interface IEquipmentEffect {
	void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx);

	void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction);

	default List<TagKey<Item>> getTags(UpgradeActionContext ctx) {
		return List.of();
	}

	default void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		Multimap<EntityAttribute, EntityAttributeModifier> map = ArrayListMultimap.create();
		getAttributeModifiers(map, slot, ctx);
		if(!map.isEmpty()) {

			for(Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entries()) {
				EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier) entry.getValue();
				double d = entityAttributeModifier.getValue();
				double e;
				PlayerEntity player = GreenResurgence.clientHelper.getPlayer();
				var bl = false;
				if(player != null && ctx.context != UpgradeActionContext.ItemContext.UPGRADE) {
					if(entry.getKey() == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
						d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
						bl = true;
					} else if(entry.getKey() == EntityAttributes.GENERIC_ATTACK_SPEED) {
						d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
						bl = true;
					}
					if(bl)
						tooltip.add(Text.translatable("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(d), Text.translatable((entry.getKey()).getTranslationKey())).formatted(Formatting.DARK_GREEN));
				}
				if(!bl) {
					if(entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE
							|| entityAttributeModifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
						e = d * 100.0;
					} else if(((EntityAttribute) entry.getKey()).equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
						e = d * 10.0;
					} else {
						e = d;
					}
					if(d > 0.0) {
						tooltip.add(
								Text.translatable(
												"attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
												MODIFIER_FORMAT.format(e),
												Text.translatable(((EntityAttribute) entry.getKey()).getTranslationKey())
										)
										.formatted(Formatting.BLUE)
						);
					} else if(d < 0.0) {
						e *= -1.0;
						tooltip.add(
								Text.translatable(
												"attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
												MODIFIER_FORMAT.format(e),
												Text.translatable(((EntityAttribute) entry.getKey()).getTranslationKey())
										)
										.formatted(Formatting.RED)
						);
					}
				}
			}
		}
	}

}
