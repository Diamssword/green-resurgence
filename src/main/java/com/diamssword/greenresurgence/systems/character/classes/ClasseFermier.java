package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class ClasseFermier extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseFermier(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(Attributes.MAX_ENERGY, (l) -> Attributes.modifier(Attributes.MAX_ENERGY, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, "3953a788-8ca2-4f88-a40a-8114a875fd07"));
		eventsRegister();
	}

	private void eventsRegister() {
	}
}
