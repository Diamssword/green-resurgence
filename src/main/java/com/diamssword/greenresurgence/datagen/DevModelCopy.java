package com.diamssword.greenresurgence.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public class DevModelCopy extends Model {
	private String path;
	private Map<String, String> searchAndReplace;

	public DevModelCopy(String baseModel, Map<String, String> searchAndReplace) {
		super(Optional.empty(), Optional.empty());
		this.path = baseModel;
		this.searchAndReplace = searchAndReplace;
	}

	@Override
	public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
		return SchematicBlockStateSupplier.deepCopyWithStringReplace(ModelHelper.readDevModel("models/items/" + path + ".json"), searchAndReplace).getAsJsonObject();
	}
}
