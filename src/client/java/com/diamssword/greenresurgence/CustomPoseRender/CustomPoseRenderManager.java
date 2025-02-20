package com.diamssword.greenresurgence.CustomPoseRender;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import java.util.HashMap;
import java.util.Map;

public class CustomPoseRenderManager {
    private static Map<String, ICustomPoseRenderer> renderers=new HashMap<>();
    static {
        renderers.put(PosesManager.CARRIED,new CarriedRenderer());
        renderers.put(PosesManager.TWOHANDWIELD,new TwoHandWieldRenderer());
        renderers.put(PosesManager.CARRYINGENTITY,new CarryingPoseRenderer());
    }
    public static ICustomPoseRenderer get(String id)
    {
        return renderers.get(id);
    }
}
