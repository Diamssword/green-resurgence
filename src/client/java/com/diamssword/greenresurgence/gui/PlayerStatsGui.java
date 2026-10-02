package com.diamssword.greenresurgence.gui;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.characters.api.stats.StatsRole;
import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.hud.BarComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StatsPackets;
import com.diamssword.greenresurgence.systems.character.classes.IClasseAdditionalTooltips;
import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlayerStatsGui extends BaseUIModelScreen<FlowLayout> {

	public PlayerStatsGui() {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("character/stats")));
	}

	private FlowLayout statsPanel;

	@Override
	public void tick() {
		if(statsPanel != null && client.world.getTime() % 20 == 0)
			fillStats(statsPanel);
	}

	@Override
	protected void build(FlowLayout root) {
		var playerComp = root.childById(PlayerComponent.class, "playerSkin");
		var player = playerComp.entity();
		var dt = ComponentManager.getPlayerDatas(player);
		var dt1 = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
		dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY).getBackpackStack());
		dt.getAppearence().clonePlayerAppearance(MinecraftClient.getInstance().player);
		var np = root.childById(FlowLayout.class, "namePanel");

		var chara = ComponentManager.getPlayerCharacter(MinecraftClient.getInstance().player).getCurrentCharacter();
		if(chara != null) {
			np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.firstname + " " + chara.stats.lastname)).horizontalSizing(Sizing.fill(100)));
			np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.origine)).horizontalSizing(Sizing.fill(100)));
			np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.faction)).horizontalSizing(Sizing.fill(100)));
			np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.job)).horizontalSizing(Sizing.fill(100)));
		}
		var pane = root.childById(FlowLayout.class, "listPanel");
		for(var k : CharactersApi.stats().getRoles().keySet()) {
			var c = new ClickableLayoutComponent(Sizing.fill(98), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
			c.surface(Surface.flat(DrawUtils.whithAlpha(DrawUtils.GRAY_GREEN, 0xFF))).padding(Insets.of(2)).margins(Insets.of(1));
			c.verticalAlignment(VerticalAlignment.CENTER);
			var r = CharactersApi.stats().getRole(k);
			var dtC = ComponentManager.getPlayerDatas(client.player);
			c.onPress(v -> loadInfos(root.childById(FlowLayout.class, "infosPanel"), k, r.get()));
			c.child(new AdaptiveLabelComponent(ColorUtils.whiteText(r.get().name + "   Niv." + dtC.getStats().getLevel(k))).horizontalSizing(Sizing.fill(80)));
			var bt = io.wispforest.owo.ui.component.Components.button(Text.literal("\uD83C\uDFB2"), (r1) -> {
				Channels.MAIN.clientHandle().send(new StatsPackets.RollStat(k));
				close();
			});
			bt.sizing(Sizing.fixed(16));
			bt.tooltip(Text.translatable("gui." + GreenResurgence.ID + ".playerstats.dice"));
			c.child(bt);
			pane.child(c);
		}
		statsPanel = root.childById(FlowLayout.class, "statsPanel");
		if(statsPanel != null)
			fillStats(statsPanel);
	}

	private void loadInfos(FlowLayout parent, String roleId, StatsRole role) {
		var st = ComponentManager.getPlayerDatas(client.player).getStats();
		parent.clearChildren();
		parent.child(Components.label(ColorUtils.whiteText(role.name)));
		var bar = new BarComponent(GreenResurgence.asRessource("textures/gui/hud/stamina.png"), 0, 0, 256, 10, 256, 64, true);
		bar.setFillPercent(CharactersApi.stats().percentOfXpForNext(client.player, roleId));
		bar.tooltip(Text.translatable(st.getXp(roleId) + "/" + CharactersApi.stats().getXpCostForLevel(st.getLevel(roleId) + 1) + " xp"));
		bar.horizontalSizing(Sizing.fill(90));
		parent.child(Components.label(ColorUtils.whiteText("WIP")));
		parent.child(bar);
		parent.child(paragraph(ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.stats.desc." + roleId)));
		addGlobalModInfos(parent, role, st.getLevel(roleId));
		var cur = st.getLevel(roleId);
		for(var i = 0; i < role.stages.length; i++) {
			addPalierInfo(parent, role, i, role.stages[i], role.stages[i] <= cur);
		}
	}

	private AdaptiveLabelComponent paragraph(Text text) {
		var c = new AdaptiveLabelComponent(text);
		c.verticalTextAlignment(VerticalAlignment.CENTER).horizontalSizing(Sizing.fill(90));
		return c;
	}

	private LabelComponent simple(Text text) {
		var c = new AdaptiveLabelComponent(text);
		c.verticalTextAlignment(VerticalAlignment.CENTER).horizontalSizing(Sizing.fill(90));
		return c;
	}

	private void addGlobalModInfos(FlowLayout parent, StatsRole role, int level) {
		var mods = role.getGlobalModifiers();
		DecimalFormat df = new DecimalFormat("0.#");
		if(!mods.isEmpty()) {
			parent.child(new AdaptiveLabelComponent(ColorUtils.textTranslated(GreenResurgence.ID + ".gui.stats.global_bonus", DrawUtils.ORANGE)).maximumScale(0.8f).horizontalSizing(Sizing.fill(90)));
			var text = ColorUtils.whiteText("");
			for(var p : mods.entrySet()) {
				var d = p.getValue().apply(level);
				text = text.append(ColorUtils.whiteText(" - ")).append(ColorUtils.whiteTextTranslated(p.getKey().getTranslationKey()));
				text = text.append(ColorUtils.whiteText(": +" + df.format(d.getValue() * 100) + "%"));
				var d1 = p.getValue().apply(1);
				text = text.append(ColorUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.stats.global_bonus.per_level", df.format(d1.getValue() * 100)));
				text = text.append("\n");
			}
			parent.child(paragraph(text).maximumScale(0.7f));
		}
	}

	private void addPalierInfo(FlowLayout parent, StatsRole role, int palier, int level, boolean unlocked) {
		var mods = role.getPalierInfos(palier);
		parent.child(new AdaptiveLabelComponent(ColorUtils.textTranslated(GreenResurgence.ID + ".gui.stats.palier.title", unlocked ? DrawUtils.ORANGE : DrawUtils.WHITE, palier + 1, level)).maximumScale(0.8f).horizontalSizing(Sizing.fill(90)));
		var text = ColorUtils.whiteText("");
		if(mods != null) {

			DecimalFormat df = new DecimalFormat("0.#");
			for(var p : mods.getModifiers().entrySet()) {
				var d = p.getValue();
				text = text.append(ColorUtils.whiteText(" - ")).append(ColorUtils.whiteTextTranslated(p.getKey().getTranslationKey()));
				text = text.append(ColorUtils.whiteText(": +" + df.format(d.getValue() * 100) + "%\n"));
			}
		}
		if(role instanceof IClasseAdditionalTooltips ctps) {
			var ls = new ArrayList<Text>();
			ctps.getTextForLevel(client.player, palier, ls);
			if(!ls.isEmpty()) {
				for(int i = 0; i < ls.size(); i++) {
					text.append(ColorUtils.whiteText(" - ").append(ls.get(i)));
					if(i < ls.size() - 1)
						text.append("\n");
				}

			}
		}
		parent.child(paragraph(text).maximumScale(0.7f));
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

	public boolean shouldPause() {
		return false;
	}
}