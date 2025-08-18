package com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.PlayerStatsGui;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StatsPackets;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class CharacterStatsPanel extends SimpleSubPanel {


	public CharacterStatsPanel() {
		super("Personnage", "gui/subpanel/stats_icon", "survival/subpanel/stats");
	}

	private void expand(ScrollContainer<FlowLayout> scroll, Size v, FlowLayout root, int topH) {
		scroll.verticalSizing(Sizing.fixed(v.height() - (topH + root.gap() + root.padding().get().vertical())));
		root.onChildMutated(scroll);
	}

	@Override
	public void build(FlowLayout root, PlayerBasedGui<?> gui, boolean fullSize) {
		var scroll = root.childById(ScrollContainer.class, "scroll1");
		var p1 = root.childById(FlowLayout.class, "flow1");
		if (root.parent() instanceof SubScreenLayout sl) {
			sl.size.observe(v -> expand(scroll, v, root, p1.verticalSizing().get().value));
			expand(scroll, sl.size.get(), root, p1.verticalSizing().get().value);
		}
		var playerComp = root.childById(PlayerComponent.class, "playerSkin");
		var player = playerComp.entity();
		var cp = new NbtCompound();
		var dt = ComponentManager.getPlayerDatas(player);
		var dt1 = player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY);
		dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(com.diamssword.greenresurgence.systems.Components.PLAYER_INVENTORY).getBackpackStack());
		dt.getAppearence().clonePlayerAppearance(MinecraftClient.getInstance().player);
		var np = root.childById(FlowLayout.class, "namePanel");
		var chara = ComponentManager.getPlayerCharacter(MinecraftClient.getInstance().player).getCurrentCharacter();
		if (chara != null) {
			np.child(Components.label(DrawUtils.whiteText(chara.stats.firstname + " " + chara.stats.lastname)));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.origine)));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.faction)));
			np.child(Components.label(DrawUtils.whiteText(chara.stats.job)));
		}
		var pane = root.childById(FreeRowGridLayout.class, "listPanel");
		for (var k : CharactersApi.stats().getRoles().keySet()) {
			var c = Containers.horizontalFlow(Sizing.fill(49), Sizing.fixed(20));
			c.surface(Surface.flat(DrawUtils.whithAlpha(DrawUtils.GRAY_GREEN, 0xFF))).padding(Insets.of(2)).margins(Insets.of(1));
			c.verticalAlignment(VerticalAlignment.CENTER);
			var r = CharactersApi.stats().getRole(k);
			c.child(Components.label(DrawUtils.whiteText(r.get().name)).horizontalSizing(Sizing.fill(50)));
			var btr = ButtonComponent.Renderer.texture(GreenResurgence.asRessource("textures/gui/dice.png"), 0, 0, 20, 40);
			var bt = io.wispforest.owo.ui.component.Components.button(Text.literal("\uD83C\uDFB2"), (r1) -> {
				Channels.MAIN.clientHandle().send(new StatsPackets.RollStat(k));
				gui.close();
			});
			bt.sizing(Sizing.fixed(16));
			//bt.renderer(btr);
			bt.tooltip(DrawUtils.whiteText("Lancer un dés"));
			c.child(bt);
			pane.child(c);
		}
		root.childById(ButtonComponent.class, "openGui").onPress(p -> {
			MinecraftClient.getInstance().setScreen(new PlayerStatsGui());
		});
	}
}