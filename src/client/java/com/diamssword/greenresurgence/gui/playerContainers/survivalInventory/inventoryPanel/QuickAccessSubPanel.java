package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.containers.player.HoloPanelConfig;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class QuickAccessSubPanel extends InstancedSubPanel {
	public static final int BUTTON_SIZE = 22;

	public QuickAccessSubPanel() {
		super("quickAccess", "gui/subpanel/quick_access", "survival/subpanel/empty", Instance::new);
	}

	static class Instance implements SubPanelInstance {
		private PlayerBasedGui<?> gui;
		Settings settings = new Settings();
		SubScreenLayout subScreen;
		SubPanelInstance subInstance;

		@Override
		public void panelTick() {

		}

		@Override
		public @Nullable SubPanelSettings getSettings() {
			return settings;
		}

		@Override
		public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {
			if(subScreen != null) {
				if(settings.mode == 0 || settings.mode == 1)
					subScreen.verticalSizing(Sizing.fixed(root.height() - (BUTTON_SIZE + 1)));
				else
					subScreen.horizontalSizing(Sizing.fixed(root.width() - (BUTTON_SIZE + 1)));
				if(subInstance != null)
					subInstance.onResize(subScreen, gui);
			}
		}

		@Override
		public void handleEditRightClick(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config, boolean hasParent) {
			if(subScreen != null && mouseX >= subScreen.x() - scr.x() && mouseY >= subScreen.y() - scr.y() && mouseX <= subScreen.x() - scr.x() + subScreen.width() && mouseY <= subScreen.y() - scr.y() + subScreen.height()) {

				BiConsumer<SubScreenLayout, HoloPanelConfig> f = (a, b) -> {
					settings.panel = b.getPanel();
					settings.settings = b.getSettings() != null ? b.getSettings() : new NbtCompound();
					settings.save();
					createComponent(Optional.of(a), settings.panel, settings.settings, gui);
				};
				var cfg = new HoloPanelConfig(0, 0, 0, 0, settings.panel);
				cfg.setSettings(settings.settings);
				subInstance.handleEditRightClick(mouseX, mouseY, subScreen, f, cfg, true);
			} else
				SubPanelInstance.super.handleEditRightClick(mouseX, mouseY, scr, recreateFn, config, hasParent);
		}

		@Override
		public void build(FlowLayout root, PlayerBasedGui<?> gui) {
			this.gui = gui;
			createComponent(Optional.empty(), settings.panel, settings.settings, gui);

			var flow1 = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
			var flowBar = Containers.horizontalFlow(Sizing.content(), Sizing.fill(100));
			if(settings.mode == 0)
				flowBar.verticalAlignment(VerticalAlignment.BOTTOM);
			else
				flowBar.verticalAlignment(VerticalAlignment.TOP);
			var scr1 = Containers.horizontalScroll(Sizing.fill(100), Sizing.fixed(BUTTON_SIZE + 1), flowBar).margins(Insets.horizontal(2));
			if(settings.mode == 2 || settings.mode == 3) {
				flow1 = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
				flowBar = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
				scr1 = Containers.verticalScroll(Sizing.fixed(BUTTON_SIZE + 1), Sizing.fill(100), flowBar).margins(Insets.vertical(2));
				if(settings.mode == 2)
					flowBar.horizontalAlignment(HorizontalAlignment.RIGHT);
				else
					flowBar.horizontalAlignment(HorizontalAlignment.LEFT);
			}
			flow1.id("container");
			flowBar.id("dock").margins(Insets.of(settings.mode == 1 ? -1 : 0, settings.mode == 0 ? -1 : 0, settings.mode == 3 ? -1 : 0, settings.mode == 2 ? -1 : 0));
			flowBar.padding(Insets.of(1));
			if(settings.mode == 0 || settings.mode == 2)
				flow1.child(scr1);
			root.child(flow1);
			flow1.child(subScreen);
			root.onChildMutated(subScreen);
			if(settings.mode == 1 || settings.mode == 3)
				flow1.child(scr1);
			updateButtons(flowBar, gui, settings.panel);
		}

		private void updateButtons(FlowLayout container, PlayerBasedGui<?> gui, String selected) {
			container.clearChildren();

			DynamicPanels.subpanels.forEach((k, v) ->
			{
				if(!k.equals("quickaccess")) {
					var b1 = DynamicPanels.makPanelButton(v, b -> {
						settings.panel = k;
						settings.settings = new NbtCompound();
						settings.save();
						createComponent(Optional.of(subScreen), k, new NbtCompound(), gui);
						updateButtons(container, gui, k);
					}).activated(selected.equals(k)).sizing(Sizing.fixed(BUTTON_SIZE - 2));
					if(selected.equals(k)) {
						if(settings.mode == 0 || settings.mode == 1)
							b1.verticalSizing(Sizing.fixed(22));
						else
							b1.horizontalSizing(Sizing.fixed(22));
					}
					container.child(b1);
				}
			});

		}

		protected void createComponent(Optional<SubScreenLayout> layout, String panel, NbtCompound settings, PlayerBasedGui<?> gui) {

			var c = new HoloPanelConfig(0, 0, 0, 0, panel);
			c.setSettings(settings);
			var b = DynamicPanels.buildPanel(layout, c, gui);
			subInstance = b.getRight();
			subScreen = b.getLeft();

		}


	}

	static class Settings implements SubPanelSettings {
		public String panel = DynamicPanels.DEFAULT_PANEL;
		public NbtCompound settings = new NbtCompound();
		public int mode = 0;

		HoloPanelConfig config;

		public void save() {
			encode(config.getOrCreateSettings());
			config.markDirty();
		}

		@Override
		public void decode(HoloPanelConfig config) {
			this.config = config;
			var s = config.getSettings();
			if(s != null) {
				if(s.contains("p"))
					panel = s.getString("p");
				if(s.contains("s"))
					settings = s.getCompound("s");
				if(s.contains("m"))
					mode = s.getInt("m");
			}

		}

		@Override
		public void mount(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, HoloPanelConfig> recreateFn, HoloPanelConfig config, boolean hasParent) {
			var g = SubPanelSettings.basePanel(scr, recreateFn, config, hasParent);
			g.setColumns(1);
			var b = Components.button(Text.translatable("gui.green_resurgence.subpanels.quickAccess.mode." + mode), (d -> {
				mode++;
				if(mode > 3)
					mode = 0;
				save();
				d.setMessage(Text.translatable("gui.green_resurgence.subpanels.quickAccess.mode." + mode));
			})).renderer(HoloPanels.button());
			g.child(b);
		}

		@Override
		public boolean needPanel() {
			return true;
		}

		@Override
		public NbtCompound encode(NbtCompound encode) {
			encode.putString("p", panel);
			encode.put("s", settings);
			encode.putInt("m", mode);
			return encode;
		}

	}
}
