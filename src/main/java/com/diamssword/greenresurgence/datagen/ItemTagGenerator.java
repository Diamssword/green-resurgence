package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.Weapons;
import com.diamssword.greenresurgence.materials.MaterialSet;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator  extends FabricTagProvider.ItemTagProvider {

    public static TagKey<Item> SHIELD= TagKey.of(RegistryKeys.ITEM,new Identifier("c","shields"));
    public static TagKey<Item> DURABILITY= TagKey.of(RegistryKeys.ITEM,new Identifier("enchantable/durability"));
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);

    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(Lootables.HAMMER).add(Weapons.SLEDGEHAMMER);
        getOrCreateTagBuilder(Lootables.WRENCH).add(Weapons.WRENCH);
        MaterialSet.sets.forEach((m,s)->{
            s.getItems().forEach(i->{
                getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, GreenResurgence.asRessource("materials/"+s.material+"/"+i.tier))).add(i);
            });
        });
        getOrCreateTagBuilder(DURABILITY).add(Weapons.TRASH_SHIELD);
        getOrCreateTagBuilder(SHIELD).add(Weapons.TRASH_SHIELD);
    }
}
