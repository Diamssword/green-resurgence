package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.render.environment.BaseEnvironmentArea;
import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import com.diamssword.greenresurgence.render.environment.FixedColorFogModifier;
import com.diamssword.greenresurgence.render.environment.NoEffectArea;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EnvironmentPacketClient {

	private static final Map<String, BiFunction<String, NbtCompound, BaseEnvironmentArea>> builders = new HashMap<>();

	static {
		builders.put("fog", FixedColorFogModifier::new);
		builders.put("zone", NoEffectArea::new);
	}

	public static void init() {
		Channels.MAIN.registerClientbound(EnvironmentPacket.AreaList.class, (msg, handler) -> {
			EnvironementAreas.fogAreas.forEach(BaseEnvironmentArea::onDestroy);
			EnvironementAreas.fogAreas.clear();
			msg.areas().getList("list", NbtElement.COMPOUND_TYPE).forEach(nbt -> {
				var key = ((NbtCompound) nbt).getString("key");
				var acc = builders.get(key);
				if(acc != null)
					EnvironementAreas.fogAreas.add(acc.apply(key, (NbtCompound) nbt));
			});

		});
	}
}
