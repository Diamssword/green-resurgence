package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface SubPanel {
	String guiLocation();

	MutableText guiName();

	String guiIcon();

	SubPanelInstance instantiate();

	default int preferredHeight() {
		return 50;
	}

	default int preferredWidth() {
		return 50;
	}


	public interface SubPanelInstance {
		void panelTick();

		default int minWidth() {
			return 50;
		}

		default int minHeight() {
			return 50;
		}

		default void handleEditRightClick(double mouseX, double mouseY, SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config) {
			var sets = getSettings();
			if(sets == null)
				baseSwapPanel(scr, instance, config);
			else
				sets.mount(mouseX, mouseY, scr, instance, config);
		}

		public static void baseSwapPanel(SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config) {
			scr.setLayout("survival/subpanel/swap_panel");
			var cols = Math.max(1, (int) Math.floor((scr.width() - 2) / 22f));
			var grid = scr.getRoot().childById(FreeRowGridLayout.class, "listPanel");
			if(grid != null) {
				grid.setColumns(cols);
				DynamicPanels.subpanels.forEach((k, v) -> {
					grid.child(Components.button(Text.literal(v.guiName().getString().substring(0, 2)), (b) -> {
						config.setPanel(k);
						instance.recreateWindow(scr, config);
					}).renderer(HoloPanels.button()).tooltip(v.guiName()));
				});
			}
		}

		void onResize(FlowLayout root, PlayerBasedGui<?> gui);

		void build(FlowLayout root, PlayerBasedGui<?> gui);

		@Nullable
		default SubPanelSettings getSettings() {
			return null;
		}
	}

	public interface SubPanelSettings {
		public void decode(DynamicPanels.PanelConfig config);

		public void mount(double mouseX, double mouseY, SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config);

		public default boolean needPanel() {
			return true;
		}

		public String encode();

		public static FreeRowGridLayout basePanel(SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config) {
			scr.setLayout("survival/subpanel/swap_panel");

			var flow = scr.getRoot().childById(FlowLayout.class, "mainFlow");
			if(flow != null) {
				var c = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
				c.child(Components.button(Text.translatable("gui.green_resurgence.generic.back"), b -> {
					instance.recreateWindow(scr, config);
				}).renderer(HoloPanels.button()));
				c.child(Components.button(Text.literal("Panels"), b -> {
					SubPanelInstance.baseSwapPanel(scr, instance, config);
				}).renderer(HoloPanels.button()));
				flow.child(c);
			}
			return scr.getRoot().childById(FreeRowGridLayout.class, "listPanel");
		}
	}


}