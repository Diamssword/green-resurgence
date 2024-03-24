package com.diamssword.greenresurgence.structure;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blocks.StructureBlock;
import com.diamssword.greenresurgence.items.StructureCreatorItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class StructurePlacerInstance {

    public final StructureBlock block;
    public final StructureCreatorItem placer;

    public final String name;
    public StructurePlacerInstance(String name, @Nullable Identifier structurePath, boolean centered, boolean isJigsaw, boolean needBlock)
    {
        block =needBlock? new StructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque()):null;
        placer= new StructureCreatorItem(new OwoItemSettings().group(MItems.GROUP), structurePath!=null?structurePath:new Identifier(GreenResurgence.ID+":placer/"+name),centered,isJigsaw);
        this.name=name;
    }
}
