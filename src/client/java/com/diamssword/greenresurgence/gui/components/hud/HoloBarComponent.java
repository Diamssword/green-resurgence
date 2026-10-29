package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.containers.player.HoloStyle;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.components.panels.HoloRenders;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import org.w3c.dom.Element;

import java.util.Map;

public class HoloBarComponent extends BaseComponent implements IHideableComponent {

	private float fillPercent = 1;
	private float goal = 1;
	private boolean hidden;
	private boolean inverted = false;
	private HoloStyle style;
	private HoloRenders.RenderSurface fillSurface;
	private HoloRenders.RenderSurface crtSurface;
	private BarDrawer drawer;

	public static BarDrawer getDefaultDrawer(int fillColor) {
		return (context, bar, fill) -> {
			if(fill > 0) {
				int w = Math.round((bar.width() - 1) * fill);
				int x = bar.inverted() ? ((bar.width() - 1) - w) : 1;
				int w1 = bar.inverted() ? bar.width() - 1 : w;
				context.fill(x, 1, w1, bar.height() - 1, fillColor);
			}
		};
	}

	public static BarDrawer getHorseBarDrawer(int mainColor, int secondColor) {
		return (context, bar, fill) -> {
			if(fill > 0) {
				float f = Math.min(fill, 0.875f);
				int w = Math.round((bar.width() - 1) * f);
				int x = bar.inverted() ? ((bar.width() - 1) - w) : 1;
				int w1 = bar.inverted() ? bar.width() - 1 : w;
				context.fill(x, 1, w1, bar.height() - 1, mainColor);
				if(fill > 0.875f) {
					float f1 = fill - 0.875f;
					int w2 = Math.round((bar.width() - 1) * f1);
					int r = Math.round((bar.width() - 1) * 0.125f);
					int x1 = bar.inverted() ? r - w2 : Math.round((bar.width() - 1) * 0.875f);
					int w3 = bar.inverted() ? r + 1 : x1 + w2;
					context.fill(x1, 1, w3, bar.height() - 1, secondColor);
				}
			}
		};
	}

	public HoloBarComponent(BarDrawer drawer) {
		this.style = HoloPanels.getCurrentStyle();
		this.drawer = drawer;
		this.fillSurface = HoloRenders.crtSidewayLines(1, 25, this.style.getBackgroundAlpha());
		this.crtSurface = HoloRenders.crtLines(1, this.style.getBackgroundAlpha());
	}

	public void tick() {

		if(goal > fillPercent) {
			float d = Math.max((goal - fillPercent) / 10f, 0.005f);
			fillPercent = Math.min(goal, fillPercent + d);
		} else if(goal < fillPercent) {
			float d = Math.max((fillPercent - goal) / 10f, 0.005f);
			fillPercent = Math.max(goal, fillPercent - d);
		}
	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {

		return 20;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {

		return 20;
	}


	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		RenderSystem.enableDepthTest();
		if(hidden)
			return;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		crtSurface.draw(context, 0, 0, width, height);
		barDrawer().render(context, this, fillPercent);
		fillSurface.draw(context, 0, 0, width, height);
		context.drawRectOutline(0, 0, width(), height(), style.getMainColor().border());


		RenderSystem.disableBlend();

		matrices.pop();
	}

	public HoloBarComponent inverted(boolean inverted) {
		this.inverted = inverted;
		return this;
	}

	public boolean inverted() {
		return inverted;
	}

	public float getFillPercent() {
		return fillPercent;
	}

	public void setFillPercent(float fillPercent) {
		this.fillPercent = Math.max(0, fillPercent);
		this.goal = Math.max(0, fillPercent);
	}

	public void animateFillPercent(float fillPercent) {
		this.goal = fillPercent;
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);

		if(element.hasAttribute("inverted"))
			this.inverted(UIParsing.parseBool(element.getAttributeNode("inverted")));

	}

	public HoloBarComponent barDrawer(BarDrawer drawer) {
		this.drawer = drawer;
		return this;
	}

	public BarDrawer barDrawer() {
		return this.drawer;
	}

	public static HoloBarComponent parse(Element element) {
		var col = 0xAFFF00BB;
		if(element.hasAttribute("color"))
			col = Color.parse(element.getAttributeNode("color")).argb();
		return new HoloBarComponent(getDefaultDrawer(col));
	}

	@Override
	public void hidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	public static interface BarDrawer {
		public void render(OwoUIDrawContext context, HoloBarComponent component, float percent);
	}

}
