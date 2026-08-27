package com.diamssword.greenresurgence.systems.crafting.stonecutters;

import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IStoneCutterTypeRecipe {


	public List<UniversalResource> getResultForOutput(UniversalResource input, @Nullable PlayerEntity player);

	public default List<UniversalResource> getResultForOutput(UniversalResource input) {
		return getResultForOutput(input, null);
	}
}
