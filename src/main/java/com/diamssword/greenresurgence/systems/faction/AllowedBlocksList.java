package com.diamssword.greenresurgence.systems.faction;

import com.diamssword.greenresurgence.network.AdventureInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AllowedBlocksList {
	public final boolean dontCheckGeneric;
	private List<Block> allowedBlocks = new ArrayList<>();
	private List<Item> allowedItems = new ArrayList<>();

	public AllowedBlocksList(boolean dontCheckGeneric) {this.dontCheckGeneric = dontCheckGeneric;}

	/**
	 * Add the block to the list only if it can't be placed by default
	 *
	 * @param world
	 * @param block
	 * @return true if it's been added, false if the rules already allow for placement
	 */
	public boolean addBlockToList(World world, Block block) {
		if(!canPlaceBlock(world, BlockPos.ORIGIN, block.getDefaultState())) {
			allowedBlocks.add(block);
			return true;
		}
		return false;
	}

	public boolean genericCanPlaceBlock(World world, BlockPos pos, BlockState blockstate) {
		return !blockstate.isFullCube(world, pos);
	}

	public void fromPacket(AdventureInteract.AllowedList packet) {
		allowedBlocks.clear();
		allowedItems.clear();
		for(Identifier block : packet.blocks()) {
			allowedBlocks.add(Registries.BLOCK.get(block));
		}
		for(Identifier block : packet.items()) {
			allowedItems.add(Registries.ITEM.get(block));
		}
	}

	public AdventureInteract.AllowedList toPacket() {
		var l1 = allowedItems.stream().map(Registries.ITEM::getId);
		var l2 = allowedBlocks.stream().map(Registries.BLOCK::getId);
		return new AdventureInteract.AllowedList(l2.toList().toArray(new Identifier[0]), l1.toList().toArray(new Identifier[0]));
	}

	public boolean genericCanBreakBlock(World world, BlockPos pos, BlockState blockstate) {
		return genericCanPlaceBlock(world, pos, blockstate);
	}

	public boolean canBreakBlock(World world, BlockPos pos, BlockState blockState) {
		return canPlaceBlock(world, pos, blockState);
	}

	public boolean canPlaceBlock(World world, BlockPos pos, BlockState blockstate) {
		if(dontCheckGeneric || !genericCanPlaceBlock(world, pos, blockstate)) {
			return allowedBlocks.contains(blockstate.getBlock());
		}
		return true;
	}

	public boolean canUseItem(World world, BlockPos pos, ItemStack stack) {

		if(stack.getItem() instanceof BlockItem be) {
			return canPlaceBlock(world, pos, be.getBlock().getDefaultState());
		} else
			return allowedItems.contains(stack.getItem());
	}

	public void clear() {
		allowedBlocks.clear();
		allowedItems.clear();
	}
}
