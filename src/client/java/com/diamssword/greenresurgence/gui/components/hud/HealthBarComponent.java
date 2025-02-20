package com.diamssword.greenresurgence.gui.components.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.util.internal.ThreadLocalRandom;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.Random;

public class HealthBarComponent extends BaseComponent implements IHideableComponent {

    private int count=10;
    private int lastHealthValue;
    private int renderHealthValue;
    private long lastHealthCheckTime;
    private long heartJumpEndTick;
    protected boolean blend = false;
    protected final Identifier texture;
    private final Random random=new Random();

    private int ticks;
    private boolean hidden;

    protected HealthBarComponent(Identifier texture) {
        super();
        this.texture=texture;
    }
    @Override
    public void hidden(boolean hidden) {
        this.hidden=hidden;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }
    public HealthBarComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }
    public HealthBarComponent count(int count) {
        this.count = count;
        return this;
    }
    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
    }
    public void tick()
    {
        ticks++;
    }
    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        var v=verticalSizing.get();
        if(v !=null && !v.isContent() && height>0)
        {
            return (int) ((height* count)*(8f/9f))+1;
        }
        return 8*count;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 9;
    }
    @Override
    protected void applySizing() {
        final var horizontalSizing = this.horizontalSizing.get();
        final var verticalSizing = this.verticalSizing.get();
        final var margins = this.margins.get();
        this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
        this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
    }
    private void renderBar(DrawContext context,PlayerEntity playerEntity,int x,int y)
    {
        if(hidden)
            return;
        int i = MathHelper.ceil(playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = (long)(this.ticks + 20);
        } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = (long)(this.ticks + 10);
        }

        if (l - this.lastHealthCheckTime > 1000L) {
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }

        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        this.random.setSeed((long)(this.ticks * 312871));
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int v = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            v = this.ticks % MathHelper.ceil(f + 5.0F);
        }
        this.renderHealthBar(context, playerEntity, x, y, v, f, i, j, p, bl,0.5f);
    }
    private void renderHealthBar( DrawContext context,PlayerEntity player,  int x, int y, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health,  int absorption, boolean blinking,float trueHeartPercent) {
        HeartType heartType = HeartType.fromPlayerState(player);
        int i =0;// 9 * (player.getWorld().getLevelProperties().isHardcore() ? 5 : 0);
        int j = MathHelper.ceil((double)maxHealth / 2.0);
        float perc=health/maxHealth;
        float perc1=lastHealth/maxHealth;
        int l = j * 2;
        for (int m = 0; m < count; m++) {
            int n = m / count;
            int o = m % count;
            int p = x + o * (int)(height*(8f/9f));
            int q = y - n;
            if (m /(float) count<trueHeartPercent && perc1<trueHeartPercent) {
                q += this.random.nextInt(2);
            }
            if (m < j && m == regeneratingHeartIndex) {
                q -= 2;
            }
            this.drawHeart(context, HeartType.CONTAINER, p, q, i, blinking, 1);
            var type1=m /(float) count>=trueHeartPercent? HeartType.ABSORBING:HeartType.TRUE;
            if(m /(float) count>=trueHeartPercent) {
                if(perc1>trueHeartPercent && heartType!=HeartType.TRUE)
                    type1 = heartType;
            }
            else
            {
                if(perc1<trueHeartPercent)
                    type1=heartType;
            }
            if(blinking && ((perc1>trueHeartPercent && m>trueHeartPercent*count) || (perc1<trueHeartPercent && m<trueHeartPercent*count)))
                this.drawHeart(context, type1, p, q, i, true, 1);
            if (m<perc1*count) {
                var per=Math.min((perc1*count)-m,1);
                var b=false;
                if(blinking && lastHealth>health)
                    b=m>=perc*count;
                this.drawHeart(context, type1, p, q, i, b, per);
            }


        }
    }
    private void drawHeart(DrawContext context, HeartType type, int x, int y, int v, boolean blinking, float fillPercent) {
        context.drawTexture(texture, x, y, (int) (height*fillPercent),height, (float) type.getU(blinking), v, (int) (9*fillPercent), 9,180,9);
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.enableDepthTest();

        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        MinecraftClient client=MinecraftClient.getInstance();
        if(client.player !=null)
            renderBar(context, client.player, 0,0);
        if (this.blend) {
            RenderSystem.disableBlend();
        }

        matrices.pop();
    }
    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);
        UIParsing.apply(children, "count", UIParsing::parseSignedInt, this::count);

    }
    public static HealthBarComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
        return new HealthBarComponent(new Identifier(textureId.getNamespace(),"textures/gui/"+textureId.getPath()));
    }
    static enum HeartType {
        CONTAINER(0, false),
        NORMAL(1, true),
        POISONED(2, true),
        WITHERED(3, true),
        ABSORBING(4, true),
        TRUE(5, true),
        FROZEN(6, false);

        private final int textureIndex;
        private final boolean hasBlinkingTexture;

        private HeartType(int textureIndex, boolean hasBlinkingTexture) {
            this.textureIndex = textureIndex;
            this.hasBlinkingTexture = hasBlinkingTexture;
        }

        /**
         * {@return the left-most coordinate of the heart texture}
         */
        public int getU(boolean blinking) {
            int i=this.hasBlinkingTexture && blinking ? 1 : 0;

            return (this.textureIndex * 2 + i) * 9;
        }

        static HeartType fromPlayerState(PlayerEntity player) {
            HeartType heartType;
            if (player.hasStatusEffect(StatusEffects.POISON)) {
                heartType = POISONED;
            } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
                heartType = WITHERED;
            } else if (player.isFrozen()) {
                heartType = FROZEN;
            } else {
                heartType = TRUE;
            }
            return heartType;
        }
    }
}
