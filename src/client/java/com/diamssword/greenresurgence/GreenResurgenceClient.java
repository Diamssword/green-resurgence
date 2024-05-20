package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntityRenderer.ImageBlockEntityRenderer;
import com.diamssword.greenresurgence.blockEntityRenderer.ItemBlockEntityRenderer;
import com.diamssword.greenresurgence.blockEntityRenderer.LootedBlockEntityRenderer;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.gui.ComponentsRegister;
import com.diamssword.greenresurgence.gui.Handlers;
import com.diamssword.greenresurgence.gui.SurvivalistInventory;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.structure.MultiblockInstance;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GreenResurgenceClient implements ClientModInitializer {
	private static KeyBinding keyBinding;
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.INIT.register((a,b)->{
			GreenResurgence.onPostInit();
		});
		for (MultiblockInstance structuresPlacer : ItemPlacers.multiblocksStructure) {
				BlockRenderLayerMap.INSTANCE.putBlock(structuresPlacer.block, RenderLayer.getCutout());
		}
		GenericBlocks.sets.forEach(set->{
			for (Block glass : set.getGlasses()) {
				BlockRenderLayerMap.INSTANCE.putBlock(glass, RenderLayer.getTranslucent());
			}
		});
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.CONNECTOR, RenderLayer.getCutout());
		BlockEntityRendererFactories.register(MBlockEntities.LOOTED_BLOCk, LootedBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.ITEM_BLOCK, ItemBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.IMAGE_BLOCK, ImageBlockEntityRenderer::new);
		ClientNetwork.initialize();
		ClientEvents.initialize();
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key."+GreenResurgence.ID+".spook",
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_I, // The keycode of the key
				"category."+GreenResurgence.ID // The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(keyBinding.isPressed())
			{
				Channels.MAIN.clientHandle().send(new GuiPackets.KeyPress(GuiPackets.KEY.Inventory));
			}

		});
		Handlers.init();
		ComponentsRegister.init();
	}

}
