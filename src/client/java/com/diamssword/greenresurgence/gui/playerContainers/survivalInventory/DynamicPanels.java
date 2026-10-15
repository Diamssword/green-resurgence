package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory;

import com.diamssword.greenresurgence.GreenResurgenceClient;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicPanels {
	public static String DEFAULT_PANEL = "stats";
	public static Map<String, SubPanel> subpanels = new HashMap<>();

	static {
		subpanels.put(DEFAULT_PANEL, new CharacterStatsPanel());
		subpanels.put("craft", new PlayerCraftPanel());
		subpanels.put("survival_stats", new SurvivalStatsPanel());
		subpanels.put("emotes", new EmotesPanel());
	}

	public static class PanelConfig {
		private String panel;
		public int x;
		private int width;
		private int height;
		private int y;
		private boolean dirty = false;
		private String settings;

		public String getPanel() {
			return panel;
		}

		public void setPanel(String panel) {
			this.panel = panel;
		}

		@Nullable
		public String getSettings() {
			return settings;
		}

		public void setSettings(String settings) {
			this.settings = settings;
			this.dirty = true;
		}

		public static PanelConfig fromJson(JsonObject ob) {
			int x = 0;
			int y = 0;
			int w = 100;
			int h = 100;
			String p = DEFAULT_PANEL;
			if(ob.has("x"))
				x = ob.get("x").getAsInt();
			if(ob.has("y"))
				y = ob.get("y").getAsInt();
			if(ob.has("w"))
				w = ob.get("w").getAsInt();
			if(ob.has("h"))
				h = ob.get("h").getAsInt();
			if(ob.has("p"))
				p = ob.get("p").getAsString();
			var cf = new PanelConfig(x, y, w, h, p);
			if(ob.has("s"))
				cf.setSettings(ob.get("s").getAsString());
			return cf;
		}

		public JsonObject toJson() {
			var ob = new JsonObject();
			ob.addProperty("x", x);
			ob.addProperty("y", y);
			ob.addProperty("w", width);
			ob.addProperty("h", height);
			ob.addProperty("p", panel);
			if(getSettings() != null)
				ob.addProperty("s", getSettings());
			return ob;
		}

		public PanelConfig(int x, int y, int width, int height, String panel) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.panel = panel;
			this.dirty = true;
		}

		public PanelConfig scaledForNewRatio(Vector2i oldSize, Vector2i newSize) {

			var x = (int) Math.round((this.x / (double) oldSize.x) * newSize.x);
			var width = (int) Math.round((this.width / (double) oldSize.x) * newSize.x);
			var y = (int) Math.round((this.y / (double) oldSize.y) * newSize.y);
			var height = (int) Math.round((this.height / (double) oldSize.y) * newSize.y);
			return new PanelConfig(x, y, width, height, panel);
		}

		public void attach(DraggableResizable<?> component) {
			component.dragObservable.observe(p -> {
				this.x = p.x;
				this.y = p.y;
				this.width = p.z;
				this.height = p.w;
				dirty = true;
			});
		}
	}

	PlayerInventoryGui gui;
	private FlowLayout root;
	private int saveIn = -1;
	private boolean editMode = false;
	private boolean firsTick = false;
	private final List<PanelConfig> config = new ArrayList<>();
	private Vector2i lastParentSize;
	private final Map<DraggableResizable<SubScreenLayout>, SubPanel.SubPanelInstance> panels = new HashMap<>();

	public DynamicPanels() {
		try {
			var arr = new GsonBuilder().create().fromJson(GreenResurgenceClient.HOLO_CONFIG.holos().survivalPanels, JsonArray.class);
			arr.forEach(a -> {
				config.add(PanelConfig.fromJson(a.getAsJsonObject()));
			});
			lastParentSize = new Vector2i(Math.max(0, GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeW), Math.max(0, GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeH));
		} catch(JsonSyntaxException e) {
			e.printStackTrace();
		}
	}

	private void checkScaling() {
		var odlS = lastParentSize;
		lastParentSize = new Vector2i(root.width(), root.height());
		if(odlS != null && odlS.length() > 0 && (odlS.x != lastParentSize.x || odlS.y != lastParentSize.y)) {
			var ls = new ArrayList<PanelConfig>();
			config.forEach(s -> ls.add(s.scaledForNewRatio(odlS, lastParentSize)));
			config.clear();
			config.addAll(ls);
			panels.keySet().forEach(p -> root.removeChild(p));
			init(root, gui);
			GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeW = lastParentSize.x;
			GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeH = lastParentSize.y;
			GreenResurgenceClient.HOLO_CONFIG.save();
		} else if(odlS == null || odlS.length() == 0) {
			GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeW = lastParentSize.x;
			GreenResurgenceClient.HOLO_CONFIG.holos().lastGuiSizeH = lastParentSize.y;
			GreenResurgenceClient.HOLO_CONFIG.save();
		}
	}

	public void init(FlowLayout rootComponent, PlayerInventoryGui gui) {
		firsTick = true;

		activePopup = null;
		this.gui = gui;
		this.root = rootComponent;
		panels.clear();
		config.forEach(cfg -> {
			var created = createComponent(cfg);
			panels.put(created.getLeft(), created.getRight());
			root.child(created.getLeft());
			created.getLeft().editMode(editMode);
			root.onChildMutated(created.getLeft());
		});

	}

	public void tick() {
		if(firsTick) {
			checkScaling();
			firsTick = false;
		}
		panels.values().forEach(SubPanel.SubPanelInstance::panelTick);
		config.forEach(c -> {
			if(c.dirty) {
				saveIn = 5;
				c.dirty = false;
			}
		});

		if(saveIn > 0)
			saveIn--;
		if(saveIn == 0) {
			saveIn = -1;
			var ar = new JsonArray();
			config.forEach(c -> ar.add(c.toJson()));
			GreenResurgenceClient.HOLO_CONFIG.holos().survivalPanels = ar.toString();
			GreenResurgenceClient.HOLO_CONFIG.save();
		}
	}

	public FlowLayout getControlPanel() {
		var c = Containers.horizontalFlow(Sizing.content(), Sizing.fixed(22));
		c.allowOverflow(true);
		var btPlus = Components.button(Text.literal("+"), b -> {
			newWindowPopup(c);
		}).renderer(HoloPanels.button()).sizing(Sizing.fixed(22)).tooltip(Text.literal("Create a new Panel"));
		var txtA = Text.literal("Edit Panels");
		var txtB = Text.literal("Click again to exit edit mode\nGrab a panel to move it\nDrag the side of a panel to resize it\nRight click a panel to change the content\nClick a panel and press the key [del] or [backspace] to remove it");

		c.child(Components.button(Text.literal("E"), (b) -> {
			editMode = !editMode;
			editMode(editMode);
			b.tooltip(editMode ? txtB : txtA);
			if(editMode)
				c.child(btPlus);
			else
				c.clearChildren().child(b);

		}).renderer(HoloPanels.button()).tooltip(editMode ? txtB : txtA).sizing(Sizing.fixed(22)));
		if(editMode)
			c.child(btPlus);
		return c;
	}

	private FlowLayout activePopup;

	private void newWindowPopup(ParentComponent parent) {
		if(activePopup != null) {
			root.removeChild(activePopup);
			activePopup = null;
		} else {
			var h = 24 * Math.max(2, (subpanels.size() / 8) + 1);
			var c = Containers.verticalFlow(Sizing.fixed(24 * Math.min(8, subpanels.size())), Sizing.fixed(h));
			activePopup = c;
			c.padding(Insets.of(2));
			c.zIndex(1000);
			c.child(new AdaptiveLabelComponent(Text.literal("Pick the panel to display")).horizontalSizing(Sizing.fill(100)).margins(Insets.of(2)));
			var grid = new FreeRowGridLayout(Sizing.fill(100), Sizing.fill(100), Math.min(8, subpanels.size()));
			subpanels.forEach((k, v) -> {
				grid.child(Components.button(Text.literal(v.guiName().getString().substring(0, 2)), (b) -> {
					root.removeChild(c);
					activePopup = null;
					createWindow(parent.x(), parent.y() - v.preferredHeight(), v.preferredWidth(), v.preferredHeight(), k);
				}).renderer(HoloPanels.button()).tooltip(v.guiName()));
			});
			c.positioning(Positioning.absolute(parent.width(), parent.y() - root.y() - h));
			c.child(grid).surface(HoloPanels.panel(true, false).and(HoloPanels.outline()));
			root.child(c);
		}
	}

	/**
	 * instantiate window from an existing config.
	 * the config object is not added to the config list
	 *
	 */
	public void instantiateWindow(PanelConfig config) {
		if(this.root != null) {
			var c = createComponent(config);
			c.getLeft().editMode(editMode);
			root.child(c.getLeft());
			root.onChildMutated(c.getLeft());
			panels.put(c.getLeft(), c.getRight());
		}
	}

	/**
	 * Create a new window and config and add it to the list
	 *
	 * @param x      initial position x of the window
	 * @param y      initial position x of the window
	 * @param width  initial width of the window
	 * @param height initial height of the window
	 * @param panel  initial panel id of the window
	 */
	public void createWindow(int x, int y, int width, int height, String panel) {
		if(this.root != null) {
			var cfg = new PanelConfig(x, y, width, height, panel);
			config.add(cfg);
			saveIn = 10;
			var c = createComponent(cfg);
			c.getLeft().editMode(editMode);
			root.child(c.getLeft());
			root.onChildMutated(c.getLeft());
			panels.put(c.getLeft(), c.getRight());
		}
	}

	public void editMode(boolean edit) {
		panels.keySet().forEach(p -> p.editMode(edit));

	}

	protected Pair<DraggableResizable<SubScreenLayout>, SubPanel.SubPanelInstance> createComponent(PanelConfig config) {
		var subscreen = buildPanel(config, gui);

		var d = new DraggableResizable<>(Sizing.fixed(config.width), Sizing.fixed(config.height), subscreen.getLeft());
		d.minSize(24, 24);
		d.positioning(Positioning.absolute(config.x, config.y));

		d.addListener((c, a, x, y) -> {
			if(a == DraggableResizable.Action.RIGHT_CLICK) {
				panels.get(c).handleEditRightClick(x, y, c.child(), this, config);

			} else if(a == DraggableResizable.Action.DELETE) {
				panels.remove(c);
				this.config.remove(config);
				saveIn = 5;
				root.removeChild(c);
			}
		});
		config.attach(d);
		return new Pair<>(d, subscreen.getRight());

	}

	/**
	 * remove a panel form the root and the panel list then re-instentiate a window with the config
	 * does not remove the config from the list
	 */
	public void recreateWindow(SubScreenLayout subscreen, PanelConfig config) {
		if(subscreen.hasParent()) {
			panels.remove(subscreen.parent());
			root.removeChild(subscreen.parent());
			instantiateWindow(config);
		}
	}

	public static Pair<SubScreenLayout, SubPanel.SubPanelInstance> buildPanel(PanelConfig config, PlayerBasedGui<?> gui) {
		var pn = subpanels.get(config.panel == null ? DEFAULT_PANEL : config.panel);
		if(pn == null)
			pn = subpanels.get(DEFAULT_PANEL);
		var inst = pn.instantiate();
		var sets = inst.getSettings();
		if(sets != null) {
			sets.decode(config);
		}
		SubPanel finalPn = pn;
		var s = new SubScreenLayout(Sizing.fill(100), Sizing.fill(100), FlowLayout.Algorithm.VERTICAL, finalPn.guiLocation()) {
			@Override
			public void inflate(Size space) {
				super.inflate(space);
				if(this.getRoot() != null)
					inst.onResize(this.getRoot(), gui);
			}
		};
		s.zIndex(300);
		s.setLayout(pn.guiLocation());
		if(s.getRoot() != null)
			inst.build(s.getRoot(), gui);

		return new Pair<>(s, inst);

	}

}
