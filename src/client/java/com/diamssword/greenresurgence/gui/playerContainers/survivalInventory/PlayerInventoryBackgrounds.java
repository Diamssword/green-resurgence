package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.AbstractMultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel.CharacterStatsPanel;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel.PlayerCraftPanel;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel.SubPanel;
import com.diamssword.greenresurgence.items.helpers.AbstractBackpackItem;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventoryBackgrounds {
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

		public Identifier getTexturePath(InventoryBGSettings settings, @Nullable Item container) {
			var type = folderName;
			if(container instanceof AbstractBackpackItem bp)
				type = bp.getGuiTexture();
			return GreenResurgence.asRessource("textures/gui/player_inventory/" + folderName + "/" + type + "_" + settings.height + "x" + settings.width + ".png");
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

		public InventoryBGGroup add(int width, int height) {
			return add(width, height, Insets.none());
		}
	}

	public static Map<String, InventoryBGGroup> backgrounds = Map.of("backpack", new InventoryBGGroup("backpack", 200, 140, false),
			"satchelLeft", new InventoryBGGroup("satchel", 60, 100, false), "satchelRight", new InventoryBGGroup("satchel", 60, 100, true),
			"player", new InventoryBGGroup("inventory", 140, 100, false), "hotbar", new InventoryBGGroup("belt", 200, 60, false));

	static {
		backgrounds.get("backpack").add(3, 2).add(4, 2).add(5, 2).add(3, 3).add(4, 3).add(5, 3).add(6, 3).add(4, 4).add(5, 4).add(6, 4);
		backgrounds.get("satchelLeft").add(1, 1).add(1, 2).add(1, 3).add(2, 2).add(2, 3);
		backgrounds.get("satchelRight").add(1, 1).add(1, 2).add(1, 3).add(2, 2).add(2, 3);
		backgrounds.get("player").add(3, 3).add(4, 3).add(5, 3);
		backgrounds.get("hotbar").add(5, 1).add(6, 1).add(7, 1).add(8, 1).add(9, 1);
	}

	public static void editInventory(FlowLayout rootComponent, AbstractMultiInvScreenHandler<?> handler) {
		backgrounds.forEach((key, bg) -> {
			var inv = rootComponent.childById(InventoryComponent.class, key);
			if(inv != null) {
				var grid = handler.getInventory(key);
				if(grid != null) {
					var settings = bg.getFor(grid.getInventory().size());
					if(inv.hasParent()) {
						//	inv.margins(Insets.horizontal(10));
						inv.parent().allowOverflow(true);
						Item item = null;
						int slot = switch(key) {
							case "backpack" -> 0;
							case "satchelLeft" -> 1;
							case "satchelRight" -> 2;
							default -> -1;
						};
						if(slot > -1)
							item = Components.PLAYER_INVENTORY.get(MinecraftClient.getInstance().player).getBag(slot).getItem();

						inv.parent().surface(createBgRenderer(bg.getTexturePath(settings, item), bg.textureWidth, bg.textureHeight, bg.revert));

					}
					//inv.surface();

				}
			}
		});
		var sL = rootComponent.childById(InventoryComponent.class, "satchelLeft");
		var sR = rootComponent.childById(InventoryComponent.class, "satchelRight");
		if(sL != null && sR != null && sL.hasParent() && sR.hasParent()) {
			if(sR.width() < sL.width()) {
				var d1 = rootComponent.childById(FlowLayout.class, "satchelPadderRight");
				if(d1 != null)
					d1.sizing(Sizing.fixed(sL.width() - sR.width()), Sizing.fixed(10));

			} else if(sL.width() < sR.width()) {
				var d1 = rootComponent.childById(FlowLayout.class, "satchelPadderLeft");
				if(d1 != null)
					d1.sizing(Sizing.fixed(sR.width() - sL.width()), Sizing.fixed(10));
			}

		}
		var mgr = rootComponent.childById(FlowLayout.class, "maingrid");
		var eqP = rootComponent.childById(FlowLayout.class, "equipmentsPanel");
		var bpM = rootComponent.childById(FlowLayout.class, "backpackMainLayout");
		var offP = rootComponent.childById(FlowLayout.class, "offHandPanel");
		if(eqP != null) {
			if(bpM != null && offP != null && mgr != null) {
				var x = (offP.x() - mgr.x()) - (6);
				if(x + eqP.width() > (bpM.x() - mgr.x()) - 20) {
					x = (bpM.x() - mgr.x()) - (eqP.width() + 20);
					offP.positioning(Positioning.absolute((x - (offP.parent().x() - mgr.x())) + (eqP.width() > 30 ? eqP.width() / 4 : 0), 0));
				}
				eqP.positioning(Positioning.absolute(x, bpM.height() - eqP.height()));
			}
			eqP.allowOverflow(true);
			eqP.surface(createBgRenderer(GreenResurgence.asRessource("textures/gui/player_inventory/quiver.png"), 100, 300, false));
		}
		if(mgr != null) {
			int YCenter = mgr.height() / 2;
			var plInv = rootComponent.childById(InventoryComponent.class, "player");
			if(plInv != null) {
				var y1 = plInv.y() - mgr.y();
				YCenter = y1 + (plInv.height() / 2);
			}
			var p1 = (rootComponent.height() / 2) - YCenter;
			if(p1 < 36)
				p1 = 36;
			mgr.positioning(Positioning.absolute((rootComponent.width() / 2) - (mgr.width() / 2), p1));
		}

	}

	public static Surface createBgRenderer(Identifier texture, int width, int height, boolean revert) {
		return (context, component) -> {

			float centerX = component.x() + component.width() / 2f;
			float centerY = component.y() + component.height() / 2f;

			int x = (int) (centerX - width / 2f);
			int y = (int) (centerY - height / 2f);
			int u = revert ? width : 0;
			int textureWidth = revert ? -width : width;

			context.drawTexture(
					texture,
					(int) x, (int) y,
					u, 0,
					width, height,
					textureWidth, height
			);
		};
	}
}
