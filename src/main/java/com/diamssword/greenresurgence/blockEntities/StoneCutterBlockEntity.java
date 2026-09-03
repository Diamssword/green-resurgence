package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.crafting.*;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.IStoneCutterTypeRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class StoneCutterBlockEntity extends BlockEntity implements ICraftingTile<IStoneCutterTypeRecipe> {


	private Identifier collection;
	private SimpleInventory slot = new SimpleInventory(1);

	public StoneCutterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.slot.addListener(c -> {

		});
	}

	public StoneCutterBlockEntity setCollection(Identifier collectionID) {
		this.collection = collectionID;
		return this;
	}

	public SimpleInventory getSlot() {
		return slot;
	}

	@Nullable
	public Identifier getCollection() {
		return this.collection;
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
	public boolean isCraftAllowed(ComposedIdentifier recipeID, @Nullable PlayerEntity player) {
		return collection != null && collection.equals(recipeID.getCollection());
	}


	@Override
	public boolean tryCraft(ComposedIdentifier recipeID, @Nullable CraftExtraControl control, @Nullable PlayerEntity player) {
		if(player != null && !player.getWorld().isClient() && isCraftAllowed(recipeID, player)) {

			return Recipes.getStoneCutter(this.collection).map(c -> {
				var item = new Identifier(recipeID.getId());
				var input = UniversalResource.fromItem(getSlot().getStack(0));
				var ls = c.getResultForInput(input, player).stream().filter(p -> p.getID().equals(item)).findFirst();
				if(ls.isPresent()) {
					var res = ls.get().asItem();
					if(!res.isEmpty()) {
						int cnt = 1;
						var st = getSlot().getStack(0);
						if(control != null)
							cnt = control.getOperationCount(ls.get());
						cnt = Math.min(st.getCount(), cnt);
						st.decrement(cnt);
						var coll = CraftExtraControl.applyMultiplierToItemStack(res, cnt);
						coll.forEach(i -> {
							if(!player.giveItemStack(i))
								ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), DefaultedList.ofSize(1, i));
						});
						return true;
					}
				}
				return false;
			}).orElse(false);
		}
		Channels.MAIN.clientHandle().send(new CraftPackets.RequestCraft(pos, recipeID, control == null ? new CraftExtraControl(false, false) : control));
		return true;
	}

	@Override
	public CraftingProvider getCraftingProvider(@Nullable PlayerEntity player) {
		return new CraftingProvider();
	}

	@Override
	public Optional<IStoneCutterTypeRecipe> recipeFromId(ComposedIdentifier id) {
		return Recipes.getStoneCutter(id.getCollection());
	}

	@Override
	public void handleStatusRequest(int requestIndex, ComposedIdentifier recipeID, ServerPlayerEntity playerEntity) {

	}

	@Override
	public void receiveStatus(int requestIndex, CraftingResult result) {

	}

	@Override
	public void markRemoved() {
		super.markRemoved();
	}

	@Override
	public void requestStatus(ComposedIdentifier recipeID, PlayerEntity player, Consumer<CraftingResult> result) {

	}

	public static class ScreenHandler extends MultiInvScreenHandler {
		private StoneCutterBlockEntity be;

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, StoneCutterBlockEntity te) {
			super(syncId, player, new GridContainer("slot", te.getSlot(), 1, 1));
			be = te;
		}

		@Override
		public void onClosed(PlayerEntity player) {

			super.onClosed(player);
			if(!player.getWorld().isClient) {
				ItemStack stack = be.getSlot().removeStack(0);
				if(!player.getInventory().insertStack(stack)) {
					ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), DefaultedList.ofSize(1, stack));
				}
			}
		}

		@Override
		public ScreenHandlerType<StoneCutterBlockEntity.ScreenHandler> type() {
			return Containers.STONE_CUTTER;
		}

	}
}
