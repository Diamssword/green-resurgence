package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.AbstractMultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.CharacterStatsPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.PlayerCraftPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.SubPanel;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventoryCustomization {
	public static List<SubPanel> subpanels = new ArrayList<>();


	static {
		subpanels.add(new PlayerCraftPanel());
		subpanels.add(new CharacterStatsPanel());
	}

	public static record InventoryBGSettings(int width, int height, Insets padding) {}

	public static class InventoryBGGroup {
		private Map<Integer, InventoryBGSettings> settings = new HashMap<>();
		private final String folderName;
		private int bigger = 0;
		private int smaller = 0;
		public final int textureWidth;
		public final int textureHeight;
		public final boolean revert;

		public InventoryBGGroup(String folderName, int textureWidth, int textureHeight, boolean revert) {
			this.folderName = folderName;
			this.revert = revert;
			this.textureHeight = textureHeight;
			this.textureWidth = textureWidth;
		}

		public InventoryBGGroup add(InventoryBGSettings settings) {
			var nb = settings.width * settings.height;
			if(nb > bigger)
				bigger = nb;
			if(nb < smaller || smaller == 0)
				smaller = nb;
			this.settings.put(nb, settings);
			return this;
		}

		public InventoryBGGroup add(int width, int height, Insets padding) {
			return add(new InventoryBGSettings(width, height, padding));
		}

		public Identifier getTexturePath(InventoryBGSettings settings) {
			return GreenResurgence.asRessource("textures/gui/player_inventory/" + folderName + "/" + folderName + "_" + settings.width + "x" + settings.height + ".png");
		}

		public InventoryBGSettings getFor(int size) {
			if(size > bigger)
				return settings.get(bigger);
			if(size < smaller)
				return settings.get(smaller);
			for(int i = size; i <= bigger; i++) {
				var s = settings.get(i);
				if(s != null)
					return s;
			}
			for(int i = size; i >= smaller; i--) {
				var s = settings.get(i);
				if(s != null)
					return s;
			}
			return settings.get(smaller);
		}

		public InventoryBGGroup add(int i, int i1) {
			return add(i, i1, Insets.none());
		}
	}

	public static Map<String, InventoryBGGroup> backgrounds = Map.of("backpack", new InventoryBGGroup("backpack", 200, 140, false),
			"satchelLeft", new InventoryBGGroup("satchel", 60, 100, false), "satchelRight", new InventoryBGGroup("satchel", 60, 100, true),
			"player", new InventoryBGGroup("inventory", 140, 100, false), "hotbar", new InventoryBGGroup("belt", 200, 60, false));

	static {
		backgrounds.get("backpack").add(2, 3).add(2, 4).add(2, 5).add(3, 3).add(3, 4).add(3, 5).add(3, 6).add(4, 4).add(4, 5).add(4, 6);
		backgrounds.get("satchelLeft").add(1, 1).add(1, 2).add(1, 3).add(2, 2).add(2, 3);
		backgrounds.get("satchelRight").add(1, 1).add(1, 2).add(1, 3).add(2, 2).add(2, 3);
		backgrounds.get("player").add(3, 3).add(3, 4).add(3, 5);
		backgrounds.get("hotbar").add(5, 1).add(6, 1).add(7, 1).add(8, 1).add(9, 1);
	}

	public static void editInventory(FlowLayout rootComponent, AbstractMultiInvScreenHandler<?> handler) {
		backgrounds.forEach((key, bg) -> {
			var inv = rootComponent.childById(InventoryComponent.class, key);
			if(inv != null) {
				var grid = handler.getInventory(key);
				if(grid != null) {
					var settings = bg.getFor(grid.getInventory().size());
					inv.surface(createBgRenderer(bg.getTexturePath(settings), bg.textureWidth, bg.textureHeight, bg.revert));
				}
			}
		});

	}

	public static InventoryComponent.InventoryBackground createBgRenderer(Identifier texture, int width, int height, boolean revert) {
		return (context, component) -> {
			context.drawTexture(texture, component.x(), component.y(), 0, 0, component.width(), component.height(), width, height);
		};
	}
}
