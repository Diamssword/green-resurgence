package com.diamssword.greenresurgence;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface AttackBlockCallback {
 
    Event<AttackBlockCallback> EVENT = EventFactory.createArrayBacked(AttackBlockCallback.class,
        (listeners) -> (pos,direction) -> {
            for (AttackBlockCallback listener : listeners) {
                ActionResult result = listener.interact(pos, direction);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult interact(BlockPos pos, Direction direction);
}