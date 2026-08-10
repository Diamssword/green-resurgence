package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.utils.TextUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
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

	private FreeRowGridLayout statsPanel;
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
		statsPanel = rootComponent.childById(FreeRowGridLayout.class, "statsPanel");
		if(statsPanel != null)
			fillStats(statsPanel);
		var emotes = rootComponent.childById(FlowLayout.class, "emoteLayout");
		if(emotes != null)
			setupEmotePanel(emotes);
	}

	private void fillStats(FreeRowGridLayout parent) {

		var player = client.player;
		var pdata = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);
		parent.clear();
		parent.child(statLabel("health", pdata.healthManager.getHealthAmount() * 5f, pdata.healthManager.getMaxHealthAmount() * 5f));
		parent.child(statLabel("infection", pdata.healthManager.getContaminationAmount(), pdata.healthManager.getMaxContaminationAmount()));
		parent.child(statLabel("shield", pdata.healthManager.getShieldAmount() * 5f, pdata.healthManager.getMaxShieldAmount() * 5f));
		parent.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory.hunger")).lineHeight(8));
		parent.child(statLabel("stamina", pdata.healthManager.getEnergyAmount(), pdata.healthManager.getMaxEnergyAmount()));
		parent.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory.thirst")).lineHeight(8));
		parent.child(statLabel("oxygen", player.getAir(), player.getMaxAir()));
		parent.child(Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory.armor").append(TextUtils.whiteText(": " + player.getArmor()))).lineHeight(8));
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

	private LabelComponent statLabel(String text, double v1, double v2) {
		DecimalFormat df = new DecimalFormat("0.#");
		var c = Components.label(TextUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.survival_inventory." + text).append(TextUtils.whiteText(": " + df.format(v1) + "/" + df.format(v2)))).lineHeight(8);
		c.margins(Insets.right(1));
		return c;
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
