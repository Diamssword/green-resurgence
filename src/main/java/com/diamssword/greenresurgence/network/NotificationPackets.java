package com.diamssword.greenresurgence.network;

import com.diamssword.characters.api.appearence.Cloth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class NotificationPackets {
	public static float BASE_TIME = 4;

	public enum Size {
		Normal,
		Double
	}

	public enum Renderer {
		Item,
		Cloth,
		None,
	}

	public record NotificationPacket(float time, Text title, Text desc, Size size, Renderer renderer, String renderId, NbtCompound additionalDatas) {
	}


	public static void init() {
		Channels.MAIN.registerClientboundDeferred(NotificationPacket.class);

	}

	public static void sendToast(PlayerEntity entity, NotificationPacket packet) {
		Channels.MAIN.serverHandle(entity).send(packet);
	}

	public static void sendMessage(PlayerEntity entity, Text message) {
		sendToast(entity, new NotificationPacket(BASE_TIME, Text.literal("Nouveau Vêtement"), message, Size.Normal, Renderer.Item, "stone", null));
	}

	public static void sendCloth(PlayerEntity entity, Cloth cloth) {
		sendToast(entity, new NotificationPacket(BASE_TIME, Text.literal("Nouveau Vêtement"), Text.literal(cloth.name()), Size.Normal, Renderer.Cloth, cloth.id().toString(), null));
	}


}
