package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.CustomSpawnEgg;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class SpawnEggGui extends MultiInvHandledScreen<CustomSpawnEgg.ScreenHandler, FlowLayout> {

	public NbtCompound itemData = new NbtCompound();
	public NbtCompound changedData = new NbtCompound();

	public SpawnEggGui(CustomSpawnEgg.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("spawn_egg_gui")));
		this.client = MinecraftClient.getInstance();
		updateStack();


	}

	@Override
	public void close() {
		if(!changedData.isEmpty())
			Channels.MAIN.clientHandle().send(new GuiPackets.ItemStackValue(changedData));
		super.close();
	}

	private void updateStack() {
		var ne = new NbtCompound();
		var st = this.client.player.getMainHandStack();
		if(st.getItem() instanceof CustomSpawnEgg)
			ne = st.getOrCreateSubNbt("EntityTag");
		else {
			st = this.client.player.getOffHandStack();
			if(st.getItem() instanceof CustomSpawnEgg)
				ne = st.getOrCreateSubNbt("EntityTag");
		}
		if(!itemData.equals(ne) && this.uiAdapter != null) {
			itemData = ne;
			onChange(this.uiAdapter.rootComponent);
		}
		itemData = ne;
		if(this.client.world.getTime() % 20 == 0) {
			if(!changedData.isEmpty()) {
				Channels.MAIN.clientHandle().send(new GuiPackets.ItemStackValue(changedData));
				changedData = new NbtCompound();
			}
		}
	}

	private void onChange(FlowLayout root) {
		var c = root.childById(TextBoxComponent.class, "in_entity");
		var i = c.getCursor();
		c.setText(itemData.getString("id"));
		c.setCursor(i);

	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		updateStack();

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void build(FlowLayout root) {
		onChange(root);
		var c = root.childById(TextBoxComponent.class, "in_entity");
		c.setCursorToStart();
		c.onChanged().subscribe(e -> {
			changedData.putString("id", e);
		});
		var nameB = root.childById(TextBoxComponent.class, "in_name");
		nameB.setText(itemData.getString("customName"));
		nameB.setCursorToStart();
		nameB.onChanged().subscribe(e -> {
			changedData.putString("name", e);
		});
		var potionB = root.childById(TextBoxComponent.class, "in_potions");
		potionB.setText(itemData.getString("customPotions"));
		potionB.setCursorToStart();
		potionB.onChanged().subscribe(e -> {
			changedData.putString("potions", e);
		});
		var v = itemData.getInt("customHealth");
		this.bindNumber("vie", v > 0 ? v + "" : "", 2000);
	/*	BlockState st = tile.getWorld().getBlockState(tile.getPos());
		this.bindNumber("max_entity", tile.getLogic().getMaxNearbyEntities(), 200);
		this.bindNumber("player_range", tile.getLogic().getRequiredPlayerRange(), 512);
		this.bindNumber("spawncount", tile.getLogic().getSpawnCount(), 100);
		this.bindNumber("cooldown", tile.getLogic().getCooldown() / 1000, 99999);
		this.bindNumber("radius", tile.getLogic().getSpawnRange(), 512);
		this.bindNumber("height", tile.getLogic().getSpawnHeight(), 256);

	 */

	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}

	private void bindNumber(String name, String base, int max) {
		var t = this.component(TextBoxComponent.class, "in_" + name);
		t.setTextPredicate(s -> {
			if(s.isEmpty())
				return true;
			try {
				var value = Integer.parseInt(s);
				return value >= 0 && value <= max;
			} catch(NumberFormatException nfe) {
				return false;
			}
		});
		t.text(base).onChanged().subscribe(v -> {
			var p = 0;
			try {
				p = Integer.parseInt(v);
			} catch(NumberFormatException e) {}
			changedData.putInt(name, p);
		});
	}
}
