package com.diamssword.greenresurgence.items;

import com.diamssword.characters.api.CharacterClothingApi;
import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.characters.api.appearence.Cloth;
import com.diamssword.characters.api.appearence.LayerDef;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.helpers.IGuiStackPacketReceiver;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.network.NotificationPackets;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClothBagItem extends Item implements IGuiStackPacketReceiver {


	public ClothBagItem() {
		super(new OwoItemSettings().group(MItems.GROUP).tab(0).maxCount(1));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if(user.isCreative() && !user.isSneaking()) {
			if(!world.isClient && hand == Hand.MAIN_HAND) {
				GuiPackets.send(user, GuiPackets.GUI.ClothBagEditGui);
				return TypedActionResult.success(itemStack);
			}
		} else if(!world.isClient) {
			var cloth = getRandomClothForPlayer(user, itemStack);
			if(cloth.isEmpty())
				return TypedActionResult.fail(itemStack);
			else {
				NotificationPackets.sendCloth(user, cloth.get());
				ComponentManager.getPlayerDatas(user).getAppearence().unlockCloth(cloth.get());
				ComponentManager.syncPlayerDatas(user);
				user.setCurrentHand(hand);
				if(!user.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}
				return TypedActionResult.consume(itemStack);
			}
		} else
			return TypedActionResult.success(itemStack);
		return TypedActionResult.fail(itemStack);
	}

	public static Optional<Cloth> getRandomClothForPlayer(PlayerEntity user, ItemStack bag) {
		var ls = getClothsAvailableFromBag(bag);

		var unlockeds = ComponentManager.getPlayerDatas(user).getAppearence().getUnlockedCloths();
		var filtered = ls.stream().filter(c -> !unlockeds.contains(c.id())).toList();
		if(!filtered.isEmpty()) {
			return Optional.of(filtered.get((int) (Math.random() * filtered.size())));
		}

		return Optional.empty();
	}

	public static Optional<Cloth> getRandomClothFromBag(ItemStack bag) {
		var ls = getClothsAvailableFromBag(bag);
		if(!ls.isEmpty()) {
			return Optional.of(ls.get((int) (Math.random() * ls.size())));
		}
		return Optional.empty();
	}

	public static List<Cloth> getClothsAvailableFromBag(ItemStack bag) {
		var tag = bag.getNbt();
		if(tag != null && tag.contains("cloth")) {
			var comp = tag.getCompound("cloth");
			var levels = comp.getString("levels").split(",");
			if(levels.length < 2)
				if(levels[0].isBlank())
					levels[0] = CharacterClothingApi.ANY_LEVEL;
			var collections = comp.getString("collections").split(",");
			if(collections.length < 2)
				if(collections[0].isBlank())
					collections[0] = CharacterClothingApi.ALL_COLLECTIONS;
			var layersS = comp.getString("layers").split(",");
			List<LayerDef> layers = new ArrayList<>();
			for(String s : layersS) {
				var l = CharactersApi.clothing().getLayers().get(s);
				if(l != null)
					layers.add(l);
			}
			if(layers.isEmpty())
				layers = CharactersApi.clothing().getClothLayers();
			return CharactersApi.clothing().getClothsIn(levels, collections, layers.toArray(new LayerDef[0]));

		}
		return List.of();
	}

	@Override
	public void receiveGuiPacket(ServerPlayerEntity player, ItemStack handStack, NbtCompound received) {
		if(!player.isCreative())
			return;
		var tag = handStack.getOrCreateSubNbt("EntityTag");
		var empty = 0;
		if(received.contains("collections")) {
			var comp = handStack.getOrCreateSubNbt("cloth");
			var coll = received.getString("collections");
			comp.putString("collections", coll);
			if(coll.isBlank())
				empty++;
		}
		if(received.contains("layers")) {
			var comp = handStack.getOrCreateSubNbt("cloth");
			var coll = received.getString("layers");
			comp.putString("layers", coll);
			if(coll.isBlank())
				empty++;
		}
		if(received.contains("levels")) {
			var comp = handStack.getOrCreateSubNbt("cloth");
			var coll = received.getString("levels");
			comp.putString("levels", coll);
			if(coll.isBlank())
				empty++;
		}
		var types = received.getString("layers");
		var levels = received.getString("levels");
		if(empty > 2)
			handStack.removeSubNbt("cloth");


	}

}
