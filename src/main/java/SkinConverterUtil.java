import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

public class SkinConverterUtil {

    public static void main(String[] args)
    {
       readFolder(new File("./skin_conf_gen/steves"));
    }
    private static BufferedImage holdClip(BufferedImage in)
    {
        var out=new BufferedImage(in.getWidth(),in.getHeight(),in.getType());
        var g=out.getGraphics();
        g.drawImage(in,0,0,null);
        g.dispose();
        return out;

    }
    private  static void readFolder(File path)
    {
        for (File file : path.listFiles()) {
            if(file.isDirectory())
                readFolder(file);
            else
            {
                try {
                    System.out.println("converting "+file);
                    var img=ImageIO.read(file);
                    var g=img.createGraphics();

                    var im1=holdClip(img.getSubimage(96,40,8,24));
                    var im2=holdClip(img.getSubimage(105,40,8,24));
                    var im3=holdClip(img.getSubimage(96,32,7,8));
                    var im4=holdClip(img.getSubimage(73,104,22,24));
                    var im5=holdClip(img.getSubimage(73,96,7,8));
                    var im6=holdClip(img.getSubimage(81,96,7,8));

                    //second layer
                    var im7=holdClip(img.getSubimage(96,72,8,24));
                    var im8=holdClip(img.getSubimage(105,72,8,24));
                    var im9=holdClip(img.getSubimage(96,64,7,8));
                    var im10=holdClip(img.getSubimage(105,104,22,24));
                    var im11=holdClip(img.getSubimage(105,96,7,8));
                    var im12=holdClip(img.getSubimage(113,96,7,8));

                    g.setBackground(new Color(0,0,0,0));

                    g.clearRect(95,40,17,24);
                    g.clearRect(95,32,9,8);
                    g.clearRect(72,104,24,24);
                    g.clearRect(72,96,16,8);

                    //2nd
                    g.clearRect(95,72,17,24);
                    g.clearRect(95,64,9,8);
                    g.clearRect(104,104,24,24);
                    g.clearRect(104,96,16,8);

                    g.drawImage(im1,95,40,null);
                    g.drawImage(im2,103,40,null);
                    g.drawImage(im3,95,32,null);
                    g.drawImage(im4,72,104,null);
                    g.drawImage(im5,72,96,null);
                    g.drawImage(im6,79,96,null);

                    //2nd
                    g.drawImage(im7,95,72,null);
                    g.drawImage(im8,103,72,null);
                    g.drawImage(im9,95,64,null);
                    g.drawImage(im10,104,104,null);
                    g.drawImage(im11,104,96,null);
                    g.drawImage(im12,112,96,null);

                    g.dispose();
                    ImageIO.write(img,"png",file);
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
        }
    }
}
