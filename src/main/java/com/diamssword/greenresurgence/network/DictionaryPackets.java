package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

import java.util.HashMap;
import java.util.Map;

public class DictionaryPackets {
    public record LootableList(LootablesReloader loader){};
    public record ClothingList(ClothingLoader loader){};
    public record RecipeList(RecipeLoader loader){};
    public static void init()
    {
        Channels.MAIN.registerClientbound(LootableList.class,(msg,ctx)->{
            Lootables.loader=msg.loader;
        });
        Channels.MAIN.registerClientbound(ClothingList.class,(msg,ctx)->{
            ClothingLoader.instance=msg.loader;
        });
        Channels.MAIN.registerClientbound(RecipeList.class,(msg,ctx)->{
            Recipes.loader=msg.loader;
        });

    }
}
