package com.diamssword.greenresurgence.systems.crafting.stonecutters;

import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConquestStoneCutterRecipe implements IStoneCutterTypeRecipe {
	@Override
	public List<UniversalResource> getResultForOutput(UniversalResource input, @Nullable PlayerEntity player) {
		return List.of();
	}
}
