package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ScrollPart {
    public static final Identifier SCROLLER_TEXTURE=GreenResurgence.asRessource("textures/gui/scroller.png");
    public int width;
    public int height;
    public int x;
    public int y;
    public int scroll=0;
    public int lines;

    public ScrollPart(int x, int y,int width, int height,int lines) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.lines=lines;
    }

    public boolean isOver(double mouseX, double mouseY)
    {
        return mouseX>=this.x && mouseX<this.x+width && mouseY>=this.y && mouseY<=y+height;
    }
    private Consumer<Integer> callback;
    public void onScroll(Consumer<Integer> consumer)
    {
        callback=consumer;
    }
    public boolean mouseDrag(double mouseY)
    {

        double m=mouseY-y;
        if(m>0 && mouseY<=this.y+this.height)
        {
            float perc=Math.min(Math.max((float)m/this.height,0),1);
            this.scroll= (int) (perc*((float)lines+1));
            onScroll();
            return true;
        }
        return false;
    }
    private void onScroll()
    {
        if(callback!=null)
            callback.accept(this.scroll);
    }
    public void draw(DrawContext ctx)
    {
        ctx.drawTexture(SCROLLER_TEXTURE,x,y,this.width, 5,32,0,32,10,64,64);
        ctx.drawTexture(SCROLLER_TEXTURE,x,y+this.height-5,this.width, 5,32,54,32,10,64,64);
        ctx.drawTexture(SCROLLER_TEXTURE,x,y+5,this.width, this.height-10,32,10,32,44,64,64);
        ctx.drawTexture(SCROLLER_TEXTURE,x, (int) (y+((height-(this.width*2))*scrollPercent())),this.width, this.width*2,0,0,32,64,64,64);
    }
    public float scrollPercent()
    {
        return scroll/Math.max((float)lines,1f);
    }
    public void scroll(int value) {

        if(value<0 && scrollPercent()<1)
        {
            scroll=scroll+1;
            onScroll();
        }
        else if(value>0)
        {
            scroll=Math.max(0,scroll-1);
            onScroll();
        }

    }

}
