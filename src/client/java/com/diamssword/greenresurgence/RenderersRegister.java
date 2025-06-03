package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.*;
import com.diamssword.greenresurgence.items.weapons.GeckoActivated;
import com.diamssword.greenresurgence.render.blockEntityRenderer.*;
import com.diamssword.greenresurgence.render.cosmetics.ModularArmorLayerRenderer;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.structure.MultiblockInstance;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class RenderersRegister {

	public static void init() {
		blocksRenderers();
		ArmorRenderer.register(
				new ModularArmorLayerRenderer(GreenResurgence.asRessource("textures/modular/armor/default.png")),
				MItems.MODULAR_BOOT, MItems.MODULAR_HEAD, MItems.MODULAR_CHEST, MItems.MODULAR_LEG
		);
		ModularArmorItem.ProviderFunction = ModularArmorRenderer::RendererProvider;
		GeckoActivated.ProviderFunction = GeckoItemRenderer::RendererProvider;
		BackPackItem.ProviderFunction = BackpackArmorRenderer::RendererProvider;
	}

	private static void blocksRenderers() {

		for (MultiblockInstance structuresPlacer : ItemPlacers.multiblocksStructure) {
			BlockRenderLayerMap.INSTANCE.putBlock(structuresPlacer.block, RenderLayer.getCutout());
		}
		GenericBlocks.sets.forEach(set -> {
			set.getGlasses().forEach((b, t) -> {
				if (t == GenericBlockSet.Transparency.CUTOUT)
					BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getCutout());
				else if (t == GenericBlockSet.Transparency.TRANSPARENT)
					BlockRenderLayerMap.INSTANCE.putBlock(b, RenderLayer.getTranslucent());
			});
		});
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.CONNECTOR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.TRASH_BAGS, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.SHADOW_BLOCk, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.DEPLOYABLE_LADDER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.NANOTEK_GENERATOR_CANISTER, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.NANOTEK_GENERATOR_SERVER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.NANOTEK_GENERATOR_SMALL_ANTENNA, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.NANOTEK_GENERATOR_BIG_ANTENNA, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.SIDEWAY_SHELF_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.ICE_COOLER_SHELF_LEFT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.ICE_COOLER_SHELF_RIGHT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(MBlocks.PLASTIC_TRASH_CAN_BROWN, RenderLayer.getCutout());


		BlockEntityRendererFactories.register(MBlocks.LOOTED_BLOCK.getEntityType(), LootedBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.ITEM_BLOCK.getEntityType(), ItemBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.IMAGE_BLOCK.getEntityType(), ImageBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.LOOT_ITEM_BLOCK.getEntityType(), ItemBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.SHELF_BLOCK.getEntityType(), ShelfBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.ARMOR_TINKERER.getEntityType(), ArmorTinkererBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(MBlocks.CRUMBELING_BLOCK.getEntityType(), CrumbelingBlockEntityRenderer::new);
	}
}
