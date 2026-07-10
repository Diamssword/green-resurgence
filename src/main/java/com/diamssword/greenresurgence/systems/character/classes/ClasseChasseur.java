package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ClasseChasseur extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseChasseur(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		this.create(2, p -> {
			p.addModifier(ReachEntityAttributes.ATTACK_RANGE, Attributes.modifier(ReachEntityAttributes.ATTACK_RANGE, 1, EntityAttributeModifier.Operation.ADDITION, "6dc3cf63-2bb9-4574-ae75-1d2272970ab6"));
			p.addModifier(ReachEntityAttributes.REACH, Attributes.modifier(ReachEntityAttributes.REACH, 1, EntityAttributeModifier.Operation.ADDITION, "915ca5b0-ef94-4eef-bada-ecb381a390e5"));
		});
		eventsRegister();
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
		if(level == 10)
			if(!pl.giveItemStack(new ItemStack(Items.BOW)))
				pl.dropItem(Items.BOW);
	}

	private void eventsRegister() {
	}
}
