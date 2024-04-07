package com.diamssword.greenresurgence.structure;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blocks.StructureBlock;
import com.diamssword.greenresurgence.items.StructurePlacerItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MultiblockInstance {

    public final StructureBlock block;
    public final StructurePlacerItem placer;

    public final String name;
    public MultiblockInstance(String name, @Nullable Identifier structurePath, boolean centered)
    {
        block = new StructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque());
        placer= new StructurePlacerItem(new OwoItemSettings().group(MItems.GROUP).tab(1), structurePath!=null?structurePath:new Identifier(GreenResurgence.ID+":placer/"+name),centered);
        this.name=name;
    }
}
