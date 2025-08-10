package com.diamssword.greenresurgence.systems.attributs;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeModifiers {


	public static Map<EntityAttribute, UUID> BaseIdMap = new HashMap<>();
	public static final UUID SPEED_ID = create(EntityAttributes.GENERIC_MOVEMENT_SPEED, "8465a342-771e-4768-94b2-24870de90183");
	public static final UUID ATTACK_DAMAGE_ID = create(EntityAttributes.GENERIC_ATTACK_DAMAGE, "8c13fc62-fbd8-4885-87ea-ad2932ad51a2");
	public static final UUID HEALTH_ID = create(EntityAttributes.GENERIC_MAX_HEALTH, "d6f22edb-abe0-47a2-9c8d-4fef8d0aff61");
	public static final UUID CRAFT_SPEED_ID = create(Attributes.CRAFT_SPEED, "b1de7f04-1426-4a26-96b1-4af3b9cbfb60");
	public static final UUID ALCOOL_RESISTANCE_ID = create(Attributes.ALCOOL_RESISTANCE, "d5460c8f-b9b9-4ca3-99c4-405b0de794bf");
	public static final UUID KNOCKBACK_RESISTANCE_ID = create(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "e830b548-7c38-4f2c-8908-b1eb81ab0c07");
	public static final UUID KNOCKBACK_ID = create(Attributes.PLAYER_KNOCKBACK, "de9b26f6-fc48-4919-b744-9c3b16e22f17");

	private static UUID create(EntityAttribute attr, String uuid) {
		BaseIdMap.put(attr, UUID.fromString(uuid));
		return BaseIdMap.get(attr);
	}
}
