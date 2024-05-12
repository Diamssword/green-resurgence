package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
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
        ImageBlock
    }
    public record GuiPacket(GUI gui, @Nullable  BlockPos pos){};
    public record GuiTileValue(BlockPos pos,String key,String value) {
        public GuiTileValue(BlockPos pos,String key,float value)
        {
            this(pos,key,value+"");
        }
        public GuiTileValue(BlockPos pos,String key,int value)
        {
            this(pos,key,value+"");
        }
        public  GuiTileValue(BlockPos pos,String key,double value)
        {
            this(pos,key,value+"");
        }
        public double asDouble()
        {
            try{
               return Double.parseDouble(this.value);
            }catch (NumberFormatException ignored){}
            return 0;
        }
        public int asInt()
        {
            try{
                return Integer.parseInt(this.value);
            }catch (NumberFormatException ignored){}
            return 0;
        }
        public float asFloat()
        {
            try{
                return Float.parseFloat(this.value);
            }catch (NumberFormatException ignored){}
            return 0;
        }
    }
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
                    ib.receiveGuiPacket(msg);
                }
                else  if(te instanceof ImageBlockEntity ib)
                {
                    ib.receiveGuiPacket(msg);
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
