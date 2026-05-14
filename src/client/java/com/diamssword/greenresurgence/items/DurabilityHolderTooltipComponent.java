package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.helpers.DurabilityStorageHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class DurabilityHolderTooltipComponent extends BaseItemHolderTooltipComponent {
	private final Text text1;
	private final Text text2;

	public DurabilityHolderTooltipComponent(DurabilityStorageHelper.DurabilityHolderTooltipData data) {
		super(data.stacks());
		var filled = 0;
		for(ItemStack stack : inventory) {
			if(!stack.isEmpty())
				filled++;
		}
		text1 = Text.translatable("green_resurgence.gui.durability_holder.percent", (int) (data.percent() * 100) + "%");
		//text1 = Text.translatable("green_resurgence.gui.gas_holder.content", data.capacity() + "/" + data.max());
		if(data.allowedItems().length > 0) {
			MutableText literal = Text.literal("");
			literal.append(Text.translatable("green_resurgence.gui.gas_holder.allowed"));
			for(int i = 0; i < data.allowedItems().length; i++) {
				literal.append(data.allowedItems()[i].getName());
				if(i < data.allowedItems().length - 1)
					literal.append(",");
			}
			text2 = literal;
		} else
			text2 = null;
	}


	@Override
	public int getWidth(TextRenderer textRenderer) {
		var m = Math.max(textRenderer.getWidth(text1), textRenderer.getWidth(text2));
		return this.getColumns() * 18 + 4 + m;
	}


	@Override
	public void drawExtra(TextRenderer textRenderer, int x, int y, DrawContext context) {
		int i = this.getColumns();
		int j = this.getRows();
		int yc = (20 * j) / 2;
		context.drawText(textRenderer, text1, x + (i * 18) + 5, y + yc - 9, Formatting.GRAY.getColorValue(), false);
		context.drawText(textRenderer, text2, x + (i * 18) + 5, y + yc + 3, Formatting.GRAY.getColorValue(), false);
	}
}
