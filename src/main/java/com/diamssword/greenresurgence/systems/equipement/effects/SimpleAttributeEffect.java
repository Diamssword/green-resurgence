package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentEffect;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.UUID;
import java.util.function.Function;

public class SimpleAttributeEffect implements IEquipmentEffect {
	private final String id;
	private final EntityAttribute attribute;
	private final UUID modifierID;
	private final EntityAttributeModifier.Operation operation;
	private final Function<Float, Float> levelBinder;
	private boolean mainOnly = true;

	public SimpleAttributeEffect(String id, EntityAttribute attribute, UUID modifierID, EntityAttributeModifier.Operation operation, Function<Float, Float> levelBinder) {
		this.id = id;
		this.attribute = attribute;
		this.modifierID = modifierID;
		this.operation = operation;
		this.levelBinder = levelBinder;
	}

	public SimpleAttributeEffect workOnAllSlot() {
		mainOnly = false;
		return this;
	}

	public static SimpleAttributeEffect addition(String id, EntityAttribute attribute, UUID modifierID, Function<Float, Float> levelBinder) {
		return new SimpleAttributeEffect(id, attribute, modifierID, EntityAttributeModifier.Operation.ADDITION, levelBinder);
	}

	public static SimpleAttributeEffect addition(String id, EntityAttribute attribute, UUID modifierID) {
		return new SimpleAttributeEffect(id, attribute, modifierID, EntityAttributeModifier.Operation.ADDITION, l -> l);
	}

	public static SimpleAttributeEffect multiplyBase(String id, EntityAttribute attribute, UUID modifierID, Function<Float, Float> levelBinder) {
		return new SimpleAttributeEffect(id, attribute, modifierID, EntityAttributeModifier.Operation.MULTIPLY_BASE, levelBinder);
	}

	public static SimpleAttributeEffect multiplyBase(String id, EntityAttribute attribute, UUID modifierID) {
		return new SimpleAttributeEffect(id, attribute, modifierID, EntityAttributeModifier.Operation.MULTIPLY_BASE, l -> l);
	}

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {
		if(!mainOnly || slot == AdvEquipmentSlot.MAINHAND || slot == AdvEquipmentSlot.DISPLAY) {
			var eff = ctx.getLevel(id);
			if(eff.getLevel() != 0)
				map.put(attribute, new EntityAttributeModifier(modifierID, "Weapon modifier", levelBinder.apply(eff.getLevel()), operation));
		}
	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

}
