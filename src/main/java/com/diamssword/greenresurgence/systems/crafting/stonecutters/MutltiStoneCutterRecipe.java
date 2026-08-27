package com.diamssword.greenresurgence.systems.crafting.stonecutters;

import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MutltiStoneCutterRecipe implements IStoneCutterTypeRecipe {
	private final IStoneCutterTypeRecipe[] subs;

	public MutltiStoneCutterRecipe(IStoneCutterTypeRecipe... subCutters) {
		this.subs = subCutters;
	}

	@Override
	public List<UniversalResource> getResultForOutput(UniversalResource input, @Nullable PlayerEntity player) {
		var res = new ArrayList<UniversalResource>();
		for(IStoneCutterTypeRecipe sub : subs) {
			res.addAll(sub.getResultForOutput(input, player));
		}
		return res;
	}
}
