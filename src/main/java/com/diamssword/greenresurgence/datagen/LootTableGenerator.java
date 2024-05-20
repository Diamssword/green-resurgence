package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.lootables.TableHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class LootTableGenerator extends SimpleFabricLootTableProvider {


    public LootTableGenerator(FabricDataOutput output) {
        super(output, LootContextTypes.ADVANCEMENT_LOCATION);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        TableHelper.generate(exporter);
        /*exporter.accept(PLANKS, LootTable.builder()
                .pool(
                        LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(Items.DIAMOND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        .with(ItemEntry.builder(Items.NETHER_STAR).conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTagGenerator.looting_tool_2))).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                )
                        //.with(ItemEntry.builder(Items.DIAMOND_SWORD)).apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F))))
                );

         */
    }
}
