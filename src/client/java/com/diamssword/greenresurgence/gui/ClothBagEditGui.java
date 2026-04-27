package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.ClothBagItem;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

public class ClothBagEditGui extends BaseUIModelScreen<FlowLayout> {

	public NbtCompound itemData = new NbtCompound();
	public NbtCompound changedData = new NbtCompound();

	public ClothBagEditGui() {
		super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("edit_cloth_bag_gui")));
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
		if(st.getItem() instanceof ClothBagItem)
			ne = st.getOrCreateSubNbt("cloth");

		itemData = ne;
		if(this.client.world.getTime() % 20 == 0) {
			if(!changedData.isEmpty()) {
				Channels.MAIN.clientHandle().send(new GuiPackets.ItemStackValue(changedData));
				changedData = new NbtCompound();
			}
		}
	}


	@Override
	public void tick() {
		super.tick();
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
		var nameB = root.childById(TextBoxComponent.class, "in_coll");
		nameB.setText(itemData.getString("collections"));
		nameB.setCursorToStart();
		nameB.onChanged().subscribe(e -> {
			changedData.putString("collections", e);
		});
		var potionB = root.childById(TextBoxComponent.class, "in_niv");
		potionB.setText(itemData.getString("levels"));
		potionB.setCursorToStart();
		potionB.onChanged().subscribe(e -> {
			changedData.putString("levels", e);
		});
		var typeF = root.childById(TextBoxComponent.class, "in_type");
		typeF.setText(itemData.getString("layers"));
		typeF.setCursorToStart();
		typeF.onChanged().subscribe(e -> {
			changedData.putString("layers", e);
		});
	}
}
