package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootableShelfEntity;
import com.diamssword.greenresurgence.blocks.ShelfBlock;
import com.diamssword.greenresurgence.genericBlocks.DoorLongBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class ShelfBlockEntityRenderer implements BlockEntityRenderer<LootableShelfEntity> {

    private final BlockRenderManager blockRenderManager;

    public ShelfBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

        this.blockRenderManager = ctx.getRenderManager();
    }
    @Override
    public void render(LootableShelfEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        ItemStack st=blockEntity.getItem();
    //    st=new ItemStack(Blocks.STONE);
        World world = blockEntity.getWorld();
        if(st.getItem() instanceof BlockItem)
        {
            var block=((BlockItem)st.getItem()).getBlock();

        matrices.push();
        var scale=0.8f;
        matrices.scale(scale,scale,scale);
        var off=(1f-scale)/2f;
        matrices.translate(off,off*2,off);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(getBlockState(block,st,blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING)),blockEntity.getPos(),world,matrices,vertexConsumers.getBuffer(RenderLayer.getTranslucent()),false,world.random);



        matrices.pop();
        }
        else
        {
            matrices.push();
            int seed=blockEntity.getPos().getX()+blockEntity.getPos().getY()+blockEntity.getPos().getZ();
            var scale=0.5f;
            matrices.translate(0.5f,0.45f,0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING).asRotation()));
            matrices.scale(scale,scale,scale);
            MinecraftClient.getInstance().getItemRenderer().renderItem(st, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world,seed);
            if(st.getCount()>1)
            {
                for (int i = 0; i < Math.min(st.getCount(),6); i++) {
                    matrices.translate(-0.2f+(i*0.1f),0,0.001f+(i*0.001f));
                    MinecraftClient.getInstance().getItemRenderer().renderItem(st, ModelTransformationMode.FIXED,light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world,seed);
                }
            }
            matrices.pop();
        }
    }
    private BlockState getBlockState(Block block, ItemStack stackinfos, Direction facing)
    {
        var st=block.getDefaultState();
        if(st.getProperties().contains(Properties.FACING))
        {
            st=st.with(Properties.FACING,facing);
        }
        else if(st.getProperties().contains(Properties.AXIS))
        {
            st=st.with(Properties.AXIS,facing.getAxis());
        }
        else if(st.getProperties().contains(Properties.HORIZONTAL_FACING))
        {
            st=st.with(Properties.HORIZONTAL_FACING,facing);
        }
        if(stackinfos.getCount() >1)
        {
            for (var p:st.getProperties()) {
               if(p instanceof IntProperty)
               {
                   IntProperty p1= (IntProperty) p;
                   var l=p1.getValues().size();
                   var min=p1.getValues().stream().findFirst();
                   if(stackinfos.getCount()>=min.get())
                   {
                       var c=Math.min(stackinfos.getCount(),min.get()+l-1);
                       st=st.with(p1,c);
                   }
                   break;
               }
            }
        }
       return st;
    }


}