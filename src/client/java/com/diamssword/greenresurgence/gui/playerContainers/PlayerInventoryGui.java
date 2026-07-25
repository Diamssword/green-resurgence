package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.utils.TextUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class PlayerInventoryGui extends PlayerBasedGui<CustomPlayerInventory.VanillaPlayerInvMokup> {

	private FreeRowGridLayout statsPanel;

	public PlayerInventoryGui(CustomPlayerInventory.VanillaPlayerInvMokup handler, PlayerInventory inv, Text title) {
		super(handler, "survival/player_stats", true);
		openSubPanelOnLoad = true;

	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var offHandFlow = rootComponent.childById(FlowLayout.class, "flowOffhand");
		var extraSLay = rootComponent.childById(FlowLayout.class, "extraSlotsLayout");
		if(extraSLay != null)
			extraSLay.padding().set(Insets.of(18, 0, 0, 2));

		if(offHandFlow != null) {
			offHandFlow.horizontalSizing(Sizing.fixed(48));
		}
		statsPanel = rootComponent.childById(FreeRowGridLayout.class, "statsPanel");
		if(statsPanel != null)
			fillStats(statsPanel);
	}

	private void fillStats(FreeRowGridLayout parent) {

		var player = client.player;
		var pdata = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);
		parent.clear();
		parent.child(statLabel("Vie  ", pdata.healthManager.getHealthAmount() * 5f, pdata.healthManager.getMaxHealthAmount() * 5f));
		parent.child(statLabel("Infec ", pdata.healthManager.getContaminationAmount(), pdata.healthManager.getMaxContaminationAmount()));
		parent.child(statLabel("Shield", pdata.healthManager.getShieldAmount() * 5f, pdata.healthManager.getMaxShieldAmount() * 5f));
		parent.child(Components.label(TextUtils.whiteText("Faim   : Plein")).lineHeight(8));
		parent.child(statLabel("Endu  ", pdata.healthManager.getEnergyAmount(), pdata.healthManager.getMaxEnergyAmount()));
		parent.child(Components.label(TextUtils.whiteText("Soif   : Plein")).lineHeight(8));
		parent.child(statLabel("Oxygen", player.getAir(), player.getMaxAir()));
		parent.child(Components.label(TextUtils.whiteText("Armure: " + player.getArmor())).lineHeight(8));


	}

	private LabelComponent statLabel(String text, double v1, double v2) {
		DecimalFormat df = new DecimalFormat("0.#");
		var c = Components.label(TextUtils.whiteText(text + ": " + df.format(v1) + "/" + df.format(v2))).lineHeight(8);
		c.margins(Insets.right(1));
		return c;
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if(statsPanel != null && client.world.getTime() % 20 == 0)
			fillStats(statsPanel);
	}


	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}
}
