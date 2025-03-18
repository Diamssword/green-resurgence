package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.IAdvancedLootableBlock;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

import java.util.HashMap;
import java.util.Map;

public class EntitiesPackets {
    public record OnWallCollided(double velocity){};
    public record AllowedList(Identifier[] blocks, Identifier[] items){};
    public static void init()
    {

        Channels.MAIN.registerServerbound(OnWallCollided.class,(msg,ctx)->{
            var mount=ctx.player().getControllingVehicle();
            if(mount !=null)
            {
                mount.getPassengerList().forEach(et->{
                    et.dismountVehicle();
                    et.damage(et.getDamageSources().flyIntoWall(), (float) (msg.velocity*5f));
                });
            }

        });
    }
}
