package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.gson.JsonObject;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ClasseFerrailleur extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseFerrailleur(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		addGlobalModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, (l) -> Attributes.modifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, l * 0.1f, EntityAttributeModifier.Operation.ADDITION, "03ec1049-14b5-4098-b283-4a7c1e970539"));
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
		pl.sendMessage(Text.literal("Si tu lis ceci, Diams a oubliée de créer l'item à te give."));
	}

	private void giveItems(PlayerEntity playerEntity, ItemStack... stacks) {
		for(ItemStack stack : stacks) {
			if(!playerEntity.giveItemStack(stack))
				playerEntity.dropItem(stack, true);
		}
	}
}
