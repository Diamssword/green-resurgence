package com.diamssword.greenresurgence.systems.crafting;

import com.google.gson.JsonObject;
import io.wispforest.owo.mixin.itemgroup.AbstractInventoryScreenMixin;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
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
import java.util.Optional;

public class UniversalResource{
    public enum Type{
        item(true,false),
        fluid(false,true),
        itemtag(true,false),
        fluidtag(false,true),
        time(false,false),
        energy(false,false);
        private Type( boolean isItem,boolean isFluid)
        {
            this.isItem=isItem;
            this.isFluid=isFluid;
        }
        public final boolean isFluid;
        public final boolean isItem;

    }
    private ItemStack[] itemCache=new ItemStack[]{ItemStack.EMPTY};
    private Fluid[] fluidCache=new Fluid[]{};
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
    public static UniversalResource fromItemOpti(ItemStack stack)
    {
        return new UniversalResource(stack);
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
    protected UniversalResource(ItemStack stack)
    {
        this.count=stack.getCount();
        this.data=stack.getNbt();
        this.item=new Identifier("empty");
        this.type=Type.item;
        this.itemCache=new ItemStack[]{stack};
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
        else if(type==Type.fluid)
        {
            fluidCache=new Fluid[]{Registries.FLUID.get(this.item)};
        }
        else if(type==Type.fluidtag)
        {

            var r= new ArrayList<Fluid>();
            Registries.FLUID.iterateEntries(TagKey.of(RegistryKeys.FLUID,item)).forEach(i->{
                i.getKey().ifPresent(v->{
                    r.add(Registries.FLUID.get(v.getValue()));
                });
            });
            fluidCache=r.toArray(new Fluid[0]);
        }
    }
    private ItemStack buildItemStack(Identifier id)
    {
        var i=new ItemStack(Registries.ITEM.get(id),this.count);
        if(this.data !=null)
            i.setNbt(this.data);
        return i;
    }
    public Fluid[] getAllFluids()
    {
        return this.fluidCache;
    }
    public ItemStack[] getAllStacks()
    {
        return this.itemCache;
    }
    public Identifier getID() {
        return item;
    }

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
        if(type==Type.fluidtag)
        {
            var r= new Identifier[fluidCache.length];
            for(int i=0;i<r.length;i++)
            {
                r[i]=Registries.FLUID.getId(fluidCache[i]);
            }
            return r;
        }
        return new Identifier[]{getID()};
    }

    /**
     * @return send back an ItemStrack or ItemStack.EMPTY if the resource is not available as an item. Always return the first item present
     */
    public ItemStack asItem() {
        if(type.isItem)
        {
            return itemCache[0].copy();
        }
        return ItemStack.EMPTY.copy();
    }
    public Optional<Fluid> asFluid() {
        if(type.isFluid)
        {
            return Optional.of(fluidCache[0]);
        }
        return Optional.empty();
    }

    /**
     *
     * @param time a moving float based on deltatick
     * @return a variant of the itemstack (used for use with tags for example)
     */
    public ItemStack getCurrentItem(float time) {
            ItemStack[] itemStacks = this.itemCache;
            return itemStacks.length == 0 ? ItemStack.EMPTY : itemStacks[MathHelper.floor(time / 30.0F) % itemStacks.length].copy();
    }
    /**
     *
     * @param time a moving float based on deltatick
     * @return a variant of the fluid
     */
    public Optional<Fluid> getCurrentFluid(float time) {
        Fluid[] itemStacks = this.fluidCache;
        return itemStacks.length == 0 ? Optional.empty() : Optional.of(itemStacks[MathHelper.floor(time / 30.0F) % itemStacks.length]);
    }
    public int getAmount() {
        return count;
    }
    public Text getName() {
        if(type.isItem)
            return asItem().getName();
        if(type.isFluid)
            return asFluid().get().getDefaultState().getBlockState().getBlock().getName();
        return Text.of("Energy");
    }
    public Text getName(float time) {
        if(type.isItem)
            return getCurrentItem(time).getName();
        if(type.isFluid)
        {
            var f=getCurrentFluid(time);
            if(f.isPresent())
                return f.get().getDefaultState().getBlockState().getBlock().getName();
            else
                return  Text.of("(Fluid Manquant)");
        }

        return Text.of("⚡ Energie");
    }

    public NbtCompound extra() {
        return this.data;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UniversalResource re)
        {
            if(re.type==this.type && re.item.equals(this.item) && re.count==this.count)
            {
                if(re.extra() == null && this.extra()==null)
                    return true;
                if(re.extra() !=null && this.extra()!=null)
                    return re.extra().equals(this.extra());
                return false;
            }
        }
        return false;
    }
    @Override
    public int hashCode(){
       return this.type.hashCode()+this.getID().hashCode()+this.count;
    }
    public static UniversalResource deserializer(JsonObject ob) throws Exception
    {

        var type=Type.item;
        var name="";
        if(ob.has("type"))
        {
            switch (ob.get("type").getAsString())
            {
                case "tag"->type=Type.itemtag;
                case "fluid"->type=Type.fluid;
                case "fluidtag"->type=Type.fluidtag;
                case "energy"->type=Type.energy;
                case "time"->type=Type.time;
            }
        }
        if((type.isFluid || type.isItem) && !ob.has("name"))
                throw new Exception("Ingredient is missing 'name' field");
        else if(ob.has("name"))
            name=ob.get("name").getAsString();
        else
            name=type.toString().toLowerCase();
        NbtCompound tag=null;
        int c=1;
        if(ob.has("count"))
            c=ob.get("count").getAsInt();
        if(ob.has("nbt"))
        {
            tag=StringNbtReader.parse(ob.get("nbt").getAsString());
        }
        return new UniversalResource(type,new Identifier(name),c,tag);
    }
    public NbtCompound toNBT()
    {
        var res=new NbtCompound();
        res.putString("name",item.toString());
        switch (type)
        {
            case item ->res.putString("type","item");
            case fluid ->res.putString("type","fluid");
            case itemtag ->res.putString("type","tag");
        }
        res.putInt("count",this.count);
        if(data !=null)
            res.put("nbt",data);
        return res;
    }
    public static UniversalResource fromNBT(NbtCompound tag)
    {
        var id=new Identifier(tag.getString("name"));
        var type=Type.item;
        switch (tag.getString("type"))
        {
            case "fluid" ->type=Type.fluid;
            case "tag" ->type=Type.itemtag;
            case "fluidtag"->type=Type.fluidtag;
            case "energy"->type=Type.energy;
            case "time"->type=Type.time;
        }
        var count=tag.getInt("count");
        NbtCompound extra=null;
        if(tag.contains("nbt"))
            extra=tag.getCompound("nbt");
        return new UniversalResource(type,id,count,extra);
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
            case fluidtag ->res.addProperty("type","fluidtag");
            case energy ->res.addProperty("type","energy");
            case time ->res.addProperty("type","time");
        }
        res.addProperty("count",this.count);
        if(data !=null)
            res.addProperty("nbt",data.toString());
        return res;
    }
}
