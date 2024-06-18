package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.gui.CharacterCustomizationScreen;
import com.diamssword.greenresurgence.gui.ImageBlockGui;
import com.diamssword.greenresurgence.gui.ItemBlockGui;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;

public class ClientGuiPacket {
    public static void init()
    {

        Channels.MAIN.registerClientbound(GuiPackets.GuiPacket.class,(message, access) -> {
            switch(message.gui())
            {

                case ImageBlock -> {
                   openGui(new ImageBlockGui(getTile(ImageBlockEntity.class,message.pos())));
                }
                case Customizer -> {
                    openGui(new CharacterCustomizationScreen(CharacterCustomizationScreen.Type.size));
                }
            }
        });
    }
    public static <T extends BlockEntity> T getTile(Class<T> clazz,BlockPos pos)
    {
        BlockEntity te= MinecraftClient.getInstance().world.getBlockEntity(pos);
        if(clazz.isInstance(te))
            return (T) te;
        return null;
    }
    private static void openGui(Screen screen)
    {
        MinecraftClient.getInstance().setScreen(screen);
    }
}
