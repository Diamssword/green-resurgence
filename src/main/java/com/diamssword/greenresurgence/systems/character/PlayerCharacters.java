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
import java.util.function.Function;

public class PlayerCharacters implements ComponentV3, AutoSyncedComponent {

	private final PlayerEntity player;
	private final Map<String, ApiCharacterValues> characters = new HashMap<>();
	private final Map<String, NbtCompound> savedStats = new HashMap<>();
	private final Map<String, NbtCompound> savedAppearence = new HashMap<>();

	private String currentCharID;
	private ApiCharacterValues currentCharacter;

	public PlayerCharacters(PlayerEntity e) {
		this.player = e;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		if (tag.contains("characters")) {
			NBTToMap(characters, tag.getCompound("characters"), t -> {
				var d = new ApiCharacterValues();
				return d.charactersfromNBT(t);
			});
			NBTToMap(savedAppearence, tag.getCompound("appearance"), t -> t);
			NBTToMap(savedStats, tag.getCompound("stats"), t -> t);
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
		tag.put("characters", mapToNBT(characters, ApiCharacterValues::toNBT));
		tag.put("stats", mapToNBT(savedStats, t -> t));
		tag.put("appearance", mapToNBT(savedAppearence, t -> t));
		if (currentCharID != null)
			tag.putString("current", currentCharID);
	}

	private <T> NbtCompound mapToNBT(Map<String, T> map, Function<T, NbtCompound> provider) {
		var t1 = new NbtCompound();
		map.forEach((k, v) -> {
			t1.put(k, provider.apply(v));
		});
		return t1;
	}

	private <T> void NBTToMap(Map<String, T> map, NbtCompound tag, Function<NbtCompound, T> provider) {
		map.clear();
		tag.getKeys().forEach(k -> {
			map.put(k, provider.apply(tag.getCompound(k)));
		});
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
		var oldChar = currentCharID;
		if (car != null) {
			currentCharacter = car;
			currentCharID = id;
			var dt = player.getComponent(Components.PLAYER_DATA);
			var newAp = savedAppearence.remove(id);
			savedAppearence.put(oldChar, dt.appearance.writeToNbt(new NbtCompound(), false));
			if (newAp != null) {
				System.out.println(savedAppearence.get(oldChar));
				dt.appearance.readFromNbt(newAp);
			}
			var newSt = savedStats.remove(id);
			savedStats.put(id, dt.stats.write());
			if (newSt != null) {
				dt.stats.read(newSt);
			}
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
