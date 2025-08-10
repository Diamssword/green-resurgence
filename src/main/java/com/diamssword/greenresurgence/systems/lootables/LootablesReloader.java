package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LootablesReloader implements SimpleSynchronousResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<Block, Lootable> lootables = new HashMap<>();
	private boolean shouldSync = false;

	public LootablesReloader() {
	}

	public Optional<Lootable> getTable(Block g) {
		return Optional.ofNullable(lootables.get(g));
	}

	@Override
	public Identifier getFabricId() {
		return GreenResurgence.asRessource("lootables");
	}

	@Override
	public void reload(ResourceManager manager) {
		lootables = new HashMap<>();
		var id = GreenResurgence.asRessource("lootables.json");
		var files = manager.getAllResources(id);
		files.forEach(file -> {
			try {
				BufferedReader reader = file.getReader();
				try {
					JsonArray jsonElement = JsonHelper.deserialize(GSON, reader, JsonArray.class);
					jsonElement.forEach(v -> {
						var ob = v.getAsJsonObject();
						if (ob.has("block")) {
							try {
								Lootable table = new Lootable(new Identifier(ob.get("block").getAsString()), new Identifier(ob.get("empty").getAsString()));
								if (ob.has("tables")) {
									var tables = ob.get("tables").getAsJsonObject();
									tables.keySet().forEach(k -> {
										table.addTool(GreenResurgence.asRessource("lootable/tools/" + k), new Identifier(tables.get(k).getAsString()));
									});
								}
								if (ob.has("requirements")) {
									var tables = ob.get("requirements").getAsJsonObject();
									tables.keySet().forEach(k -> {
										var st1 = tables.get(k).getAsString().split(",");
										for (var d : st1) {
											var st = d.split(":");
											if (st.length > 1)
												table.addRequirement(GreenResurgence.asRessource("lootable/tools/" + k), st[0], Integer.parseInt(st[1]));
										}

									});
								}
								if (ob.has("connected")) {
									table.setConnected(new Identifier(ob.get("connected").getAsString()));
								}
								lootables.put(table.getBlock(), table);
							} catch (Exception e) {
								LOGGER.error("Couldn't parse block from file {} for {}", id, ob.get("block").getAsString());
							}
						} else
							LOGGER.error("Empty declaration in file {}", id);
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

	public void worldTick(MinecraftServer server) {
		if (shouldSync) {
			shouldSync = false;
			Channels.serverHandle(server).send(new DictionaryPackets.LootableList(this));
		}
	}

	public static void serializer(PacketByteBuf write, LootablesReloader val) {
		NbtList list = new NbtList();

		val.lootables.forEach((u, v) -> {
			list.add(v.toNBT());
		});
		var comp = new NbtCompound();
		comp.put("list", list);
		write.writeNbt(comp);
	}

	public static LootablesReloader unserializer(PacketByteBuf read) {

		LootablesReloader loader = new LootablesReloader();
		var comp = read.readNbt();
		var list = comp.getList("list", NbtElement.COMPOUND_TYPE);
		list.forEach(el -> {
			try {
				var t = Lootable.fromNBT((NbtCompound) el);
				loader.lootables.put(t.getBlock(), t);
			} catch (Exception e) {
				LOGGER.error("Couldn't parse packet data for lootable: {}", el, e);
			}
		});
		return loader;
	}
}
