package com.diamssword.greenresurgence.network;

import net.minecraft.util.Identifier;

public class EntitiesPackets {
	public record AllowedList(Identifier[] blocks, Identifier[] items) {}

	;

	public static void init() {

	}
}
