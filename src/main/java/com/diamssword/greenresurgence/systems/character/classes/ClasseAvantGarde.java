package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.utils.TextUtils;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ClasseAvantGarde extends com.diamssword.characters.api.stats.StatsRole implements IClasseAdditionalTooltips {

	public ClasseAvantGarde(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, (l) -> Attributes.modifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "e5fc7be2-963f-4f52-afc9-35b5c851c6d1"));
		eventsRegister();
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
		if(level == 20) {
			if(!pl.giveItemStack(new ItemStack(MItems.REMOVABLE_LADDER)))
				pl.dropItem(MItems.REMOVABLE_LADDER);

		}
	}

	@Override
	public void getTextForLevel(PlayerEntity player, int palier, List<Text> lines) {
		if(palier == 2)
			lines.add(TextUtils.whiteText("Get a deployable rope"));
	}

	private void eventsRegister() {
	}
}
