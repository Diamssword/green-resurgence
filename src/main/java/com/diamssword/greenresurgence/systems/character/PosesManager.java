package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.character.customPoses.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PosesManager {

    public static final String CARRIED = "carried";
    public static final String TWOHANDWIELD = "two_hand_wield";
    public static final String KNUCLESHANDWIELD = "knuckles_hand_wield";
    public static final String CARRYINGENTITY = "carrying_entity";
    public static final String PUSHINGCART = "pushing_cart";
    private static final Map<String, Function<PlayerEntity, IPlayerCustomPose>> posesRegister = new HashMap<>();

    static {
        posesRegister.put(CARRIED, BeingCarriedPose::new);
        posesRegister.put(TWOHANDWIELD, TwoHandWield::new);
        posesRegister.put(KNUCLESHANDWIELD, KnucklesHandWield::new);
        posesRegister.put(CARRYINGENTITY, CarryingPose::new);
        posesRegister.put(PUSHINGCART, PushingCartPose::new);
    }

    public static IPlayerCustomPose createPose(String id, PlayerEntity player) {
        if (posesRegister.containsKey(id)) {
            return posesRegister.get(id).apply(player);
        }
        return null;
    }
}
