package com.diamssword.greenresurgence.systems.armor;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ArmorLoader implements SimpleSynchronousResourceReloadListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static ArmorLoader loader = new ArmorLoader();
	private final List<ArmorModelInfos> modeles = new ArrayList<>();
	private final Map<String, ArmorModelDef> modelsBind = new HashMap<>();
	private boolean shouldSync = false;

	public record ArmorModelInfos(String[] models, String[] textures, EquipmentSlot[] slots) {
	}

	public record ArmorModelDef(String model, String texture, EquipmentSlot[] slots) {
	}

	public Optional<ArmorModelDef> getModel(String id) {
		return Optional.ofNullable(modelsBind.get(id));
	}

	public Map<String, ArmorModelDef> getModels() {
		return modelsBind;
	}

	public static boolean isSLotValidFor(ArmorModelDef model, EquipmentSlot slot) {
		for (EquipmentSlot equipmentSlot : model.slots) {
			if (equipmentSlot == slot)
				return true;
		}
		return false;
	}

	@Override
	public Identifier getFabricId() {
		return GreenResurgence.asRessource("armor.json");
	}

	@Override
	public void reload(ResourceManager manager) {
		modeles.clear();
		var id = GreenResurgence.asRessource("armor_models.json");
		var files = manager.getAllResources(id);
		files.forEach(file -> {
			try {
				BufferedReader reader = file.getReader();
				try {
					JsonArray jsonElement = JsonHelper.deserialize(GSON, reader, JsonArray.class);
					jsonElement.forEach(v -> {
						var ob = v.getAsJsonObject();
						String[] models = null;
						if (ob.has("models")) {
							models = ob.get("models").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList().toArray(new String[0]);
						} else if (ob.has("model")) {
							models = new String[]{ob.get("model").getAsString()};

						}
						String[] textures = new String[0];
						if (ob.has("textures")) {
							textures = ob.get("textures").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList().toArray(new String[0]);
						}
						EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
						if (ob.has("slots")) {
							try {
								slots = ob.get("slots").getAsJsonArray().asList().stream().map(v1 -> EquipmentSlot.byName(v1.getAsString())).toArray(EquipmentSlot[]::new);
							} catch (IllegalArgumentException e) {
								LOGGER.error("failed to parse slots ", e);
							}

						}
						if (models != null && models.length > 0) {
							modeles.add(new ArmorModelInfos(models, textures, slots));
						} else
							LOGGER.error("An empty model declaration have been found for " + getFabricId());
					});
				} finally {
					((Reader) reader).close();
					shouldSync = true;
				}
			} catch (JsonParseException | IOException | IllegalArgumentException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), exception);
			}
		});

	}

	public void bindModels() {
		modelsBind.clear();
		this.modeles.forEach(c -> {
			for (String model : c.models) {
				if (c.textures.length == 0) {
					modelsBind.put(model, new ArmorModelDef(model, model, c.slots));
				} else
					for (String texture : c.textures) {
						modelsBind.put(model + "_" + texture, new ArmorModelDef(model, texture, c.slots));
					}

			}


		});
	}

	public void worldTick(MinecraftServer server) {
		if (shouldSync) {
			shouldSync = false;
			Channels.serverHandle(server).send(new DictionaryPackets.ArmorList(this));
			bindModels();
		}
	}

	public static void serializer(PacketByteBuf write, ArmorLoader val) {
		var arr = new JsonArray();
		val.modeles.forEach((u) -> {
			arr.add(GSON.toJson(u));
		});
		write.writeString(arr.toString());
	}

	public static ArmorLoader unserializer(PacketByteBuf read) {
		ArmorLoader loader = new ArmorLoader();
		try {
			var list = JsonHelper.deserialize(GSON, read.readString(), JsonArray.class);
			list.forEach(el -> {
				var js = GSON.fromJson(el.getAsString(), ArmorModelInfos.class);
				loader.modeles.add(js);
			});
		} catch (Exception e) {
			LOGGER.error("Couldn't parse packet data for ArmorLoader: ", e);
		}
		return loader;
	}
}
