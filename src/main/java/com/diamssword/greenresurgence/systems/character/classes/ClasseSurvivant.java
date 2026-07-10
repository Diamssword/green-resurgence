package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ClasseSurvivant extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseSurvivant(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(Attributes.MAX_SHIELD, (l) -> Attributes.modifier(Attributes.MAX_SHIELD, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "fc982ba9-635a-46a2-9e54-5b919a280ea1"));
		addGlobalModifier(Attributes.ENERGY_RATE, (l) -> Attributes.modifier(Attributes.ENERGY_RATE, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "8b00b183-ca21-4988-822b-6b0ee7d20607"));
		eventsRegister();
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
		if(level == 20)
			if(!pl.giveItemStack(new ItemStack(MItems.REMOVABLE_LADDER)))
				pl.dropItem(MItems.REMOVABLE_LADDER);
	}

	private void eventsRegister() {
	}
}
