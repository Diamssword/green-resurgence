package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class GuiPackets {
    public static enum KEY{
        Inventory
    }
    public static enum GUI{
        ItemBlock
    }
    public record GuiPacket(GUI gui, @Nullable  BlockPos pos){};
    public record GuiTileValue(BlockPos pos,String key,double value){};
    public record KeyPress(KEY key){};
    public static void init()
    {
        Channels.MAIN.registerClientboundDeferred(GuiPacket.class);
        Channels.MAIN.registerServerbound(GuiTileValue.class,(msg,ctx)->{
            BlockEntity te=ctx.player().getWorld().getBlockEntity(msg.pos);
            if(te != null)
            {
                if(te instanceof ItemBlockEntity ib)
                {
                    var pos=ib.getPosition();
                    var rot=ib.getRotation();
                    switch (msg.key)
                    {
                        case "posX"->ib.setPosition(new Vec3d(msg.value,pos.y,pos.z));
                        case "posY"->ib.setPosition(new Vec3d(pos.x,msg.value,pos.z));
                        case "posZ"->ib.setPosition(new Vec3d(pos.x,pos.y,msg.value));
                        case "rotX"->ib.setRotation(new Vec3d(msg.value,rot.y,rot.z));
                        case "rotY"->ib.setRotation(new Vec3d(rot.x,msg.value,rot.z));
                        case "rotZ"->ib.setRotation(new Vec3d(rot.x,rot.y,msg.value));
                        case "size"->ib.setSize(msg.value);
                        case "collision"->{
                        BlockState st=ctx.player().getWorld().getBlockState(msg.pos);
                        ctx.player().getWorld().setBlockState(msg.pos,st.with(ItemBlock.COLLISION,msg.value==1));
                        }
                    }

                }
            }
        });
        Channels.MAIN.registerServerbound(KeyPress.class,(msg,ctx)->{
            switch (msg.key)
            {

                case Inventory -> {
                 /*   NamedScreenHandlerFactory screen=new NamedScreenHandlerFactory() {
                        @Nullable
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                            return new MutliInvScreenHandler(syncId,playerInventory,new GridContainer("container",3,3),new GridContainer("bag",8,1));
                        }

                        @Override
                        public Text getDisplayName() {
                            return Text.of("Inventaire");
                        }
                    };
                    ctx.player().openHandledScreen(screen);
                  */
                }
            }
        });
    }
    public static void send(PlayerEntity entity,GUI gui)
    {
        Channels.MAIN.serverHandle(entity).send(new GuiPacket(gui,entity.getBlockPos()));
    }
    public static void send(PlayerEntity entity,GUI gui,BlockPos b)
    {
        Channels.MAIN.serverHandle(entity).send(new GuiPacket(gui,b));
    }
}
