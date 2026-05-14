package com.diamssword.greenresurgence;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MGas {
	private static Map<Identifier, GasInfos> registry = new ConcurrentHashMap<>();
	public static final GasInfos EMPTY = register(new Identifier("empty"), 0);
	public static final GasInfos OXYGEN = register(GreenResurgence.asRessource("oxygen"), 0x8f009da5);

	public static void init() {

	}

	public static GasInfos getGas(Identifier gas) {
		return registry.getOrDefault(gas, EMPTY);
	}

	private static GasInfos register(Identifier id, int color) {
		var inf = new GasInfos(id, color);
		registry.put(id, inf);
		return inf;
	}

	public record GasInfos(Identifier id, int color) {
		public Text getTranslation() {
			return Text.translatable(id.getNamespace() + ".gas." + id.getPath() + ".name");
		}
	}


}
