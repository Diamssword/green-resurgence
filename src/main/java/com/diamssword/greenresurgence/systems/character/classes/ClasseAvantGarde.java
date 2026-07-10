package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class ClasseAvantGarde extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseAvantGarde(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, (l) -> Attributes.modifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "e5fc7be2-963f-4f52-afc9-35b5c851c6d1"));
		eventsRegister();
	}


	private void eventsRegister() {
	}
}
