package com.diamssword.greenresurgence.gui.hud;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.gui.components.ClothButtonComponent;
import com.diamssword.greenresurgence.network.NotificationPackets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResurgenceToast implements Toast {
	public final NotificationPackets.Size size;
	public final int duration;
	public final Text title;
	public final Text desc;
	public final NotificationPackets.Renderer renderer;
	public final String renderID;
	public final @Nullable NbtCompound renderExtra;

	public ResurgenceToast(NotificationPackets.Size size, float durationInSec, Text title, Text desc, NotificationPackets.Renderer renderer, String renderID, @Nullable NbtCompound renderExtra) {
		this.size = size;
		this.duration = (int) (durationInSec * 1000);
		this.title = title;
		this.desc = desc;
		this.renderer = renderer;
		this.renderID = renderID;
		this.renderExtra = renderExtra;
	}

	@Override
	public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
		int i = this.getWidth();
		int j = this.getHeight();
		this.drawPart(context, i, 0, 0, 28);
		if(j > 32) {
			for(int m = 28; m < j - 4; m += 16) {
				this.drawPart(context, i, 4, m, 16);
			}
		}
		this.drawPart(context, i, 32 - 4, j - 4, 4);
		switch(renderer) {
			case Item -> {
				var it = new ItemStack(Registries.ITEM.get(new Identifier(renderID)));
				if(renderExtra != null)
					it.setNbt(renderExtra);
				context.drawItemWithoutEntity(it, 8, 8);
			}
			case Cloth -> {
				var cloth = CharactersApi.clothing().getCloth(new Identifier(renderID));

				cloth.ifPresent(value -> {
					float scale = value.layer().getDisplayMode() != 0 ? (value.layer().getDisplayMode() == 2 ? 30 : 28) : 12;
					float off = value.layer().getDisplayMode() != 0 ? (value.layer().getDisplayMode() == 2 ? -30 : 12) : 0;
					ClothButtonComponent.drawClothing(context, 8, 2, 16, getHeight(), scale, off, value, startTime * 0.1f);
				});
			}
			case None -> {}
		}
		drawText(context, manager, startTime);

		return startTime >= duration * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}

	public void renderItem(DrawContext context) {


	}

	public void drawText(DrawContext context, ToastManager manager, long startTime) {
		boolean small = manager.getClient().textRenderer.getWidth(desc) >= 125;
		if(small)
			context.scale(0.65f, 0.65f, 0.65f);
		int i1 = 16776960;
		if(!small) {
			context.drawText(manager.getClient().textRenderer, title, 30, 7, i1 | 0xFF000000, false);
			context.drawText(manager.getClient().textRenderer, desc, 30, (getHeight() / 2) + 2, -1, false);
		} else {
			float fH = (manager.getClient().textRenderer.fontHeight * 0.65f);
			List<OrderedText> list = manager.getClient().textRenderer.wrapLines(desc, 190);
			var max = (getHeight() - (7 + fH)) / fH;
			if(list.size() <= max) {
				context.drawText(manager.getClient().textRenderer, title, 46, 7, i1 | 0xFF000000, false);
				int start = (int) (12 + fH);
				for(OrderedText orderedText : list) {
					context.drawText(manager.getClient().textRenderer, orderedText, 46, start, -1, false);
					start += (int) (fH + 4);
				}

			} else {
				int start = 6;
				if(startTime < 1000L) {
					start = (int) (12 + fH);
					int k = MathHelper.floor(MathHelper.clamp((float) (1000L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					context.drawText(manager.getClient().textRenderer, title, 46, 7, i1 | k, false);
				}
				for(OrderedText orderedText : list) {
					if(start >= (getHeight() / 0.65f) - fH - 4) {
						context.drawText(manager.getClient().textRenderer, "...", (int) ((getWidth() - 8) / 0.65f), start - (int) (fH + 4), -1, false);
						break;
					}
					context.drawText(manager.getClient().textRenderer, orderedText, 46, start, -1, false);
					start += (int) (fH + 4);

				}

			}
		}
	}

	private void drawPart(DrawContext context, int width, int textureV, int y, int height) {
		int i = textureV == 0 ? 20 : 5;
		int j = Math.min(60, width - i);
		context.drawTexture(TEXTURE, 0, y, 0, textureV, i, height);

		for(int k = i; k < width - j; k += 64) {
			context.drawTexture(TEXTURE, k, y, 32, textureV, Math.min(64, width - k - j), height);
		}

		context.drawTexture(TEXTURE, width - j, y, 160 - j, textureV, j, height);
	}

	@Override
	public Object getType() {
		return Toast.super.getType();
	}

	@Override
	public int getHeight() {
		return switch(size) {
			case Normal -> 32;
			case Double -> 64;
		};
	}

}
