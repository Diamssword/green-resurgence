package com.diamssword.greenresurgence.http;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;

import java.util.concurrent.CompletableFuture;

public class ApiCharacterValues {
	public ApiSkinValues appearence;
	public ApiStatsValues stats;
	public String base64Skin;
	public String base64SkinHead;

	public NbtCompound toNBT() {
		var tag = new NbtCompound();
		tag.put("appearence", appearence.toNBT());
		tag.put("stats", stats.toNBT());
		tag.putString("base64Skin", base64Skin);
		tag.putString("base64SkinHead", base64SkinHead);
		return tag;
	}

	public ApiCharacterValues charactersfromNBT(NbtCompound tag) {
		appearence = new ApiSkinValues();
		appearence.fromNBT(tag.getCompound("appearence"));
		stats = new ApiStatsValues();
		stats.fromNBT(tag.getCompound("stats"));
		base64Skin = tag.getString("base64Skin");
		base64SkinHead = tag.getString("base64SkinHead");
		return this;
	}

	public static CompletableFuture<ApiCharacterValues> getCharacterValues(GameProfile profile) {
		var partUrl = APIService.url + "/files/skin/" + profile.getId().toString().replaceAll("-", "");
		return APIService.getRequest(partUrl + ".json", "").thenApply(d -> {
			if (d.statusCode() == 200) {
				var b = d.body();
				var v = new Gson().fromJson(b, ApiCharacterValues.class);
				if (v.appearence == null)
					v.appearence = new ApiSkinValues();
				if (v.stats == null)
					v.stats = new ApiStatsValues();
			}
			return null;
		});
	}


}
