package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.SpawnerBlockEntity;
import com.diamssword.greenresurgence.blocks.SpawnerBlock;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class SpawnerBlockGui extends MultiInvHandledScreen<SpawnerBlock.ScreenHandler, FlowLayout> {
	private SpawnerBlockEntity tile;
	private boolean locked = false;

	public SpawnerBlockGui(SpawnerBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("spawner_block_gui")));
		handler.onReady(v -> {
			tile = ClientGuiPacket.getTile(SpawnerBlockEntity.class, v.getPos());
			locked = tile.isLocked();
			onReady();
		});

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {

	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}

	private void onReady() {

		this.bindNumber("max_entity", tile.getLogic().getMaxNearbyEntities(), 200);
		this.bindNumber("player_range", tile.getLogic().getRequiredPlayerRange(), 512);
		this.bindNumber("spawncount", tile.getLogic().getSpawnCount(), 100);
		this.bindNumber("cooldown", tile.getLogic().getCooldown() / 1000, 99999);
		this.bindNumber("radius", tile.getLogic().getSpawnRange(), 512);
		this.bindNumber("height", tile.getLogic().getSpawnHeight(), 256);
		var bt = this.component(ButtonComponent.class, "lock");
		bt.setMessage(Text.literal(locked ? "Locked" : "Unlocked"));
		bt.onPress(p -> {
			this.locked = !locked;
			Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(), "lock", locked));
			bt.setMessage(Text.literal(locked ? "Locked" : "Unlocked"));
		});
		var fc = this.component(CheckboxComponent.class, "in_floor");
		fc.checked(tile.getLogic().isFloorCheck());
		fc.onChanged(c -> {
			Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(), "floor", c));
		});

	}

	private void bindNumber(String name, int base, int max) {
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
		t.text(base + "").onChanged().subscribe(v -> {
			Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(), name, v));
		});
	}
}
