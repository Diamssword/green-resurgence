package com.diamssword.greenresurgence.gui.hud;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ComponentsRegister;
import com.diamssword.greenresurgence.gui.components.hud.*;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HudGui extends BaseUIModelScreen<FlowLayout> {
    public static final int blueColor=0xff54ceed;
    private FlowLayout root;
    private List<Runnable> tickers= new ArrayList<>();

    protected HudGui() {
        super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("hud")));
    }
    private boolean hideBars()
    {
        return !this.client.interactionManager.hasStatusBars();
    }
    private PlayerEntity getCameraPlayer() {
        return !(this.client.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.client.getCameraEntity();
    }
    private LivingEntity getRiddenEntity() {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            Entity entity = playerEntity.getVehicle();
            if (entity == null) {
                return null;
            }

            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }

        return null;
    }
    @Override
    protected void build(FlowLayout rootComponent) {
        this.root=rootComponent;
        tickers.clear();
        attachWithTicker(HealthBarComponent.class,"health",(h)->{
                h.tick();
                h.hidden(hideBars());
        });
        attachWithTicker(BarComponent.class,"mountHealth",(h)->{
            var rid=getRiddenEntity();
            if(rid !=null)
            {
                var p=rid.getHealth()/ rid.getMaxHealth();
                h.setFillPercent(p);
            }
            h.hidden(rid==null);
        });
        attachWithTicker(BarComponent.class,"mountJump",h->{
            h.setFillPercent(this.client.player.getMountJumpStrength());
            h.hidden(this.client.player.getJumpingMount()==null);
        });
        attachWithTicker(BarComponent.class,"air",(h)->{
            var p=this.client.player.getAir()/(float)this.client.player.getMaxAir();
            h.setFillPercent(p);
            h.hidden(hideBars() || p>=1);
        });
        attachWithTicker(IconComponent.class,"airIcon",(h)->{
            var p=this.client.player.getAir()/(float)this.client.player.getMaxAir();
            h.hidden(hideBars() || p>=1);
        });
        attachWithTicker(ItemTooltipComponent.class,"heldtooltip", h->{
            h.tick();
            h.hidden(this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR);
        });
        attachWithTicker(HotBarComponent.class,"hotbar",(h)->{
            h.hidden(this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR);
            var ls= DefaultedList.ofSize(5, ItemStack.EMPTY);
            for (int i = 0; i < 5; i++) {
                ls.set(i,client.player.getInventory().main.get(i));
            }
            h.setStacks(ls);
            h.setSelected(client.player.getInventory().selectedSlot);
        });
        attachWithTicker(SingleSlotComponent.class,"offhandleft",h->{
            var ind=client.player.getMainArm().getOpposite()!= Arm.LEFT;
            h.setIndicatorMode(ind);
            var st=client.player.getOffHandStack();

            if(!ind)
                h.hidden(st.isEmpty());
            h.setStacks(st);
        });
        attachWithTicker(SingleSlotComponent.class,"offhandright",h->{
            var ind=client.player.getMainArm().getOpposite()!= Arm.RIGHT;
            h.setIndicatorMode(ind);
            var st=client.player.getOffHandStack();

            if(!ind)
                h.hidden(st.isEmpty());
            h.setStacks(st);
        });
        attachWithTicker(IconComponent.class,"armorIcon",(h)->{
            var arm=client.player.getArmor();
            h.hidden(hideBars() || arm<=0);
        } );
        attachWithTicker(LabelComponent.class,"armorText",(h)-> {
            var arm=client.player.getArmor();
            if(!hideBars()&& arm>0)
            {
                h.text(Text.literal(arm+"").setStyle(Style.EMPTY.withColor(blueColor)));
            }
            else
                h.text(Text.literal(""));
        });

    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); // Draw chat and base GUI
    }
    private <T extends Component> void attachWithTicker(@NotNull Class<T> expectedClass, @NotNull String id, Consumer<T> ticker)
    {
    var comp=this.root.childById(expectedClass,id);
    if(comp !=null)
        tickers.add(()->{ticker.accept(comp);});
    }
    @Override
    public void tick() {
        super.tick();
        tickers.forEach(Runnable::run);

    }

    public void debug(boolean enable)
    {
        if(uiAdapter !=null) {
            uiAdapter.enableInspector = enable;
            uiAdapter.globalInspector = enable;
        }
    }
}
