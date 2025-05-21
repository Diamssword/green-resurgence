package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.base.BaseParentComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class SearchBoxComponent extends TextBoxComponent {
	private final Map<String, BaseParentComponent> list = new HashMap<>();
	private String selected;
	private boolean inited = false;
	private int resizedAt = -1;
	private final Map<String, BaseParentComponent> filteredList = new HashMap<>();
	private int totalHeight = 0;
	private int offset = 0;
	private Size space;

	protected SearchBoxComponent(Sizing horizontalSizing) {
		super(horizontalSizing);
		this.textValue.observe(v -> {
			selected = null;
			offset = 0;
			updateContent();
		});
	}

	public void setOptions(Map<String, BaseParentComponent> options) {
		list.clear();
		list.putAll(options);
		updateContent();
	}

	public String getSelected() {
		return selected;
	}

	public void onClickMenu(double mouseX, double mouseY) {
		for (var c : filteredList.entrySet()) {
			if (c.getValue().isInBoundingBox(mouseX, mouseY)) {
				setText(c.getKey());
				selected = c.getKey();
				updateContent();
				setFocused(true);
				setEditable(true);
				return;
			}
		}
	}

	@Override
	public void inflate(Size space) {
		super.inflate(space);
		this.space = space;
		this.inited = true;
		resizedAt = 2;
		this.tick();
	}

	@Override
	public void tick() {
		super.tick();
		if (resizedAt > -1) {
			resizedAt--;
			if (resizedAt == 0) {
				updateContent();
				resizedAt = -1;
			}
		}

	}

	@Override
	public boolean onMouseScroll(double mouseX, double mouseY, double amount) {

		offset = (int) Math.min(Math.max(0, offset - amount), list.size() - 1);
		updateContent();
		return true;
	}

	private void updateContent() {
		if (!inited) {
			return;
		}
		filteredList.clear();

		var txt = textValue.get().toLowerCase();
		if (selected != null || txt.isEmpty()) {
			totalHeight = 0;
			return;
		}
		var l = txt.isBlank() ? list.keySet().stream() : list.keySet().stream().filter(v1 -> v1.toLowerCase().contains(txt)).sorted();
		var i = this.y();
		totalHeight = 0;
		var d = -1;
		if (this.space != null)
			d = space.height() - (this.height() + 2);
		var ls = l.toList();
		if (ls.isEmpty()) {
			totalHeight = 0;
			return;
		}
		if (offset < 0)
			offset = 0;
		if (offset >= ls.size())
			offset = ls.size() - 1;
		for (int j = offset; j < ls.size(); j++) {
			var t1 = ls.get(j);
			var comp = list.get(t1);
			comp.inflate(Size.of(this.width, this.height));
			i += comp.height();
			totalHeight += comp.height();
			if (d > -1 && totalHeight >= d)
				break;
			comp.mount(null, this.x(), i);
			filteredList.put(t1, comp);
		}
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		var ctx = OwoUIDrawContext.of(context);
		ctx.drawGradientRect(
				this.x(), this.y() + this.height, this.width(), totalHeight,
				0xC0101010, 0xC0101010, 0xD0101010, 0xD0101010
		);
		super.renderButton(context, mouseX, mouseY, delta);


		for (var comp : filteredList.values()) {

			comp.draw(ctx, mouseX, mouseY, 0, delta);
		}

	}
}
