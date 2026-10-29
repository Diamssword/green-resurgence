package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.containers.player.HoloPanelConfig;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

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

		default void handleEditRightClick(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config, boolean hasParent) {
			var sets = getSettings();
			if(!hasParent && (sets == null || !sets.needPanel()))
				baseSwapPanel(scr, recreateFn, config);
			else if(sets != null)
				sets.mount(mouseX, mouseY, scr, recreateFn, config, hasParent);
		}

		public static void baseSwapPanel(SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config) {
			scr.setLayout("survival/subpanel/swap_panel");
			var cols = Math.max(1, (int) Math.floor((scr.width() - 2) / 22f));
			var grid = scr.getRoot().childById(FreeRowGridLayout.class, "listPanel");
			if(grid != null) {
				grid.setColumns(cols);
				DynamicPanels.subpanels.forEach((k, v) -> {
					grid.child(DynamicPanels.makPanelButton(v, (b) -> {
						config.setPanel(k);
						recreateFn.accept(scr, config);
					}));
				});
				if(grid.parent() instanceof FlowLayout fl) {
					fl.child(Components.button(Text.translatable("gui.green_resurgence.generic.back"), b -> {
						recreateFn.accept(scr, config);
					}).renderer(HoloPanels.button()).margins(Insets.top(1)));
				}
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
		public void decode(HoloPanelConfig config);

		public void mount(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config, boolean hasParent);

		public default boolean needPanel() {
			return true;
		}

		public NbtCompound encode(NbtCompound encode);

		public static FreeRowGridLayout basePanel(SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config, boolean hasParent) {
			scr.setLayout("survival/subpanel/swap_panel");

			var flow = scr.getRoot().childById(FlowLayout.class, "mainFlow");
			var lab = flow.childById(AdaptiveLabelComponent.class, "editLabel");
			if(lab != null)
				lab.text(Text.literal("Settings"));
			if(flow != null) {
				var c = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
				c.child(Components.button(Text.translatable("gui.green_resurgence.generic.back"), b -> {
					recreateFn.accept(scr, config);
				}).renderer(HoloPanels.button()));
				if(!hasParent) {
					c.child(Components.button(Text.literal("Panels"), b -> {
						SubPanelInstance.baseSwapPanel(scr, recreateFn, config);
					}).renderer(HoloPanels.button()));
				}
				flow.child(c);
			}
			return scr.getRoot().childById(FreeRowGridLayout.class, "listPanel");
		}
	}


}