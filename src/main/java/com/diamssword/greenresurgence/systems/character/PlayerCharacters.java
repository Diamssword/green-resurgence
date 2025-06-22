package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.http.ApiCharacterValues;
import com.diamssword.greenresurgence.systems.Components;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerCharacters implements ComponentV3, AutoSyncedComponent {

	private final PlayerEntity player;
	private final Map<String, ApiCharacterValues> characters = new HashMap<>();
	private String currentCharID;
	private ApiCharacterValues currentCharacter;

	public PlayerCharacters(PlayerEntity e) {
		this.player = e;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		if (tag.contains("characters")) {
			characters.clear();
			var t1 = tag.getCompound("characters");
			t1.getKeys().forEach(k -> {
				var val = new ApiCharacterValues();
				val.charactersfromNBT(t1.getCompound(k));
				characters.put(k, val);
			});
		}
		if (tag.contains("current")) {
			currentCharID = tag.getString("current");
			currentCharacter = characters.get(currentCharID);
		}
	}


	@Override
	public void writeToNbt(NbtCompound tag) {
		var t1 = new NbtCompound();
		characters.forEach((k, v) -> {
			t1.put(k, v.toNBT());
		});
		tag.put("characters", t1);
		if (currentCharID != null)
			tag.putString("current", currentCharID);
	}

	public Set<String> getCharactersNames() {
		return characters.keySet();
	}

	public ApiCharacterValues getCurrentCharacter() {
		return currentCharacter;
	}

	public String getCurrentCharacterID() {
		return currentCharID;
	}

	public void switchCharacter(String id) {
		var car = characters.get(id);
		if (car != null) {
			currentCharacter = car;
			currentCharID = id;
			SkinServerCache.serverCache.addToCache(player.getUuid(), car.base64Skin, car.base64SkinHead, car.appearence.slim);
			player.syncComponent(Components.PLAYER_CHARACTERS);

		}
	}

	public void deleteCharacter(String id) {
		characters.remove(id);
		if (id.equals(currentCharID)) {
			currentCharID = null;
		}
	}

	public String addNewCharacter(ApiCharacterValues character) {
		var id = character.stats.firstname.toLowerCase().replaceAll(" ", "") + "_" + character.stats.lastname.toLowerCase().replaceAll(" ", "");
		int i = 1;
		var itid = id;
		while (characters.containsKey(itid)) {
			itid = id + i;
			i++;
		}
		characters.put(itid, character);
		return itid;
	}

	public void replaceCharacter(String id, ApiCharacterValues character) {
		characters.put(id, character);
	}

	@Override
	public boolean shouldSyncWith(ServerPlayerEntity player) {
		return player == this.player;
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
		NbtCompound tag = new NbtCompound();
		if (this.currentCharacter != null) {
			tag.put("current", this.currentCharacter.toNBT());
			tag.putString("currentID", this.currentCharID);
		}
		buf.writeNbt(tag);
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		NbtCompound tag = buf.readNbt();
		if (tag != null) {
			if (tag.contains("current")) {
				var d = new ApiCharacterValues();
				d.charactersfromNBT(tag.getCompound("current"));
				currentCharacter = d;
				this.currentCharID = tag.getString("currentID");
			}
		}
	}

}
