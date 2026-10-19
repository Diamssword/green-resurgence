package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.ComposedIdentifier;
import com.diamssword.greenresurgence.systems.crafting.PendingCraft;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.TimedCraftingProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Contract;

public class PlayerInventoryData implements ComponentV3, ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {

	private TimedCraftingProvider crafterProvider = new TimedCraftingProvider();
	private final CustomPlayerInventory inventory = new CustomPlayerInventory();
	public final PlayerEntity player;

	private DefaultedList<ItemStack> bags = DefaultedList.ofSize(3, ItemStack.EMPTY);

	public PlayerInventoryData(PlayerEntity e) {
		this.player = e;
		inventory.fromNBT(new NbtCompound(), player);
		if(!e.getWorld().isClient) {
			crafterProvider.onNewRecipeQueued(() -> player.syncComponent(Components.PLAYER_INVENTORY));
		}
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player) {
		return true;
	}

	public TimedCraftingProvider getCrafterProvider() {
		crafterProvider.setForPlayer(player);
		return crafterProvider;
	}

	public CustomPlayerInventory getInventory() {
		return inventory;
	}

	public ItemStack getBackpackStack() {
		return bags.get(0);
	}

	public ItemStack getLeftSatchel() {
		return bags.get(1);
	}

	public ItemStack getRightSatchel() {
		return bags.get(2);
	}

	public ItemStack getBag(int index) {
		if(index < bags.size())
			return bags.get(index);
		return ItemStack.EMPTY;
	}

	public void setBackpackStack(ItemStack stack) {
		bags.set(0, stack);
	}

	@Override
	public void clientTick() {
		crafterProvider.tickClient(player);
		inventory.updateItems();
	}

	@Override
	public void serverTick() {
		crafterProvider.tick(player);
		inventory.updateItems();
		var bl = false;
		for(int i = 0; i < 3; i++) {
			var i1 = inventory.getBags().getStack(i);
			if(bags.get(i).getItem() != i1.getItem()) {
				bags.set(i, i1.copy());
				bl = true;
			}
		}
		if(bl)
			player.syncComponent(Components.PLAYER_INVENTORY);
		if(inventory.InventoryScreenNeedRefresh) {
			CustomPlayerInventory.openInventoryScreen(player);
			inventory.InventoryScreenNeedRefresh = false;
		}
	}

	@Contract(mutates = "param1")
	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
		NbtCompound tag = new NbtCompound();

		if(recipient == player) {
			NbtList pendingCrafts = new NbtList();
			this.crafterProvider.getPendingCrafts().forEach(c -> {
				pendingCrafts.add(NbtString.of(c.recipe.getId().toString()));
			});
			tag.put("pendingcrafts", pendingCrafts);
		}
		var ls = new NbtList();
		this.bags.forEach(i -> {
			ls.add(i.writeNbt(new NbtCompound()));
		});
		tag.put("bags", ls);
		buf.writeNbt(tag);
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		NbtCompound tag = buf.readNbt();
		if(tag != null) {
			if(tag.contains("pendingcrafts")) {
				var ls = tag.getList("pendingcrafts", NbtElement.STRING_TYPE);
				crafterProvider.clearPendings();
				ls.forEach(l1 -> {
					var r = Recipes.getRecipe(new ComposedIdentifier(l1.asString()));
					r.ifPresent(recipe -> crafterProvider.addPending(new PendingCraft(recipe)));
				});
			}
			if(tag.contains("bags")) {
				var ls = tag.getList("bags", NbtElement.COMPOUND_TYPE);
				for(int i = 0; i < ls.size(); i++) {
					if(i < bags.size())
						bags.set(i, ItemStack.fromNbt(ls.getCompound(i)));
				}
			}
		}
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		inventory.fromNBT(tag, player);
		if(tag.contains("crafter")) {
			crafterProvider = new TimedCraftingProvider(tag.getCompound("crafter"));
			if(!player.getWorld().isClient)
				crafterProvider.onNewRecipeQueued(() -> player.syncComponent(Components.PLAYER_INVENTORY));
		}

	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.put("crafter", crafterProvider.toNBT());
		inventory.toNBT(tag);

	}
}
