package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.utils.TextUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerInventoryGui extends PlayerBasedGui<CustomPlayerInventory.VanillaPlayerInvMokup> {

	private FlowLayout statsPanel;
	private Map<PosesManager.EmoteDef, RButtonComponent> emotesBts = new HashMap<>();
	private boolean isShift = false;

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
		statsPanel = rootComponent.childById(FlowLayout.class, "statsPanel");
		if(statsPanel != null)
			fillStats(statsPanel);
		var emotes = rootComponent.childById(FlowLayout.class, "emoteLayout");
		if(emotes != null)
			setupEmotePanel(emotes);
	}

	private void fillStats(FlowLayout parent) {

		var player = client.player;
		parent.clearChildren();
		var c1 = Containers.grid(Sizing.content(), Sizing.content(), 4, 2);
		var c2 = Containers.grid(Sizing.content(), Sizing.content(), 4, 2);
		parent.child(c1);
		parent.child(c2);
		var pdata = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);


		statLabel(0, c1, "health", pdata.healthManager.getHealthAmount() * 5f, pdata.healthManager.getMaxHealthAmount() * 5f);
		statLabel(1, c1, "shield", pdata.healthManager.getShieldAmount() * 5f, pdata.healthManager.getMaxShieldAmount() * 5f);
		statLabel(2, c1, "hunger", "full");
		statLabel(3, c1, "thirst", "full");
		statLabel(0, c2, "infection", pdata.healthManager.getContaminationAmount(), pdata.healthManager.getMaxContaminationAmount());
		statLabel(1, c2, "stamina", pdata.healthManager.getEnergyAmount(), pdata.healthManager.getMaxEnergyAmount());
		statLabel(2, c2, "oxygen", player.getAir(), player.getMaxAir());
		statLabel(3, c2, "armor", player.getArmor());
	}

	private void statLabel(int pos, GridLayout panel, String text, double v1, double v2) {
		DecimalFormat df = new DecimalFormat("0.#");
		panel.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).lineHeight(8).margins(Insets.right(1)), pos, 0);
		panel.child(Components.label(TextUtils.whiteText(": " + df.format(v1) + "/" + df.format(v2))).lineHeight(8).margins(Insets.right(1)), pos, 1);
	}

	private void statLabel(int pos, GridLayout panel, String text, int value) {
		panel.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).lineHeight(8).margins(Insets.right(1)), pos, 0);
		panel.child(Components.label(TextUtils.whiteText(": " + value)).lineHeight(8).margins(Insets.right(1)), pos, 1);
	}

	private void statLabel(int pos, GridLayout panel, String text, String value) {
		panel.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).lineHeight(8).margins(Insets.right(1)), pos, 0);
		panel.child(Components.label(TextUtils.whiteText(": ").append(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text + "." + value))).lineHeight(8).margins(Insets.right(1)), pos, 1);
	}

	private void setupEmotePanel(FlowLayout emotes) {
		Function<PosesManager.EmoteDef, RButtonComponent> btGen = s -> {
			var d = new RButtonComponent(Text.literal(""), (z) -> {
				var em = s.poseID;
				if(hasShiftDown()) {
					var al = s.getAlt();
					if(al.isPresent())
						em = al.get().poseID;
				}
				Channels.MAIN.clientHandle().send(new PosesPackets.EmoteRequest(em, false));
				this.close();
			});
			d.sizing(Sizing.fixed(28));
			d.tooltip(Text.translatable(GreenResurgence.ID + ".emotes." + s.poseID));
			d.icon("emotes/" + s.poseID);
			return d;
		};
		for(int i = 0; i < PosesManager.emotes.size(); i += 3) {
			var grid = Containers.grid(Sizing.fill(100), Sizing.content(), 1, 3);
			grid.horizontalAlignment(HorizontalAlignment.CENTER);
			grid.padding(Insets.top(2));
			var emote = PosesManager.emotes.get(i);
			var bt = btGen.apply(emote);
			emotesBts.put(emote, bt);
			grid.child(bt, 0, 0);
			if(i + 1 < PosesManager.emotes.size()) {
				var emote1 = PosesManager.emotes.get(i + 1);
				var bt1 = btGen.apply(emote1);
				emotesBts.put(emote1, bt1);
				grid.child(bt1, 0, 1);
			}
			if(i + 2 < PosesManager.emotes.size()) {
				var emote1 = PosesManager.emotes.get(i + 2);
				var bt1 = btGen.apply(emote1);
				emotesBts.put(emote1, bt1);
				grid.child(bt1, 0, 2);
			}
			emotes.child(grid);
		}
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if(statsPanel != null && client.world.getTime() % 20 == 0)
			fillStats(statsPanel);
		if(hasShiftDown() != isShift) {
			emotesBts.forEach((k, v) -> {
				var em = k;
				if(hasShiftDown()) {
					var em1 = k.getAlt();
					if(em1.isPresent())
						em = em1.get();
				}
				v.tooltip(Text.translatable(GreenResurgence.ID + ".emotes." + em.poseID));
				v.icon("emotes/" + em.poseID);
			});
			isShift = hasShiftDown();
		}
	}


	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {
	}
}
