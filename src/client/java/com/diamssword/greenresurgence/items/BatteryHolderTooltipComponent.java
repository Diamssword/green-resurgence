package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.helpers.BatteryStorageHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class BatteryHolderTooltipComponent implements TooltipComponent {
	public static final Identifier TEXTURE = new Identifier("green_resurgence", "textures/gui/slots/battery_bundle.png");
	private static final int field_32381 = 4;
	private static final int field_32382 = 1;
	private static final int TEXTURE_SIZE = 128;
	private static final int WIDTH_PER_COLUMN = 18;
	private static final int HEIGHT_PER_ROW = 20;
	private final DefaultedList<ItemStack> inventory;
	private final Text text1;
	private final Text text2;

	public BatteryHolderTooltipComponent(BatteryStorageHelper.BatteryHolderTooltipData data) {
		this.inventory = data.stacks();
		var filled = 0;
		for(ItemStack stack : inventory) {
			if(!stack.isEmpty())
				filled++;
		}

		var tiers = data.min() == data.max() ? data.min().name() : data.min().name() + " - " + data.max().name();
		text1 = Text.translatable("green_resurgence.gui.battery_holder.content", filled + "/" + inventory.size());
		text2 = Text.translatable("green_resurgence.gui.battery_holder.required", tiers);
	}

	@Override
	public int getHeight() {
		return this.getRows() * 20 + 2 + 4;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		var m = Math.max(textRenderer.getWidth(text1), textRenderer.getWidth(text2));
		return this.getColumns() * 18 + 4 + m;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		int i = this.getColumns();
		int j = this.getRows();
		int k = 0;

		for(int l = 0; l < j; l++) {
			for(int m = 0; m < i; m++) {
				int n = x + m * 18 + 1;
				int o = y + l * 20 + 1;
				this.drawSlot(n, o, k++, context, textRenderer);
			}
		}
		this.drawOutline(x, y, i, j, context);
		int yc = (20 * j) / 2;
		context.drawText(textRenderer, text1, x + (i * 18) + 5, y + yc - 9, Formatting.GRAY.getColorValue(), false);
		context.drawText(textRenderer, text2, x + (i * 18) + 5, y + yc + 3, Formatting.GRAY.getColorValue(), false);
	}

	private void drawSlot(int x, int y, int index, DrawContext context, TextRenderer textRenderer) {
		if(index < this.inventory.size()) {
			ItemStack itemStack = (ItemStack) this.inventory.get(index);
			this.draw(context, x, y, Sprite.SLOT);
			context.drawItem(itemStack, x + 1, y + 1, index);
			context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);

		}
	}

	private void drawOutline(int x, int y, int columns, int rows, DrawContext context) {
		this.draw(context, x, y, Sprite.BORDER_CORNER_TOP);
		this.draw(context, x + columns * 18 + 1, y, Sprite.BORDER_CORNER_TOP);

		for(int i = 0; i < columns; i++) {
			this.draw(context, x + 1 + i * 18, y, Sprite.BORDER_HORIZONTAL_TOP);
			this.draw(context, x + 1 + i * 18, y + rows * 20, Sprite.BORDER_HORIZONTAL_BOTTOM);
		}

		for(int i = 0; i < rows; i++) {
			this.draw(context, x, y + i * 20 + 1, Sprite.BORDER_VERTICAL);
			this.draw(context, x + columns * 18 + 1, y + i * 20 + 1, Sprite.BORDER_VERTICAL);
		}

		this.draw(context, x, y + rows * 20, Sprite.BORDER_CORNER_BOTTOM);
		this.draw(context, x + columns * 18 + 1, y + rows * 20, Sprite.BORDER_CORNER_BOTTOM);
	}

	private void draw(DrawContext context, int x, int y, Sprite sprite) {
		context.drawTexture(TEXTURE, x, y, 0, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
	}

	private int getColumns() {
		var size = this.inventory.size();
		if(size == 1)
			return 1;
		else if(size == 3)
			return 3;
		else if(size <= 4)
			return 2;
		else if(size == 7 || size == 8 || size > 9)
			return 4;
		return 3;

	}

	private int getRows() {
		var size = this.inventory.size();
		if(size <= 3)
			return 1;
		else if(size < 9)
			return 2;
		else if(size == 9)
			return 3;
		return 4;
	}

	@Environment(EnvType.CLIENT)
	static enum Sprite {
		SLOT(0, 0, 18, 20),
		BLOCKED_SLOT(0, 40, 18, 20),
		BORDER_VERTICAL(0, 18, 1, 20),
		BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
		BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
		BORDER_CORNER_TOP(0, 20, 1, 1),
		BORDER_CORNER_BOTTOM(0, 60, 1, 1);

		public final int u;
		public final int v;
		public final int width;
		public final int height;

		private Sprite(int u, int v, int width, int height) {
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
		}
	}
}
