package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import com.diamssword.greenresurgence.render.environment.FixedColorFogModifier;
import com.diamssword.greenresurgence.render.environment.FogModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Map;
import java.util.function.Consumer;

public class EnvironmentPacketClient {

	private static final Map<String, Consumer<NbtCompound>> acceptors = Map.of("fog", EnvironmentPacketClient::fogBuilder);

	public static void init() {
		Channels.MAIN.registerClientbound(EnvironmentPacket.AreaList.class, (msg, handler) -> {
			EnvironementAreas.fogAreas.forEach(FogModifier::onDestroy);
			EnvironementAreas.fogAreas.clear();
			msg.areas().getList("list", NbtElement.COMPOUND_TYPE).forEach(nbt -> {
				var key = ((NbtCompound) nbt).getString("key");
				var acc = acceptors.get(key);
				if(acc != null)
					acc.accept((NbtCompound) nbt);
			});

		});
	}

	private static void fogBuilder(NbtCompound nbt) {
		EnvironementAreas.fogAreas.add(new FixedColorFogModifier(nbt));
	}


}
