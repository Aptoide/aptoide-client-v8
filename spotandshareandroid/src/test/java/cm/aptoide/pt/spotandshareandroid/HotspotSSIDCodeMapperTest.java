package cm.aptoide.pt.spotandshareandroid;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by filipe on 04-04-2017.
 */

public class HotspotSSIDCodeMapperTest {

  private HotspotSSIDCodeMapper hotspotSSIDCodeMapper;

  @Test public void teste() {
    hotspotSSIDCodeMapper = new HotspotSSIDCodeMapper();

    testEncodeAllCharacters();
    testEncodeLimits();
  }

  @Test public void testEncodeAllCharacters() {
    char encoded;
    for (int i = 0; i < 10; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('0' + i, encoded);
    }

    for (int i = 10; i <= 35; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('A' + i - 10, encoded);
    }

    for (int i = 36; i <= 61; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('a' + i - 36, encoded);
    }
  }

  @Test public void testEncodeLimits() {

    char encoded = hotspotSSIDCodeMapper.encode(0);
    Assert.assertEquals('0', encoded);

    encoded = hotspotSSIDCodeMapper.encode(9);
    Assert.assertEquals('9', encoded);

    encoded = hotspotSSIDCodeMapper.encode(10);
    Assert.assertEquals('A', encoded);

    encoded = hotspotSSIDCodeMapper.encode(35);
    Assert.assertEquals('Z', encoded);

    encoded = hotspotSSIDCodeMapper.encode(36);
    Assert.assertEquals('a', encoded);

    encoded = hotspotSSIDCodeMapper.encode(61);
    Assert.assertEquals('z', encoded);

    for (int i = 0; i < 10; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('0' + i, encoded);
    }

    for (int i = 10; i <= 35; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('A' + i - 10, encoded);
    }

    for (int i = 36; i <= 61; i++) {
      encoded = hotspotSSIDCodeMapper.encode(i);
      Assert.assertEquals('a' + i - 36, encoded);
    }
  }

  @Test public void testDecodeLimits() {
    int decoded = hotspotSSIDCodeMapper.decode('0');
    Assert.assertEquals(0, decoded);

    decoded = hotspotSSIDCodeMapper.decode('9');
    Assert.assertEquals(9, decoded);

    decoded = hotspotSSIDCodeMapper.decode('A');
    Assert.assertEquals(10, decoded);

    decoded = hotspotSSIDCodeMapper.decode('Z');
    Assert.assertEquals(35, decoded);

    decoded = hotspotSSIDCodeMapper.decode('a');
    Assert.assertEquals(36, decoded);

    decoded = hotspotSSIDCodeMapper.decode('z');
    Assert.assertEquals(61, decoded);
  }

  @Test public void testDecodeAllCharacters() {
    int decoded;

    for (int i = 48; i < 58; i++) {
      decoded = hotspotSSIDCodeMapper.decode((char) i);
      Assert.assertEquals(i - 48, decoded);
    }

    for (int i = 65; i < 91; i++) {
      decoded = hotspotSSIDCodeMapper.decode((char) i);
      Assert.assertEquals(i - 55, decoded);
    }

    for (int i = 97; i < 123; i++) {
      decoded = hotspotSSIDCodeMapper.decode((char) i);
      Assert.assertEquals(i - 61, decoded);
    }
  }
}
