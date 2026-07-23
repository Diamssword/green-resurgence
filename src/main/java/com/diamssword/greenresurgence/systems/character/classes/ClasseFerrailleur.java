package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.utils.TextUtils;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;

import java.util.List;

public class ClasseFerrailleur extends com.diamssword.characters.api.stats.StatsRole implements IClasseAdditionalTooltips {

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

		if(level == 10) {
			var sp = Equipments.getEquipment(Equipments.TYPE_SPIKE, "short").get().getEquipmentItem();
			var st = new ItemStack(sp);

			try {
				var nbt = NbtHelper.fromNbtProviderString("{skin: \"screwdriver_flat\",upgrades:{head: {id: \"green_resurgence:equipments/iron_head\", Count: 1b}, skin: {id: \"green_resurgence:equipments/skin_modifier\", Count: 1b, tag: {skin: \"screwdriver_flat\"}}, binding: {id: \"green_resurgence:equipments/screw_binding\", Count: 1b}, extra_head: {id: \"green_resurgence:equipments/tool_screwdriver_head_extra\", Count: 1b}, handle: {id: \"green_resurgence:equipments/bronze_handle\", Count: 1b}}}");
				st.setNbt(nbt);
				giveItems(pl, st);
			} catch(CommandSyntaxException e) {
				e.printStackTrace();
			}
		} else if(level == 20) {
			var sp = Equipments.getEquipment(Equipments.TYPE_HAMMER, "short").get().getEquipmentItem();
			var st = new ItemStack(sp);

			try {
				var nbt = NbtHelper.fromNbtProviderString("{skin: \"wrench\",upgrades:{head: {id: \"green_resurgence:equipments/iron_head\", Count: 1b}, skin: {id: \"green_resurgence:equipments/skin_modifier\", Count: 1b, tag: {skin: \"wrench\"}}, binding: {id: \"green_resurgence:equipments/screw_binding\", Count: 1b}, extra_head: {id: \"green_resurgence:equipments/tool_hammer_head_extra\", Count: 1b}, handle: {id: \"green_resurgence:equipments/bronze_handle\", Count: 1b}}}");
				st.setNbt(nbt);
				giveItems(pl, st);
			} catch(CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	private void giveItems(PlayerEntity playerEntity, ItemStack... stacks) {
		for(ItemStack stack : stacks) {
			if(!playerEntity.giveItemStack(stack))
				playerEntity.dropItem(stack, true);
		}
	}

	@Override
	public void getTextForLevel(PlayerEntity player, int palier, List<Text> lines) {
		if(palier == 0)
			lines.add(TextUtils.whiteText("Get a screwdriver"));
		if(palier == 2)
			lines.add(TextUtils.whiteText("Get a hammer"));
	}
}
