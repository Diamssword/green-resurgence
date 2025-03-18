package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimedCraftingProvider extends CraftingProvider{
    List<PendingCraft> pendingCrafts = new ArrayList<>();
    PendingCraft currentCraft;
    int totalTime;
    int elapsedTime;
    private  int updateNeeded=0;
    private Runnable onNewRecipeListener;
    public TimedCraftingProvider()
    {

    }
    public TimedCraftingProvider(NbtCompound tag)
    {
        if(tag.contains("pending"))
        {
            var ls=tag.getList("pending", NbtElement.COMPOUND_TYPE);
            ls.forEach(v->{
                if(v instanceof NbtCompound comp)
                {
                    if(comp.contains("recipe"))
                    {
                        var re=Recipes.getRecipe(new Identifier(comp.getString("recipe")));
                        if(re.isPresent())
                        {
                            var d = new PendingCraft(re.get());
                            if(comp.contains("stacks"))
                            {
                                d.stacks.readNbtList(comp.getList("stacks",NbtElement.COMPOUND_TYPE));
                            }
                            pendingCrafts.add(d);
                        }

                    }

                }
            });
        }
        if(tag.contains("time"))
            elapsedTime=tag.getInt("time");


    }

    public NbtCompound toNBT()
    {
        var ls=new ArrayList<PendingCraft>();
        if(currentCraft !=null)
            ls.add(currentCraft);
        ls.addAll(pendingCrafts);
        var nbt= new NbtCompound();
        var nbl= new NbtList();
        ls.forEach(c->{
            var nbt1= new NbtCompound();
            nbt1.putString("recipe",c.recipe.getId().toString());
            nbt1.put("stacks",c.stacks.toNbtList());
            nbl.add(nbt1);
        });
        nbt.put("pending",nbl);
        nbt.putInt("time",elapsedTime);
        return nbt;
    }
    public final CraftingProvider setForPlayer(CustomPlayerInventory player)
    {

        var ls=player.getAllInventories().stream().map(i->InventoryStorage.of(i,null)).toList();
            setInventories(ls.toArray(new InventoryStorage[0]));
        return this;
    }
    public void clearPendings()
    {
        this.pendingCrafts.clear();
        this.currentCraft=null;
        updateNeeded++;
    }
    public void addPending(PendingCraft craft)
    {
        this.pendingCrafts.add(craft);
       updateNeeded++;
    }
    public  List<PendingCraft> getPendingCrafts()
    {
        var ls= new ArrayList<PendingCraft>();
        if(currentCraft !=null)
        {
            ls.add(currentCraft);
        }
        ls.addAll(pendingCrafts);
        return ls;
    }
    public void onNewRecipeQueued(Runnable consumer)
    {
        onNewRecipeListener=consumer;
    }
    public void tick(PlayerEntity player)
    {
        if(currentCraft==null && !pendingCrafts.isEmpty()) {
            currentCraft = pendingCrafts.remove(0);
            updateNeeded++;
            totalTime=getCraftingTime(currentCraft.recipe,player);
            elapsedTime=0;
        }
        else if(currentCraft !=null)
        {
            if(elapsedTime >=totalTime)
            {
                var res=currentCraft.recipe.result(player);
                if(res.getType().isItem)
                {
                    if(!player.giveItemStack(res.asItem()))
                        player.dropStack(res.asItem());
                }
                updateNeeded++;
                currentCraft=null;
            }

            elapsedTime++;
        }
        if(updateNeeded>0)
        {
            updateNeeded=0;
            if(onNewRecipeListener !=null)
                onNewRecipeListener.run();
        }

    }
    public static int getCraftingTime(SimpleRecipe recipe,@Nullable PlayerEntity player)
    {
        var time=0;
        var ingredients=recipe.ingredients(player);
        for (var ing:ingredients) {
            if(ing.getType()== UniversalResource.Type.time)
                time+=ing.getAmount();

        }
        return time;
    }
    @Override
    public boolean craftRecipe(SimpleRecipe recipe,PlayerEntity player)
    {

        var pending = new PendingCraft(recipe);
        var stackCopy=pending.getAsStorage();
        var ingrs=recipe.ingredients(player);
        var complete=true;
        try (Transaction t1 = Transaction.openOuter()) {
            for (UniversalResource ingr : ingrs) {
                if(ingr.getType().isItem)
                {
                    if(!hasItem(ingr,t1,stackCopy))
                        complete=false;
                }
                else if(ingr.getType().isFluid)
                {
                    if(!hasFluid(ingr,t1,null))
                        complete=false;
                }
                else if(ingr.getType() != UniversalResource.Type.time)
                    complete=false;
            }
            if(!complete)
                t1.abort();
            else
                t1.commit();
        }
        if(complete) {
            var res = recipe.result(player);
            if(res.getType().isItem)
            {
                pendingCrafts.add(pending);
                updateNeeded++;
            }
            return true;
        }
        return false;
    }
    public CraftingResult getRecipeStatus(SimpleRecipe recipe, @Nullable PlayerEntity player)
    {
        Map<UniversalResource,Boolean> status=new HashMap<>();
        var ingrs=recipe.ingredients(player);
        var complete=true;
        try (Transaction t1 = Transaction.openOuter()) {
            for (UniversalResource ingr : ingrs) {
                if(ingr.getType().isItem)
                {
                    var d=hasItem(ingr,t1,null);
                    status.put(ingr,d);
                    if(!d)
                        complete=false;
                }
                else if(ingr.getType().isFluid)
                {
                    var d=hasFluid(ingr,t1,null);
                    status.put(ingr,d);
                    if(!d)
                        complete=false;
                }
                else
                    complete=false;
            }
            t1.abort();
        }
        return new CraftingResult(complete,status);

    }

    public void tickClient(PlayerEntity player) {
        if(currentCraft==null && !pendingCrafts.isEmpty()) {
            currentCraft = pendingCrafts.remove(0);
            totalTime=getCraftingTime(currentCraft.recipe,player);
            elapsedTime=0;
            updateNeeded++;
        }
         if(currentCraft !=null)
        {
            if(elapsedTime >=totalTime)
            {
                currentCraft=null;
                updateNeeded++;
            }
            elapsedTime++;
        }
        if(updateNeeded>0)
        {
            updateNeeded=0;
            if(onNewRecipeListener !=null)
                onNewRecipeListener.run();
        }
    }
    public float getCraftProgress()
    {
        if(totalTime>0)
            return elapsedTime/(float)totalTime;
        return 0f;
    }
}
