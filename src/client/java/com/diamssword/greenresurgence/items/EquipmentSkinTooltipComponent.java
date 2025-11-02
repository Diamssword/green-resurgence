package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinTooltipData;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSkinTooltipComponent implements TooltipComponent {

	public static final Identifier TEXTURE = new Identifier("textures/gui/container/bundle.png");
	private static final int WIDTH_PER_COLUMN = 18;
	private static final int HEIGHT_PER_ROW = 18;
	//private final List<Item> blueprints;
	private final List<IEquipmentDef> defs = new ArrayList<>();
	private final String skin;

	public EquipmentSkinTooltipComponent(EquipmentSkinTooltipData data) {
		this.skin = data.skin();

		var d = EquipmentSkins.skins.get(data.skin());
		if(d != null) {
			var skins = d.keySet().stream().toList();
			for(Item s : skins) {
				if(s instanceof IEquipementItem def) {
					this.defs.add(def.getEquipment(new ItemStack(s)).getEquipment());

				}
			}
		}
	}

	@Override
	public int getHeight() {
		return defs.size() * HEIGHT_PER_ROW + 16;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		var d = textRenderer.getWidth(Text.translatable("equipment.green_resurgence.tooltip.skin.desc"));
		for(var de : defs) {

			var d1 = 42 + textRenderer.getWidth(Text.translatable("item.green_resurgence.equipments." + de.getEquipmentType() + "_" + de.getEquipmentSubtype()));
			if(d1 > d)
				d = d1;
		}

		return d;
	}

	@Override
	public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
		context.drawText(textRenderer, Text.translatable("equipment.green_resurgence.tooltip.skin.desc"), x, y, Formatting.GRAY.getColorValue(), true);
		int i = this.defs.size();
		int k = 0;

		for(int m = 0; m < i; m++) {
			int o = y + m * 20 + 10;
			this.drawSlot(x, o, true, k, context, textRenderer);

			context.drawText(textRenderer, ">", x + 18, o + 6, Formatting.GRAY.getColorValue(), false);
			this.drawSlot(x + 22, o, false, k, context, textRenderer);
			var d = this.defs.get(k);
			context.drawText(textRenderer, Text.translatable("item.green_resurgence.equipments." + d.getEquipmentType() + "_" + d.getEquipmentSubtype()), x + 42, o + 6, Formatting.GRAY.getColorValue(), false);

			k++;
		}

		//this.drawOutline(x, y, i, j, context);
	}

	private void drawSlot(int x, int y, boolean bp, int index, DrawContext context, TextRenderer textRenderer) {
		if(index < this.defs.size()) {
			ItemStack itemStack;
			if(bp)
				itemStack = new ItemStack(this.defs.get(index).getBlueprintItem(), 1);
			else {
				itemStack = new ItemStack(this.defs.get(index).getEquipmentItem(), 1);
				var nbt = itemStack.getOrCreateNbt();
				nbt.putString("skin", skin);
			}
			//		this.draw(context, x, y);
			context.drawItem(itemStack, x + 1, y + 1, index);
			//	context.drawItemInSlot(textRenderer, itemStack, x + 1, y + 1);

		}
	}

	private void draw(DrawContext context, int x, int y) {
		context.drawTexture(TEXTURE, x, y, 0, 0, 0, 18, 20, 128, 128);
	}

}
