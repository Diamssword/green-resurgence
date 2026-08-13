package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.utils.TextUtils;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.OrderedText;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextHandler.class)
public abstract class TextHandlerMixin {
	@Final
	@Shadow
	TextHandler.WidthRetriever widthRetriever;

	@Inject(
			method = "getWidth(Lnet/minecraft/text/OrderedText;)F",
			at = @At("HEAD"),
			cancellable = true
	)
	private void smallfont$scaleWidth(
			OrderedText text, CallbackInfoReturnable<Float> cir
	) {
		MutableFloat mutableFloat = new MutableFloat();
		text.accept((index, style, codePoint) -> {
			var m = this.widthRetriever.getWidth(codePoint, style);
			if(style.getFont().equals(TextUtils.MC_LITTLE))
				m = m * TextUtils.MC_LITTLE_SCALE;
			mutableFloat.add(m);
			return true;
		});
		cir.setReturnValue(mutableFloat.floatValue());

	}
}