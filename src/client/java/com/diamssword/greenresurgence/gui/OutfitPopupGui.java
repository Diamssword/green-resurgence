package com.diamssword.greenresurgence.gui;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextAreaComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class OutfitPopupGui extends BaseUIModelScreen<FlowLayout> {

	private final WardrobeGui parent;
	private final int index;
	private final RButtonComponent bt;

	public OutfitPopupGui(WardrobeGui parent, int index, RButtonComponent bt) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("wardrobe_popup")));
		this.parent = parent;
		this.index = index;
		this.bt = bt;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(RButtonComponent.class, "button").onPress(v -> {
			var text = rootComponent.childById(TextAreaComponent.class, "text");
			if (!text.getText().isEmpty()) {
				CharactersApi.clothing().clientAskSaveOutfit(text.getText(), index);
				bt.tooltip(Text.literal(text.getText()));
				MinecraftClient.getInstance().setScreen(parent);
			}
		});
		rootComponent.childById(RButtonComponent.class, "cancel").onPress(v -> {
			MinecraftClient.getInstance().setScreen(parent);
		});
	}

	public boolean shouldPause() {
		return false;
	}
}