package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.materials.Materials;
import com.diamssword.greenresurgence.utils.TextUtils;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ClasseMedecin extends com.diamssword.characters.api.stats.StatsRole implements IClasseAdditionalTooltips {

	public ClasseMedecin(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
		if(level == 10)
			giveItems(pl, new ItemStack(MItems.SATCHEL), new ItemStack(Materials.medicine.get("paracetamol").asItem(), 5));
		if(level == 20)
			giveItems(pl, new ItemStack(MItems.SATCHEL), new ItemStack(Materials.medicine.get("paracetamol").asItem(), 10));
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
			lines.add(TextUtils.whiteText("Get a satchel and 5 Dollypranes"));
		if(palier == 2)
			lines.add(TextUtils.whiteText("Get a satchel and 10 Dollypranes"));
	}
}
