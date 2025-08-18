package com.diamssword.greenresurgence.systems.attributs;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Attributes {

	public static final Map<Identifier, EntityAttribute> plAttributes = new HashMap<>();
	public static final EntityAttribute MAX_SHIELD = register("max_shield", new ClampedEntityAttribute("attribute.name.green_resurgence.max_shield", 20, 0.0D, 1024.0D).setTracked(true));
	public static final EntityAttribute MAX_ENERGY = register("max_energy", new ClampedEntityAttribute("attribute.name.green_resurgence.max_energy", 100.0D, 0.0D, 1024.0D).setTracked(true));
	public static final EntityAttribute CRAFT_SPEED = register("craft_speed", new ClampedEntityAttribute("attribute.name.green_resurgence.craft_speed", 1D, 0.1D, 1024.0D).setTracked(true));
	public static final EntityAttribute ALCOOL_RESISTANCE = register("alcool_resistance", new ClampedEntityAttribute("attribute.name.green_resurgence.alcool_resistance", 0D, -10D, 10.0D).setTracked(true));
	public static final EntityAttribute PLAYER_KNOCKBACK = register("player_knockback", new ClampedEntityAttribute("attribute.name.green_resurgence.player_knockback", 0D, 0D, 100.0D).setTracked(true));

	private static EntityAttribute register(String id, EntityAttribute attr) {
		plAttributes.put(GreenResurgence.asRessource(id), attr);
		return attr;
	}

	public static void init() {
		plAttributes.forEach((a, b) -> Registry.register(Registries.ATTRIBUTE, a, b));
	}

	public static EntityAttributeModifier modifier(EntityAttribute attribute, UUID id, float value, EntityAttributeModifier.Operation operation) {

		return new EntityAttributeModifier(id, GreenResurgence.ID + ".role_modifier." + attribute.getTranslationKey(), value, operation);
	}

	public static EntityAttributeModifier modifier(EntityAttribute attribute, float value, EntityAttributeModifier.Operation operation) {
		var r = AttributeModifiers.BaseIdMap.get(attribute);
		if (r == null)
			throw new NullPointerException();
		return new EntityAttributeModifier(r, GreenResurgence.ID + ".role_modifier." + attribute.getTranslationKey(), value, operation);
	}
}
