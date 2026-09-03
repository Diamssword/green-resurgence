package com.diamssword.greenresurgence.systems.crafting.stonecutters;

import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IStoneCutterTypeRecipe {


	public List<UniversalResource> getResultForInput(UniversalResource input, @Nullable PlayerEntity player);

	public default List<UniversalResource> getResultForInput(UniversalResource input) {
		return getResultForInput(input, null);
	}

}
