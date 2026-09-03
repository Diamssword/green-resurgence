package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.RessourceGuiHelper;
import com.diamssword.greenresurgence.systems.crafting.*;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.IStoneCutterTypeRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ButtonInventoryComponent<T extends IRessourceDisplay> extends BaseComponent {
	private final int slotSize = 18;
	public static final Identifier SLOT_TEXTURE = GreenResurgence.asRessource("textures/gui/highlight.png");
	private final EventStream<ItemPicked<T>> onPicked = ItemPicked.newPickStream();
	public Identifier collectionID;
	private UniversalResource hovered;
	private int columns = 3;
	private List<T> items = new ArrayList<>();
	private Comparator<T> sorter;
	private String lastResearch = "";
	protected boolean blend = false;
	private float time = 0;

	protected ButtonInventoryComponent(Sizing size, Identifier collectionID) {
		this.collectionID = collectionID;
		items = getItems();
		this.horizontalSizing(size);
		this.verticalSizing(Sizing.content());

	}

	public boolean displayItemExtra() {
		return false;
	}

	public abstract List<T> getItems();

	public void bindSearchField(TextBoxComponent field) {
		field.onChanged().subscribe(t -> {
			if(!t.trim().equals(lastResearch)) {
				lastResearch = t.toLowerCase().trim();
				refreshSearch();
			}
		});
	}

	public void setCollection(Identifier id) {
		this.collectionID = id;
		refreshSearch();
	}

	public void setSize() {
		this.applySizing();
		if(this.parent != null)
			this.parent.onChildMutated(this);
	}

	public Comparator<T> getSorter() {
		return sorter;
	}

	public void setSorter(Comparator<T> sorter) {
		this.sorter = sorter;
	}

	public void refreshSearch() {
		PlayerEntity pl = MinecraftClient.getInstance().player;
		if(this.lastResearch.isBlank())
			this.items = getItems();
		else
			this.items = new ArrayList<>(getItems().stream().filter(v -> v.getDisplay(pl).getName().getString().toLowerCase().trim().contains(lastResearch)).toList());
		if(sorter != null)
			this.items.sort(sorter);
		setSize();
	}

	@Override
	public boolean canFocus(FocusSource source) {
		return source == FocusSource.MOUSE_CLICK;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		this.columns = Math.max(1, width / slotSize);

		return (int) (Math.ceil(this.items.size() / (float) columns) * slotSize);
	}

	@Override
	public boolean onMouseDown(double mouseX, double mouseY, int button) {
		int x = (int) mouseX / slotSize;
		int y = (int) mouseY / slotSize;

		var d = x + (y * columns);
		if(x < columns && d < items.size()) {
			UISounds.playButtonSound();
			onPicked.sink().onPicked(items.get(d), this.collectionID);
			return false;
		}
		return super.onMouseDown(mouseX, mouseY, button);
	}

	public EventSource<ItemPicked<T>> onRecipePicked() {
		return this.onPicked.source();
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(!Screen.hasControlDown()) {
			this.time += delta;
		}
		RenderSystem.enableDepthTest();
		if(this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}
		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		int i = 0;
		int j = 0;
		hovered = null;
		for(T item : items) {
			UniversalResource it = item.getDisplay(MinecraftClient.getInstance().player);
			var w1 = (i * slotSize);
			var h1 = (j * slotSize);
			if(mouseX >= this.x + w1 && mouseX <= this.x + w1 + slotSize - 1 && mouseY >= this.y + h1 && mouseY <= this.y + h1 + slotSize - 1) {
				hovered = it;
				context.drawTexture(SLOT_TEXTURE, w1, h1, 0, 0, slotSize, slotSize, 32, 32);
			}
			drawResource(it, context, w1 + 1, h1 + 1);
			i++;
			if(i >= this.columns) {
				j++;
				i = 0;
			}
		}

		if(this.blend) {
			RenderSystem.disableBlend();
		}

		matrices.pop();
	}

	protected void drawResource(UniversalResource resource, OwoUIDrawContext context, int x, int y) {
		RessourceGuiHelper.drawRessource(context, resource, x, y, time);
		if(displayItemExtra())
			RessourceGuiHelper.drawRessourceExtra(context, resource, x, y, time, 16777215);
	}

	public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
		RessourceGuiHelper.drawTooltip(context, hovered, mouseX, mouseY, time);
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);

	}

	public static class RecipeListComponent extends ButtonInventoryComponent<SimpleRecipe> {
		RecipeCollection collection;

		protected RecipeListComponent(Sizing size, Identifier collectionID) {
			super(size, collectionID);
			this.collection = Recipes.get(collectionID).orElse(new RecipeCollection(new Identifier("void")));
		}

		@Override
		public void setCollection(Identifier id) {
			super.setCollection(id);
			this.collection = Recipes.get(id).orElse(new RecipeCollection(new Identifier("void")));
			refreshSearch();
		}

		@Override
		public List<SimpleRecipe> getItems() {
			if(this.collection == null)
				return List.of();
			return this.collection.getRecipes(MinecraftClient.getInstance().player);
		}
	}

	public static class SimpleResourceListComponent extends ButtonInventoryComponent<UniversalResource> {

		private Supplier<List<UniversalResource>> supplier = List::of;

		protected SimpleResourceListComponent(Sizing size, Identifier collectionID) {
			super(size, collectionID);
		}

		public void setSupplier(Supplier<List<UniversalResource>> supplier) {
			this.supplier = supplier;
			refreshSearch();
		}

		@Override
		public List<UniversalResource> getItems() {
			if(supplier == null)
				return List.of();
			return supplier.get();
		}
	}

	public static class StoneCutterListComponent extends ButtonInventoryComponent<UniversalResource> {
		private IStoneCutterTypeRecipe cutter;
		private UniversalResource input;

		protected StoneCutterListComponent(Sizing size, Identifier collectionID) {
			super(size, collectionID);
			this.cutter = Recipes.getStoneCutter(collectionID).orElse(null);
		}

		@Override
		public boolean displayItemExtra() {
			return true;
		}

		@Override
		public void setCollection(Identifier id) {
			super.setCollection(id);
			this.cutter = Recipes.getStoneCutter(id).orElse(null);
		}

		public void setInput(UniversalResource input) {
			this.input = input;
			refreshSearch();
		}

		@Override
		public List<UniversalResource> getItems() {
			if(cutter != null && input != null)
				return cutter.getResultForInput(input, MinecraftClient.getInstance().player);
			return List.of();
		}
	}

	public static ButtonInventoryComponent<?> parse(Element element) {
		//UIParsing.expectAttributes(element, "collection");
		var type = "recipe";
		if(element.hasAttribute("type"))
			type = element.getAttribute("type");
		var invId = new Identifier("empty");
		if(element.hasAttribute("collection"))
			invId = UIParsing.parseIdentifier(element.getAttributeNode("collection"));
		return switch(type) {
			case "stonecutter" -> new StoneCutterListComponent(Sizing.fill(100), invId);
			case "recipe" -> new RecipeListComponent(Sizing.fill(100), invId);
			default -> new SimpleResourceListComponent(Sizing.fill(100), invId);
		};
	}

	public interface ItemPicked<T extends IRessourceDisplay> {
		boolean onPicked(T picked, Identifier collectionID);

		static <T extends IRessourceDisplay> EventStream<ItemPicked<T>> newPickStream() {
			return new EventStream<>(subscribers -> (T picked, Identifier collectionID) -> {
				var anyTriggered = false;
				for(var subscriber : subscribers) {
					anyTriggered |= subscriber.onPicked(picked, collectionID);
				}
				return anyTriggered;
			});
		}
	}

}
