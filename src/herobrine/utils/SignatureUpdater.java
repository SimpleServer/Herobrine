package herobrine.utils;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

public class SignatureUpdater {
  public final static void main(String[] args) {
    String version = "8.2.3";
    String date = "21.7.2011";
    String minecraft = "1.7.3";

    Signature sig;
    try {
      sig = new Signature("signature_plain.png");
      sig.setFont(new Font("Calibri", Font.BOLD, 15), Color.BLACK);
      sig.print(version + " - " + date, 588, 38);
      sig.print("Beta " + minecraft, 588, 80);
      sig.save("signature.png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      sig = new Signature("signature_small_plain.png");
      sig.setFont(new Font("Myriad Pro", Font.PLAIN, 11), Color.BLACK);
      sig.print("v" + version + " - " + date, 213, 20);
      sig.setFont(new Color(0x34, 0x34, 0x34));
      sig.printVertical("For Beta " + minecraft, 10, 82);
      sig.save("signature_small.png");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
