package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EmotesPanel extends InstancedSubPanel {


	public EmotesPanel() {
		super("emotes", "gui/subpanel/emotes", "survival/subpanel/emotes", Instance::new);
	}


	private static class Instance implements SubPanelInstance {

		private Map<PosesManager.EmoteDef, RButtonComponent> emotesBts = new HashMap<>();
		private boolean isShift;

		@Override
		public void panelTick() {
			if(Screen.hasShiftDown() != isShift) {
				emotesBts.forEach((k, v) -> {
					var em = k;
					if(Screen.hasShiftDown()) {
						var em1 = k.getAlt();
						if(em1.isPresent())
							em = em1.get();
					}
					v.tooltip(Text.translatable(GreenResurgence.ID + ".emotes." + em.poseID));
					v.icon("emotes/" + em.poseID);
				});
				isShift = Screen.hasShiftDown();
			}
		}

		@Override
		public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {
			var grid = root.childById(FreeRowGridLayout.class, "emoteGrid");
			if(grid != null) {
				grid.setColumns((int) Math.max(1, Math.floor((root.width() - 4) / 30d)));
				grid.horizontalSizing(grid.horizontalSizing().get());
			}
		}

		@Override
		public int minWidth() {
			return 36;
		}

		@Override
		public int minHeight() {
			return 36;
		}

		@Override
		public void build(FlowLayout rootComponent, PlayerBasedGui<?> gui) {

			var grid = rootComponent.childById(FreeRowGridLayout.class, "emoteGrid");
			if(grid != null) {
				grid.setColumns((int) Math.max(1, Math.floor((rootComponent.width() - 4) / 30d)));
				setupEmotePanel(grid, gui);
			}

		}

		private void setupEmotePanel(FreeRowGridLayout grid, PlayerBasedGui<?> gui) {
			Function<PosesManager.EmoteDef, RButtonComponent> btGen = s -> {
				var d = new RButtonComponent(Text.literal(""), (z) -> {
					var em = s.poseID;
					if(Screen.hasShiftDown()) {
						var al = s.getAlt();
						if(al.isPresent())
							em = al.get().poseID;
					}
					Channels.MAIN.clientHandle().send(new PosesPackets.EmoteRequest(em, false));
					gui.close();
				});
				d.renderer(HoloPanels.button());
				d.sizing(Sizing.fixed(28));
				d.margins(Insets.of(2));
				d.tooltip(Text.translatable(GreenResurgence.ID + ".emotes." + s.poseID));
				d.icon("emotes/" + s.poseID);
				return d;
			};
			grid.clear();
			PosesManager.emotes.forEach(e -> {
				var bt = btGen.apply(e);
				grid.child(bt);
				emotesBts.put(e, bt);
			});
		}
	}

}