package com.diamssword.greenresurgence.events;

import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlaceBlockCallback {
    Event<PlaceBlockCallback> EVENT = EventFactory.createArrayBacked(PlaceBlockCallback.class,
            (listeners) -> (player, base) -> {
                for (PlaceBlockCallback listener : listeners) {
                   var result=listener.place(player, base);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });
    ActionResult place(ItemPlacementContext context, BlockState state);
}
