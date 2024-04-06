package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenResurgence implements ModInitializer {
	public static final String ID="green_resurgence";
	public static final Logger LOGGER = LoggerFactory.getLogger("green_resurgence");

	@Override
	public void onInitialize() {
		FieldRegistrationHandler.register(MItems.class, ID, false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();

	}
}