package com.diamssword.greenresurgence.systems.crafting;

import com.google.gson.JsonObject;
import io.wispforest.owo.mixin.itemgroup.AbstractInventoryScreenMixin;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UniversalResource implements IResource{
    public enum Type{
        item,
        fluid,
        itemtag
    }
    private ItemStack[] itemCache=new ItemStack[]{ItemStack.EMPTY};
    private final int count;
    private final NbtCompound data;
    private final Identifier item;
    private final Type type;
    public static UniversalResource fromItemTag(TagKey<Item> key,int count)
    {
        return new UniversalResource(Type.itemtag,key.id(),count,null);
    }
    public static UniversalResource fromItemTag(TagKey<Item> key,int count,NbtCompound tag)
    {
        return new UniversalResource(Type.itemtag,key.id(),count,tag);
    }
    public static UniversalResource fromItem(ItemStack stack)
    {
        return new UniversalResource(Type.item,Registries.ITEM.getId(stack.getItem()),stack.getCount(),null);
    }
    public static UniversalResource fromItem(Identifier itemId)
    {
        return new UniversalResource(Type.item,itemId,1,null);
    }
    public static UniversalResource fromItem(ItemStack stack,NbtCompound tag)
    {
        return new UniversalResource(Type.item,Registries.ITEM.getId(stack.getItem()),stack.getCount(),tag);
    }
    protected UniversalResource(Type type, Identifier id, int count, @Nullable NbtCompound tag)
    {
        this.count=count;
        this.data=tag;
        this.item=id;
        this.type=type;
        fillCache();
    }
    public Type getType()
    {
        return type;
    }
    private void fillCache()
    {
        if(this.type==Type.item)
        {
            itemCache=new ItemStack[]{buildItemStack(this.item)};
        }
        else if(type==Type.itemtag)
        {
            var r= new ArrayList<ItemStack>();
            Registries.ITEM.iterateEntries(TagKey.of(RegistryKeys.ITEM,item)).forEach(i->{
                i.getKey().ifPresent(v->{
                    r.add(buildItemStack(v.getValue()));
                });
            });
            if(r.isEmpty())
            {
                Registries.BLOCK.iterateEntries(TagKey.of(RegistryKeys.BLOCK,item)).forEach(i->{
                    i.getKey().ifPresent(v->{
                        r.add(buildItemStack(v.getValue()));
                    });
                });
            }
            itemCache=r.toArray(new ItemStack[0]);
        }
    }
    private ItemStack buildItemStack(Identifier id)
    {
        var i=new ItemStack(Registries.ITEM.get(id),this.count);
        if(this.data !=null)
            i.setNbt(this.data);
        return i;
    }
    @Override
    public Identifier getID() {
        return item;
    }

    @Override
    public Identifier[] getAllPossibleIds() {
        if(type==Type.itemtag)
        {
            var r= new Identifier[itemCache.length];
            for(int i=0;i<r.length;i++)
            {
                r[i]=Registries.ITEM.getId(itemCache[i].getItem());
            }
            return r;
        }
        return new Identifier[]{getID()};
    }

    /**
     * @return send back an ItemStrack or ItemStack.EMPTY if the resource is not available as an item. Always return the first item present
     */
    public ItemStack asItem() {
        if(type==Type.item || type==Type.itemtag)
        {
            return itemCache[0];
        }
        return ItemStack.EMPTY;
    }

    /**
     *
     * @param time a moving float based on deltatick
     * @return a variant of the itemstack (used for use with tags for example)
     */
    public ItemStack getCurrentItem(float time) {
            ItemStack[] itemStacks = this.itemCache;
            return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[MathHelper.floor(time / 30.0F) % itemStacks.length];
    }

    @Override
    public int getAmount() {
        return count;
    }
    @Override
    public Text getName() {
        return asItem().getName();
    }

    @Override
    public NbtCompound extra() {
        return this.data;
    }
    public static UniversalResource deserializer(JsonObject ob) throws Exception
    {
        if(!ob.has("name"))
            throw new Exception("Ingredient is missing 'name' field");
        var type=Type.item;
        if(ob.has("type"))
        {
            var t=ob.get("type").getAsString();
            if(t.equals("tag"))
            {
                type=Type.itemtag;
            }
            else if(t.equals("fluid"))
            {
                type=Type.fluid;
            }
        }
        NbtCompound tag=null;
        int c=1;
        if(ob.has("count"))
            c=ob.get("count").getAsInt();
        if(ob.has("nbt"))
        {
            tag=StringNbtReader.parse(ob.get("nbt").getAsString());
        }
        return new UniversalResource(type,new Identifier(ob.get("name").getAsString()),c,tag);
    }
    public JsonObject serializer()
    {
        var res=new JsonObject();
        res.addProperty("name",item.toString());
        switch (type)
        {
            case item ->res.addProperty("type","item");
            case fluid ->res.addProperty("type","fluid");
            case itemtag ->res.addProperty("type","tag");
        }
        res.addProperty("count",this.count);
        if(data !=null)
            res.addProperty("nbt",data.toString());
        return res;
    }
}
