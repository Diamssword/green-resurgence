package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
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
		int offset = 0;

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
			}
		}

		@Override
		public void handleEditRightClick(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, DynamicPanels.PanelConfig> recreateFn, DynamicPanels.PanelConfig config, boolean hasParent) {
			if(subScreen != null && mouseX >= subScreen.x() - scr.x() && mouseY >= subScreen.y() - scr.y() && mouseX <= subScreen.x() - scr.x() + subScreen.width() && mouseY <= subScreen.y() - scr.y() + subScreen.height()) {

				BiConsumer<SubScreenLayout, DynamicPanels.PanelConfig> f = (a, b) -> {
					settings.panel = b.getPanel();
					settings.settings = b.getSettings() != null ? b.getSettings() : "";
					settings.save();
					createComponent(Optional.of(a), settings.panel, settings.settings, gui);
				};
				var cfg = new DynamicPanels.PanelConfig(0, 0, 0, 0, settings.panel);
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
			var scr1 = Containers.horizontalScroll(Sizing.fill(100), Sizing.fixed(BUTTON_SIZE + 1), flowBar);
			if(settings.mode == 2 || settings.mode == 3) {
				flow1 = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
				flowBar = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
				scr1 = Containers.verticalScroll(Sizing.fixed(BUTTON_SIZE + 1), Sizing.fill(100), flowBar);
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
			updateButtons(flowBar, gui);
		}

		private void updateButtons(FlowLayout container, PlayerBasedGui<?> gui) {
			container.clearChildren();

			DynamicPanels.subpanels.forEach((k, v) -> container.child(Components.button(Text.literal(v.guiName().getString().substring(0, 2)), b -> {
				settings.panel = k;
				settings.settings = "";
				settings.save();
				createComponent(Optional.of(subScreen), k, "", gui);

			}).renderer(HoloPanels.button()).tooltip(v.guiName()).sizing(Sizing.fixed(BUTTON_SIZE - 2))));

		}

		protected void createComponent(Optional<SubScreenLayout> layout, String panel, String settings, PlayerBasedGui<?> gui) {

			var c = new DynamicPanels.PanelConfig(0, 0, 0, 0, panel);
			c.setSettings(settings);
			var b = DynamicPanels.buildPanel(layout, c, gui);
			subInstance = b.getRight();
			subScreen = b.getLeft();

		}


	}

	static class Settings implements SubPanelSettings {
		public String panel = DynamicPanels.DEFAULT_PANEL;
		public String settings = "";
		public int mode = 0;

		DynamicPanels.PanelConfig config;

		public void save() {
			config.setSettings(encode());
		}

		@Override
		public void decode(DynamicPanels.PanelConfig config) {
			this.config = config;
			var s = config.getSettings();
			if(s != null) {
				var ob = new GsonBuilder().create().fromJson(s, JsonObject.class);
				if(ob != null) {
					if(ob.has("p"))
						panel = ob.get("p").getAsString();
					if(ob.has("s"))
						settings = ob.get("s").getAsString();
					if(ob.has("m"))
						mode = ob.get("m").getAsInt();
				}
			}

		}

		@Override
		public void mount(double mouseX, double mouseY, SubScreenLayout scr, BiConsumer<SubScreenLayout, DynamicPanels.PanelConfig> recreateFn, DynamicPanels.PanelConfig config, boolean hasParent) {
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
		public String encode() {
			var ob = new JsonObject();
			ob.addProperty("p", panel);
			ob.addProperty("s", settings);
			ob.addProperty("m", mode);
			return ob.toString();
		}
	}
}
