package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.gui.ClickableLayoutComponent;
import com.diamssword.greenresurgence.gui.components.hud.*;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ComponentsRegister {
	public static void init() {
		MinecraftClient mc = MinecraftClient.getInstance();

		UIParsing.registerFactory("player", PlayerComponent::parse);
		UIParsing.registerFactory("inventory", InventoryComponent::parse);
		UIParsing.registerFactory("inventorysearch", InventorySearchableComponent::parse);
		UIParsing.registerFactory("buttoninventory", ButtonInventoryComponent::parse);
		UIParsing.registerFactory("character", CharacterComponent::parse);
		UIParsing.registerFactory("separator", SeparatorComponent::parse);
		UIParsing.registerFactory("clothlist", ClothInventoryComponent::parse);
		UIParsing.registerFactory("rbutton", (a) -> new RButtonComponent(Text.empty(), (RButtonComponent button) -> {
		}));
		UIParsing.registerFactory("arrowbutton", (a) -> new ArrowButtonComponent((ArrowButtonComponent button) -> {
		}));
		UIParsing.registerFactory("recipedisplay", a -> new RecipDisplayComponent(Sizing.fill(100)));
		UIParsing.registerFactory("hudbar", BarComponent::parse);
		UIParsing.registerFactory("healthbar", HealthBarComponent::parse);
		UIParsing.registerFactory("hotbar", HotBarComponent::parse);
		UIParsing.registerFactory("heldtooltip", (e) -> new ItemTooltipComponent());
		UIParsing.registerFactory("hudicon", IconComponent::parse);
		UIParsing.registerFactory("offhandslot", SingleSlotComponent::parse);
		UIParsing.registerFactory("subscreen", SubScreenLayout::parse);
		UIParsing.registerFactory("button-layout", ClickableLayoutComponent::parse);
		UIParsing.registerFactory("search-box", (e) -> new SearchBoxComponent(Sizing.content()));


	}
}
