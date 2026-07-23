package com.diamssword.greenresurgence.systems.character.classes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public interface IClasseAdditionalTooltips {
	public void getTextForLevel(PlayerEntity player, int palier, List<Text> lines);
}
