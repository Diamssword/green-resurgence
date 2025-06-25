package com.diamssword.greenresurgence.containers.player;

import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.SlotedSimpleInventory;
import com.diamssword.greenresurgence.containers.player.grids.ArmorGrid;
import com.diamssword.greenresurgence.containers.player.grids.BagsGrid;
import com.diamssword.greenresurgence.containers.player.grids.OffHandGrid;
import com.diamssword.greenresurgence.containers.player.grids.PlayerGrid;
import com.diamssword.greenresurgence.items.AbstractBackpackItem;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.InventoryPackets;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class CustomPlayerInventory {

	private PlayerEntity parent;
	private SlotedSimpleInventory bags;
	private ItemStack cursorStack = ItemStack.EMPTY;
	private final Map<String, Inventory> cachedInventories = new HashMap<>();

	public PlayerEntity getPlayer() {
		return parent;
	}

	private int lastHotbarSize = 0;

	public List<IGridContainer> getAsContainers() {
		List<IGridContainer> res = new ArrayList<>();

		var hot = getHotBar();
		res.add(new PlayerGrid("hotbar", hot, hot.size(), 1));
		res.add(new OffHandGrid("offhand", getOffhand(), 1, 1));
		res.add(new PlayerGrid("player", getMain(), 3, 3));
		var b = getBackPack();
		if (b != null) {
			var dim = invDimsFor(0);
			res.add(new PlayerGrid("backpack", b, dim.getLeft(), dim.getRight()));
		}
		b = getSatchelLeft();
		if (b != null) {
			var dim = invDimsFor(1);
			res.add(new PlayerGrid("satchelLeft", b, dim.getLeft(), dim.getRight()));
		}
		b = getSatchelRight();
		if (b != null) {
			var dim = invDimsFor(2);
			res.add(new PlayerGrid("satchelRight", b, dim.getLeft(), dim.getRight()));
		}
		res.add(new ArmorGrid("armor", getArmor(), 1, 4));
		return res;
	}

	private Pair<Integer, Integer> invDimsFor(int bagslotid) {
		if (getBags().getStack(bagslotid).getItem() instanceof AbstractBackpackItem be) {
			return new Pair<>(be.inventoryWidth(getBags().getStack(bagslotid)), be.inventoryHeight(getBags().getStack(bagslotid)));
		}
		return new Pair<>(0, 0);
	}

	public void updateItems() {
		this.getAsContainers().forEach(c -> {
			var iHotbar = c.getName().equals("hotbar");
			for (int j = 0; j < c.getInventory().size(); j++) {
				var s = c.getInventory().getStack(j);

				if (s != null && !s.isEmpty()) {
					if (s.getItem() instanceof AbstractBackpackItem bi) {
						if (!bi.bagInventoryTick(s, parent, j))
							c.getInventory().setStack(j, ItemStack.EMPTY);
					} else
						s.inventoryTick(parent.getWorld(), parent, j, iHotbar && this.parent.getInventory().selectedSlot == j);
				}
			}
		});
		for (int j = 0; j < bags.size(); j++) {
			var s = bags.getStack(j);
			if (s != null && !s.isEmpty()) {
				if (s.getItem() instanceof AbstractBackpackItem bi) {
					bi.bagSlotTick(s, parent, j);
				}
			}
		}

	}

	public void fromNBT(NbtCompound tag, PlayerEntity player) {
		this.parent = player;
		bags = new PlayerLinkedInventory(parent, 3);
		if (tag.contains("accessories"))
			bags.readNbtList(tag.getList("accessories", NbtElement.COMPOUND_TYPE));
		if (tag.contains("cursorStack"))
			cursorStack = ItemStack.fromNbt(tag.getCompound("cursorStack"));

	}

	public NbtCompound toNBT(NbtCompound tag) {
		tag.put("accessories", bags.toNbtList());
		tag.put("cursorStack", cursorStack.writeNbt(new NbtCompound()));
		return tag;
	}

	public NbtCompound toNBTComplete() {
		var tag = new NbtCompound();
		toNBT(tag);
		tag.put("main", this.getPlayer().getInventory().writeNbt(new NbtList()));
		return tag;
	}

	public void fromNBTComplete(NbtCompound tag, PlayerEntity player) {

		fromNBT(tag, player);
		if (tag.contains("main")) {
			player.getInventory().readNbt(tag.getList("main", NbtElement.COMPOUND_TYPE));
		}
	}

	private <T extends Inventory> T cachedInventory(String name, Supplier<T> builder) {
		if (!cachedInventories.containsKey(name))
			cachedInventories.put(name, builder.get());
		return (T) cachedInventories.get(name);
	}

	public void clearCache() {
		cachedInventories.clear();
	}


	public Inventory getBackPack() {
		return cachedInventory("backpack", () -> {
			if (getBags().getStack(0).getItem() instanceof AbstractBackpackItem ba) {
				return ba.getInventory(getBags().getStack(0));
			}
			return null;
		});
	}

	public Inventory getSatchelLeft() {
		return cachedInventory("satchelLeft", () -> {
			if (getBags().getStack(1).getItem() instanceof AbstractBackpackItem ba) {
				return ba.getInventory(getBags().getStack(1));
			}
			return null;
		});
	}

	public SimpleInventory getBags() {
		//return bags;
		return cachedInventory("bags", () -> bags);
	}

	public Inventory getSatchelRight() {
		return cachedInventory("satchelRight", () -> {
			if (getBags().getStack(2).getItem() instanceof AbstractBackpackItem ba) {
				return ba.getInventory(getBags().getStack(2));
			}
			return null;
		});
	}

	public OffsetInventory getHotBar() {
		var c = getHotbarSlotCount(parent);
		if (lastHotbarSize != c) {
			clearCache();
			lastHotbarSize = c;
		}
		return cachedInventory("hotbar", () -> new OffsetInventory(parent.getInventory(), 0, c));
	}

	public OffsetInventory getArmor() {
		return cachedInventory("armor", () -> new OffsetInventory(parent.getInventory(), 36, 4));
	}

	public OffsetInventory getMain() {
		return cachedInventory("main", () -> new OffsetInventory(parent.getInventory(), 9, 9));
	}

	public OffsetInventory getOffhand() {
		return cachedInventory("offhand", () -> new OffsetInventory(parent.getInventory(), 40, 1));
	}

	public List<Inventory> getAllInventories() {
		List<Inventory> res = new ArrayList<>();
		res.add(getHotBar());
		res.add(getMain());
		res.add(getArmor());
		res.add(getOffhand());
		var d = getBackPack();
		if (d != null)
			res.add(d);
		d = getSatchelLeft();
		if (d != null)
			res.add(d);
		d = getSatchelRight();
		if (d != null)
			res.add(d);
		res.add(getBags());
		return res;
	}

	public static int getHotbarSlotCount(PlayerEntity player) {
		if (player.isCreative())
			return 9;
		else
			return 6;
	}

	public ItemStack getAndClearCursorStack() {
		return cursorStack.copyAndEmpty();
	}

	public void setCursorStack(ItemStack cursorStack) {
		this.cursorStack = cursorStack;
	}

	public List<Inventory> getAllPickingInventories() {
		List<Inventory> res = new ArrayList<>();
		res.add(getHotBar());
		res.add(getOffhand());
		res.add(getMain());
		var d = getBackPack();
		if (d != null)
			res.add(d);
		d = getSatchelLeft();
		if (d != null)
			res.add(d);
		d = getSatchelRight();
		if (d != null)
			res.add(d);
		return res;
	}

	public Optional<Pair<Inventory, Integer>> getEmptySlotInInventory() {
		for (var inv : getAllPickingInventories()) {
			var v = getEmptySlot(inv);
			if (v > -1)
				return Optional.of(new Pair<>(inv, v));
		}
		return Optional.empty();

	}

	public boolean insterStack(ItemStack stack) {
		if (stack.isDamaged()) {
			var p = getEmptySlotInInventory();
			if (p.isPresent()) {
				p.get().getLeft().setStack(p.get().getRight(), stack.copyAndEmpty());
				p.get().getLeft().getStack(p.get().getRight()).setBobbingAnimationTime(5);
				return true;
			}
		} else {
			int i;
			do {
				i = stack.getCount();
				stack.setCount(this.addStack(stack));
			} while (!stack.isEmpty() && stack.getCount() < i);
			return stack.getCount() < i;
		}
		return false;
	}

	private int addStack(ItemStack stack) {
		var i = this.getOccupiedSlotWithRoomForStack(stack);
		if (i.isEmpty()) {
			i = this.getEmptySlotInInventory();
		}

		return i.isEmpty() ? stack.getCount() : this.addStack(i.get(), stack);
	}

	public int addStack(Pair<Inventory, Integer> pair, ItemStack stack) {
		return addStack(pair.getLeft(), pair.getRight(), stack);
	}

	public int addStack(Inventory inv, int slot, ItemStack stack) {
		Item item = stack.getItem();
		int i = stack.getCount();
		ItemStack itemStack = inv.getStack(slot);
		if (itemStack.isEmpty()) {
			itemStack = new ItemStack(item, 0);
			if (stack.hasNbt()) {
				itemStack.setNbt(stack.getNbt().copy());
			}

			inv.setStack(slot, itemStack);
		}

		int j = i;
		if (i > itemStack.getMaxCount() - itemStack.getCount()) {
			j = itemStack.getMaxCount() - itemStack.getCount();
		}

		if (j > inv.getMaxCountPerStack() - itemStack.getCount()) {
			j = inv.getMaxCountPerStack() - itemStack.getCount();
		}

		if (j == 0) {
			return i;
		} else {
			i -= j;
			itemStack.increment(j);
			itemStack.setBobbingAnimationTime(5);
			return i;
		}
	}

	public Optional<Pair<Inventory, Integer>> getOccupiedSlotWithRoomForStack(ItemStack stack) {
		if (this.canStackAddMore(this.getHotBar().getStack(this.parent.getInventory().selectedSlot), stack)) {
			return Optional.of(new Pair<>(this.getHotBar(), this.parent.getInventory().selectedSlot));
		} else if (this.canStackAddMore(this.getOffhand().getStack(0), stack)) {
			return Optional.of(new Pair<>(this.getOffhand(), 0));
		} else {
			for (var inv : getAllPickingInventories()) {
				for (int i = 0; i < inv.size(); i++) {
					if (this.canStackAddMore(inv.getStack(i), stack)) {
						return Optional.of(new Pair<>(inv, i));
					}
				}
			}
			return Optional.empty();
		}
	}

	private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
		return !existingStack.isEmpty()
				&& ItemStack.canCombine(existingStack, stack)
				&& existingStack.isStackable()
				&& existingStack.getCount() < existingStack.getMaxCount()
				&& existingStack.getCount() < 64;
	}

	public int getEmptySlot(Inventory inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.getStack(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	public static class PlayerLinkedInventory extends SlotedSimpleInventory {
		public final PlayerEntity player;

		public PlayerLinkedInventory(PlayerEntity player, int size) {
			super(size);
			this.player = player;
		}
	}

	public static class OffsetInventory implements Inventory {

		public final Inventory parent;
		public final int offset;
		public final int length;

		public OffsetInventory(Inventory inventory, int startIndex, int size) {
			this.offset = startIndex;
			this.length = size;
			this.parent = inventory;
		}

		@Override
		public int size() {
			return length;
		}

		@Override
		public boolean isEmpty() {
			for (int i = 0; i < size(); i++) {
				if (!getStack(i).isEmpty())
					return false;
			}
			return true;
		}

		@Override
		public ItemStack getStack(int slot) {
			return parent.getStack(slot + offset);
		}

		@Override
		public ItemStack removeStack(int slot, int amount) {
			return parent.removeStack(slot + offset, amount);
		}

		@Override
		public ItemStack removeStack(int slot) {
			return parent.removeStack(slot + offset);
		}

		@Override
		public void setStack(int slot, ItemStack stack) {
			parent.setStack(slot + offset, stack);
		}

		@Override
		public void markDirty() {
			parent.markDirty();
		}

		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return parent.canPlayerUse(player);
		}

		@Override
		public void clear() {
			parent.clear();
		}
	}

	public void syncHotbarToServer() {
		if (getPlayer().isCreative()) {
			var hot = this.getHotBar();
			var cont = new ItemStack[hot.size()];
			for (int i = 0; i < hot.size(); i++) {
				cont[i] = hot.getStack(i);
			}
			Channels.MAIN.clientHandle().send(new InventoryPackets.SyncCreativeHotbar(cont));
		}

	}

	public boolean InventoryScreenNeedRefresh = false;

	public static void openInventoryScreen(PlayerEntity player) {
		NamedScreenHandlerFactory screen = new NamedScreenHandlerFactory() {
			@Nullable
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

				var inv = player.getComponent(Components.PLAYER_INVENTORY).getInventory();
				return new VanillaPlayerInvMokup(syncId, player, new BagsGrid(inv, "bags", inv.getBags(), 1, 3));
			}

			@Override
			public Text getDisplayName() {
				return Text.literal("Inventory");
			}
		};
		player.openHandledScreen(screen);
	}


}
