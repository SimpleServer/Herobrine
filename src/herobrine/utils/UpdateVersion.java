package herobrine.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class UpdateVersion {
  private static final String TEMPLATE_PATH = "";
  private static final String RESULT_PATH = "";

  public static void main(String[] args) {
    System.out.println("Hello World");
    System.out.println(new UpdateVersion().updateSignature());
  }

  private boolean updateSignature() {
    BufferedImage img;
    try {
      img = ImageIO.read(new File(TEMPLATE_PATH));
    } catch (Exception e) {
      return false;
    }

    Graphics2D g = img.createGraphics();
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING,
                       RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);
    Font font = new Font("Calibri", Font.BOLD, 15);
    g.setFont(font);
    g.setColor(Color.BLACK);

    g.drawString("8.1.3 - 30.05.2011", 588, 38);
    g.drawString("Beta 1.6.6", 588, 80);

    try {
      File outputfile = new File(RESULT_PATH);
      ImageIO.write(img, "png", outputfile);
    } catch (Exception e) {
      return false;
    }

    return true;
  }
}
