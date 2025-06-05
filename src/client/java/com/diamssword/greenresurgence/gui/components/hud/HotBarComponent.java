package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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
		if (hidden || hideTimer == 0)
			return;
		RenderSystem.enableDepthTest();

		if (this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.hideTimer / 100f);
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		var mc = MinecraftClient.getInstance();
		context.drawTexture(this.texture, 1, 1, 1, 22, 0, 0, 1, 22, textureSize, textureSize);
		for (int i = 0; i < size; i++) {
			context.drawTexture(this.texture, 2 + (20 * i), 1, 20, 22, 1, 0, 20, 22, textureSize, textureSize);
		}
		context.drawTexture(this.texture, 2 + (20 * size), 1, 1, 22, 21, 0, 1, 22, textureSize, textureSize);
		context.drawTexture(this.texture, 20 * selected, 0, 24, 24, 22, 0, 24, 24, textureSize, textureSize);


		matrices.pop();
		matrices.push();
		matrices.translate(x, y, 0);

		for (int i = 0; i < size; i++) {
			int n = i * 20 + 4;
			int o = 4;
			DrawUtils.renderHotbarItem(mc, context, n, o, delta, mc.player, stacks.get(i), i);
		}
		matrices.pop();
		if (this.blend) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
		}
	}

	public static void renderHotbarItem(MinecraftClient client, OwoUIDrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {
		if (!stack.isEmpty()) {
			float g = (float) stack.getBobbingAnimationTime() - f;
			if (g > 0.0F) {
				float h = 1.0F + g / 5.0F;
				context.getMatrices().push();
				context.getMatrices().translate((float) (x + 8), (float) (y + 12), 0.0F);
				context.getMatrices().scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				context.getMatrices().translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
			}

			context.drawItem(player, stack, x, y, seed);
			if (g > 0.0F) {
				context.getMatrices().pop();
			}

			context.drawItemInSlot(client.textRenderer, stack, x, y);
		}
	}

	public List<ItemStack> getStacks() {
		return stacks;
	}

	public void setStacks(DefaultedList<ItemStack> stacks) {
		if (!stacks.equals(this.stacks))
			hideTimer = 100;
		this.stacks = stacks;
		if (this.size != stacks.size()) {
			this.size = stacks.size();
			this.dirty = true;
			this.applySizing();
		} else
			this.size = stacks.size();
		hideTimer = Math.max(hideTimer - 1, 0);
	}

	public float getSelected() {
		return selected;
	}

	public float getSize() {
		return size;
	}

	public void setSelected(int selected) {
		var s = Math.min(selected, size - 1);
		if (this.selected != s)
			hideTimer = 100;
		this.selected = s;
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
