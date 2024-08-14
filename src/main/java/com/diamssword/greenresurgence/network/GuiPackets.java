package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.MovementManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GuiPackets {
    public static enum KEY{
        Inventory,
    }
    public static enum GUI{
        ImageBlock,
        Wardrobe,
        Customizer
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
            if(te != null && ctx.player().isCreative())
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
                   // MovementManager.toggleCrawl(ctx.player());
                    var ls=ctx.player().getWorld().getComponent(Components.BASE_LIST);
                    var l1=ls.getAt(ctx.player().getBlockPos());
                    l1.ifPresent(v->{
                        var terr=l1.get().getSubTerrainAt(ctx.player().getBlockPos());
                        NamedScreenHandlerFactory screen=new NamedScreenHandlerFactory() {
                            @Nullable
                            @Override
                            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                                return  terr.get().storage.createMenu(syncId,playerInventory,player);
                            }

                            @Override
                            public Text getDisplayName() {
                                return terr.get().storage.getDisplayName();
                            }
                        };
                        ctx.player().openHandledScreen(screen);

                    });


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
