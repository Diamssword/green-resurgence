package com.diamssword.greenresurgence.containers.player;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class HoloPanelConfig {
	private String panel;
	private int x;
	private int width;
	private int height;
	private int y;
	private boolean dirty = false;
	private NbtCompound settings;

	public String getPanel() {
		return panel;
	}

	public void setPanel(String panel) {
		this.panel = panel;
	}

	public boolean isDirty() {
		var d = this.dirty;
		this.dirty = false;
		return d;
	}

	public int getX() {
		return x;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getY() {
		return y;
	}

	@Nullable
	public NbtCompound getSettings() {
		return settings;
	}

	public void markDirty() {
		this.dirty = true;
	}

	public NbtCompound getOrCreateSettings() {
		if(this.settings == null) {
			this.settings = new NbtCompound();
			this.dirty = true;
		}
		return this.settings;
	}

	public void setSettings(NbtCompound settings) {
		this.settings = settings;
		this.dirty = true;
	}

	public static HoloPanelConfig fromNBT(NbtCompound tag) {
		int x = 0;
		int y = 0;
		int w = 100;
		int h = 100;
		String p = "stats";
		if(tag.contains("x"))
			x = tag.getInt("x");
		if(tag.contains("y"))
			y = tag.getInt("y");
		if(tag.contains("w"))
			w = tag.getInt("w");
		if(tag.contains("h"))
			h = tag.getInt("h");
		if(tag.contains("p"))
			p = tag.getString("p");
		var cf = new HoloPanelConfig(x, y, w, h, p);
		if(p.contains("s"))
			cf.setSettings(tag.getCompound("s"));
		cf.dirty = false;
		return cf;
	}

	public NbtCompound toNBT() {
		var tag = new NbtCompound();
		tag.putInt("x", x);
		tag.putInt("y", y);
		tag.putInt("w", width);
		tag.putInt("h", height);
		tag.putString("p", panel);
		tag.putInt("x", x);
		if(getSettings() != null)
			tag.put("s", getSettings());
		return tag;
	}


	public HoloPanelConfig(int x, int y, int width, int height, String panel) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.panel = panel;
		this.dirty = true;
	}

	public HoloPanelConfig scaledForNewRatio(Vector2i oldSize, Vector2i newSize) {

		var x = (int) Math.round((this.x / (double) oldSize.x) * newSize.x);
		var width = (int) Math.round((this.width / (double) oldSize.x) * newSize.x);
		var y = (int) Math.round((this.y / (double) oldSize.y) * newSize.y);
		var height = (int) Math.round((this.height / (double) oldSize.y) * newSize.y);
		return new HoloPanelConfig(x, y, width, height, panel);
	}

	public void onChange(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		dirty = true;
	}
}
