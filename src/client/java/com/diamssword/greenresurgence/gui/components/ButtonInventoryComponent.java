package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.RessourceGuiHelper;
import com.diamssword.greenresurgence.systems.crafting.RecipeCollection;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
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

public class ButtonInventoryComponent extends BaseComponent {
	private final int slotSize = 18;
	public static final Identifier SLOT_TEXTURE = GreenResurgence.asRessource("textures/gui/highlight.png");
	private final EventStream<RecipePicked> onPicked = RecipePicked.newPickStream();
	public Identifier collectionID;
	private UniversalResource hovered;
	private RecipeCollection collection;
	private int columns = 3;
	private List<SimpleRecipe> items = new ArrayList<>();
	private Comparator<SimpleRecipe> sorter;
	private String lastResearch = "";
	protected boolean blend = false;
	private float time = 0;
	private int sizingHeight;

	protected ButtonInventoryComponent(Sizing size, Identifier collectionID, RecipeCollection collection) {
		this.collectionID = collectionID;
		this.collection = collection;
		items = this.collection.getRecipes(MinecraftClient.getInstance().player);
		this.sizing(size);

	}

	public void bindSearchField(TextBoxComponent field) {
		field.onChanged().subscribe(t -> {
			if (!t.trim().equals(lastResearch)) {
				lastResearch = t.toLowerCase().trim();
				refreshSearch();
			}
		});
	}


	public void setCollection(RecipeCollection collection, Identifier id) {
		this.collection = collection;
		this.collectionID = id;
		refreshSearch();
	}

	public void setSize() {
		this.applySizing();
		if (this.parent != null)
			this.parent.onChildMutated(this);
	}

	public Comparator<SimpleRecipe> getSorter() {
		return sorter;
	}

	public void setSorter(Comparator<SimpleRecipe> sorter) {
		this.sorter = sorter;
	}

	public void refreshSearch() {
		PlayerEntity pl = MinecraftClient.getInstance().player;
		if (this.lastResearch.isBlank())
			this.items = this.collection.getRecipes(pl);
		else
			this.items = new ArrayList<>(this.collection.getRecipes(pl).stream().filter(v -> v.result(pl).getName().getString().toLowerCase().trim().contains(lastResearch)).toList());
		if (sorter != null)
			this.items.sort(sorter);
		setSize();
	}

	@Override
	public boolean canFocus(FocusSource source) {
		return source == FocusSource.MOUSE_CLICK;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		return this.sizingHeight;
	}

	@Override
	public void applySizing() {
		super.applySizing();
		this.columns = Math.max(1, width / slotSize);
		this.sizingHeight = (int) (Math.ceil(this.items.size() / (float) columns) * slotSize);
		super.applySizing();

	}

	@Override
	public boolean onMouseDown(double mouseX, double mouseY, int button) {
		int x = (int) mouseX / slotSize;
		int y = (int) mouseY / slotSize;

		var d = x + (y * columns);
		if (x < columns && d < items.size()) {
			UISounds.playButtonSound();
			onPicked.sink().onPicked(items.get(d), this.collection, this.collectionID);
			return false;
		}
		return super.onMouseDown(mouseX, mouseY, button);
	}

	public EventSource<RecipePicked> onRecipePicked() {
		return this.onPicked.source();
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if (!Screen.hasControlDown()) {
			this.time += delta;
		}
		RenderSystem.enableDepthTest();
		if (this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}
		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		int i = 0;
		int j = 0;
		hovered = null;
		for (SimpleRecipe item : items) {
			UniversalResource it = item.result(MinecraftClient.getInstance().player);
			var w1 = (i * slotSize);
			var h1 = (j * slotSize);
			if (mouseX >= this.x + w1 && mouseX <= this.x + w1 + slotSize - 1 && mouseY >= this.y + h1 && mouseY <= this.y + h1 + slotSize - 1) {
				hovered = it;
				context.drawTexture(SLOT_TEXTURE, w1, h1, 0, 0, slotSize, slotSize, 32, 32);
			}
			drawResource(it, context, w1 + 1, h1 + 1);
			i++;
			if (i >= this.columns) {
				j++;
				i = 0;
			}
		}

		if (this.blend) {
			RenderSystem.disableBlend();
		}

		matrices.pop();
	}

	protected void drawResource(UniversalResource resource, OwoUIDrawContext context, int x, int y) {
		RessourceGuiHelper.drawRessource(context, resource, x, y, time);
	}

	public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
		RessourceGuiHelper.drawTooltip(context, hovered, mouseX, mouseY, time);
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);

	}

	public static ButtonInventoryComponent parse(Element element) {
		UIParsing.expectAttributes(element, "collection");
		var invId = UIParsing.parseIdentifier(element.getAttributeNode("collection"));
		var r = Recipes.get(invId).orElse(new RecipeCollection(new Identifier("minecraft:void")));
		return new ButtonInventoryComponent(Sizing.fill(100), invId, r);
	}

	public interface RecipePicked {
		boolean onPicked(SimpleRecipe picked, RecipeCollection collection, Identifier collectionID);

		static EventStream<RecipePicked> newPickStream() {
			return new EventStream<>(subscribers -> (SimpleRecipe picked, RecipeCollection collection, Identifier collectionID) -> {
				var anyTriggered = false;
				for (var subscriber : subscribers) {
					anyTriggered |= subscriber.onPicked(picked, collection, collectionID);
				}
				return anyTriggered;
			});
		}
	}

}
