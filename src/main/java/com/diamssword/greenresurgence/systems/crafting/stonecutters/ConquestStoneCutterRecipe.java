package com.diamssword.greenresurgence.systems.crafting.stonecutters;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConquestStoneCutterRecipe implements IStoneCutterTypeRecipe {
	public final static Map<String, Integer> variants = new HashMap<>();

	static {
		variants.put("small_arch", 1);
		variants.put("small_arch_half", 2);
		variants.put("two_meter_arch", 2);
		variants.put("two_meter_arch_half", 4);
		variants.put("arrowslit", 1);
		variants.put("small_window", 1);
		variants.put("small_window_half", 4);
		variants.put("balustrade", 1);
		variants.put("capital", 1);
		variants.put("sphere", 1);
		variants.put("slab", 8);
		variants.put("quarter_slab", 4);
		variants.put("corner_slab", 2);
		variants.put("eighth_slab", 8);
		variants.put("vertical_corner_slab", 2);
		variants.put("vertical_slab", 5);
		variants.put("vertical_corner", 5);
		variants.put("vertical_quarter", 5);
		variants.put("stairs", 1);
		variants.put("wall", 2);
		variants.put("pillar", 4);
	}

	@Override
	public List<UniversalResource> getResultForInput(UniversalResource input, @Nullable PlayerEntity player) {
		var st = input.asItem();
		if(!st.isEmpty()) {
			var id = Registries.ITEM.getId(st.getItem());
			if(id.getNamespace().equals("minecraft") || id.getNamespace().equals("conquest")) {
				var m = variants.keySet().stream().filter(p -> id.getPath().endsWith(p)).toList();
				if(!m.isEmpty()) {
					var variant = m.get(0);
					for(String s : m) {
						if(s.length() > variant.length())
							variant = s;
					}
					return makeForVariant(input, variant);
				} else if(st.getItem() instanceof BlockItem be && be.getBlock().getDefaultState().isFullCube(GreenResurgence.clientHelper.getMainWorld(), BlockPos.ORIGIN)) {
					var ls = new ArrayList<UniversalResource>();
					for(var variant : variants.entrySet()) {
						var id1 = new Identifier("conquest", id.getPath() + "_" + variant.getKey());
						var item1 = Registries.ITEM.get(id1);
						if(item1 != Items.AIR && item1 != st.getItem()) {
							ls.add(UniversalResource.fromItem(new ItemStack(item1, variant.getValue())));
						}
					}
					return ls;
				}
			}
		}
		return List.of();
	}

	private List<UniversalResource> makeForVariant(UniversalResource input, String variant) {
		var baseID = input.getID().getPath().substring(0, input.getID().getPath().length() - variant.length());
		var cost = variants.get(variant);
		var ls = new ArrayList<UniversalResource>();
		for(var v1 : variants.entrySet()) {
			var c = v1.getValue() / cost;
			if(!v1.getKey().equals(variant) && c > 0) {
				var id1 = new Identifier("conquest", baseID + v1.getKey());
				var item1 = Registries.ITEM.get(id1);
				if(item1 != Items.AIR) {
					ls.add(UniversalResource.fromItem(new ItemStack(item1, c)));
				}
			}
		}
		return ls;
	}

}
