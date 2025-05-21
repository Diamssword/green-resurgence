package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CraftingProvider {
	Storage<ItemVariant>[] itemStorages = new Storage[0];
	Storage<FluidVariant>[] fluidStorages = new Storage[0];

	public final CraftingProvider setInventories(Storage<ItemVariant>... storages) {
		this.itemStorages = storages;
		return this;
	}

	public final CraftingProvider setForFaction(PlayerEntity player, BlockPos pos) {
		var ls = player.getWorld().getComponent(Components.BASE_LIST);
		ls.getTerrainAt(pos).ifPresent(t -> {
			setInventories(InventoryStorage.of(player.getInventory(), null), InventoryStorage.of(t.getOwner().storage, null));
		});
		return this;
	}

	public final CraftingProvider setForTerrain(FactionZone terrain, @Nullable PlayerEntity player) {
		if (player != null)
			setInventories(InventoryStorage.of(player.getInventory(), null), InventoryStorage.of(terrain.getOwner().storage, null));
		else
			setInventories(InventoryStorage.of(terrain.getOwner().storage, null));
		return this;
	}

	public CraftingProvider setTanks(Storage<FluidVariant>... storages) {
		this.fluidStorages = storages;
		return this;
	}

	public boolean craftRecipe(SimpleRecipe recipe, PlayerEntity player) {
		var ingrs = recipe.ingredients(player);
		var complete = true;
		try (Transaction t1 = Transaction.openOuter()) {
			for (UniversalResource ingr : ingrs) {
				if (ingr.getType().isItem) {
					if (!hasItem(ingr, t1, null))
						complete = false;
				} else if (ingr.getType().isFluid) {
					if (!hasFluid(ingr, t1, null))
						complete = false;
				} else
					complete = false;
			}
			if (!complete)
				t1.abort();
			else
				t1.commit();
		}
		if (complete) {
			var res = recipe.result(player);
			if (res.getType().isItem) {
				if (!player.giveItemStack(res.asItem()))
					player.dropItem(res.asItem(), true);
			}
			return true;
		}
		return false;
	}

	public CraftingResult getRecipeStatus(SimpleRecipe recipe, @Nullable PlayerEntity player) {
		Map<UniversalResource, Boolean> status = new HashMap<>();
		var ingrs = recipe.ingredients(player);
		var complete = true;
		try (Transaction t1 = Transaction.openOuter()) {
			for (UniversalResource ingr : ingrs) {
				if (ingr.getType().isItem) {
					var d = hasItem(ingr, t1, null);
					status.put(ingr, d);
					if (!d)
						complete = false;
				} else if (ingr.getType().isFluid) {
					var d = hasFluid(ingr, t1, null);
					status.put(ingr, d);
					if (!d)
						complete = false;
				} else
					complete = false;
			}
			t1.abort();
		}
		return new CraftingResult(complete, status);

	}

	public boolean hasFluid(UniversalResource r, TransactionContext ctx, @Nullable Storage<FluidVariant> collector) {
		long missing = r.getAmount();
		for (Storage<FluidVariant> fluidStorage : fluidStorages) {

			for (Fluid stack : r.getAllFluids()) {
				var ex = fluidStorage.extract(FluidVariant.of(stack, r.extra()), missing, ctx);
				missing = missing - ex;
				if (collector != null)
					collector.insert(FluidVariant.of(stack), ex, ctx);
				if (missing <= 0)
					return true;
			}

		}
		return missing <= 0;
	}

	public boolean hasItem(UniversalResource r, TransactionContext ctx, @Nullable Storage<ItemVariant> collector) {
		long missing = r.getAmount();
		for (Storage<ItemVariant> itemStorage : itemStorages) {
			for (ItemStack stack : r.getAllStacks()) {
				var ex = itemStorage.extract(ItemVariant.of(stack), missing, ctx);
				missing = missing - ex;
				if (collector != null)
					collector.insert(ItemVariant.of(stack), ex, ctx);
				if (missing <= 0)
					return true;
			}

		}
		return missing <= 0;
	}

}
