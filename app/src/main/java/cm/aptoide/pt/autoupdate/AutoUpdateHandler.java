package cm.aptoide.pt.autoupdate;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class AutoUpdateHandler extends DefaultHandler2 {

  private final String packageName;
  private final AutoUpdateViewModel autoUpdateViewModel;
  private StringBuilder stringBuilder;

  public AutoUpdateHandler(String packageName, StringBuilder stringBuilder,
      AutoUpdateViewModel autoUpdateViewModel) {
    this.packageName = packageName;
    this.stringBuilder = stringBuilder;
    this.autoUpdateViewModel = autoUpdateViewModel;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    super.startElement(uri, localName, qName, attributes);
    stringBuilder.setLength(0);
  }

  @Override public void endElement(String uri, String localName, String qName) throws SAXException {
    super.endElement(uri, localName, qName);

    if (localName.equals("versionCode")) {
      autoUpdateViewModel.setVercode(Integer.parseInt(stringBuilder.toString()));
    } else if (localName.equals("uri")) {
      autoUpdateViewModel.setPath(stringBuilder.toString());
    } else if (localName.equals("md5")) {
      autoUpdateViewModel.setMd5(stringBuilder.toString());
    } else if (localName.equals("minSdk")) {
      autoUpdateViewModel.setMindsdk(Integer.parseInt(stringBuilder.toString()));
    } else if (localName.equals("minAptVercode")) {
      autoUpdateViewModel.setMinAptoideVercode(Integer.parseInt(stringBuilder.toString()));
    }
    autoUpdateViewModel.setInfoPackageName(packageName);
  }

  @Override public void characters(char[] chars, int start, int length) throws SAXException {
    super.characters(chars, start, length);
    stringBuilder.append(chars, start, length);
  }
}
