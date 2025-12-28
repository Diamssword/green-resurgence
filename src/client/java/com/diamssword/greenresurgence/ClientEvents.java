package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.entities.TwoPassengerVehicle;
import com.diamssword.greenresurgence.event.AttackBlockCallback;
import com.diamssword.greenresurgence.events.PlaceBlockCallback;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerInventoryGui;
import com.diamssword.greenresurgence.mixin.client.ClientPlayerAccessor;
import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.render.AdventureBlockHighlight;
import com.diamssword.greenresurgence.render.BoxRenderers;
import com.diamssword.greenresurgence.render.WireRenderer;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.lootables.IAdvancedLootableBlock;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import static com.diamssword.greenresurgence.render.BoxRenderers.drawBaseOverlays;
import static com.diamssword.greenresurgence.render.BoxRenderers.drawStructureItemOverlay;

public class ClientEvents {
	static PlayerListEntry playerListEntry;
	static long cooldown = 0;

	public static void initialize() {

		ClientPlayConnectionEvents.INIT.register((a, b) -> GreenResurgence.onPostInit());

		ClientTickEvents.START_CLIENT_TICK.register(mc -> {

			if(mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator() && !(mc.currentScreen instanceof PlayerInventoryGui) && mc.options.inventoryKey.wasPressed()) {
				Channels.MAIN.clientHandle().send(new GuiPackets.KeyPress(GuiPackets.KEY.PInventory));

			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			Keybinds.tick();


		});
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((te, w) -> {
			if(te instanceof ConnectorBlockEntity) {
				((ConnectorBlockEntity) te).unloadClientCables();
			}
		});
		WorldRenderEvents.LAST.register((ctx) -> {
			drawStructureItemOverlay(ctx.matrixStack());
			drawBaseOverlays(ctx.matrixStack());
			WireRenderer.render(ctx);
		});

		AttackBlockCallback.EVENT.register(ClientEvents::attackBlock);
		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(ClientEvents::beforeBlockOutline);
		PlaceBlockCallback.EVENT.register(ClientEvents::placeBlock);
		ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::vehicleControl);
	}

	private static boolean beforeBlockOutline(WorldRenderContext ctx, @Nullable HitResult hit) {
		if(hit != null && hit.getType() == HitResult.Type.BLOCK) {
			if(hit instanceof BlockHitResult hitB) {
				if(playerListEntry == null || ctx.world().getTime() % 20 == 0)
					playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
				if(playerListEntry != null && playerListEntry.getGameMode().isSurvivalLike()) {
					ItemStack st = MinecraftClient.getInstance().player.getMainHandStack();
					BlockState state = ctx.world().getBlockState((hitB).getBlockPos());
					if(state.getBlock() == MBlocks.LOOTED_BLOCK) {
						LootedBlockEntity ent = MBlocks.LOOTED_BLOCK.getBlockEntity((hitB).getBlockPos(), ctx.world());
						if(st != null) {
							var tool = LootableLogic.getGoodTool(st, ent.getDisplayBlock(), 2);
							if(tool != null) {
								var col = tool == Lootables.CONTAINER.id() ? 0.5f : 1;
								BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx, col, col, 1);
								return false;
							}
						}

					} else if(state.hasBlockEntity() && ctx.world().getBlockEntity(hitB.getBlockPos()) instanceof IAdvancedLootableBlock res) {
						if(res.canBeInteracted()) {
							BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx);
							return false;
						}
					} else if(AdventureBlockHighlight.blocks.containsKey(state.getBlock())) {
						if(AdventureBlockHighlight.blocks.get(state.getBlock()).shouldHighlight(state, ctx.world(), (hitB).getBlockPos())) {
							BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx);
							return false;
						}
					} else if(st != null) {
						var tool = LootableLogic.getGoodTool(st, state, 2);
						if(tool != null) {
							var col = tool == Lootables.CONTAINER.id() ? 0.5f : 1;
							BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx, col, col, 1);
							return false;
						}
					}
					if(CurrentZonePacket.currentZone != null) {
						for(FactionZone box : CurrentZonePacket.currentZone.zones) {
							if(box.getBounds().contains(hitB.getBlockPos())) {
								return BaseInteractions.shouldOverlayBlock(state.getBlock());
							}
						}
					}
					return false;
				}

			}
		}
		return true;
	}

	private static ActionResult attackBlock(BlockPos pos, Direction dir) {
		if(System.currentTimeMillis() < cooldown + 600) {
			return ActionResult.FAIL;
		}
		if(playerListEntry != null && playerListEntry.getGameMode().isSurvivalLike()) {
			PlayerEntity player = MinecraftClient.getInstance().player;
			ItemStack st = player.getMainHandStack();
			BlockState state = player.getWorld().getBlockState(pos);
			if(state.getBlock() == MBlocks.LOOTED_BLOCK) {
				LootedBlockEntity ent = MBlocks.LOOTED_BLOCK.getBlockEntity(pos, player.getWorld());
				if(st != null && LootableLogic.isGoodTool(st, ent.getDisplayBlock(), 0)) {
					sendInteract(pos, player);
					return ActionResult.SUCCESS;
				}
			} else if(state.hasBlockEntity() && player.getWorld().getBlockEntity(pos) instanceof IAdvancedLootableBlock res) {
				if(res.canBeInteracted()) {
					sendInteract(pos, player);
					return ActionResult.SUCCESS;
				}
			} else if(st != null && LootableLogic.isGoodTool(st, MinecraftClient.getInstance().world.getBlockState(pos), 0)) {
				sendInteract(pos, player);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

	private static void vehicleControl(MinecraftClient client) {
		if(client.player != null) {
			var p = client.player;
			if(p.getControllingVehicle() instanceof TwoPassengerVehicle boatEntity) {
				boatEntity.setInputs(p.input.pressingLeft, p.input.pressingRight, p.input.pressingForward, p.input.pressingBack);
				((ClientPlayerAccessor) p).setRiding(p.isRiding() | (p.input.pressingLeft || p.input.pressingRight || p.input.pressingForward || p.input.pressingBack));
			}
		}
	}

	private static void sendInteract(BlockPos pos, PlayerEntity pl) {
		cooldown = System.currentTimeMillis();
		Channels.MAIN.clientHandle().send(new AdventureInteract.BlockInteract(pos));
		if(pl.preferredHand != null)
			pl.swingHand(pl.preferredHand);
	}

	private static ActionResult placeBlock(ItemPlacementContext ctx, BlockState state) {
		GameMode mode = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(ctx.getPlayer().getUuid()).getGameMode();
		if(mode.equals(GameMode.SURVIVAL)) {
			if(CurrentZonePacket.currentZone != null) {
				for(FactionZone box : CurrentZonePacket.currentZone.zones) {
					if(box.getBounds().contains(ctx.getBlockPos()) && BaseInteractions.allowedBlocks.contains(state.getBlock()))
						return ActionResult.PASS;
				}
			}
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}

}
