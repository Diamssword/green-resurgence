package com.diamssword.greenresurgence.systems.attributs;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Attributes {

    public static final Map<Identifier, EntityAttribute> plAttributes = new HashMap<>();
    public static final EntityAttribute MAX_SHIELD = register("max_shield", new ClampedEntityAttribute("attribute.name.green_resurgence.max_shield", 20, 0.0D, 1024.0D).setTracked(true));
    public static final EntityAttribute MAX_ENERGY = register("max_energy", new ClampedEntityAttribute("attribute.name.green_resurgence.max_energy", 100.0D, 0.0D, 1024.0D).setTracked(true));

    private static EntityAttribute register(String id, EntityAttribute attr) {
        plAttributes.put(GreenResurgence.asRessource(id), attr);
        return attr;
    }

    public static void init() {
        plAttributes.forEach((a, b) -> Registry.register(Registries.ATTRIBUTE, a, b));
    }
}
