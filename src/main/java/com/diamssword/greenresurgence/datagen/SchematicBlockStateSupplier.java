package com.diamssword.greenresurgence.datagen;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public record SchematicBlockStateSupplier(Block block, String baseModel, Map<String, String> searchAndReplace) implements BlockStateSupplier {

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public JsonElement get() {
		return deepCopyWithStringReplace(getModel(), searchAndReplace);
	}

	private JsonElement deepCopyWithStringReplace(JsonElement input, Map<String, String> replacement) {
		if (input == null || input.isJsonNull()) {
			return JsonNull.INSTANCE;
		}

		if (input.isJsonObject()) {
			JsonObject obj = input.getAsJsonObject();
			JsonObject newObj = new JsonObject();
			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				newObj.add(entry.getKey(), deepCopyWithStringReplace(entry.getValue(), replacement));
			}
			return newObj;

		} else if (input.isJsonArray()) {
			JsonArray arr = input.getAsJsonArray();
			JsonArray newArr = new JsonArray();
			for (JsonElement item : arr) {
				newArr.add(deepCopyWithStringReplace(item, replacement));
			}
			return newArr;

		} else if (input.isJsonPrimitive()) {
			JsonPrimitive prim = input.getAsJsonPrimitive();
			if (prim.isString()) {
				var str = prim.getAsString();
				for (var d : replacement.entrySet()) {
					str = str.replace("$" + d.getKey(), d.getValue());
				}
				return new JsonPrimitive(str);
			}
			return prim.deepCopy();
		}

		return input.deepCopy(); // fallback
	}

	private JsonObject getModel() {
		Gson gson = new Gson();
		Path path = LangGenerator.getDevPath("models/blockstates/" + baseModel + ".json");
		try (Reader reader = Files.newBufferedReader(path)) {
			return gson.fromJson(reader, JsonObject.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
