package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public interface ICraftingTile<T> {
	/**
	 *
	 * @param recipeID the id representing the recipe, it's up to the implementer to decide how to implement it (usually it's a SimpleRecipe)
	 * @param player   (optional) the player asking to craft
	 * @return true if this craft is allowed, allowed doesn't mean it hast all the materials to process
	 */
	public boolean isCraftAllowed(Identifier recipeID, @Nullable PlayerEntity player);

	/**
	 * On client, send a craft packet request to the server, on server try to craft
	 * implementations should use the @getCraftingProvider method to craft
	 *
	 * @param recipeID the id representing the recipe, it's up to the implementer to decide how to implement it (usually it's a SimpleRecipe)
	 * @param player   (optional) the player asking to craft
	 * @return always true on client, true on server is craft was successful
	 */
	public boolean tryCraft(Identifier recipeID, @Nullable PlayerEntity player);

	/**
	 * return the crafting provider for this block postion and optionally this player
	 *
	 * @param player (optional) the player asking for the craft
	 */
	public CraftingProvider getCraftingProvider(@Nullable PlayerEntity player);

	/**
	 * @param id the id representing the recipe, it's up to the implementer to decide how to implement it (usually it's a SimpleRecipe)
	 * @return the Recipe Object tied to this ID
	 */
	public Optional<T> recipeFromId(Identifier id);

	/**
	 * Should handle a status request from a client on the server side and send back the status to the client
	 *
	 * @param requestIndex the index tied to the request
	 * @param recipeID     the id representing the recipe, it's up to the implementer to decide how to implement it (usually it's a SimpleRecipe)
	 * @param playerEntity the player making the request
	 */
	public void handleStatusRequest(int requestIndex, Identifier recipeID, ServerPlayerEntity playerEntity);

	/**
	 * handle receiving the feasibility status, mostly client side
	 *
	 * @param requestIndex the index tied to the request
	 * @param result       the status
	 */
	public void receiveStatus(int requestIndex, CraftingResult result);

	/**
	 * Allow a player to track the feasibility of a recipe, implementation should ensure regular updates so the client doesn't have to ask everytime
	 *
	 * @param recipeID the id representing the recipe, it's up to the implementer to decide how to implement it (usually it's a SimpleRecipe)
	 * @param player   the player tracking the status
	 * @param result   a methods that will be called everytime the status is updated
	 */
	public void requestStatus(Identifier recipeID, PlayerEntity player, Consumer<CraftingResult> result);
}
