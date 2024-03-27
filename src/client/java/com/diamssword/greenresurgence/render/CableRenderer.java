/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.blocks.IDisplayOffset;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diamssword.greenresurgence.systems.CableNetwork.clientCables;

@Environment(value=EnvType.CLIENT)
public class CableRenderer {



    public static void render(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx)
    {
        PlayerEntity p=ctx.gameRenderer().getClient().player;
        List<Pair<BlockPos,BlockPos>> toRemove=new ArrayList<>();
        clientCables.forEach((k)->{
            if(!k.getRight().isWithinDistance(p.getPos(),1000) && !k.getLeft().isWithinDistance(p.getPos(),256))
            {
                toRemove.add(k);
            }
            else {
                Vec3d off1=Vec3d.ZERO;
                Vec3d off2=Vec3d.ZERO;
                float scale=1f;
                BlockState st1=ctx.world().getBlockState(k.getLeft());
                BlockState st2=ctx.world().getBlockState(k.getRight());
                if(st1.getBlock() == Blocks.AIR && st2.getBlock() == Blocks.AIR)
                    toRemove.add(k);
                if(st1.getBlock() instanceof IDisplayOffset)
                {
                    off1=((IDisplayOffset) st1.getBlock()).getOffset(st1,ctx.world());
                    scale=((IDisplayOffset) st1.getBlock()).getScale(st1,ctx.world());
                }
                if(st2.getBlock() instanceof IDisplayOffset)
                {
                    off2=((IDisplayOffset) st2.getBlock()).getOffset(st2,ctx.world());
                }
                renderLeashFrom(ctx, k.getLeft().toCenterPos().add(off1), k.getRight().toCenterPos().add(off2),scale);
            }
        });
        toRemove.forEach(clientCables::remove);
    }
    public static void renderLeashFrom(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx,Vec3d p1,Vec3d p2,float scale)
    {
        ctx.matrixStack().push();
        ctx.matrixStack().translate(-ctx.camera().getPos().x,-ctx.camera().getPos().y,-ctx.camera().getPos().z);
        renderLeash(ctx.world(),p1, ctx.tickDelta(), ctx.matrixStack(),ctx.consumers(),p2,scale);
        ctx.matrixStack().pop();
    }
    public  static  <E extends Entity> void renderLeash(World w, Vec3d to, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, Vec3d from,float scale) {
        int u;
        matrices.push();
        double d = (double)(MathHelper.lerp((float)tickDelta, 0, 0) * ((float)Math.PI / 180)) + 1.5707963267948966;
        matrices.translate(from.x, from.y, from.z);
        float j = (float)(to.x - from.x);
        float k = (float)(to.y - from.y);
        float l = (float)(to.z - from.z);
        float m = 0.025f;
        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float n = MathHelper.inverseSqrt((float)(j * j + l * l)) * 0.025f / 2.0f;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos =BlockPos.ofFloored(to.x,to.y,to.z);
        BlockPos blockPos2 = BlockPos.ofFloored(from.x,from.y,from.z);
        int q = 0;//w.getLightLevel(LightType.SKY,blockPos);//.getBlockLight(entity, blockPos);
        int r =0;//w.getLightLevel(blockPos2);// this.dispatcher.getRenderer(holdingEntity).getBlockLight(holdingEntity, blockPos2);
        int s =  w.getLightLevel(LightType.SKY, blockPos);
        int t = w.getLightLevel(LightType.SKY, blockPos2);
        float sc=0.125f*scale;
        for (u = 0; u <= 48; ++u) {
            renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, sc, sc, o, p, u, false);
        }
        for (u = 48; u >= 0; --u) {
            renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, sc, 0.0f, o, p, u, true);
        }
        matrices.pop();
    }
    private static void renderLeashPiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot) {
        float m = (float)pieceIndex / 48.0f;
        int n = (int)MathHelper.lerp((float)m, (float)leashedEntityBlockLight, (float)holdingEntityBlockLight);
        int o = (int)MathHelper.lerp((float)m, (float)leashedEntitySkyLight, (float)holdingEntitySkyLight);
        int p = LightmapTextureManager.pack(n, o);
        float q = pieceIndex % 2 == (isLeashKnot ? 1 : 0) ? 1f : 2.0f;
        float r = 0.1f * q;
        float s = 0.1f * q;
        float t = 0.1f * q;
        float u = f * m;
        float v = g > 0.0f ? g * m * m : g - g * (1.0f - m) * (1.0f - m);
        float w = h * m;
        vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(r, s, t, 1.0f).light(p).next();
        vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(r, s, t, 1.0f).light(p).next();
    }
}

