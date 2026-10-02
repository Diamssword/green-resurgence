package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdaptiveLabelComponent extends LabelComponent {

	private SyncedScale scaler = null;
	protected float maximumScale = 1.0f;
	protected float minimumScale = 0.4f;
	protected float minimumPhysicalTextHeight = 8.0f;

	protected float adaptiveScale = 1.0f;
	protected int adaptiveWidth = -1;
	protected boolean adaptiveLayoutDirty = true;

	public AdaptiveLabelComponent(Text text) {
		super(text);
	}

	public AdaptiveLabelComponent(SyncedScale scaler, Text text) {
		super(text);
		this.scaler = scaler;
		this.scaler.addLabel(this);
	}

	public AdaptiveLabelComponent minimumScale(float scale) {
		this.minimumScale = Math.max(0.01f, Math.min(scale, 1.0f));
		this.adaptiveLayoutDirty = true;
		this.notifyParentIfMounted();
		return this;
	}

	public float minimumScale() {
		return this.minimumScale;
	}

	public AdaptiveLabelComponent maximumScale(float scale) {
		this.maximumScale = Math.max(0.01f, Math.min(scale, 1.0f));
		this.adaptiveLayoutDirty = true;
		this.notifyParentIfMounted();
		return this;
	}

	public float maximumScale() {
		return this.maximumScale;
	}

	public AdaptiveLabelComponent minimumPhysicalTextHeight(float pixels) {
		this.minimumPhysicalTextHeight = Math.max(0, pixels);
		this.adaptiveLayoutDirty = true;
		this.notifyParentIfMounted();
		return this;
	}

	public float minimumPhysicalTextHeight() {
		return this.minimumPhysicalTextHeight;
	}

	@Override
	public AdaptiveLabelComponent text(Text text) {
		super.text(text);
		this.adaptiveLayoutDirty = true;
		return this;
	}

	@Override
	public AdaptiveLabelComponent maxWidth(int maxWidth) {
		super.maxWidth(maxWidth);
		this.adaptiveLayoutDirty = true;
		return this;
	}

	@Override
	public AdaptiveLabelComponent lineHeight(int lineHeight) {
		super.lineHeight(lineHeight);
		this.adaptiveLayoutDirty = true;
		return this;
	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {
		if(sizing.method == Sizing.Method.FIXED) {
			return sizing.value;
		}
		return Math.min(this.textRenderer.getWidth(this.text), this.maxWidth);
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		int availableWidth;

		if(this.horizontalSizing.get().isContent()) {
			availableWidth = Math.min(
					this.textRenderer.getWidth(this.text),
					this.maxWidth
			);
		} else if(this.width > 0) {
			availableWidth = this.width;
		} else {
			availableWidth = this.maxWidth;
		}
		return calculateAdaptiveLayout(Math.max(1, availableWidth)).scaledHeight;
	}

	@Override
	public void inflate(Size space) {
		super.inflate(space);
		this.adaptiveLayoutDirty = true;
		ensureAdaptiveLayout();
	}

	protected AdaptiveLayout calculateAdaptiveLayout(int availableWidth) {
		availableWidth = Math.max(1, Math.min(availableWidth, this.maxWidth));

		List<Text> explicitLines = splitExplicitLines();

		int widestTextWidth = 0;

		for(Text line : explicitLines) {
			widestTextWidth = Math.max(
					widestTextWidth,
					this.textRenderer.getWidth(line)
			);
		}

		if(widestTextWidth <= availableWidth) {
			List<OrderedText> lines = new ArrayList<>();

			for(Text line : explicitLines) {
				lines.add(line.asOrderedText());
			}

			return new AdaptiveLayout(
					this.maximumScale,
					lines,
					calculateScaledHeight(lines.size(), this.maximumScale)
			);
		}

		float requiredScale = Math.min(
				(float) availableWidth / widestTextWidth,
				this.maximumScale
		);

		float minimumReadableScale = calculateMinimumReadableScale();

		if(requiredScale >= minimumReadableScale) {
			List<OrderedText> lines = new ArrayList<>();

			for(Text line : explicitLines) {
				lines.add(line.asOrderedText());
			}

			return new AdaptiveLayout(
					requiredScale,
					lines,
					calculateScaledHeight(lines.size(), requiredScale)
			);
		}

		float scale = minimumReadableScale;
		int wrappingWidth = Math.max(
				1,
				(int) Math.ceil(availableWidth / scale)
		);

		List<OrderedText> lines = new ArrayList<>();

		for(Text line : explicitLines) {
			List<OrderedText> wrapped = this.textRenderer.wrapLines(
					line,
					wrappingWidth
			);

			if(wrapped.isEmpty()) {
				lines.add(line.asOrderedText());
			} else {
				lines.addAll(wrapped);
			}
		}

		return new AdaptiveLayout(
				scale,
				lines,
				calculateScaledHeight(lines.size(), scale)
		);
	}

	protected List<Text> splitExplicitLines() {
		List<MutableText> lines = new ArrayList<>();
		lines.add(Text.empty());

		this.text.visit((style, string) -> {
			String[] parts = string.split("\n", -1);

			for(int i = 0; i < parts.length; i++) {
				if(!parts[i].isEmpty()) {
					lines.get(lines.size() - 1).append(
							Text.literal(parts[i]).setStyle(style)
					);
				}

				if(i < parts.length - 1) {
					lines.add(Text.empty());
				}
			}

			return Optional.empty();
		}, Style.EMPTY);

		return new ArrayList<>(lines);
	}

	protected float calculateMinimumReadableScale() {
		double guiScale = MinecraftClient.getInstance()
				.getWindow()
				.getScaleFactor();

		float physicalFontHeight =
				this.textRenderer.fontHeight * (float) guiScale;

		float physicalScaleRequirement =
				this.minimumPhysicalTextHeight <= 0
						? 0.0f
						: this.minimumPhysicalTextHeight / physicalFontHeight;

		return Math.max(
				this.minimumScale,
				Math.min(physicalScaleRequirement, this.maximumScale)
		);
	}

	protected int calculateScaledHeight(int lineCount, float scale) {
		if(lineCount <= 0) {
			return 0;
		}

		int normalHeight = lineCount * (this.lineHeight() + 2) - 2;
		return (int) Math.ceil(normalHeight * scale);
	}

	protected void ensureAdaptiveLayout() {
		if(this.width <= 0) {
			return;
		}

		if(!this.adaptiveLayoutDirty && this.adaptiveWidth == this.width) {
			if(this.scaler != null)
				this.scaler.onSizeChange(this, adaptiveScale);
			return;
		}

		AdaptiveLayout layout = calculateAdaptiveLayout(this.width);

		this.adaptiveScale = layout.scale;
		this.wrappedText = layout.lines;
		this.adaptiveWidth = this.width;
		this.adaptiveLayoutDirty = false;
		if(this.scaler != null)
			this.scaler.onSizeChange(this, adaptiveScale);
	}

	@Override
	public void draw(
			OwoUIDrawContext context,
			int mouseX,
			int mouseY,
			float partialTicks,
			float delta
	) {
		ensureAdaptiveLayout();

		if(this.width <= 0 || this.wrappedText.isEmpty()) {
			return;
		}

		int x = this.x;
		int y = this.y;

		if(this.horizontalSizing.get().isContent()) {
			x += this.horizontalSizing.get().value;
		}

		if(this.verticalSizing.get().isContent()) {
			y += this.verticalSizing.get().value;
		}

		final int renderX = x;
		final int renderY = y;
		final float scale = this.adaptiveScale;
		final int scaledTextHeight = calculateScaledHeight(
				this.wrappedText.size(),
				scale
		);

		int textY = renderY;

		switch(this.verticalTextAlignment) {
			case CENTER -> textY += (this.height - scaledTextHeight) / 2;
			case BOTTOM -> textY += this.height - scaledTextHeight;
		}

		final int finalTextY = textY;

		context.draw(() -> {
			var matrices = context.getMatrices();
			matrices.push();

			matrices.translate(
					0,
					1 / MinecraftClient.getInstance().getWindow().getScaleFactor(),
					0
			);
			matrices.translate(renderX, finalTextY, 0);
			matrices.scale(scale, scale, 1.0f);

			float virtualWidth = this.width / scale;

			for(int i = 0; i < this.wrappedText.size(); i++) {
				OrderedText text = this.wrappedText.get(i);
				int textWidth = this.textRenderer.getWidth(text);
				int lineX = 0;

				switch(this.horizontalTextAlignment) {
					case CENTER -> lineX = (int) ((virtualWidth - textWidth) / 2);
					case RIGHT -> lineX = (int) (virtualWidth - textWidth);
				}

				lineX = Math.max(0, lineX);

				int lineY = i * (this.lineHeight() + 2)
						+ this.lineHeight()
						- this.textRenderer.fontHeight;

				context.drawText(
						this.textRenderer,
						text,
						lineX,
						lineY,
						this.color.get().argb(),
						this.shadow
				);
			}

			matrices.pop();
		});
	}

	@Override
	public void drawTooltip(
			OwoUIDrawContext context,
			int mouseX,
			int mouseY,
			float partialTicks,
			float delta
	) {
		super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);

		if(!this.isInBoundingBox(mouseX, mouseY)) {
			return;
		}

		ensureAdaptiveLayout();

		Style style = this.styleAtScaled(
				mouseX - this.x,
				mouseY - this.y
		);

		context.drawHoverEvent(
				this.textRenderer,
				style,
				mouseX,
				mouseY
		);
	}

	@Override
	public boolean onMouseDown(double mouseX, double mouseY, int button) {
		ensureAdaptiveLayout();

		Style style = this.styleAtScaled(
				(int) mouseX - this.x,
				(int) mouseY - this.y
		);

		return this.textClickHandler.apply(style)
				| super.onMouseDown(mouseX, mouseY, button);
	}

	protected Style styleAtScaled(int mouseX, int mouseY) {
		if(this.wrappedText.isEmpty()) {
			return Style.EMPTY;
		}

		int scaledTextHeight = calculateScaledHeight(
				this.wrappedText.size(),
				this.adaptiveScale
		);

		float textY = switch(this.verticalTextAlignment) {
			case CENTER -> (this.height - scaledTextHeight) / 2.0f;
			case BOTTOM -> this.height - scaledTextHeight;
			default -> 0;
		};

		float unscaledX = mouseX / this.adaptiveScale;
		float unscaledY = (mouseY - textY) / this.adaptiveScale;

		if(unscaledY < 0) {
			return Style.EMPTY;
		}

		int line = (int) Math.floor(
				unscaledY / (this.lineHeight() + 2)
		);

		if(line < 0 || line >= this.wrappedText.size()) {
			return Style.EMPTY;
		}

		OrderedText orderedText = this.wrappedText.get(line);
		int lineWidth = this.textRenderer.getWidth(orderedText);
		float virtualWidth = this.width / this.adaptiveScale;

		float lineX = switch(this.horizontalTextAlignment) {
			case CENTER -> (virtualWidth - lineWidth) / 2.0f;
			case RIGHT -> virtualWidth - lineWidth;
			default -> 0;
		};

		float textX = unscaledX - lineX;

		if(textX < 0 || textX > lineWidth) {
			return Style.EMPTY;
		}

		return this.textRenderer.getTextHandler().getStyleAt(
				orderedText,
				(int) textX
		);
	}

	@Override
	public void parseProperties(
			UIModel model,
			Element element,
			Map<String, Element> children
	) {
		super.parseProperties(model, element, children);

		UIParsing.apply(children, "text", UIParsing::parseText, this::text);
		UIParsing.apply(children, "max-width", UIParsing::parseUnsignedInt, this::maxWidth);
		UIParsing.apply(children, "color", Color::parse, this::color);
		UIParsing.apply(children, "shadow", UIParsing::parseBool, this::shadow);
		UIParsing.apply(children, "line-height", UIParsing::parseUnsignedInt, this::lineHeight);
		UIParsing.apply(
				children,
				"vertical-text-alignment",
				VerticalAlignment::parse,
				this::verticalTextAlignment
		);
		UIParsing.apply(
				children,
				"horizontal-text-alignment",
				HorizontalAlignment::parse,
				this::horizontalTextAlignment
		);

		if(children.containsKey("minimum-scale")) {
			this.minimumScale = Float.parseFloat(
					children.get("minimum-scale").getTextContent()
			);
		}

		if(children.containsKey("maximum-scale")) {
			this.maximumScale = Float.parseFloat(
					children.get("maximum-scale").getTextContent()
			);
		}

		if(children.containsKey("minimum-physical-text-height")) {
			this.minimumPhysicalTextHeight = Float.parseFloat(
					children.get("minimum-physical-text-height").getTextContent()
			);
		}

		this.adaptiveLayoutDirty = true;
	}

	@Override
	public void dismount(DismountReason reason) {
		super.dismount(reason);
	}

	protected static class AdaptiveLayout {
		final float scale;
		final List<OrderedText> lines;
		final int scaledHeight;

		protected AdaptiveLayout(float scale, List<OrderedText> lines, int scaledHeight) {
			this.scale = scale;
			this.lines = lines;
			this.scaledHeight = scaledHeight;
		}
	}

	public static class SyncedScale {
		private float scale = 1;
		private List<AdaptiveLabelComponent> labels = new ArrayList<>();

		public void addLabel(AdaptiveLabelComponent label) {
			label.maximumScale(scale);
			if(label.mounted)
				onSizeChange(label, label.maximumScale);
			labels.add(label);
		}

		public void onSizeChange(AdaptiveLabelComponent label, float scale) {
			if(scale < this.scale) {
				for(AdaptiveLabelComponent lab : labels) {
					if(lab != label) {
						lab.maximumScale(scale);
					}
				}
				this.scale = scale;
			}
		}

		public void clear() {
			labels.clear();
			scale = 1;
		}
	}
}
