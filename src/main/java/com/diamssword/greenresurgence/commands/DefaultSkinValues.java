package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.http.APIService;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class DefaultSkinValues {
    public String hair;
    public String hairColor;
    public String beard;
    public String beardColor;
    public String underwear;
    public int height;
    public boolean slim;

    public NbtCompound toNBT()
    {
        var tag=new NbtCompound();
        if(hair!=null && !hair.isEmpty()) {
            tag.putString("hairColor", hairColor);
            tag.putString("hair", hair);
        }
        if(beard!=null && !beard.isEmpty()) {
            tag.putString("beard", beard);
            tag.putString("beardColor", beardColor);
        }
        if(underwear!=null && !underwear.isEmpty())
            tag.putString("underwear",underwear);
        tag.putInt("height",height);
        tag.putBoolean("skinny",slim);
        return tag;
    }
    public DefaultSkinValues fromNBT(NbtCompound tag)
    {
        hairColor=tag.getString("hairColor");
        hair=tag.getString("hair");
        if(tag.contains("beard")) {
            beard = tag.getString("beard");
            beardColor = tag.getString("beardColor");
        }
        underwear=tag.getString("underwear");
        height=Math.min(99,Math.max(50,tag.getInt("height")));
        slim=tag.getBoolean("skinny");
        return this;
    }
    public int getHairColor()
    {
        if(hairColor ==null)
        {
            hairColor="5D3A1AFF";
        }
        return Integer.parseInt(hairColor,16);
    }
    public int getBeardColor()
    {
        return Integer.parseInt(beardColor,16);
    }

    public static CompletableFuture<DefaultSkinValues> getSkinValues(GameProfile profile)
    {
        var partUrl= APIService.url+"/files/skin/"+profile.getId().toString().replaceAll("-","");
       return APIService.getRequest(partUrl+".json","").thenApply(d->{
            if(d.statusCode()==200) {
                var b=d.body();

                return new Gson().fromJson(b,DefaultSkinValues.class);
            }
            return  null;
        });
    }

    /**
     *
     * @param tailleM la partie Metre de la taille (en general 1M)
     * @param tailleCM la partie centimetre de la taille (par defaut 80)
     * @return le scale Y du joueur (entre 0 et 1 basé sur la taille standard d'1.8)
     */
    public static float HeightMToMCScale(int tailleM,int tailleCM)
    {
        float v=tailleM+(tailleCM/100f);
        return v/1.80f;
    }
    /**
     *
     * @param model le type de model
     * @return le scale X du joueur (entre 0 et 1 basé le model)
     */
    public static float ModelToWidthScale(String model)
    {
        var r=1f;
            r = switch (model) {
                case "maigre" -> 0.9f;
                case "muscle" -> 1.1f;
                case "large" -> 1.2f;
                default -> r;
            };
        return r;
    }
}
