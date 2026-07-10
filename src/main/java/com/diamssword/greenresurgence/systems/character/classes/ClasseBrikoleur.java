package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class ClasseBrikoleur extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseBrikoleur(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {

		addGlobalModifier(EntityAttributes.GENERIC_MAX_HEALTH, (l) -> Attributes.modifier(EntityAttributes.GENERIC_MAX_HEALTH, l * 0.0025f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "77d3223a-fc51-4e58-be07-a43778a856ba"));

	}

}
