package cm.aptoide.pt.v8engine;

import android.app.Activity;
import android.content.Context;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Created by danielchen on 01/09/17.
 */

public class AutoUpdateHandler extends DefaultHandler2 {

    private Context context;

    public AutoUpdateHandler(Context context){
        this.context = context;
    }

    AutoUpdate.AutoUpdateInfo info = new AutoUpdate.AutoUpdateInfo();
    private StringBuilder sb = new StringBuilder();

    public AutoUpdate.AutoUpdateInfo getAutoUpdateInfo() {
        return info;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        sb.setLength(0);
    }

    @Override public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);

        if (localName.equals("versionCode")) {
            info.vercode = Integer.parseInt(sb.toString());
        } else if (localName.equals("uri")) {
            info.path = sb.toString();
        } else if (localName.equals("md5")) {
            info.md5 = sb.toString();
        } else if (localName.equals("minSdk")) {
            info.minsdk = Integer.parseInt(sb.toString());
        } else if (localName.equals("minAptVercode")) {
            info.minAptoideVercode = Integer.parseInt(sb.toString());
        }
        info.packageName = context.getPackageName();
    }

    @Override public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        sb.append(ch, start, length);
    }
}
