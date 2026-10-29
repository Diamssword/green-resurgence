package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.player.HoloStyle;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.components.panels.HoloRenders;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

import java.util.Map;

public class SingleSlotComponent extends BaseComponent implements IHideableComponent {

	private ItemStack stack = ItemStack.EMPTY;
	protected boolean blend = false;

	protected boolean indicatorMode = false;
	protected static final Identifier textureAtt = GreenResurgence.asRessource("textures/gui/hud/attack.png");
	private boolean hidden;
	private int hideTimer = 200;
	private HoloStyle style = HoloPanels.getCurrentStyle();

	protected SingleSlotComponent() {
		super();
	}

	public SingleSlotComponent blend(boolean blend) {
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
		return 22;
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
		return 22;
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(hidden || (hideTimer <= 0 && !indicatorMode))
			return;
		RenderSystem.enableDepthTest();

		if(this.blend && !indicatorMode) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, Math.min(100, this.hideTimer) / 100f);
		}

		var matrices = context.getMatrices();

		var mc = MinecraftClient.getInstance();
		if(!indicatorMode) {
			drawSlot(context, this.x, this.y, style.getSecondaryColor().background(), style.getMainColor().solid(), style.getMainColor().border(), style.getSecondaryColor().border(), true, true);

			matrices.push();
			matrices.translate(x, y, 0);
			DrawUtils.renderHotbarItem(mc, context, 3, 3, delta, mc.player, stack, 42);
			matrices.pop();
		} else {
			matrices.push();
			matrices.translate(x, y, 0);
			if(mc.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
				float f = mc.player.getAttackCooldownProgress(0.0F);
				if(f < 1.0F) {
					int p = (int) (f * 19.0F);
					context.drawTexture(textureAtt, 2, 2, 18, 18, 0, 0, 18, 18, 36, 18);
					context.drawTexture(textureAtt, 2, 2 + (18 - p), 18, p, 18, 18 - p, 18, p, 36, 18);

				}
			}
			matrices.pop();
		}
		if(this.blend) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
		}

	}

	private static HoloRenders.RenderSurface panel = HoloRenders.crtLines(1, 70);

	public static void drawSlot(OwoUIDrawContext context, int x, int y, int bgColor, int sideColor, int innerRingColor, int outerColor, boolean leftRounded, boolean rightRounded) {
		//	panel = HoloPanels.crtLines(1, 70);
		panel.draw(context, x + 2, y + 2, 18, 18);
		context.fill(x + 3, y + 3, x + 19, y + 19, bgColor);
		//first ring
		context.fill(x + 2, y + 2, x + 20, y + 3, innerRingColor);
		context.fill(x + 2, y + 19, x + 20, y + 20, innerRingColor);
		context.fill(x + 2, y + 3, x + 3, y + 19, innerRingColor);
		context.fill(x + 19, y + 3, x + 20, y + 19, innerRingColor);
		//second ring
		context.fill(x + (leftRounded ? 2 : 1), y + 1, x + (rightRounded ? 20 : 21), y + 2, sideColor);
		context.fill(x + (leftRounded ? 2 : 1), y + 20, x + (rightRounded ? 20 : 21), y + 21, sideColor);
		context.fill(x + 1, y + 2, x + 2, y + 20, sideColor);
		context.fill(x + 20, y + 2, x + 21, y + 20, sideColor);
		//outer ring
		context.fill(x + (leftRounded ? 2 : 1), y, x + (rightRounded ? 20 : 21), y + 1, outerColor);
		context.fill(x + (leftRounded ? 2 : 1), y + 21, x + (rightRounded ? 20 : 21), y + 22, outerColor);
		if(rightRounded) {
			context.fill(x + 20, y + 20, x + 21, y + 21, outerColor);
			context.fill(x + 20, y + 1, x + 21, y + 2, outerColor);
			context.fill(x + 21, y + 2, x + 22, y + 20, outerColor);
		}
		if(leftRounded) {
			context.fill(x + 1, y + 1, x + 2, y + 2, outerColor);
			context.fill(x + 1, y + 20, x + 2, y + 21, outerColor);
			context.fill(x, y + 2, x + 1, y + 20, outerColor);
		}

	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStacks(ItemStack stack) {

		if(!HotbarComponent.areEqual(stack, this.stack))
			hideTimer = 110;
		this.stack = stack;
		hideTimer = MinecraftClient.getInstance().player.isCreative() ? 200 : Math.max(hideTimer - 1, 0);

	}

	public boolean isIndicatorMode() {
		return indicatorMode;
	}

	public void setIndicatorMode(boolean indicatorMode) {
		this.indicatorMode = indicatorMode;
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);
		UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

	}

	public static SingleSlotComponent parse(Element element) {
		return new SingleSlotComponent();
	}
}
