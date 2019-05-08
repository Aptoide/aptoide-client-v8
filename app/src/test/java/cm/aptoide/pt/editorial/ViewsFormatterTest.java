package cm.aptoide.pt.editorial;

import org.junit.Assert;
import org.junit.Test;

import static cm.aptoide.pt.editorial.ViewsFormatter.formatNumberOfViews;

public class ViewsFormatterTest {

  @Test public void correctFormatTest() {
    String oneDigit = "1";
    String twoDigit = "12";
    String threeDigit = "123";
    String fourDigit = "1234";
    String fiveDigit = "12345";
    String sixDigit = "123456";
    String sevenDigit = "1234567";
    String eightDigit = "12345678";
    String nineDigit = "123456789";
    String tenDigit = "1234567890";
    String twentyDigit = "12345678901234567890";

    Assert.assertEquals("1", formatNumberOfViews(oneDigit));
    Assert.assertEquals("12", formatNumberOfViews(twoDigit));
    Assert.assertEquals("123", formatNumberOfViews(threeDigit));
    Assert.assertEquals("1.23k", formatNumberOfViews(fourDigit));
    Assert.assertEquals("12.3k", formatNumberOfViews(fiveDigit));
    Assert.assertEquals("123k", formatNumberOfViews(sixDigit));
    Assert.assertEquals("1235k", formatNumberOfViews(sevenDigit));
    Assert.assertEquals("12.3M", formatNumberOfViews(eightDigit));
    Assert.assertEquals("123M", formatNumberOfViews(nineDigit));
    Assert.assertEquals("1235M", formatNumberOfViews(tenDigit));
  }
}
