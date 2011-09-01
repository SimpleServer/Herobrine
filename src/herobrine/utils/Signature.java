package herobrine.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Signature {
  private static final String BASE_PATH = "icon";
  private Graphics2D g;
  private BufferedImage img;

  public Signature(String filename) throws IOException {
    File file = new File(BASE_PATH + File.separator + filename);
    if (!file.exists()) {
      throw new IOException("No such file: " + file);
    }
    img = ImageIO.read(file);

    g = img.createGraphics();
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING,
                       RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);

    setFont(new Font("Calibri", Font.BOLD, 15), Color.BLACK);
  }

  public void setFont(Font font) {
    g.setFont(font);
  }

  public void setFont(Color color) {
    g.setColor(color);
  }

  public void setFont(Font font, Color color) {
    setFont(font);
    setFont(color);
  }

  public void print(String string, int x, int y) {
    g.drawString(string, x, y);
  }

  public void printVertical(String string, int x, int y) {
    AffineTransform fontAT = new AffineTransform();
    Font font = g.getFont();
    fontAT.rotate(-Math.PI / 2);
    Font vertFont = font.deriveFont(fontAT);
    g.setFont(vertFont);
    print(string, x, y);
    g.setFont(font);
  }

  public void save(String filename) throws IOException {
    File outputfile = new File(BASE_PATH + File.separator + filename);
    ImageIO.write(img, "png", outputfile);
  }
}
