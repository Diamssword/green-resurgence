package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.FilteredInventory;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.entities.vehicles.VehicleWithInventory;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VehicleToolKit extends Item {
	public VehicleToolKit() {
		super(new OwoItemSettings().group(MItems.GROUP).tab(0));
	}

	private IGridContainer[] createContainers(ServerPlayerEntity player, VehicleWithInventory ve) {
		List<IGridContainer> grids = new ArrayList<IGridContainer>();
		var chest = new FilteredInventory(1, (t, s) -> s.isEmpty() || s.getItem() == Items.CHEST).setSingleItem(true);
		var dye = new FilteredInventory(1, (t, s) -> s.isEmpty() || s.getItem() instanceof DyeItem).setSingleItem(true);
		if(ve.canBeDyed()) {
			grids.add(new GridContainer("dye", dye, 1, 1));
		}
		if(ve.canHaveChest()) {
			if(ve.hasChest()) {
				chest.setStack(0, new ItemStack(Items.CHEST));
			}
			grids.add(new GridContainer("chest", chest, 1, 1));
		}
		return grids.toArray(new IGridContainer[0]);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("item." + GreenResurgence.ID + ".vehicle_toolkit.desc"));
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if(entity instanceof VehicleWithInventory ve) {

			if(!user.getWorld().isClient) {
				Containers.createHandler(user, user.getBlockPos(), (sync, inv, p1) -> new VehicleToolKit.ScreenHandler(sync, ve, user, createContainers((ServerPlayerEntity) p1, ve)));
				return ActionResult.CONSUME;
			}
		}
		return super.useOnEntity(stack, user, entity, hand);
	}

	public static void onGuiClosed(ScreenHandler handler, PlayerEntity player, VehicleWithInventory vehicle) {
		var invC = handler.getInventory("chest");
		var invD = handler.getInventory("dye");
		if(vehicle.isDead() || vehicle.isRemoved()) {
			if(invC != null)
				player.dropItem(invC.getInventory().getStack(0), true);
			if(invD != null)
				player.dropItem(invD.getInventory().getStack(0), true);
			return;
		}
		if(invC != null) {
			if(invC.getInventory().getStack(0).getItem() == Items.CHEST)
				vehicle.setHasChest(true);
			else
				vehicle.removeChest();
		}
		if(invD != null) {
			if(invD.getInventory().getStack(0).getItem() instanceof DyeItem d) {
				vehicle.setColor(d.getColor());
			}
		}
	}

	public static class ScreenHandler extends MultiInvScreenHandler {
		private final VehicleWithInventory vehicle;

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
			this.vehicle = null;
		}

		public ScreenHandler(int syncId, VehicleWithInventory vehicle, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player, inventories);
			this.vehicle = vehicle;
		}

		@Override
		public void onClosed(PlayerEntity player) {
			super.onClosed(player);
			if(vehicle != null)
				onGuiClosed(this, player, vehicle);

		}

		@Override
		public ScreenHandlerType<VehicleToolKit.ScreenHandler> type() {
			return Containers.VEHICLE_TOOLKIT;
		}
	}
}
