package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.*;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CrafterBlockEntity extends BlockEntity implements ICraftingTile<SimpleRecipe> {
	private int statusIndex = 0;
	private Consumer<CraftingResult> currentTrackedClient;
	private final Map<ServerPlayerEntity, CraftStatusTracked> currentTrackedServer = new HashMap<>();

	private record CraftStatusTracked(Integer index, SimpleRecipe recipe) {}

	private FactionZone terrain;
	private Identifier collection;
	private CraftingProvider craftProvider = new CraftingProvider();

	public CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public CrafterBlockEntity setCollection(Identifier collectionID) {
		this.collection = collectionID;
		return this;
	}

	@Nullable
	public Identifier getCollection() {
		return this.collection;
	}

	public void onClose(PlayerEntity player) {
		currentTrackedServer.remove(player);
		currentTrackedClient = null;
	}

	public static void tick(World world, BlockPos pos, BlockState state, CrafterBlockEntity blockEntity) {
		if(world.getTime() % 10 == 0) {
			var bl = world.getComponent(Components.BASE_LIST);
			bl.getTerrainAt(pos).ifPresent(p -> blockEntity.terrain = p);

		}
		if(world.getTime() % 20 == 0) {
			blockEntity.currentTrackedServer.forEach((k, va) -> {
				Channels.MAIN.serverHandle(k).send(new CraftPackets.SendCraftStatus(va.index, pos, blockEntity.getCraftingProvider(k).getRecipeStatus(va.recipe, k)));
			});
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	@Override
	public boolean isCraftAllowed(Identifier recipeID, @Nullable PlayerEntity player) {
		return collection != null && collection.equals(RecipeCollection.getCollectionFromFullId(recipeID));
	}

	@Override
	public boolean tryCraft(Identifier recipeID, @Nullable PlayerEntity player) {
		if(!world.isClient() && isCraftAllowed(recipeID, player)) {
			var recipe = recipeFromId(recipeID);
			var prov = getCraftingProvider(player);
			if(recipe.isPresent()) {
				var b = prov.craftRecipe(recipe.get(), player);
				if(b && player instanceof ServerPlayerEntity sp) {
					var track = currentTrackedServer.get(sp);
					Channels.MAIN.serverHandle(player).send(new CraftPackets.SendCraftStatus(track.index, pos, prov.getRecipeStatus(track.recipe, player)));

				}
				return b;
			}
			return false;
		}
		CraftPackets.sendCraftRequest(recipeID, pos);
		return true;
	}

	@Override
	public CraftingProvider getCraftingProvider(@Nullable PlayerEntity player) {
		return craftProvider.setForTerrain(terrain, player);
	}

	@Override
	public Optional<SimpleRecipe> recipeFromId(Identifier id) {
		return Recipes.getRecipe(id);
	}

	@Override
	public void handleStatusRequest(int requestIndex, Identifier recipeID, ServerPlayerEntity playerEntity) {
		recipeFromId(recipeID).ifPresent(re -> {
			currentTrackedServer.put(playerEntity, new CraftStatusTracked(requestIndex, re));
			Channels.MAIN.serverHandle(playerEntity).send(new CraftPackets.SendCraftStatus(requestIndex, pos, getCraftingProvider(playerEntity).getRecipeStatus(re, playerEntity)));
		});


	}

	@Override
	public void receiveStatus(int requestIndex, CraftingResult result) {
		if(requestIndex == statusIndex && currentTrackedClient != null) {
			currentTrackedClient.accept(result);
		}
	}


	@Override
	public void requestStatus(Identifier recipeID, PlayerEntity player, Consumer<CraftingResult> result) {
		statusIndex++;
		currentTrackedClient = result;
		Channels.MAIN.clientHandle().send(new CraftPackets.RequestCraftStatus(statusIndex, pos, recipeID));
	}
}
