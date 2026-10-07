package com.diamssword.greenresurgence.gui.character_sheet;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.ComponentManager;
import com.diamssword.characters.api.http.SkinLayerValue;
import com.diamssword.characters.api.skin.BodyLayerCategory;
import com.diamssword.characters.api.skin.BodyLayerImageGroup;
import com.diamssword.characters.api.skin.BodyLayerInfo;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.SeparatorComponent;
import com.diamssword.greenresurgence.utils.ColorUtils;
import com.eliotlash.mclib.utils.MathUtils;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.SlimSliderComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BodyPartEditorGui extends BaseUIModelScreen<FlowLayout> {

	public static final String CHSHEET_ID = "character_sheet";
	private final List<BodyLayerInfo> layerBts = new ArrayList<>();
	private final Map<String, Map<Integer, Pair<SkinLayerValue, SkinLayerValue>>> layers = new HashMap<>();
	private Function<PlayerEntity, SkinLayerValue[]> equippedProvider;
	private final String saveId;
	private final String[] allowedLayers;
	private final Map<String, Map<Integer, Integer>> layersLinkage = new HashMap<>();
	private final Map<String, Map<Integer, Boolean>> splittedState = new HashMap<>();
	private String currentLayer;

	private String lastSearch = "";
	private LayeredPlayerComponent playerComp;
	private FlowLayout partsLayout;
	private int selectedLayerIndex = 0;
	private FreeRowGridLayout popup;
	private ButtonComponent popupOpenFor;
	private boolean clickOnMenu = false;

	public static Map<BodyLayerInfo, List<SkinLayerValue>> filterAllowedLayers(SkinLayerValue[] inputs, List<String> allowedLayers) {
		var map = new HashMap<BodyLayerInfo, List<SkinLayerValue>>();
		for(SkinLayerValue layer : inputs) {
			if(allowedLayers.isEmpty() || allowedLayers.contains(layer.layer)) {
				CharactersApi.bodyParts().getBodyLayer(layer.layer).ifPresent(b -> {
					if(!b.external()) {
						var ls = map.computeIfAbsent(b, c -> new ArrayList<>());
						if((b.multi() || ls.isEmpty() || (ls.size() == 1 && b.splited())) && ls.size() < 30) {
							ls.add(layer);
						}
					}
				});
			}
		}
		return map;
	}

	public static SkinLayerValue[] flattenMap(Map<BodyLayerInfo, List<SkinLayerValue>> map) {
		return map.entrySet()
				.stream()
				.sorted((a, b) -> (int) ((a.getKey().size() * 100000) - (int) (b.getKey().size() * 100000)))
				.flatMap(entry -> entry.getValue().stream())
				.toArray(SkinLayerValue[]::new);
	}

	public static SkinLayerValue[] mixandMatchLayers(PlayerEntity player, SkinLayerValue[] inputs, List<String> allowedLayers) {
		var map = filterAllowedLayers(inputs, allowedLayers);
		if(!allowedLayers.isEmpty()) {
			var playerLayers = CharactersApi.bodyParts().getCachedClientLayersFor(player.getUuid());

			for(SkinLayerValue layer : playerLayers) {
				if(!allowedLayers.contains(layer.layer)) {
					CharactersApi.bodyParts().getBodyLayer(layer.layer).ifPresent(b -> {
						if(!b.external()) {
							var ls = map.computeIfAbsent(b, c -> new ArrayList<>());
							if((b.multi() || ls.isEmpty() || (ls.size() == 1 && b.splited())) && ls.size() < 30) {
								ls.add(layer);
							}
						}
					});
				}
			}
		}
		return flattenMap(map);
	}

	public BodyPartEditorGui(SkinLayerValue[] preFilledLayers, String saveId, String... allowedLayers) {
		this(saveId, allowedLayers);
		this.equippedProvider = (p) -> mixandMatchLayers(MinecraftClient.getInstance().player, preFilledLayers, List.of(allowedLayers));
	}

	public BodyPartEditorGui(String saveId, String... allowedLayers) {
		super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("skin_editor")));
		this.saveId = saveId;
		this.allowedLayers = allowedLayers;
		var allL = List.of(allowedLayers);
		CharactersApi.bodyParts().getBodyLayers().forEach((k, v) -> {
			if(allL.isEmpty() || allL.contains(k)) {
				if(currentLayer == null)
					currentLayer = k;
				if(!v.external())
					layerBts.add(v);
			}
		});
		equippedProvider = (p) -> {
			return CharactersApi.bodyParts().getCachedClientLayersFor(p.getUuid());
		};

	}

	public static record BodyPartRef(BodyLayerImageGroup image, String layer, @Nullable BodyLayerImageGroup parent, @Nullable String category) {

	}

	private static Map<String, List<BodyPartRef>> convertList(String layer, List<Pair<BodyLayerCategory, List<BodyLayerImageGroup>>> list) {
		Map<String, List<BodyPartRef>> map = new HashMap<>();
		var clearable = CharactersApi.bodyParts().getBodyLayer(layer).map(BodyLayerInfo::clearable).orElse(false);
		list.forEach(p -> {
			var ls = map.get(p.getLeft().id());
			if(ls == null) {
				map.put(p.getLeft().id(), ls = new ArrayList<>());
				ls.add(new BodyPartRef(new BodyLayerImageGroup("clear", null), layer, null, null));
			}
			var cat = p.getLeft().id().equals("default") ? null : p.getLeft().id();
			ls.addAll(p.getRight().stream().map(l -> new BodyPartRef(l, layer, null, cat)).toList());
		});
		return map;
	}

	private SkinLayerValue[] flattenLayers() {
		List<SkinLayerValue> skin = new ArrayList<>();
		layers.forEach((k, v) -> {
			v.forEach((k1, v1) -> {
				if(v1.getLeft() != null)
					skin.add(v1.getLeft());
				if(v1.getRight() != null)
					skin.add(v1.getRight());
			});
		});
		return skin.toArray(new SkinLayerValue[0]);
	}

	private void exportSkin() {
		CharactersApi.bodyParts().clientAskSkinChange(flattenLayers());
		this.close();
	}

	private void generateCat(FlowLayout parent, Map<String, List<BodyPartEditorGui.BodyPartRef>> list, String added, String side) {

		list.forEach((cat, ls) -> {
			var lay = new FreeRowGridLayout(Sizing.fill(100), Sizing.content(), 7);
			if(!cat.equals("default")) {
				parent.child(Components.label(Text.literal(added + cat)));
			}
			ls.forEach(ref -> {
				lay.child(makePartButton(ref, side, null));
			});
			parent.child(lay);
		});
	}

	private void loadCloths(FlowLayout layout, String filter) {
		//selectedLayerIndex use this as multi layer index
		lastSearch = filter;
		layout.clearChildren();
		CharactersApi.bodyParts().getBodyLayer(currentLayer).ifPresent(layerInfo -> {
			CharactersApi.bodyParts().getBodyParts(currentLayer).ifPresent(dictionary -> {
				var list = dictionary.filterImageGroups(filter);
				var converted = convertList(dictionary.id(), list);
				if(layerInfo.splited()) {
					var state = splittedState.computeIfAbsent(currentLayer, s -> new HashMap<>()).getOrDefault(selectedLayerIndex, false);
					layout.child(Components.checkbox(Text.literal("Splitted")).checked(state).onChanged(b -> {
						splittedState.computeIfAbsent(currentLayer, s -> new HashMap<>()).put(selectedLayerIndex, b);
						if(!b) {
							var l = getLayerPair(selectedLayerIndex);
							if(l.getLeft() != null && CharactersApi.bodyParts().getBodyLayer(currentLayer).get().splited()) {
								var n = new SkinLayerValue();
								n.layer = l.getLeft().layer;
								n.side = "right";
								n.parent = l.getLeft().parent;
								n.id = l.getLeft().id;
								n.category = l.getLeft().category;
								l.setRight(n);
							} else
								l.setRight(null);
							playerComp.getRenderer().setPart(l.getLeft(), layersLinkage.get(currentLayer).get(selectedLayerIndex));
							playerComp.getRenderer().setPart(l.getRight(), layersLinkage.get(currentLayer).get(selectedLayerIndex));
						}
						loadCloths(layout, filter);
					}));
					layout.child(new SeparatorComponent(Sizing.fill(100), Sizing.fixed(2)).margins(Insets.vertical(2)));
					if(state)
						layout.child(Components.label(Text.literal("Left")));
					generateCat(layout, converted, "  ", state ? "left" : null);

					if(state) {
						layout.child(new SeparatorComponent(Sizing.fill(100), Sizing.fixed(2)).margins(Insets.vertical(2)));
						layout.child(Components.label(Text.literal("Right")));
						generateCat(layout, converted, "  ", "right");
					}

				} else {
					generateCat(layout, converted, "", null);
				}
			});
		});

	}

	private void closeMenu() {
		if(popup != null) {
			this.uiAdapter.rootComponent.removeChild(popup);
			popup = null;
			popupOpenFor = null;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		var r = super.mouseClicked(mouseX, mouseY, button);
		if(clickOnMenu) {
			clickOnMenu = false;
		} else
			closeMenu();
		return r;
	}

	private void makeMenu(int x, int y, BodyPartRef ref, String side, SkinButton parent) {
		closeMenu();
		clickOnMenu = true;
		if(ref.image.hasSubs()) {
			int l = Math.min(ref.image.subs().length * 27, partsLayout.width());
			if(ref.image.subs().length < 3)
				l += 4;
			var xp = MathUtils.clamp(x - (l / 2), partsLayout.x(), (partsLayout.x() + partsLayout.width()) - l);
			popup = new FreeRowGridLayout(Sizing.fixed(l), Sizing.content(), l / 26) {
				@Override
				public boolean onMouseDown(double mouseX, double mouseY, int button) {
					clickOnMenu = true;
					return super.onMouseDown(mouseX, mouseY, button);
				}
			};
			popup.positioning(Positioning.absolute(xp, y + 26));
			popup.allowOverflow(true);
			popup.zIndex(1000);
			popup.padding(Insets.of(2));
			popup.surface(Surface.flat(ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xff)).and(Surface.outline(ColorUtils.whithAlpha(ColorUtils.GREEN, 0xff))));
			for(BodyLayerImageGroup sub : ref.image.subs()) {

				popup.child(makePartButton(new BodyPartRef(sub, ref.layer, ref.image, ref.category), side, parent));
			}

			this.uiAdapter.rootComponent.child(popup);
		}

	}

	private boolean isButtonSelected(BodyPartRef ref, @Nullable String side) {
		var ls = layers.get(ref.layer());
		if(ls != null) {
			for(Map.Entry<Integer, Integer> entry : layersLinkage.get(ref.layer()).entrySet()) {

				if(entry.getKey() == selectedLayerIndex) {

					var pair = ls.get(entry.getKey());
					if(pair == null)
						return false;
					var v = "right".equals(side) ? pair.getRight() : pair.getLeft();
					if(v == null)
						return false;
					if(ref.image().hasSubs()) {
						return ref.image().id().equals(v.parent);
					} else {
						if(v.parent == null)
							return ref.image().id().equals(v.id);
						else if(ref.parent() != null) {
							return v.parent.equals(ref.parent().id()) && ref.image().id().equals(v.id);
						}

					}
					return false;
				}
			}
		}
		return false;
	}

	private Pair<SkinLayerValue, SkinLayerValue> getLayerPair(int key) {
		return layers.computeIfAbsent(currentLayer, k -> new HashMap<>()).computeIfAbsent(key, k -> new Pair<>(null, null));
	}

	private SkinButton makePartButton(BodyPartRef ref, String side, @Nullable SkinButton parent) {
		var part = ref.image.hasSubs() && ref.image.subs().length == 1 ? new BodyPartRef(ref.image.subs()[0], ref.layer, ref.image, ref.category) : ref;

		var bt = new SkinButton(Text.literal(part.image.hasSubs() ? "▼" : ""), b -> {
			if(part.image.hasSubs()) {
				if(popupOpenFor == b)
					closeMenu();
				else {
					makeMenu(b.x(), b.y(), part, side, (SkinButton) b);
					popupOpenFor = b;
				}
			} else {
				var l = new SkinLayerValue();
				l.layer = part.layer();
				if(part.parent() != null)
					l.parent = part.parent().id();
				l.category = part.category();
				l.id = part.image().id();
				l.side = side;
				if(parent != null)
					parent.parent().children().forEach(c -> {
						if(c instanceof SkinButton sk)
							sk.selected(false);
					});
				b.parent().children().forEach(c -> {
					if(c instanceof SkinButton sk)
						sk.selected(false);
				});
				((SkinButton) b).selected(true);
				if(parent != null)
					parent.selected(true);
				playerComp.getRenderer().setPart(l, layersLinkage.computeIfAbsent(currentLayer, k -> new HashMap<>()).getOrDefault(selectedLayerIndex, 0));
				//((BodyPartButtonComponent) a).isEquipped = true;
				var sp = CharactersApi.bodyParts().getBodyLayer(currentLayer).get().splited();
				if("right".equals(side) && sp)
					getLayerPair(selectedLayerIndex).setRight(l);
				else
					getLayerPair(selectedLayerIndex).setLeft(l);

				//	layout.components.forEach(c1 -> c1.isEquipped = c1.isSelectedFn.apply(c1.getPart()));

			}
		});
		bt.selected(isButtonSelected(ref, side));
		bt.sizing(Sizing.fixed(25)).margins(Insets.of(1));
		bt.tooltip(Text.literal((part.image.id().replace("/", " ").replace("_", " "))));

		var val = new SkinLayerValue();
		val.layer = part.layer;
		val.id = part.image.id();
		if(part.image.hasSubs()) {
			val.id = part.image.subs()[0].id();
			val.parent = part.image.id();
		}
		if(part.parent != null)
			val.parent = part.parent.id();
		val.category = part.category;
		var texture = val.getTexturePath();
		int i = texture.indexOf(":");
		var path = texture;
		var id = GreenResurgence.ID;
		if(i != -1) {
			id = texture.substring(0, i);
			path = texture.substring(i + 1);
		}
		if(part.image.id().equals("clear"))
			bt.icon(new Identifier("character_sheet:textures/gui/clear.png"));
		else
			bt.icon(new Identifier(id, "textures/bodyparts/icons/" + path + ".png"));
		return bt;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		this.layersLinkage.clear();

		partsLayout = rootComponent.childById(FlowLayout.class, "clothContainer");
		var search = rootComponent.childById(TextBoxComponent.class, "search");
		var flow = rootComponent.childById(FreeRowGridLayout.class, "layerLayout");
		var multiLay = rootComponent.childById(FlowLayout.class, "multiLayersLayout");
		var playerLay = rootComponent.childById(FlowLayout.class, "playerLayout");
		var slider = Components.slimSlider(SlimSliderComponent.Axis.HORIZONTAL);
		rootComponent.childById(ButtonComponent.class, "confirmBT").onPress(b -> {
			exportSkin();
			this.close();
		});
		playerComp = new LayeredPlayerComponent(Sizing.fill(100));
		playerLay.child(playerComp.scaleToFit(true).transform(m -> m.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40))).lookAtCursor(false).verticalSizing(Sizing.fill(90)));
		playerLay.child(slider.margins(Insets.left(5)).sizing(Sizing.fill(90), Sizing.fill(10)));
		var player = playerComp.entity();
		var dt = ComponentManager.getPlayerDatas(player);
		dt.getAppearence().clonePlayerAppearance(MinecraftClient.getInstance().player);
		loadCloths(partsLayout, "");
		search.onChanged().subscribe(v -> loadCloths(partsLayout, v.toLowerCase()));
		search.setPlaceholder(Text.translatable("gui." + GreenResurgence.ID + ".generic.search"));
		//this.setFocused(search);
		partsLayout.focusGained().subscribe(v -> {
			this.setFocused(search);
		});
		var outfits = dt.getAppearence().getSavedLayersLabels(this.saveId);
		for(int i = 1; i <= 7; i++) {
			var v = Text.translatable(CHSHEET_ID + ".wardrobe.outfitbt", i);
			final var i1 = i - 1;
			if(i1 < outfits.size())
				v = Text.literal(outfits.get(i1).getLeft());
			var ar = new ArrayList<Text>();
			ar.add(v);
			ar.add(Text.translatable(CHSHEET_ID + ".wardrobe.outfitbt.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
			var bt = rootComponent.childById(RButtonComponent.class, "memory" + i);

			bt.onPress(v1 -> {
				if(Screen.hasShiftDown()) {
					createOutfitWindow(i1);
				} else {
					var layers = dt.getAppearence().getSavedLayers(this.saveId, i1);
					if(layers != null && layers.length > 0) {
						MinecraftClient.getInstance().setScreen(new BodyPartEditorGui(layers, this.saveId, this.allowedLayers));
					}
				}
			}).tooltip(ar);
			bt.setMessage(v);
		}
		slider.value(0.5);
		slider.onChanged().subscribe(v -> {
			playerComp.rotation((int) (-180 + (v * 360f)));
		});

		if(flow != null) {
			final List<RButtonComponent> bts = new ArrayList<>();
			for(BodyLayerInfo value : layerBts) {
				var bt = new RButtonComponent(Text.empty(), (o) -> {
					for(var d : bts) {
						d.setActivated(false);
					}
					o.setActivated(true);
					currentLayer = value.id();
					CharactersApi.bodyParts().getBodyLayer(currentLayer).ifPresent(l -> {
						if(l.multi())
							refreshMultiLayers(multiLay, 0, layersLinkage.computeIfAbsent(currentLayer, s -> new HashMap<>()).getOrDefault(-1, 1));
						else
							multiLay.clearChildren();
					});
					//	updateDrawer(null);
					selectedLayerIndex = 0;
					loadCloths(partsLayout, lastSearch);
				});
				bt.icon("body/" + value.id() + ".png").sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID + ".body_editor.layerbt." + value.id())).margins(Insets.of(2, 0, 2, 0));
				flow.child(bt);
				bts.add(bt);

			}

			List<Pair<SkinLayerValue, SkinLayerValue>> ls1 = new ArrayList<>();
			layers.values().forEach((a) -> ls1.addAll(a.values()));
			if(layers.isEmpty())
				ls1.addAll(layerArrayConverter(equippedProvider.apply(MinecraftClient.getInstance().player)));
			layers.clear();

			playerComp.getRenderer().init(ls1, (l, i) -> {
				var map = layersLinkage.computeIfAbsent(l.getLeft().layer, k -> new HashMap<>());
				var cur = map.getOrDefault(-1, 0);
				map.put(cur, i);
				var map1 = layers.computeIfAbsent(l.getLeft().layer, k -> new HashMap<>());
				map1.put(cur, l);
				map.put(-1, cur + 1);
			});

			CharactersApi.bodyParts().getBodyLayer(currentLayer).ifPresent(l -> {
				if(l.multi())
					refreshMultiLayers(multiLay, 0, layersLinkage.computeIfAbsent(currentLayer, s -> new HashMap<>()).getOrDefault(-1, 1));
				else
					multiLay.clearChildren();
			});
		}

	}

	private void refreshMultiLayers(FlowLayout container, int selected, int count) {
		container.clearChildren();
		int finalCount = Math.min(count, 20);
		for(int i = 0; i < count; i++) {
			int finalI = i;
			var b = Components.button(Text.literal("" + (i + 1)), b1 -> {
				if(hasShiftDown()) {
					var map = layersLinkage.get(currentLayer);
					if(map != null) {
						playerComp.getRenderer().removeLayer(map.get(finalI));
						layers.get(currentLayer).remove(finalI);
						map.put(-1, Math.max(map.get(-1) - 1, 0));
					}
					refreshMultiLayers(container, Math.max(0, finalI - 1), finalCount - 1);
				} else {
					selectedLayerIndex = finalI;
					loadCloths(partsLayout, lastSearch);
				}
			}).tooltip(Text.literal("Shift+Click to delete "));
			container.child(b.sizing(Sizing.fixed(20)));
		}
		if(count < 20) {
			container.child(Components.button(Text.literal("+"), b -> {
				var map = layersLinkage.computeIfAbsent(currentLayer, k -> new HashMap<>());
				var ind = playerComp.getRenderer().addLayer(CharactersApi.bodyParts().getBodyLayer(currentLayer).get());
				map.put(finalCount, ind);
				map.put(-1, map.get(-1) + 1);
				refreshMultiLayers(container, finalCount, finalCount + 1);
				if(container.parent() instanceof ScrollContainer<?> sc) {
					sc.scrollTo(1);
				}
			}).tooltip(Text.literal("Add Layer")).sizing(Sizing.fixed(20)));
		}
		if(selected != selectedLayerIndex && selected < count && selected > 0) {
			selectedLayerIndex = selected;
			loadCloths(partsLayout, lastSearch);
		}
	}

	private List<Pair<SkinLayerValue, SkinLayerValue>> layerArrayConverter(SkinLayerValue[] layers) {
		var list = new ArrayList<Pair<SkinLayerValue, SkinLayerValue>>();

		Map<String, List<SkinLayerValue>> rightSideds = new HashMap<>();
		List<String> added = new ArrayList<>();
		for(SkinLayerValue layer : layers) {

			if(!added.contains(layer.layer))
				added.add(layer.layer);
			if("right".equals(layer.side)) {
				rightSideds.computeIfAbsent(layer.layer, k -> new ArrayList<>()).add(layer);
			} else {
				list.add(new Pair<>(layer, null));
			}
		}
		CharactersApi.bodyParts().getBodyLayers().forEach((k, lay) -> {
			if(!added.contains(k)) {
				var lv = new SkinLayerValue();
				lv.layer = k;
				if(lay.clearable())
					lv.id = "clear";
				else
					lv = SkinLayerValue.createDefaultFor(k);
				if(lay.splited()) {
					lv.side = "left";
					var lv1 = new SkinLayerValue();
					lv1.id = lv.id;
					lv1.side = "right";
					lv1.layer = lv.layer;
					lv1.category = lv.category;
					lv1.parent = lv.parent;
					var l1 = new ArrayList<SkinLayerValue>();
					l1.add(lv1);
					rightSideds.put(k, l1);
				}
				list.add(new Pair<>(lv, null));
			}
		});
		list.forEach(p1 -> {
			var s = p1.getLeft();
			var l1 = rightSideds.get(s.layer);
			if(l1 != null && !l1.isEmpty()) {
				p1.setRight(l1.remove(0));
			}
		});
		return list;
	}

	private void createOutfitWindow(int index) {
		MinecraftClient.getInstance().setScreen(new LayersPopupGui(allowedLayers, saveId, index, flattenLayers()));
	}

	public boolean shouldPause() {
		return false;
	}
}