package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.components.panels.HoloStyle;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public class HotBarComponent extends BaseComponent implements IHideableComponent {

	private final int textureSize = 46;
	private int selected = 1;
	private int size = 5;
	private int hideTimer = 200;

	private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
	protected boolean blend = false;
	protected final Identifier texture;
	private HoloStyle style = HoloPanels.getCurrentStyle();
	private boolean hidden;

	protected HotBarComponent(Identifier texture) {
		super();
		this.texture = texture;
	}

	public HotBarComponent blend(boolean blend) {
		this.blend = blend;
		return this;
	}

	@Override
	protected void applySizing() {
		final var horizontalSizing = this.horizontalSizing.get();
		final var verticalSizing = this.verticalSizing.get();
		final var margins = this.margins.get();
		this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
		this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
	}

	public boolean blend() {
		return this.blend;
	}

	@Override
	public void update(float delta, int mouseX, int mouseY) {
		super.update(delta, mouseX, mouseY);

	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {
		return (20 * size) + 4;
	}

	@Override
	public void hidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		return 24;
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(hidden || hideTimer == 0)
			return;
		RenderSystem.enableDepthTest();

		if(this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, Math.min(100, this.hideTimer) / 100f);
			RenderSystem.enableDepthTest();
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		var mc = MinecraftClient.getInstance();
		//	context.drawTexture(this.texture, 1, 1, 1, 22, 0, 0, 1, 22, textureSize, textureSize);
		SingleSlotComponent.drawSlot(context, 1, 1, style.getSecondaryColor().background(), style.getMainColor().solid(), style.getSecondaryColor().border(), true, false);
		if(size > 2) {
			for(int i = 0; i < size - 2; i++) {
				SingleSlotComponent.drawSlot(context, 21 + (i * 20), 1, style.getSecondaryColor().background(), style.getMainColor().solid(), style.getSecondaryColor().border(), false, false);
				//context.drawTexture(this.texture, 2 + (20 * i), 1, 20, 22, 1, 0, 20, 22, textureSize, textureSize);
			}
		}
		SingleSlotComponent.drawSlot(context, 21 + ((size - 2) * 20), 1, style.getSecondaryColor().background(), style.getMainColor().solid(), style.getSecondaryColor().border(), false, true);
		//	context.drawTexture(this.texture, 2 + (20 * size), 1, 1, 22, 21, 0, 1, 22, textureSize, textureSize);
		drawHiglight(context, 20 * selected, 0, style.getSecondaryColor().border(), style.getHightlightColor().border(), style.getHightlightColor().background());
		//context.drawTexture(this.texture, 20 * selected, 0, 24, 24, 22, 0, 24, 24, textureSize, textureSize);


		matrices.pop();
		matrices.push();
		matrices.translate(x, y, 0);

		for(int i = 0; i < size; i++) {
			int n = i * 20 + 4;
			int o = 4;
			DrawUtils.renderHotbarItem(mc, context, n, o, delta, mc.player, stacks.get(i), i);
		}
		matrices.pop();
		if(this.blend) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
		}
	}

	public static void drawHiglight(OwoUIDrawContext context, int x, int y, int insideColor, int borderColor, int centerColor) {
		if(insideColor != 0) {
			context.fill(x + 1, y + 1, x + 23, y + 4, insideColor);
			context.fill(x + 1, y + 4, x + 4, y + 20, insideColor);
			context.fill(x + 1, y + 20, x + 23, y + 23, insideColor);
			context.fill(x + 20, y + 4, x + 23, y + 20, insideColor);
		}
		if(borderColor != 0) {
			context.fill(x, y, x + 24, y + 1, borderColor);
			context.fill(x, y + 1, x + 1, y + 23, borderColor);
			context.fill(x, y + 23, x + 24, y + 24, borderColor);
			context.fill(x + 23, y + 1, x + 24, y + 23, borderColor);
		}
		if(centerColor != 0)
			context.fill(x + 4, y + 4, x + 20, y + 20, centerColor);
	}

	public List<ItemStack> getStacks() {
		return stacks;
	}

	public void setStacks(DefaultedList<ItemStack> stacks) {
		if(!areEqual(stacks, this.stacks))
			hideTimer = 110;
		this.stacks = stacks;
		if(this.size != stacks.size()) {
			this.size = stacks.size();
			this.dirty = true;
			this.applySizing();
		} else
			this.size = stacks.size();
		hideTimer = MinecraftClient.getInstance().player.isCreative() ? 200 : Math.max(hideTimer - 1, 0);
	}

	public float getSelected() {
		return selected;
	}

	public float getSize() {
		return size;
	}

	public void setSelected(int selected) {
		var s = Math.min(selected, size - 1);
		if(this.selected != s)
			hideTimer = 210;
		this.selected = s;
	}

	public static boolean areEqual(DefaultedList<ItemStack> left, DefaultedList<ItemStack> right) {
		if(left.size() != right.size())
			return false;
		for(int i = 0; i < left.size(); i++) {
			if(!areEqual(left.get(i), right.get(i)))
				return false;
		}
		return true;
	}

	public static boolean areEqual(ItemStack left, ItemStack right) {
		if(left == right) {
			return true;
		} else {
			return left.getItem() == right.getItem() && left.getCount() == right.getCount();
		}
	}

	public void setSize(int size) {
		this.size = size;
		stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);
		UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);
		UIParsing.apply(children, "size", UIParsing::parseSignedInt, this::setSize);

	}

	public static HotBarComponent parse(Element element) {
		UIParsing.expectAttributes(element, "texture");
		var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
		return new HotBarComponent(new Identifier(textureId.getNamespace(), "textures/gui/" + textureId.getPath()));
	}
}
