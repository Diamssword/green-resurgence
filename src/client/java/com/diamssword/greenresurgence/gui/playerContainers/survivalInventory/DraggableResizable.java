package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory;

import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.ui.container.WrappingParentComponent;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.Observable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DraggableResizable<C extends Component> extends WrappingParentComponent<C> {

	protected int grabBorder = 5;
	protected boolean alwaysOnTop = false;

	protected int baseX = 0, baseY = 0;
	protected double xOffset = 0, yOffset = 0, snappedXOffset, snappedYOffset;
	protected final Observable<Vector4i> dragObservable = Observable.of(new Vector4i());

	private final List<Event<C>> actionObserver = new ArrayList<>();
	protected Vector2i grabbed = new Vector2i();
	private boolean editMode = false;
	private int minWidth = 20;
	private int minHeight = 20;
	private int moveSnap = 5;

	public static enum Action {
		RIGHT_CLICK,
		DELETE;
	}

	protected DraggableResizable(Sizing horizontalSizing, Sizing verticalSizing, C child) {
		super(horizontalSizing, verticalSizing, child);
		this.padding(Insets.none());
		dragObservable.set(new Vector4i(this.x, this.y, this.width, this.height));

	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(this.alwaysOnTop) context.getMatrices().translate(0, 0, 500);
		super.draw(context, mouseX, mouseY, partialTicks, delta);
		if(editMode) {
			context.getMatrices().push();
			context.getMatrices().translate(0, 0, zIndex() + 10);
			context.drawRectOutline(this.x(), this.y(), this.width(), this.height(), ColorUtils.whithAlpha(ColorUtils.WHITE, 0xFF));
			if((grabbed.length() != 0 || isInBoundingBox(mouseX, mouseY))) {

				if(grabbed.x == 1 || (mouseX < this.x() + this.width() && mouseX >= this.x() + this.width() - grabBorderSize()))
					context.fill(this.x() + this.width() - grabBorderSize(), this.y(), this.x() + this.width(), this.y() + this.height(), ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF));
				if(grabbed.x == -1 || (mouseX >= this.x() && mouseX < this.x() + grabBorderSize()))
					context.fill(this.x(), this.y(), this.x() + this.grabBorderSize(), this.y() + this.height(), ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF));
				if(grabbed.y == 1 || (mouseY < this.y() + this.height() && mouseY >= this.y() + this.height() - grabBorderSize()))
					context.fill(this.x(), this.y() + this.height() - grabBorderSize(), this.x() + this.width(), this.y() + this.height(), ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF));
				if(grabbed.y == -1 || (mouseY >= this.y() && mouseY < this.y() + grabBorderSize()))
					context.fill(this.x(), this.y(), this.x() + this.width, this.y() + this.grabBorderSize(), ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xFF));

			}
			context.getMatrices().translate(0, 0, -(zIndex() + 10));
			context.getMatrices().pop();
		}

		this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.childView);
		if(this.alwaysOnTop) context.getMatrices().translate(0, 0, -500);
	}

	@Override
	public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(this.alwaysOnTop) context.getMatrices().translate(0, 0, 500);
		super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
		if(this.alwaysOnTop) context.getMatrices().translate(0, 0, -500);
	}

	@Override
	public boolean canFocus(FocusSource source) {
		return source == FocusSource.MOUSE_CLICK;
	}

	@Override
	public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
		if(editMode && (keyCode == 261 || keyCode == 259)) {
			actionObserver.forEach(r -> r.onEvent(this, Action.DELETE, 0, 0));
		}
		return super.onKeyPress(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean onMouseDown(double mouseX, double mouseY, int button) {
		if(button == 1 && editMode) {
			actionObserver.forEach(r -> r.onEvent(this, Action.RIGHT_CLICK, mouseX, mouseY));
			return true;
		}
		int moveX = 0;
		int moveY = 0;
		if(mouseX < grabBorderSize())
			moveX = -1;
		else if(mouseX >= this.width - grabBorderSize()) {
			moveX = 1;
		}
		if(mouseY < grabBorderSize())
			moveY = -1;
		else if(mouseY > this.height - grabBorderSize()) {
			moveY = 1;
		}
		this.grabbed = new Vector2i(moveX, moveY);
		this.cursorStyle(grabbed.length() != 0 ? CursorStyle.MOVE : CursorStyle.HAND);

		if(editMode && this.grabbed.length() != 0)
			return true;
		return super.onMouseDown(mouseX, mouseY, button);
	}

	public DraggableResizable<C> snapStep(int snapStep) {
		this.moveSnap = snapStep;
		return this;
	}

	public int snapStep() {
		return moveSnap;
	}

	@Override
	public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
		var moveX = grabbed.x;
		var moveY = grabbed.y;
		var msnape = Screen.hasShiftDown() ? 1 : moveSnap;
		var xChanged = false;
		var yChanged = false;
		var sizeChanged = false;
		if(moveX == 1) {
			var w = (int) Math.round(this.width + (mouseX - this.width));
			this.horizontalSizing(Sizing.fixed(Math.round((float) Math.max(w, minWidth) / msnape) * msnape));
			sizeChanged = true;
		} else if(moveX == -1) {
			var w = (int) Math.round(this.width + (mouseX - this.width));
			w = (w / msnape) * msnape;
			if(w < 0 || (w > 0 && this.width > Math.round((float) minWidth / msnape) * msnape)) {
				this.xOffset += w;
				this.horizontalSizing(Sizing.fixed(Math.round((float) Math.max(this.width + (-w), minWidth) / msnape) * msnape));
				xChanged = true;
			}
		}
		if(moveY == 1) {
			var w = (int) Math.round(this.height + (mouseY - this.height));
			this.verticalSizing(Sizing.fixed(Math.round((float) Math.max(w, minHeight) / msnape) * msnape));
			sizeChanged = true;
		} else if(moveY == -1) {
			var w = (int) Math.round(this.height + (mouseY - this.height));
			w = (w / msnape) * msnape;
			if(w < 0 || (w > 0 && this.height > Math.round((float) minHeight / msnape) * msnape)) {
				this.yOffset += w;
				this.verticalSizing(Sizing.fixed(Math.round((float) Math.max(this.height + (-w), minHeight) / msnape) * msnape));
				yChanged = true;
			}
		}
		if(moveX == 0 && moveY == 0) {
			this.xOffset += deltaX;
			this.yOffset += deltaY;
			xChanged = true;
			yChanged = true;

		}
		if(yChanged) {
			super.updateY((int) (Math.round((this.baseY + this.yOffset) / msnape) * msnape));
			snappedYOffset = (int) (Math.round(this.yOffset / msnape) * msnape);
		}
		if(xChanged) {

			super.updateX((int) (Math.round((this.baseX + this.xOffset) / msnape) * msnape));
			snappedXOffset = (int) (Math.round(this.xOffset / msnape) * msnape);
		}
		if(xChanged || yChanged || sizeChanged)
			dragObservable.set(new Vector4i(this.x(), this.y(), this.width(), this.height()));
		return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
	}


	@Override
	public void mount(ParentComponent parent, int x, int y) {
		super.mount(parent, x, y);
		enforceBounds();
	}


	public void enforceBounds() {
		var p = this.positioning().get();
		if(hasParent() && p.type == Positioning.Type.ABSOLUTE) {
			if(this.x() + (this.width - (grabBorderSize() * 2)) < parent().x()) {
				updateX(parent().x());
			} else if(this.x() > parent().x() + parent().width() - (grabBorderSize() * 2)) {
				updateX(parent().x() + parent().width() - (grabBorderSize() * 2));
			}
			if(this.y() + (this.height - (grabBorderSize() * 2)) < parent().y()) {
				updateY(parent().y());
			} else if(this.y() > parent().y() + parent().height() - (grabBorderSize() * 2)) {
				updateY(parent().y() + parent().height() - (grabBorderSize() * 2));
			}
			if(this.width > parent().width()) {
				this.horizontalSizing(Sizing.fixed(parent().width()));
			}
			if(this.height > parent().height()) {
				this.verticalSizing(Sizing.fixed(parent().height()));
			}
		}
	}

	public DraggableResizable<C> editMode(boolean editMode) {
		this.editMode = editMode;
		this.cursorStyle(editMode ? CursorStyle.HAND : CursorStyle.NONE);
		return this;
	}

	public boolean editMode() {
		return editMode;
	}

	@Override
	public @Nullable Component childAt(int x, int y) {
		if(editMode && this.isInBoundingBox(x, y)) {
			return this;
		}
		return super.childAt(x, y);
	}


	@Override
	public void updateX(int x) {
		baseX = x;
		super.updateX((int) (x + snappedXOffset));
	}

	@Override
	public void updateY(int y) {
		this.baseY = y;
		super.updateY((int) (y + Math.round(this.snappedYOffset)));
	}

	public DraggableResizable<C> minSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		if(this.horizontalSizing().get().method == Sizing.Method.FIXED && this.horizontalSizing().get().value < this.minWidth)
			this.horizontalSizing(Sizing.fixed(this.minWidth));
		if(this.verticalSizing().get().method == Sizing.Method.FIXED && this.verticalSizing().get().value < this.minHeight)
			this.verticalSizing(Sizing.fixed(this.minHeight));

		return this;
	}

	public int minHeight() {
		return minHeight;
	}

	public int minWidth() {
		return minWidth;
	}

	public DraggableResizable<C> grabBorderSize(int grabBorderSize) {
		this.grabBorder = grabBorderSize;

		return this;
	}

	public int grabBorderSize() {
		return this.grabBorder;
	}

	public DraggableResizable<C> alwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
		return this;
	}

	public boolean alwaysOnTop() {
		return this.alwaysOnTop;
	}

	public DraggableResizable<C> addListener(Event<C> consumer) {
		actionObserver.add(consumer);
		return this;
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);
		UIParsing.apply(children, "grab-border-size", UIParsing::parseUnsignedInt, this::grabBorderSize);
		UIParsing.apply(children, "always-on-top", UIParsing::parseBool, this::alwaysOnTop);
	}

	@FunctionalInterface
	public interface Event<C extends Component> {
		public void onEvent(DraggableResizable<C> component, Action action, double x, double y);
	}
}
