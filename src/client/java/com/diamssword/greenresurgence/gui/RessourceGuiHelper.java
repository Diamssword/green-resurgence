package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class RessourceGuiHelper {
	public static final Identifier ENERGY_TEXTURE = GreenResurgence.asRessource("textures/gui/energy.png");

	public static void drawTooltip(OwoUIDrawContext context, UniversalResource resource, int mouseX, int mouseY, float time) {

		if (resource != null) {
			if (resource.getType().isItem)
				context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, resource.getCurrentItem(time), mouseX, mouseY);

			else
				context.drawTooltip(MinecraftClient.getInstance().textRenderer, mouseX, mouseY, List.of(TooltipComponent.of(resource.getName(time).asOrderedText())));
		}
	}

	public static void drawRessource(OwoUIDrawContext context, UniversalResource resource, int x, int y, float time) {

		if (resource != null) {
			if (resource.getType().isItem) {
				context.drawItem(resource.getCurrentItem(time), x, y);
			} else if (resource.getType().isFluid) {
				resource.getCurrentFluid(time).ifPresent(f -> {
					var rend = FluidRenderHandlerRegistry.INSTANCE.get(f);
					if (rend != null) {
						var sprites = rend.getFluidSprites(null, null, f.getDefaultState());
						var col = rend.getFluidColor(null, null, f.getDefaultState());
						var col1 = Color.ofRgb(col);
						if (sprites.length > 0)
							context.drawSprite(x, y, 1, 16, 16, sprites[0], col1.red(), col1.green(), col1.blue(), 1);
					}
				});
			} else {
				context.drawTexture(ENERGY_TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
			}
		}
	}

	public static void drawRessourceExtra(DrawContext ctx, UniversalResource resource, int x, int y, float time, int color) {
		if (resource != null) {
			if (resource.getType().isItem) {
				drawItemCountData(ctx, resource.getCurrentItem(time), x, y, color);
			}
			var textRenderer = MinecraftClient.getInstance().textRenderer;
			ctx.getMatrices().push();
			//ctx.getMatrices().translate(0.0F, 0.0F, 200.0F);
			var str = getCountDisplay(resource.getAmount(), resource.getType().isFluid);
			ctx.getMatrices().translate((x + 19 - 2 - textRenderer.getWidth(str)), y + 8, 180f);
			if (str.length() > 4) {
				ctx.getMatrices().scale(0.5f, 0.5f, 1f);
				ctx.getMatrices().translate(14 + (textRenderer.getWidth(str) / 2f), 10, 100f);
			} else if (str.length() > 2) {
				ctx.getMatrices().scale(0.5f, 0.5f, 1f);
				ctx.getMatrices().translate(9 + (textRenderer.getWidth(str) / 2f), 10, 80f);
			}
			ctx.drawText(textRenderer, str, 0, 0, color, true);
			ctx.getMatrices().pop();
		}
	}

	public static String getCountDisplay(int count, boolean fluid) {
		if (count < 1000 && !fluid)
			return count + "";
		else if (count < 1000)
			return count + "mB";
		String[] unites = {"", "K", "M", "G", "T"};
		String[] unitesB = {"mB", "B"};
		int i = 0;
		double d = count;
		while (d > 1000 && i < (fluid ? unitesB.length : unites.length) - 1) {
			d /= 1000;
			i++;
		}
		return String.format("%.1f%s", d, fluid ? unitesB[i] : unites[i]);
	}

	public static void drawItemCountData(DrawContext ctx, ItemStack stack, int x, int y, int color) {
		if (!stack.isEmpty()) {
			ctx.getMatrices().push();
			int k;
			int l;
			if (stack.isItemBarVisible()) {
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				k = x + 2;
				l = y + 13;
				ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
				ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (f > 0.0F) {
				k = y + MathHelper.floor(16.0F * (1.0F - f));
				l = k + MathHelper.ceil(16.0F * f);
				ctx.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
			}

			ctx.getMatrices().pop();
		}
	}
}
