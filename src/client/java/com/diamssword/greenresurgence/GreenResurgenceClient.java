package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.items.*;
import com.diamssword.greenresurgence.render.blockEntityRenderer.ImageBlockEntityRenderer;
import com.diamssword.greenresurgence.render.blockEntityRenderer.ItemBlockEntityRenderer;
import com.diamssword.greenresurgence.render.blockEntityRenderer.LootedBlockEntityRenderer;
import com.diamssword.greenresurgence.render.blockEntityRenderer.ShelfBlockEntityRenderer;
import com.diamssword.greenresurgence.render.cosmetics.ModularArmorLayerRenderer;
import com.diamssword.greenresurgence.render.cosmetics.CustomPlayerModel;
import com.diamssword.greenresurgence.gui.hud.CustomHud;
import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.gui.components.ComponentsRegister;
import com.diamssword.greenresurgence.gui.Handlers;
import com.diamssword.greenresurgence.items.weapons.GeckoActivated;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.render.Entities;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.structure.MultiblockInstance;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import org.lwjgl.glfw.GLFW;

public class GreenResurgenceClient implements ClientModInitializer {
	public static Item[] MARKER_ITEMS={
			MBlocks.SHADOW_BLOCk.asItem(),
			MBlocks.ITEM_BLOCK.asItem(),
			MBlocks.IMAGE_BLOCK.asItem(),
			MBlocks.LOOT_ITEM_BLOCK.asItem(),
	};
	private static KeyBinding keyBinding;
	private static KeyBinding keyBinding1;

	public static final EntityModelLayer PLAYER_MODEL = new EntityModelLayer(GreenResurgence.asRessource("player"),"main");
	public static final EntityModelLayer PLAYER_MODEL_S = new EntityModelLayer(GreenResurgence.asRessource("player_slim"),"main");
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(PLAYER_MODEL,()-> TexturedModelData.of(CustomPlayerModel.getTexturedModelData(Dilation.NONE,false),64,64));
		EntityModelLayerRegistry.registerModelLayer(PLAYER_MODEL_S,()-> TexturedModelData.of(CustomPlayerModel.getTexturedModelData(Dilation.NONE,true),64,64));
		//EntityModelLayerImpl.PROVIDERS.put(EntityModelLayers.PLAYER,);
		Entities.init();
		ClientPlayConnectionEvents.INIT.register((a,b)->{
			GreenResurgence.onPostInit();
		});
		for (MultiblockInstance structuresPlacer : ItemPlacers.multiblocksStructure) {
				BlockRenderLayerMap.INSTANCE.putBlock(structuresPlacer.block, RenderLayer.getCutout());
		}
		GenericBlocks.sets.forEach(set->{
			set.getGlasses().forEach((b,t)->{
				if(t== GenericBlockSet.Transparency.CUTOUT)
					BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getCutout());
				else if(t== GenericBlockSet.Transparency.TRANSPARENT)
					BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getTranslucent());
			});
		});
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.CONNECTOR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.TRASH_BAGS, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.SHADOW_BLOCk, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.DEPLOYABLE_LADDER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.SIDEWAY_SHELF_BLOCK, RenderLayer.getTranslucent());
		BlockEntityRendererFactories.register(MBlockEntities.LOOTED_BLOCk, LootedBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.ITEM_BLOCK, ItemBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.IMAGE_BLOCK, ImageBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.LOOT_ITEM_BLOCK, ItemBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlockEntities.LOOTABLE_SHELF, ShelfBlockEntityRenderer::new);
		ClientNetwork.initialize();
		ClientEvents.initialize();
		ItemModelOverrideProvider.init();
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key."+GreenResurgence.ID+".player_inv",
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_J, // The keycode of the key
				"category."+GreenResurgence.ID // The translation key of the keybinding's category.
		));
		keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key."+GreenResurgence.ID+".base_inv",
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_C, // The keycode of the key
				"category."+GreenResurgence.ID // The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(keyBinding1.isPressed())
			{
				Channels.MAIN.clientHandle().send(new GuiPackets.KeyPress(GuiPackets.KEY.Inventory));
			}

		});
		ArmorRenderer.register(
				new ModularArmorLayerRenderer(GreenResurgence.asRessource("textures/modular/armor/default.png")),
				MItems.MODULAR_BOOT,MItems.MODULAR_HEAD,MItems.MODULAR_CHEST,MItems.MODULAR_LEG
		);
		ModularArmorItem.ProviderFunction= ModularArmorRenderer::RendererProvider;
		GeckoActivated.ProviderFunction= GeckoItemRenderer::RendererProvider;
		BackPackItem.ProviderFunction=BackpackArmorRenderer::RendererProvider;
		Handlers.init();
		ComponentsRegister.init();
		CustomHud.init();
	}

}
