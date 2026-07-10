package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class ClasseKuisto extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseKuisto(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, (l) -> Attributes.modifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, l * 0.01f, EntityAttributeModifier.Operation.ADDITION, "2e1a39f3-4ab9-46b3-aa6d-092f09a45eef"));
	}
}
