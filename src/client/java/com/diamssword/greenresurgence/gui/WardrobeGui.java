package com.diamssword.greenresurgence.gui;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.characters.api.appearence.Cloth;
import com.diamssword.characters.api.appearence.LayerDef;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ClothButtonComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SlimSliderComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class WardrobeGui extends BaseUIModelScreen<FlowLayout> {

	public static final String CHSHEET_ID = "character_sheet";
	private final List<Pair<String, LayerDef[]>> layerBts = new ArrayList<>();
	private final boolean shouldShowOutifits;
	private final Function<PlayerEntity, Map<String, Cloth>> equippedProvider;

	private Pair<String, LayerDef[]> currentLayer;
	private Map<String, Cloth> oldCloths;

	private String lastSearch = "";

	public WardrobeGui(String type) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("wardrobe")));
		List<LayerDef> layers;
		if (type == null || type.equals("default")) {
			shouldShowOutifits = true;
			layers = CharactersApi.clothing().getClothLayers();
			layerBts.add(new Pair<>("all", layers.toArray(new LayerDef[0])));
			layerBts.add(new Pair<>("current", layers.toArray(new LayerDef[0])));
			equippedProvider = (p) -> ComponentManager.getPlayerDatas(p).getAppearence().getEquippedCloths();
		} else {
			shouldShowOutifits = false;
			layers = CharactersApi.clothing().getLayers().values().stream().filter(v -> type.equals(v.getSpecialEditor())).toList();
			equippedProvider = (p) -> ComponentManager.getPlayerDatas(p).getAppearence().getEquippedLayers();
		}
		for (var l : layers) {
			layerBts.add(new Pair<>(l.getId(), new LayerDef[]{l}));
		}
		currentLayer = layerBts.get(0);
	}

	private void loadCloths(FreeRowGridLayout layout, PlayerComponent playerComp, String filter) {
		lastSearch = filter;
		var player = playerComp.entity();
		var dt = ComponentManager.getPlayerDatas(player);
		oldCloths = equippedProvider.apply(player);
		var equip = oldCloths.values().stream().filter(Objects::nonNull).toList();
		List<Cloth> list;
		if (currentLayer.getLeft().equals("current"))
			list = equip;
		else
			list = CharactersApi.clothing().getAvailablesClothsCollectionForPlayer(MinecraftClient.getInstance().player, "all", currentLayer.getRight());
		if (!filter.isEmpty())
			list = list.stream().filter(v -> v.name().toLowerCase().contains(filter) || (!v.collection().equals("default") && v.collection().toLowerCase().contains(filter))).toList();
		layout.clear();
		for (var c : list) {
			var bt = new ClothButtonComponent(c);
			bt.onPress((__) -> {
				var v = bt.getCloth();
				if (oldCloths.containsValue(v)) {
					dt.getAppearence().setCloth(v.layer().id, null);
					CharactersApi.clothing().clientAskEquipCloth(new Identifier("null", "null"), v.layer().getId());
				} else {
					dt.getAppearence().setCloth(v.layer().id, v);
					CharactersApi.clothing().clientAskEquipCloth(v.id(), v.layer().toString());
				}
				oldCloths = equippedProvider.apply(player);
				updateSelected(layout, oldCloths.values().stream().filter(Objects::nonNull).toList());

			});
			bt.onClothHovered().subscribe(v -> {
				if (v != null)
					dt.getAppearence().setCloth(v);
				else {
					oldCloths.forEach((a, v1) -> {
						dt.getAppearence().setCloth(a, v1);
					});
				}
			});
			layout.child(bt.sizing(Sizing.fixed(30), Sizing.fixed(50)).margins(Insets.of(1)));
		}
		updateSelected(layout, equip);
	}

	private void updateSelected(FreeRowGridLayout layout, List<Cloth> equipped) {
		for (Component child : layout.children()) {
			if (child instanceof ClothButtonComponent cb) {
				cb.setSelected(equipped.stream().anyMatch(v -> v.id().equals(cb.getCloth().id())));
			}
		}
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		//   var ward=rootComponent.childById(ClothInventoryComponent.class,"cloths");
		var wardLay = rootComponent.childById(FreeRowGridLayout.class, "clothContainer");
		var search = rootComponent.childById(TextBoxComponent.class, "search");
		var flow = rootComponent.childById(FreeRowGridLayout.class, "layerLayout");
		var txt1 = rootComponent.childById(LabelComponent.class, "title_right");
		var playerComp = rootComponent.childById(PlayerComponent.class, "player");
		var slider = rootComponent.childById(SlimSliderComponent.class, "slider");
		var player = playerComp.entity();
		var dt = ComponentManager.getPlayerDatas(player);
		dt.getAppearence().clonePlayerAppearance(MinecraftClient.getInstance().player);
		loadCloths(wardLay, playerComp, "");
		search.onChanged().subscribe(v -> loadCloths(wardLay, playerComp, v.toLowerCase()));
		search.setPlaceholder(Text.literal("Recherche"));
		//this.setFocused(search);
		wardLay.focusGained().subscribe(v -> {
			this.setFocused(search);
		});
		var outfits = dt.getAppearence().getOutfits();
		slider.value(0.5);
		slider.onChanged().subscribe(v -> {
			playerComp.rotation((int) (-180 + (v * 360f)));
		});
		if (!shouldShowOutifits) {
			var c = rootComponent.childById(FlowLayout.class, "outfits");
			c.clearChildren();
			c.surface(Surface.flat(0));
		} else {
			for (int i = 1; i <= 7; i++) {
				var v = Text.translatable(CHSHEET_ID + ".wardrobe.outfitbt", i);
				final var i1 = i - 1;
				if (i1 < outfits.size())
					v = Text.literal(outfits.get(i1).getLeft());
				var ar = new ArrayList<Text>();
				ar.add(v);
				ar.add(Text.translatable(CHSHEET_ID + ".wardrobe.outfitbt.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
				var bt = rootComponent.childById(RButtonComponent.class, "memory" + i);

				bt.onPress(v1 -> {
					if (Screen.hasShiftDown()) {
						createOutfitWindow(v1, i1);
					} else {
						CharactersApi.clothing().clientAskEquipOutfit(i1);
						dt.getAppearence().equipOutfit(i1);
						loadCloths(wardLay, playerComp, lastSearch);
					}
				}).tooltip(ar);
				bt.setMessage(v);
			}
		}
		if (flow != null) {
			final List<RButtonComponent> bts = new ArrayList<>();
			for (Pair<String, LayerDef[]> value : layerBts) {
				var bt = new RButtonComponent(Text.empty(), (o) -> {
					for (var d : bts) {
						d.setActivated(false);
					}
					o.setActivated(true);
					currentLayer = value;
					txt1.text(Text.translatable(GreenResurgence.ID + ".wardrobe.collection." + currentLayer.getLeft()));
					loadCloths(wardLay, playerComp, lastSearch);
				});
				if (value.getLeft().equals("all"))
					bt.setActivated(true);
				bt.icon(value.getLeft()).sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID + ".wardrobe.layerbt." + value.getLeft())).margins(Insets.of(2, 0, 2, 0));
				flow.child(bt);
				bts.add(bt);

			}

		}

	}

	private void createOutfitWindow(RButtonComponent bt, int index) {
		MinecraftClient.getInstance().setScreen(new OutfitPopupGui(this, index, bt));
	}

	public boolean shouldPause() {
		return false;
	}
}