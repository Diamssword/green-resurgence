package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntityRenderer.LootedBlockEntityRenderer;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.structure.StructurePlacerInstance;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class GreenResurgenceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (StructurePlacerInstance structuresPlacer : MItems.STRUCTURES_PLACERS) {
			if(structuresPlacer.block!=null)
				BlockRenderLayerMap.INSTANCE.putBlock(structuresPlacer.block, RenderLayer.getCutout());
		}
		GenericBlocks.sets.forEach(set->{
			for (Block glass : set.getGlasses()) {
				BlockRenderLayerMap.INSTANCE.putBlock(glass, RenderLayer.getTranslucent());
			}
		});
		BlockEntityRendererFactories.register(MBlocks.LOOTED_BLOCK_ENTITY, LootedBlockEntityRenderer::new);
		ClientNetwork.initialize();
		ClientEvents.initialize();

	}

}
