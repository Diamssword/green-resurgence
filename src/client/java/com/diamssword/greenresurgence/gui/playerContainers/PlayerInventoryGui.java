package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
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

	private AdaptiveLabelComponent.SyncedScale scaler = new AdaptiveLabelComponent.SyncedScale();
	private AdaptiveLabelComponent.SyncedScale scaler1 = new AdaptiveLabelComponent.SyncedScale();

	private void fillStats(FlowLayout parent) {
		scaler.clear();
		scaler1.clear();
		var player = client.player;
		parent.clearChildren();
		var cC1 = Containers.horizontalFlow(Sizing.fill(48), Sizing.content());
		cC1.horizontalAlignment(HorizontalAlignment.LEFT);
		var cC2 = Containers.horizontalFlow(Sizing.fill(48), Sizing.content());
		cC2.horizontalAlignment(HorizontalAlignment.LEFT);
		var c1 = Containers.verticalFlow(Sizing.fill(48), Sizing.content());
		var cM1 = Containers.verticalFlow(Sizing.fill(4), Sizing.content());
		var c2 = Containers.verticalFlow(Sizing.fill(48), Sizing.content());
		var c3 = Containers.verticalFlow(Sizing.fill(48), Sizing.content());
		var cM2 = Containers.verticalFlow(Sizing.fill(4), Sizing.content());
		var c4 = Containers.verticalFlow(Sizing.fill(48), Sizing.content());
		cC1.child(c1);
		cC1.child(cM1);
		cC1.child(c2);
		parent.child(cC1);
		cC2.child(c3);
		cC2.child(cM2);
		cC2.child(c4);
		parent.child(cC2);
		for(int i = 0; i < 4; i++) {
			cM1.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteText(":")).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.content(), Sizing.fixed(8)));
			cM2.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteText(":")).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.content(), Sizing.fixed(8)));
		}

		var pdata = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);


		statLabel(c1, c2, "health", pdata.healthManager.getHealthAmount() * 5f, pdata.healthManager.getMaxHealthAmount() * 5f);
		statLabel(c1, c2, "shield", pdata.healthManager.getShieldAmount() * 5f, pdata.healthManager.getMaxShieldAmount() * 5f);
		statLabel(c1, c2, "hunger", "full");
		statLabel(c1, c2, "thirst", "full");
		statLabel(c3, c4, "infection", pdata.healthManager.getContaminationAmount(), pdata.healthManager.getMaxContaminationAmount());
		statLabel(c3, c4, "stamina", pdata.healthManager.getEnergyAmount(), pdata.healthManager.getMaxEnergyAmount());
		statLabel(c3, c4, "oxygen", player.getAir(), player.getMaxAir());
		statLabel(c3, c4, "armor", player.getArmor());
	}

	private void statLabel(FlowLayout panel1, FlowLayout panel2, String text, double v1, double v2) {
		DecimalFormat df = new DecimalFormat("0.#");
		panel1.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.fill(100), Sizing.fixed(8)));
		panel2.child(new AdaptiveLabelComponent(scaler1, ColorUtils.whiteText(df.format(v1) + "/" + df.format(v2))).verticalTextAlignment(VerticalAlignment.CENTER).lineHeight(8).sizing(Sizing.fill(100), Sizing.fixed(8)).margins(Insets.right(1)));
	}

	private void statLabel(FlowLayout panel1, FlowLayout panel2, String text, int value) {
		panel1.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.fill(100), Sizing.fixed(8)));
		panel2.child(new AdaptiveLabelComponent(scaler1, ColorUtils.whiteText(value + "")).verticalTextAlignment(VerticalAlignment.CENTER).lineHeight(8).sizing(Sizing.fill(100), Sizing.fixed(8)).margins(Insets.right(1)));
	}

	private void statLabel(FlowLayout panel1, FlowLayout panel2, String text, String value) {
		panel1.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text)).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.fill(100), Sizing.fixed(8)));
		panel2.child(new AdaptiveLabelComponent(scaler1, ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text + "." + value)).verticalTextAlignment(VerticalAlignment.CENTER).lineHeight(8).sizing(Sizing.fill(100), Sizing.fixed(8)).margins(Insets.right(1)));
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
