package cm.aptoide.pt.spotandshareandroid;

import java.util.Random;

/**
 * Created by filipe on 04-04-2017.
 */

public class HotspotSSIDCodeMapper {

  public HotspotSSIDCodeMapper() {
  }

  public int decode(char c) {
    int charValue = (int) c;
    if (!assetDomain(charValue)) {
      throw new IllegalArgumentException("value is outside of the domain !");
    }
    int value = charValue - '0';
    if (charValue > '9') {
      value -= ('A' - ('9' + 1));
    }

    if (charValue > 'Z') {
      value -= ('a' - ('Z' + 1));
    }
    return value;
  }

  private boolean assetDomain(int value) {
    if (value >= 0 && value <= 61) {
      return true;
    }
    return false;
  }

  public char generateRandomChar() {
    Random r = new Random();
    return encode(r.nextInt(61));
  }

  public char encode(int value) {

    if (!assetDomain(value)) {
      throw new IllegalArgumentException("value is outside domain !");
    }

    int tmp = '0';

    int maxNumber = 9;
    if (value > maxNumber) {
      tmp += ('A' - ('9' + 1));
    }

    int letterCount = 26;
    if (value > (maxNumber + letterCount)) {
      tmp += ('a' - ('Z' + 1));
    }

    return (char) (tmp + value);
  }

  public String generateRandomSequence(int lenght) {
    StringBuilder sb = new StringBuilder();
    Random r = new Random();
    for (int i = 0; i < lenght; i++) {
      sb.append(encode(r.nextInt(61)));
    }
    return sb.toString();
  }
}
