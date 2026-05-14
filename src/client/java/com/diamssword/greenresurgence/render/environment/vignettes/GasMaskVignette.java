package com.diamssword.greenresurgence.render.environment.vignettes;

import com.diamssword.greenresurgence.MSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class GasMaskVignette implements ItemVignetteRenderer {
	public final Identifier[] textures;

	public GasMaskVignette(Identifier... textures) {
		this.textures = textures;
	}

	@Override
	public float getAlpha(MinecraftClient client, int layer) {
		if(layer == 0)
			return 1;
		float time = (client.world.getTime() % 200) / 200.0f;
		return (float) (0.5f * Math.sin(time * 2 * Math.PI));
	}

	@Override
	public Identifier[] getTextures() {
		return textures;
	}

	@Override
	public void tick(MinecraftClient client) {
		if(client.world.getTime() % 200 == 0)
			client.player.playSound(MSounds.BREATH, SoundCategory.AMBIENT, 0.6f, (float) (0.4f + (Math.random() * 0.2f)));
	}
}
