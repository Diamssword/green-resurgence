package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GuiPackets {
	public enum KEY {
		Inventory,
		PInventory,
		Crawl,
	}

	public enum GUI {
		ImageBlock,
		Stats,
		FactionClaimAntenna,
	}

	public record GuiPacket(GUI gui, @Nullable BlockPos pos) {
	}

	public record ReturnValue(String topic, String value) {
	}

	public record ReturnError(String topic, Text message) {
	}

	public record GuiTileValue(BlockPos pos, String key, String value) {
		public GuiTileValue(BlockPos pos, String key, float value) {
			this(pos, key, value + "");
		}

		public GuiTileValue(BlockPos pos, String key, int value) {
			this(pos, key, value + "");
		}

		public GuiTileValue(BlockPos pos, String key, double value) {
			this(pos, key, value + "");
		}

		public double asDouble() {
			try {
				return Double.parseDouble(this.value);
			} catch (NumberFormatException ignored) {
			}
			return 0;
		}

		public int asInt() {
			try {
				return Integer.parseInt(this.value);
			} catch (NumberFormatException ignored) {
			}
			return 0;
		}

		public float asFloat() {
			try {
				return Float.parseFloat(this.value);
			} catch (NumberFormatException ignored) {
			}
			return 0;
		}
	}

	public record KeyPress(KEY key) {
	}

	public static void init() {
		Channels.MAIN.registerClientboundDeferred(GuiPacket.class);
		Channels.MAIN.registerClientboundDeferred(ReturnValue.class);
		Channels.MAIN.registerClientboundDeferred(ReturnError.class);
		Channels.MAIN.registerServerbound(GuiTileValue.class, (msg, ctx) -> {
			BlockEntity te = ctx.player().getWorld().getBlockEntity(msg.pos);
			if (te != null && ctx.player().isCreative()) {
				if (te instanceof ItemBlockEntity ib) {
					ib.receiveGuiPacket(msg);
				} else if (te instanceof ImageBlockEntity ib) {
					ib.receiveGuiPacket(msg);
				}
			}
		});
		Channels.MAIN.registerServerbound(KeyPress.class, (msg, ctx) -> {
			switch (msg.key) {

				case Inventory -> {
					var ls = ctx.player().getWorld().getComponent(Components.BASE_LIST);
					var terr = ls.getTerrainAt(ctx.player().getBlockPos());
					terr.ifPresent(v -> {
						NamedScreenHandlerFactory screen = new NamedScreenHandlerFactory() {
							@Nullable
							@Override
							public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
								return terr.get().getOwner().storage.createMenu(syncId, playerInventory, player);
							}

							@Override
							public Text getDisplayName() {
								return terr.get().getOwner().storage.getDisplayName();
							}
						};
						ctx.player().openHandledScreen(screen);

					});


				}
				case PInventory -> CustomPlayerInventory.openInventoryScreen(ctx.player());
				case Crawl -> {
					var dt = ctx.player().getComponent(Components.PLAYER_DATA);
					dt.setForcedPose(dt.getPose() == EntityPose.SWIMMING ? EntityPose.STANDING : EntityPose.SWIMMING);

				}
			}
		});
	}

	public static void send(PlayerEntity entity, GUI gui) {
		Channels.MAIN.serverHandle(entity).send(new GuiPacket(gui, entity.getBlockPos()));
	}

	public static void send(PlayerEntity entity, GUI gui, BlockPos b) {
		Channels.MAIN.serverHandle(entity).send(new GuiPacket(gui, b));
	}
}
