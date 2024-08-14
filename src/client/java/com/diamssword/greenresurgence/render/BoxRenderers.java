package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.items.IStructureProvider;
import com.diamssword.greenresurgence.structure.StructureInfos;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class BoxRenderers {
    public static void drawAdventureOutline(BlockPos pos, net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx)
    {
        BlockState st=ctx.world().getBlockState(pos);
        VertexConsumerProvider.Immediate store= MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        MatrixStack matrix=ctx.matrixStack();
        matrix.push();
        VoxelShape shape=st.getOutlineShape(ctx.world(),pos, ShapeContext.of(ctx.gameRenderer().getClient().player));
        Vec3d camPos=ctx.camera().getPos();
        matrix.translate(-camPos.x,-camPos.y,-camPos.z);
        if(shape!=null && !shape.isEmpty()) {
            shape = shape.offset(pos.getX(), pos.getY(), pos.getZ());
            Box box = shape.getBoundingBox().expand(0.005);
            long ticks=MinecraftClient.getInstance().world.getTime();
            float tot=(float) (Math.sin(2 * Math.PI * ticks / 40) * (0.7f - -0f) / 2 + (0.7f + -0f) / 2); // Math.min(0.5f,(ticks % 20) / 20f);
            WorldRenderer.drawBox(matrix, store.getBuffer(RenderLayer.LINES), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1, 1, 1, 0.3f+tot);
        }
        matrix.pop();
    }
    public static void drawStructureBox(MatrixStack matrix,Vec3d pos, Vec3d size,float r,float g,float b,float a)
    {
        MinecraftClient mc=MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate store= MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        matrix.push();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        if(camera.isReady()) {

            Vec3d cam=camera.getPos().negate();
            //matrix.translate(cam.x,cam.y,cam.z);
            Box box = new Box(pos.x, pos.y, pos.z, pos.x + size.x, pos.y + size.y, pos.z + size.z).expand(0.005);
            box=box.offset(camera.getPos().negate());
            WorldRenderer.drawBox(matrix, store.getBuffer(RenderLayer.LINES), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);

            matrix.pop();
        }
    }
    public static void drawStructureItemOverlay(MatrixStack matrix)
    {
        MinecraftClient mc=MinecraftClient.getInstance();
        ItemStack st=mc.player.getMainHandStack();
        if(st !=null && st.getItem() instanceof IStructureProvider) {
            IStructureProvider provider = (IStructureProvider) st.getItem();
            drawStructureOverlay(matrix,provider,st);
        }
        ItemStack st1=mc.player.getStackInHand(Hand.OFF_HAND);
        if(st1 !=null && st1.getItem() instanceof IStructureProvider) {
            IStructureProvider provider = (IStructureProvider) st1.getItem();
            drawStructureOverlay(matrix,provider,st1);
        }
    }
    public static void drawStructureOverlay(MatrixStack matrix,IStructureProvider provider,ItemStack st)
    {
        MinecraftClient mc=MinecraftClient.getInstance();
            Direction d=provider.getDirection(st,mc.world);
            Identifier name =provider.getStructureName(st,mc.world);
            BlockPos pos=provider.getPosition(st,mc.world);
            IStructureProvider.StructureType jigsaw=provider.strutctureType(st,mc.world);
            if(pos!=null && name!=null && d!=null)
            {

                StructureInfos.StructureInfo inf=StructureInfos.getInfos(name,d,jigsaw);
                BlockPos pos1=pos;
                int i,j,k;

                if(jigsaw== IStructureProvider.StructureType.jigsaw) {
                    i=j=k=1;
                    switch (d) {
                        case NORTH -> {
                            pos1 = pos.add(-inf.offset().getX() - (inf.size().getX()), -inf.offset().getY(), -inf.offset().getZ() - (inf.size().getZ()));
                        }
                        case SOUTH -> {
                            pos1 = pos.add(-inf.offset().getX(), -inf.offset().getY(), -inf.offset().getZ());
                        }
                        case WEST -> {
                            pos1 = pos.add(-inf.offset().getX() - inf.size().getX(), -inf.offset().getY(), -inf.offset().getZ());
                        }
                        case EAST -> {
                            pos1 = pos.add(-inf.offset().getX(), -inf.offset().getY(), -inf.offset().getZ() - inf.size().getZ());
                        }
                    }
                }
                else
                {
                    i=j=k=0;
                    switch (d) {
                        case NORTH -> {
                            pos1 = pos.add(-inf.offset().getX(), inf.offset().getY(), -inf.offset().getZ() );
                        }
                        case SOUTH -> {
                            pos1 = pos.add(inf.offset().getX(), inf.offset().getY(), inf.offset().getZ() );
                        }
                        case WEST -> {
                            pos1 = pos.add(-inf.offset().getX(), inf.offset().getY(), inf.offset().getZ() );
                        }
                        case EAST -> {
                            pos1 = pos.add(inf.offset().getX(), inf.offset().getY(), -inf.offset().getZ());
                        }
                    }
                }

                drawStructureBox(matrix,new Vec3d(pos.getX(),pos.getY()+1,pos.getZ()),new Vec3d(1,1,1),0.5f,1f,1,1);
                drawStructureBox(matrix,new Vec3d(pos1.getX(),pos1.getY()+1,pos1.getZ()),new Vec3d(inf.size().getX()+i,inf.size().getY()+j,inf.size().getZ()+k),1,0.5f,1,1);
            }
    }

    public static void drawBaseOverlays(MatrixStack matrix)
    {
        MinecraftClient mc=MinecraftClient.getInstance();

        if(mc.world!=null && mc.getEntityRenderDispatcher().shouldRenderHitboxes()) {
            List<Triple<String, String, BlockBox>> ls = mc.world.getComponent(Components.BASE_LIST).getBoxesForClient();
            ls.forEach(b -> {
                VertexConsumerProvider.Immediate store= MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                VertexConsumerProvider.Immediate store1= MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();

                int hash= b.getLeft().hashCode();
                int c1=(hash)&0xFF;
                int c2= (hash>>8)&0xFF;
                int c3=(hash>>16)&0xFF;
                float red=Math.min(Math.max(c1%255f,0f),255f);
                float green=Math.min(Math.max(c2%255f,0f),255f);
                float blue=Math.min(Math.max(c3%255f,0f),255f);
                DebugRenderer.drawBox(matrix,store,new BlockPos(b.getRight().getMinX(), b.getRight().getMinY(), b.getRight().getMinZ()),new BlockPos(b.getRight().getMaxX()+1, b.getRight().getMaxY()+1, b.getRight().getMaxZ()+1),red,green,blue,0.2f);
                BlockPos p1=b.getRight().getCenter();
                DebugRenderer.drawString(matrix,store1,"Camp: "+b.getLeft(),p1.getX(),p1.getY(),p1.getZ(),0xffffff,0.1f,true,0,true);
                DebugRenderer.drawString(matrix,store1,"Sub: "+b.getMiddle(),p1.getX(),p1.getY()-1,p1.getZ(),0xffffff,0.1f,true,0,true);
                drawStructureBox(matrix, new Vec3d(b.getRight().getMinX(), b.getRight().getMinY(), b.getRight().getMinZ()), Vec3d.of(b.getRight().getDimensions().add(1,1,1)), red, green, blue, 1);
            });
        }


    }
}
