package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.hud.BarComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StatsPackets;
import com.diamssword.greenresurgence.systems.character.stats.ClassesLoader;
import com.diamssword.greenresurgence.systems.character.stats.StatsRole;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class PlayerStatsGui extends BaseUIModelScreen<FlowLayout> {

	public PlayerStatsGui() {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("character/stats")));
	}

	private FreeRowGridLayout statsPanel;

	@Override
	public void tick() {
		if (statsPanel != null && client.world.getTime() % 20 == 0)
			fillStats(statsPanel);
	}

	@Override
	protected void build(FlowLayout root) {
		var playerComp = root.childById(PlayerComponent.class, "playerSkin");
		var player = playerComp.entity();
		var cp = new NbtCompound();
		MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA).writeToNbt(cp);
		var dt = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);
		dt.readFromNbt(cp);
		var dt1 = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
		dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY).getBackpackStack());
		dt.appearance.refreshSkinDataForFakePlayer(MinecraftClient.getInstance().player);
		var np = root.childById(FlowLayout.class, "namePanel");
		var chara = MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_CHARACTERS).getCurrentCharacter();
		if (chara != null) {
			np.child(Components.label(DrawUtils.whiteText(chara.stats.firstname + " " + chara.stats.lastname)).lineHeight(8));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.origine)).lineHeight(8));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.faction)).lineHeight(8));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.job)).lineHeight(8));
		}
		var pane = root.childById(FlowLayout.class, "listPanel");
		for (var k : ClassesLoader.getRoles().keySet()) {
			var c = new ClickableLayoutComponent(Sizing.fill(98), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
			c.surface(Surface.flat(DrawUtils.whithAlpha(DrawUtils.GRAY_GREEN, 0xFF))).padding(Insets.of(2)).margins(Insets.of(1));
			c.verticalAlignment(VerticalAlignment.CENTER);
			var r = ClassesLoader.getRole(k);
			c.onPress(v -> loadInfos(root.childById(FlowLayout.class, "infosPanel"), k, r.get()));
			c.child(Components.label(DrawUtils.whiteTitle(r.get().name + "   Niv." + dt.stats.getLevel(k))).horizontalSizing(Sizing.fill(80)));
			var btr = ButtonComponent.Renderer.texture(GreenResurgence.asRessource("textures/gui/dice.png"), 0, 0, 20, 40);
			var bt = io.wispforest.owo.ui.component.Components.button(Text.literal("\uD83C\uDFB2"), (r1) -> {
				Channels.MAIN.clientHandle().send(new StatsPackets.RollStat(k));
				close();
			});
			bt.sizing(Sizing.fixed(16));
			//bt.renderer(btr);
			bt.tooltip(DrawUtils.whiteText("Lancer un dés"));
			c.child(bt);
			pane.child(c);
		}
		statsPanel = root.childById(FreeRowGridLayout.class, "statsPanel");
		if (statsPanel != null)
			fillStats(statsPanel);
	}

	private void loadInfos(FlowLayout parent, String roleId, StatsRole role) {
		var st = client.player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA).stats;
		parent.clearChildren();
		parent.child(Components.label(DrawUtils.whiteTitle(role.name)));
		var bar = new BarComponent(GreenResurgence.asRessource("textures/gui/hud/stamina.png"), 0, 0, 256, 10, 256, 64, true);
		bar.setFillPercent(ClassesLoader.instance.percentOfXpForNext(client.player, roleId));
		bar.tooltip(DrawUtils.whiteText(st.getXp(roleId) + "/" + ClassesLoader.instance.getXpCostForLevel(st.getLevel(roleId) + 1) + " xp"));
		bar.horizontalSizing(Sizing.fill(90));
		parent.child(bar);
		parent.child(paragraph(DrawUtils.whiteTextTranslated(GreenResurgence.ID + ".gui.stats.desc." + roleId)));
		addGlobalModInfos(parent, role, st.getLevel(roleId));
		var cur = st.getLevel(roleId);
		for (var i = 0; i < role.stages.length; i++) {
			addPalierInfo(parent, role, i, role.stages[i], role.stages[i] <= cur);
		}
	}

	private LabelComponent paragraph(Text text) {
		var c = Components.label(text);
		c.verticalTextAlignment(VerticalAlignment.CENTER).lineHeight(4).horizontalSizing(Sizing.fill(90));
		return c;
	}

	private LabelComponent simple(Text text) {
		var c = Components.label(text);
		c.verticalTextAlignment(VerticalAlignment.CENTER).lineHeight(6).horizontalSizing(Sizing.fill(90));
		return c;
	}

	private void addGlobalModInfos(FlowLayout parent, StatsRole role, int level) {
		var mods = role.getGlobalModifiers();
		DecimalFormat df = new DecimalFormat("0.#");
		if (!mods.isEmpty()) {
			parent.child(Components.label(DrawUtils.textTranslated(GreenResurgence.ID + ".gui.stats.global_bonus", DrawUtils.ORANGE)).horizontalSizing(Sizing.fill(90)));
			var text = DrawUtils.whiteText("");
			for (var p : mods.entrySet()) {
				var d = p.getValue().apply(level);
				text = text.append(" - ").append(DrawUtils.whiteTextTranslated(p.getKey().getTranslationKey()));
				text = text.append(": +" + df.format(d.getValue() * 100) + "%\n");
			}
			parent.child(paragraph(text));
		}
	}

	private void addPalierInfo(FlowLayout parent, StatsRole role, int palier, int level, boolean unlocked) {
		var mods = role.getPalierInfos(palier);
		if (mods != null) {
			parent.child(Components.label(DrawUtils.textTranslated(GreenResurgence.ID + ".gui.stats.palier.title", unlocked ? DrawUtils.ORANGE : DrawUtils.WHITE, palier + 1, level)).horizontalSizing(Sizing.fill(90)));
			DecimalFormat df = new DecimalFormat("0.#");
			var text = DrawUtils.whiteText("");
			for (var p : mods.getModifiers().entrySet()) {
				var d = p.getValue();
				text = text.append(" - ").append(DrawUtils.whiteTextTranslated(p.getKey().getTranslationKey()));
				text = text.append(": +" + df.format(d.getValue() * 100) + "%\n");
			}
			parent.child(paragraph(text));
		}
	}

	private void fillStats(FreeRowGridLayout parent) {

		var player = client.player;
		var pdata = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_DATA);
		parent.clear();
		parent.child(statLabel("Vie  ", pdata.healthManager.getHealthAmount(), pdata.healthManager.getMaxHealthAmount()));
		parent.child(statLabel("Infec ", 0f, 0f));
		parent.child(statLabel("Shield", pdata.healthManager.getShieldAmount(), pdata.healthManager.getMaxShieldAmount()));
		parent.child(Components.label(DrawUtils.whiteText("Faim   : Plein")).lineHeight(8));
		parent.child(statLabel("Endu  ", pdata.healthManager.getEnergyAmount(), pdata.healthManager.getMaxEnergyAmount()));
		parent.child(Components.label(DrawUtils.whiteText("Soif   : Plein")).lineHeight(8));
		parent.child(statLabel("Oxygen", player.getAir(), player.getMaxAir()));
		parent.child(Components.label(DrawUtils.whiteText("Armure: " + player.getArmor())).lineHeight(8));


	}

	private LabelComponent statLabel(String text, double v1, double v2) {
		DecimalFormat df = new DecimalFormat("0.#");
		var c = Components.label(DrawUtils.whiteText(text + ": " + df.format(v1) + "/" + df.format(v2))).lineHeight(8);
		c.margins(Insets.right(1));
		return c;
	}

	public boolean shouldPause() {
		return false;
	}
}