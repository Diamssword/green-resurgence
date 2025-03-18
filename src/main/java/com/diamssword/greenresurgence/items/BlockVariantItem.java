package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.containers.*;
import com.diamssword.greenresurgence.network.GuiPackets;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class BlockVariantItem extends Item {
    private List<Identifier> variants =new ArrayList<>();
    public BlockVariantItem(Settings properties) {
        super(properties);
    }
    public void addVariant(Identifier id)
    {
        variants.add(id);
    }
    public List<Identifier> getVariants()
    {
    return variants;
    }
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                tooltip.addAll(Text.of("Cliquez pour choisir une variante").getWithStyle(Style.EMPTY.withItalic(true)));
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!user.getWorld().isClient) {
            Containers.createHandler(user,null, (sync, inv, p1) -> new Container(sync, inv,true));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    public static class Container extends CreativeMultiInvScreenHandler {
        public Container(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public Container(int syncId, PlayerInventory player,boolean unused) {
            super(syncId, player, true);
        }

        @Override
        public ScreenHandlerType<BlockVariantItem.Container> type() {
            return Containers.BLOCK_VARIANT_INV;
        }
    }
}