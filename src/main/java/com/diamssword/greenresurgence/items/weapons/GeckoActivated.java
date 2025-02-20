package com.diamssword.greenresurgence.items.weapons;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GeckoActivated extends ActivatedSword implements GeoItem {
    public static final RawAnimation POWERED_ANIM= RawAnimation.begin().thenLoop("powered");
    public static final RawAnimation IDLE_ANIM= RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    public static BiFunction<String,Boolean,Object> ProviderFunction;
    private final boolean emissive;
    public GeckoActivated(ToolMaterial toolMaterial, int offDamage, float offSpeed, float onDamage, float onSpeed, boolean emissive, Settings settings) {
        super(toolMaterial, offDamage, offSpeed, onDamage, onSpeed, settings);
        this.emissive=emissive;
    }


    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(ProviderFunction.apply(Registries.ITEM.getId(this).getPath(),emissive));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var st= user.getStackInHand(hand);
        var comp=st.getOrCreateNbt();
        comp.putBoolean("activated",!comp.getBoolean("activated"));
        st.setNbt(comp);
        user.getItemCooldownManager().set(this, 20);
        return TypedActionResult.consume(st);
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var cont=new AnimationController<>(this, 1, state -> {
            // Apply our generic idle animation.
            // Whether it plays or not is decided down below.
            var st=state.getData(DataTickets.ITEMSTACK);
            if(st!=null &&st.getOrCreateNbt().getBoolean("activated")) {
                state.getController().setAnimation(POWERED_ANIM);
            }
            else
                state.getController().setAnimation(IDLE_ANIM);

            // Play the animation if the full set is being worn, otherwise stop
            return PlayState.CONTINUE;
        });
        cont.setParticleKeyframeHandler(event -> {
        });
        controllers.add(cont);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


}
