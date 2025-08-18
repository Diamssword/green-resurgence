package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.characters.api.ComponentManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lootable {
	private final Map<Identifier, Identifier> tables = new HashMap<>();
	private final Map<Identifier, List<Pair<String, Integer>>> statRequirement = new HashMap<>();
	private final Block block;
	private final Block empty;
	private Block connected = null;

	public Lootable(Identifier block, Identifier empty) throws Exception {
		Block b = Registries.BLOCK.get(block);
		Block b1 = Registries.BLOCK.get(empty);
		if (b == null || b == Blocks.AIR)
			throw new Exception();
		this.block = b;
		if (b1 == null)
			b1 = Blocks.AIR;
		this.empty = b1;
	}

	public Lootable(Block block, Block empty) {
		this.block = block;
		this.empty = empty;
	}

	public Lootable setConnected(Identifier block) {
		this.connected = Registries.BLOCK.get(block);
		return this;
	}

	public Block getConnected() {
		return this.connected;
	}

	public boolean asTool(Identifier id) {
		return tables.containsKey(id);
	}

	public Block getEmptyBlock() {
		return empty;
	}

	public Block getBlock() {
		return block;
	}

	public Identifier getLootForTool(Identifier tool) {
		return tables.get(tool);
	}

	public Lootable addTool(Identifier tool, Identifier lootable) {
		tables.put(tool, lootable);
		return this;
	}

	public boolean playerMeetRequirement(Identifier tool, PlayerEntity player) {
		var req = statRequirement.get(tool);
		if (req != null) {
			for (var v : req) {
				if (v.getRight() <= ComponentManager.getPlayerDatas(player).getStats().getPalier(v.getLeft()))
					return true;
			}
			return req.isEmpty();
		}
		return true;

	}

	public Lootable addRequirement(Identifier tool, String classe, int min_level) {
		if (!statRequirement.containsKey(tool))
			statRequirement.put(tool, new ArrayList<>());
		statRequirement.get(tool).add(new Pair<>(classe, min_level));
		return this;
	}

	public NbtCompound toNBT() {
		var comp = new NbtCompound();
		comp.putString("block", Registries.BLOCK.getId(this.block).toString());
		comp.putString("empty", Registries.BLOCK.getId(this.empty).toString());
		var ls = new NbtCompound();
		this.tables.forEach((v, t) -> {
			ls.putString(v.toString(), t.toString());
		});
		comp.put("tools", ls);
		return comp;
	}

	public static Lootable fromNBT(NbtCompound comp) throws Exception {
		Identifier b = new Identifier(comp.getString("block"));
		Identifier b1 = new Identifier(comp.getString("empty"));
		var res = new Lootable(b, b1);
		var ls = comp.getCompound("tools");
		ls.getKeys().forEach(k -> {
			res.addTool(new Identifier(k), new Identifier(ls.getString(k)));
		});
		return res;
	}
}
