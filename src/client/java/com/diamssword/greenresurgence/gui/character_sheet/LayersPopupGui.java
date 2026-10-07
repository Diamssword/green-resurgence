package com.diamssword.greenresurgence.gui.character_sheet;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.characters.api.http.SkinLayerValue;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.TextAreaComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class LayersPopupGui extends BaseUIModelScreen<FlowLayout> {


	private final int index;
	private final String guiId;
	private final SkinLayerValue[] layers;
	private final String[] allowedLayers;

	public LayersPopupGui(String[] allowedLayers, String guiId, int index, SkinLayerValue[] layers) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("wardrobe_popup")));
		this.allowedLayers = allowedLayers;
		this.index = index;
		this.layers = layers;
		this.guiId = guiId;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(RButtonComponent.class, "button").onPress(v -> {
			var text = rootComponent.childById(TextAreaComponent.class, "text");
			
			text.tooltip(Text.translatable("character_sheet.wardrobe.outfit_name"));
			if(!text.getText().isEmpty()) {
				ComponentManager.getPlayerDatas(client.player).getAppearence().saveLayers(guiId, text.getText(), index, new SkinLayerValue[0]);
				CharactersApi.bodyParts().clientAskSaveLayers(text.getText(), index, layers);
				MinecraftClient.getInstance().setScreen(new BodyPartEditorGui(layers, this.guiId, allowedLayers));
			}
		});
		rootComponent.childById(RButtonComponent.class, "cancel").onPress(v -> {
			MinecraftClient.getInstance().setScreen(new BodyPartEditorGui(this.guiId, allowedLayers));
		});
	}

	public boolean shouldPause() {
		return false;
	}
}