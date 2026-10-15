package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.greenresurgence.gui.PlayerStatsGui;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StatsPackets;
import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class CharacterStatsPanel extends InstancedSubPanel {


	public CharacterStatsPanel() {
		super("character", "gui/subpanel/stats_icon", "survival/subpanel/stats", Instance::new);
	}


	@Override
	public int preferredWidth() {
		return 100;
	}

	@Override
	public int preferredHeight() {
		return 100;
	}

	static class Instance implements SubPanelInstance {
		public Settings settings = new Settings();

		@Override
		public void panelTick() {

		}

		@Override
		public @Nullable SubPanelSettings getSettings() {
			return settings;
		}

		@Override
		public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {

			var scroll = root.childById(ScrollContainer.class, "scroll1");
			if(settings.showCard) {
				var p1 = root.childById(FlowLayout.class, "flow1");
				if(p1 != null) {
					if(settings.showList && scroll != null)
						scroll.verticalSizing(Sizing.fixed(root.height() - root.gap() - root.padding().get().vertical() - p1.height()));
					else
						p1.sizing(Sizing.fill(100));
				}
			} else if(scroll != null)
				scroll.verticalSizing(Sizing.fill(100));
		}

		private void expand(ScrollContainer<FlowLayout> scroll, Size v, FlowLayout root, int topH) {
			scroll.verticalSizing(Sizing.fixed(v.height() - (topH + root.gap() + root.padding().get().vertical())));
			root.onChildMutated(scroll);
		}

		@Override
		public void build(FlowLayout root, PlayerBasedGui<?> gui) {
			var scroll = root.childById(ScrollContainer.class, "scroll1");
			var p1 = root.childById(FlowLayout.class, "flow1");
			var np = root.childById(FlowLayout.class, "namePanel");
			if(!(settings.showList && settings.showCard)) {
				root.padding(Insets.of(0));
				root.surface(Surface.BLANK);
			}
			if(settings.showCard || !settings.showList) {
				var playerComp = root.childById(PlayerComponent.class, "playerSkin");
				if(!settings.showPlayer) {
					p1.removeChild(playerComp);
					np.horizontalSizing(Sizing.fill(100));
					np.verticalSizing(Sizing.fill(100));
					np.horizontalAlignment(HorizontalAlignment.CENTER);
					np.margins(Insets.of(0));
					np.padding(Insets.of(3));
				}
				var player = playerComp.entity();
				var dt = ComponentManager.getPlayerDatas(player);
				var dt1 = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
				dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY).getBackpackStack());
				dt.getAppearence().clonePlayerAppearance(MinecraftClient.getInstance().player);

				var chara = ComponentManager.getPlayerCharacter(MinecraftClient.getInstance().player).getCurrentCharacter();
				if(chara != null) {
					np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.firstname + " " + chara.stats.lastname)).horizontalSizing(Sizing.fill(100)));
					np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.origine)).horizontalSizing(Sizing.fill(100)));
					np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.faction)).horizontalSizing(Sizing.fill(100)));
					np.child(new AdaptiveLabelComponent(ColorUtils.whiteText(chara.stats.job)).horizontalSizing(Sizing.fill(100)));
					np.child(Components.button(Text.translatable("gui.green_resurgence.generic.open"), (f) -> {
						MinecraftClient.getInstance().setScreen(new PlayerStatsGui());
					}).renderer(HoloPanels.button()).margins(Insets.top(2)));
				}
			} else
				root.removeChild(p1);
			if(settings.showList) {
				var pane = root.childById(FreeRowGridLayout.class, "listPanel");
				var scaler = new AdaptiveLabelComponent.SyncedScale();
				for(var k : CharactersApi.stats().getRoles().keySet()) {
					var c = Containers.horizontalFlow(Sizing.fill(49), Sizing.fixed(20));
					c.surface(HoloPanels.panel(true, false)).padding(Insets.of(2)).margins(Insets.of(1));
					c.verticalAlignment(VerticalAlignment.CENTER);
					var r = CharactersApi.stats().getRole(k);
					var bt = io.wispforest.owo.ui.component.Components.button(Text.literal("\uD83C\uDFB2"), (r1) -> {
						Channels.MAIN.clientHandle().send(new StatsPackets.RollStat(k));
						gui.close();
					});
					bt.sizing(Sizing.fixed(16)).margins(Insets.right(2));
					bt.renderer(HoloPanels.button());
					bt.tooltip(Text.translatable("gui.green_resurgence.playerstats.dice"));
					c.child(bt);
					c.child(new AdaptiveLabelComponent(scaler, ColorUtils.whiteText(r.get().name)).horizontalTextAlignment(HorizontalAlignment.CENTER).verticalTextAlignment(VerticalAlignment.CENTER).sizing(Sizing.fill(55), Sizing.fill(90)));
					pane.child(c);
				}
			} else
				root.removeChild(scroll);
		}
	}

	static class Settings implements SubPanelSettings {
		public boolean showPlayer = true;
		public boolean showList = true;
		public boolean showCard = true;

		@Override
		public void decode(DynamicPanels.PanelConfig config) {
			var s = config.getSettings();
			if(s != null) {
				var splitted = s.split(",");
				if(splitted.length > 0)
					showPlayer = !"false".equals(splitted[0]);
				if(splitted.length > 1)
					showList = !"false".equals(splitted[1]);
				if(splitted.length > 2)
					showCard = !"false".equals(splitted[2]);
			}
		}

		@Override
		public void mount(double mouseX, double mouseY, SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config) {
			var g = SubPanelSettings.basePanel(scr, instance, config);
			g.setColumns(1);
			var b = Components.checkbox(Text.literal("Model")).checked(showPlayer);
			b.onChanged(c -> {
				showPlayer = c;
				config.setSettings(encode());
			});
			b.tooltip(Text.literal("Show Player Model"));
			g.child(b);
			var b1 = Components.checkbox(Text.literal("Card")).checked(showCard);
			b1.onChanged(c -> {
				showCard = c;
				config.setSettings(encode());
			});
			b1.tooltip(Text.literal("Show Info Card"));
			g.child(b1);
			var b2 = Components.checkbox(Text.literal("List")).checked(showList);
			b2.onChanged(c -> {
				showList = c;
				config.setSettings(encode());
			});
			b2.tooltip(Text.literal("Show Dice List"));
			g.child(b2);
		}

		@Override
		public String encode() {
			return showPlayer + "," + showList + ',' + showCard;
		}
	}
}