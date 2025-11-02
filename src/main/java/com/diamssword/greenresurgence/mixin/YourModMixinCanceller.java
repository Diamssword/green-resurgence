package com.diamssword.greenresurgence.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class YourModMixinCanceller implements MixinCanceller {
	@Override
	public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
		if(mixinClassName.equals("com.conquestrefabricated.mixin.LeavesBlockMixin") || mixinClassName.equals("com.conquestrefabricated.mixin.AbstractBlockStateMixin")) {
			return true;
		}
		return false;
	}
}