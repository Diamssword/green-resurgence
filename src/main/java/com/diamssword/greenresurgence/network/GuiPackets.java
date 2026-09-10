package com.diamssword.greenresurgence.network;

import com.diamssword.characters.api.ComponentManager;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.IGuiPacketReceiver;
import com.diamssword.greenresurgence.containers.IOptionalInventory;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.entities.ILightAndSoundMount;
import com.diamssword.greenresurgence.items.helpers.IGuiStackPacketReceiver;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GuiPackets {
	public enum KEY {
		Inventory,
		PInventory,
		Crawl,
		Klaxon,
		Flashlight
	}

	public enum GUI {
		ImageBlock,
		Stats,
		FactionClaimAntenna,
		ClothBagEditGui
	}

	public record GuiPacket(GUI gui, @Nullable BlockPos pos, int... extras) {
	}


	public record ItemStackValue(NbtCompound tag) {}

	public record GuiTileValue(BlockPos pos, String key, String value) {
		public GuiTileValue(BlockPos pos, String key, float value) {
			this(pos, key, value + "");
		}

		public GuiTileValue(BlockPos pos, String key, Text text) {
			this(pos, key, Text.Serializer.toJson(text));
		}

		public GuiTileValue(BlockPos pos, String key, int value) {
			this(pos, key, value + "");
		}

		public GuiTileValue(BlockPos pos, String key, double value) {
			this(pos, key, value + "");
		}

		public GuiTileValue(BlockPos pos, String key, boolean value) {
			this(pos, key, value ? "1" : "0");
		}

		public void replyTo(PlayerEntity player, String key, String value) {
			if(!player.getWorld().isClient)
				Channels.MAIN.serverHandle(player).send(new GuiTileValue(this.pos, key, value));
		}

		public void replyToServer(String key, String value) {
			if(GreenResurgence.clientHelper.isClient())
				Channels.MAIN.clientHandle().send(new GuiTileValue(this.pos, key, value));
		}

		public void replyTo(PlayerEntity player, String key, float value) {
			if(!player.getWorld().isClient)
				Channels.MAIN.serverHandle(player).send(new GuiTileValue(this.pos, key, value));
		}

		public void replyToServer(String key, float value) {
			if(GreenResurgence.clientHelper.isClient())
				Channels.MAIN.clientHandle().send(new GuiTileValue(this.pos, key, value));
		}

		public void replyTo(PlayerEntity player, String key, int value) {

			if(!player.getWorld().isClient)
				Channels.MAIN.serverHandle(player).send(new GuiTileValue(this.pos, key, value));
		}

		public void replyToServer(String key, int value) {
			if(GreenResurgence.clientHelper.isClient())
				Channels.MAIN.clientHandle().send(new GuiTileValue(this.pos, key, value));
		}

		public void replyTo(PlayerEntity player, String key, double value) {
			if(!player.getWorld().isClient)
				Channels.MAIN.serverHandle(player).send(new GuiTileValue(this.pos, key, value));
		}

		public void replyToServer(String key, double value) {
			if(GreenResurgence.clientHelper.isClient())
				Channels.MAIN.clientHandle().send(new GuiTileValue(this.pos, key, value));
		}

		public void replyTo(PlayerEntity player, String key, boolean value) {
			if(!player.getWorld().isClient)
				Channels.MAIN.serverHandle(player).send(new GuiTileValue(this.pos, key, value));
		}

		public void replyToServer(String key, boolean value) {
			if(GreenResurgence.clientHelper.isClient())
				Channels.MAIN.clientHandle().send(new GuiTileValue(this.pos, key, value));
		}

		public MutableText asText() {
			return Text.Serializer.fromJson(value);
		}

		public String asString() {
			return value;
		}

		public double asDouble() {
			try {
				return Double.parseDouble(this.value);
			} catch(NumberFormatException ignored) {
			}
			return 0;
		}

		public int asInt() {
			try {
				return Integer.parseInt(this.value);
			} catch(NumberFormatException ignored) {
			}
			return 0;
		}

		public boolean asBool() {
			try {
				return Integer.parseInt(this.value) == 1;
			} catch(NumberFormatException ignored) {
			}
			return false;
		}

		public float asFloat() {
			try {
				return Float.parseFloat(this.value);
			} catch(NumberFormatException ignored) {
			}
			return 0;
		}
	}

	public record KeyPress(KEY key) {
	}

	public static void init() {
		Channels.MAIN.registerClientboundDeferred(GuiPacket.class);
		Channels.MAIN.registerServerbound(ItemStackValue.class, (msg, ctx) -> {
			var hand = ctx.player().getMainHandStack();
			if(hand.getItem() instanceof IGuiStackPacketReceiver re) {
				re.receiveGuiPacket(ctx.player(), hand, msg.tag);
			}
		});
		Channels.MAIN.registerServerbound(GuiTileValue.class, (msg, ctx) -> {
			BlockEntity te = ctx.player().getWorld().getBlockEntity(msg.pos);
			if(te != null) {
				if(te instanceof IGuiPacketReceiver ib)
					ib.receiveGuiPacket(ctx.player(), msg);
			}
		});
		Channels.MAIN.registerClientboundDeferred(GuiTileValue.class);
		Channels.MAIN.registerServerbound(KeyPress.class, (msg, ctx) -> {
			switch(msg.key) {

				case Inventory -> {
					var ls = ctx.player().getWorld().getComponent(Components.BASE_LIST);
					var terr = ls.getAreaAt(ctx.player().getBlockPos());
					terr.ifPresent(v -> {
						NamedScreenHandlerFactory screen = new NamedScreenHandlerFactory() {
							@Nullable
							@Override
							public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
								return terr.get().getStorage().createMenu(syncId, playerInventory, player);
							}

							@Override
							public Text getDisplayName() {
								return terr.get().getStorage().getDisplayName();
							}
						};
						ctx.player().openHandledScreen(screen);

					});


				}
				case PInventory -> {
					if(ctx.player().getVehicle() instanceof RideableInventory rideableInventory) {
						if(rideableInventory instanceof IOptionalInventory op) {
							if(op.hasInventory(ctx.player()))
								rideableInventory.openInventory(ctx.player());
							else
								CustomPlayerInventory.openInventoryScreen(ctx.player());
						} else
							rideableInventory.openInventory(ctx.player());
					} else
						CustomPlayerInventory.openInventoryScreen(ctx.player());
				}
				case Crawl -> {
					if(ctx.player().isCreative() || ComponentManager.getPlayerDatas(ctx.player()).getStats().getLevel("survivant") >= 10) {
						var dt = ctx.player().getComponent(Components.PLAYER_DATA);
						dt.setForcedPose(dt.getPose() == EntityPose.SWIMMING ? EntityPose.STANDING : EntityPose.SWIMMING);
					}

				}
				case Klaxon -> {
					var v = ctx.player().getVehicle();
					if(v instanceof ILightAndSoundMount mount) {
						var s = mount.getKlaxonSound();
						if(s != null)
							v.playSound(s, 1f, (float) (0.9f + (Math.random() * 0.5)));
					}
				}
				case Flashlight -> {
					var v = ctx.player().getVehicle();
					if(v instanceof ILightAndSoundMount mount) {
						v.playSound(SoundEvents.BLOCK_LEVER_CLICK, 0.5f, (float) (1f + Math.random()));
						mount.setHasLight(!mount.isLightOn(v, null));
					}
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
