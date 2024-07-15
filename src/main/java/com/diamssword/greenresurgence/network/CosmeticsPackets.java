package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CosmeticsPackets {
    public record EquipCloth( String clothID, @Nullable String layerID){};
    public record EquipOutfit(int index){};
    public record SaveOutfit(String name,int index){};
    public static void init()
    {
        Channels.MAIN.registerServerbound(EquipCloth.class,(msg, ctx)->{
            if(!msg.clothID.equals("null"))
            {
                var c=ClothingLoader.instance.getCloth(msg.clothID);
                if(ctx.player().isCreative())
                    c.ifPresent(cloth -> ctx.player().getComponent(Components.PLAYER_DATA).appearance.setCloth(cloth.layer(), cloth));
                else
                    c.ifPresent(cloth -> ctx.player().getComponent(Components.PLAYER_DATA).appearance.equipCloth(cloth.layer(), cloth));
            }
            else if(msg.layerID !=null)
            {
                try {
                    ctx.player().getComponent(Components.PLAYER_DATA).appearance.equipCloth(ClothingLoader.Layer.valueOf(msg.layerID), null);
                }catch (Exception e){

                }
            }

        });
        Channels.MAIN.registerServerbound(EquipOutfit.class,(msg, ctx)->{
            ctx.player().getComponent(Components.PLAYER_DATA).appearance.equipOutfit(msg.index);
        });
        Channels.MAIN.registerServerbound(SaveOutfit.class,(msg, ctx)->{
            ctx.player().getComponent(Components.PLAYER_DATA).appearance.saveOutfit(msg.name,msg.index);
        });
    }
}
