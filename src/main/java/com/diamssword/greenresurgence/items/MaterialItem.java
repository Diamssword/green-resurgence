package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaterialItem extends Item {
    public final int tier;
    public final String id;
    public final String material;
    private static Formatting[] tiersClrs=new Formatting[]{
            Formatting.GRAY,
            Formatting.DARK_GREEN,
            Formatting.BLUE,
            Formatting.GOLD,
            Formatting.DARK_RED
    };

    public MaterialItem(Settings settings,int tier,String id,String material) {
        super(settings);
        this.tier=tier;
        this.id=id;
        this.material=material;

    }
    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey(stack)).formatted(tiersClrs[Math.max(1,Math.min(tier,tiersClrs.length))-1]);
    }
    @Override
    public Text getName() {
        return Text.translatable(this.getTranslationKey()).formatted(tiersClrs[Math.max(1,Math.min(tier,tiersClrs.length))-1]);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        MutableText tierL=Text.translatable("materials.tier."+tier).formatted(Formatting.BOLD,tiersClrs[Math.max(1,Math.min(tier,tiersClrs.length))-1]);
       // tierL.append(" (").append(Text.translatable("desc."+ GreenResurgence.ID+".materials.tier."+tier+"."+material)).append(")");
        //tooltip.add(tierL);

        tooltip.add(Text.translatable("desc."+GreenResurgence.ID+".material_"+material+"_"+id).formatted(Formatting.ITALIC,Formatting.GRAY));
    }
}
