package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blocks.DeployableLadderBlock;
import net.minecraft.block.Block;

import java.util.HashMap;
import java.util.Map;

public class AdventureBlockHighlight {

    public static Map<Block,BlockHighlightContext> blocks = new HashMap<>();
    static {
        blocks.put(MBlocks.DEPLOYABLE_LADDER,(a,b,c)-> a.get(DeployableLadderBlock.MASTER));
    }



}
