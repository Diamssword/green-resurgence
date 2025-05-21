package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.gui.*;
import com.diamssword.greenresurgence.gui.faction.ClaimAntennaGui;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;

public class ClientGuiPacket {
	public static void init() {

		Channels.MAIN.registerClientbound(GuiPackets.GuiPacket.class, (message, access) -> {
			switch (message.gui()) {

				case ImageBlock -> {
					openGui(new ImageBlockGui(getTile(ImageBlockEntity.class, message.pos())));
				}
				case Customizer -> {
					openGui(new CharacterCustomizationScreen(CharacterCustomizationScreen.Type.size));
				}
				case Wardrobe -> {
					openGui(new WardrobeGui());
				}
				case Stats -> {
					openGui(new PlayerStatsGui());
				}
				case FactionClaimAntenna -> {
					openGui(new ClaimAntennaGui(getTile(ClaimBlockEntity.class, message.pos())));
				}
			}
		});
		Channels.MAIN.registerClientbound(GuiPackets.ReturnValue.class, (m, c) -> {
			if (MinecraftClient.getInstance().currentScreen instanceof IPacketNotifiedChange pn)
				pn.onChangeReceived(m.topic(), m.value());
		});
		Channels.MAIN.registerClientbound(GuiPackets.ReturnError.class, (m, c) -> {
			if (MinecraftClient.getInstance().currentScreen instanceof IPacketNotifiedChange pn)
				pn.onErrorReceived(m.topic(), m.message());
		});
	}

	public static <T extends BlockEntity> T getTile(Class<T> clazz, BlockPos pos) {
		BlockEntity te = MinecraftClient.getInstance().world.getBlockEntity(pos);
		if (clazz.isInstance(te))
			return (T) te;
		return null;
	}

	private static void openGui(Screen screen) {
		MinecraftClient.getInstance().setScreen(screen);
	}
}
