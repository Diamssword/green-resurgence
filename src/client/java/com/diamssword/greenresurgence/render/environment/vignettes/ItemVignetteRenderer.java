package com.diamssword.greenresurgence.render.environment.vignettes;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public interface ItemVignetteRenderer {

	public default Vector3f getColor(MinecraftClient client, int layer) {
		return new Vector3f(1, 1, 1);
	}

	public default ItemStack getHeadStack() {
		return MinecraftClient.getInstance().player.getInventory().getArmorStack(3);
	}

	public default float getAlpha(MinecraftClient client, int layer) {
		return 1f;
	}

	public Identifier[] getTextures();

	public default void render(DrawContext context, Entity entity) {

		MinecraftClient client = MinecraftClient.getInstance();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		var textures = getTextures();
		if(textures != null) {
			for(int i = 0; i < textures.length; i++) {
				var text = textures[i];
				RenderSystem.setShaderTexture(0, text);
				var col = getColor(client, i);
				RenderSystem.setShaderColor(col.x, col.y, col.z, getAlpha(client, i));

				context.drawTexture(
						text,
						0, 0,
						0, 0,
						client.getWindow().getScaledWidth(),
						client.getWindow().getScaledHeight(),
						client.getWindow().getScaledWidth(),
						client.getWindow().getScaledHeight()
				);
			}
		}

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

	void tick(MinecraftClient client);
}
