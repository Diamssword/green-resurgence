package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.blocks.ArmorTinkererBlock;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.EquipmentScreenHandler;
import com.diamssword.greenresurgence.containers.grids.ContainerArmorGrid;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ArmorTinkererBlockEntity extends MultiEquipmentTinkererBlockEntity {
	public ArmorTinkererBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory.addListener((l) -> markUpdate());
	}


	public ItemStack getArmorStack(EquipmentSlot slot) {
		if(inventory != null) {
			return inventory.getStack(revertedArmorIndex(slot));
		}
		return ItemStack.EMPTY;
	}

	public static int revertedArmorIndex(EquipmentSlot slot) {
		switch(slot) {
			case FEET -> {
				return 3;
			}
			case LEGS -> {
				return 2;
			}
			case CHEST -> {
				return 1;
			}
			default -> {
				return 0;
			}
		}
	}

	@Override
	public void openInventory(ServerPlayerEntity player) {
		if(currentTools[0] == null) {
			onToolChange(null);
		}
		trackedPlayers.add(player);
		Containers.createHandler(player, pos, (sync, inv, p1) -> {
			var handler = new Container(sync, player, new Grid(inventory, 1, 4), upgrades);
			handler.onClosed = () -> trackedPlayers.remove(player);
			return handler;
		});

	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		if(!this.getCachedState().get(ArmorTinkererBlock.BOTTOM)) {
			return;
		}
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		if(!this.getCachedState().get(ArmorTinkererBlock.BOTTOM)) {return;}
		super.readNbt(nbt);
	}

	public static class Container extends EquipmentScreenHandler {
		public Container(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public Container(int syncId, PlayerEntity player, IGridContainer inventory, SimpleInventory... upgrades) {
			super(syncId, player, inventory, upgrades);
		}

		@Override
		public ScreenHandlerType<Container> type() {
			return Containers.ARMOR_TINKERER;
		}
	}

	public static class Grid extends ContainerArmorGrid {
		protected static final ArmorItem.Type[] EQUIPMENT_SLOT_ORDER = new ArmorItem.Type[]{ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS};

		public Grid(Inventory inv, int width, int height) {
			super("tool_slot", inv, width, height);
		}

		public Grid(String s, int width, int height) {
			super(s, width, height);
		}

		@Override
		public int getQuickSlotPriority(ItemStack item) {
			return 2000;
		}

		@Override
		public Slot createSlotFor(int index, int x, int y) {

			return new Slot(this.getInventory(), index, x, y) {
				@Override
				public int getMaxItemCount() {
					return 1;
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					var eq = Equipments.getEquipment("armor", EQUIPMENT_SLOT_ORDER[index].getName());
					return eq.filter(iEquipmentDef -> stack.getItem() == iEquipmentDef.getBlueprintItem() || stack.getItem() == iEquipmentDef.getEquipmentItem()).isPresent();

				}

				@Override
				public Pair<Identifier, Identifier> getBackgroundSprite() {
					return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[index]);
				}
			};
		}
	}
}
