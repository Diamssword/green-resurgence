package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.DynamicPanels;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContainerSubPanel extends InstancedSubPanel {

	public ContainerSubPanel() {
		super("container", "gui/subpanel/container", "survival/subpanel/empty", Instance::new);
	}

	static class Instance implements SubPanelInstance {
		Settings settings = new Settings();
		List<Pair<SubScreenLayout, SubPanelInstance>> panels = new ArrayList<>();
		private PlayerBasedGui<?> gui;

		@Override
		public void panelTick() {

		}

		@Override
		public @Nullable SubPanelSettings getSettings() {
			return settings;
		}

		@Override
		public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {

		}

		protected SubScreenLayout getLayout(int index) {
			if(index >= panels.size()) {
				for(int i = panels.size(); i <= index; i++) {
					settings.configs.add(new Pair<>("empty", ""));
					createComponent("empty", "", gui, -1);
				}
				settings.save();
				return panels.get(index).getLeft();
			}
			return panels.get(index).getLeft();
		}

		public void setPanel(int index, String panel) {
			if(index < settings.configs.size()) {
				settings.configs.get(0).setLeft(panel);
				settings.save();
			}

		}

		public void subBaseSwapPanel(SubScreenLayout scr, int index) {
			scr.setLayout("survival/subpanel/swap_panel");
			var cols = Math.max(1, (int) Math.floor((scr.width() - 2) / 22f));
			var grid = scr.getRoot().childById(FreeRowGridLayout.class, "listPanel");
			if(grid != null) {
				grid.setColumns(cols);
				DynamicPanels.subpanels.forEach((k, v) -> {
					grid.child(Components.button(Text.literal(v.guiName().getString().substring(0, 2)), (b) -> {
						setPanel(index, k);
						createComponent(k, "", gui, index);
					}).renderer(HoloPanels.button()).tooltip(v.guiName()));
				});
			}
		}

		@Override
		public void build(FlowLayout root, PlayerBasedGui<?> gui) {
			this.gui = gui;
			panels.clear();
			settings.configs.forEach(a -> createComponent(a.getLeft(), a.getRight(), gui, -1));
			var main = root;
			root.padding(Insets.of(5));
			if(main != null) {
				if(settings.mode == 0) {
					var sub = getLayout(0);
					main.child(sub);
					main.onChildMutated(sub);
				}
			}

		}

		protected void createComponent(String panel, String settings, PlayerBasedGui<?> gui, int index) {
			if(panel.equals("empty")) {
				var sub = new SubScreenLayout(Sizing.fill(100), Sizing.fill(100), FlowLayout.Algorithm.VERTICAL, "survival/subpanel/empty");
				subBaseSwapPanel(sub, panels.size());
				var p = new Pair<SubScreenLayout, SubPanelInstance>(sub, new SubPanelInstance() {
					@Override
					public void panelTick() {

					}

					@Override
					public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {

					}

					@Override
					public void build(FlowLayout root, PlayerBasedGui<?> gui) {

					}
				});
				if(index < 0)
					panels.add(p);
				else
					panels.set(index, p);
			} else {
				var c = new DynamicPanels.PanelConfig(0, 0, 0, 0, panel);
				c.setSettings(settings);
				if(index < 0)
					panels.add(DynamicPanels.buildPanel(c, gui));
				else
					panels.set(index, DynamicPanels.buildPanel(c, gui));
			}


		}

	}

	static class Settings implements SubPanelSettings {
		int mode = 0;
		List<Pair<String, String>> configs = new ArrayList<>();
		DynamicPanels.PanelConfig config;

		public void save() {
			config.setSettings(encode());
		}

		@Override
		public void decode(DynamicPanels.PanelConfig config) {
			this.config = config;
			var s = config.getSettings();
			configs.clear();
			if(s != null) {
				var ob = new GsonBuilder().create().fromJson(s, JsonObject.class);
				if(ob.has("mode"))
					mode = ob.get("mode").getAsInt();
				if(ob.has("panels")) {
					ob.getAsJsonArray("panels").forEach(p -> {
						var ob1 = p.getAsJsonObject();
						if(ob1.has("p")) {
							String s1 = "";
							if(ob1.has("s"))
								s1 = ob1.get("s").getAsString();
							configs.add(new Pair<>(ob1.get("p").getAsString(), s1));
						}
					});
				}
			}

		}

		@Override
		public void mount(double mouseX, double mouseY, SubScreenLayout scr, DynamicPanels instance, DynamicPanels.PanelConfig config) {

		}

		@Override
		public String encode() {
			var ob = new JsonObject();
			ob.addProperty("mode", mode);
			var arr = new JsonArray();
			configs.forEach(c -> {
				var ob1 = new JsonObject();
				ob1.addProperty("p", c.getLeft());
				ob1.addProperty("s", c.getRight());
				arr.add(ob1);
			});
			ob.add("panels", arr);
			return ob.toString();
		}
	}
}
