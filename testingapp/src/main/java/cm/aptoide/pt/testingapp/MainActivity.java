package cm.aptoide.pt.testingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cm.aptoide.pt.aptoidesdk.ads.Ad;
import cm.aptoide.pt.aptoidesdk.ads.Aptoide;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;

/**
 * Created by neuro on 28-10-2016.
 */

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tv = (TextView) findViewById(R.id.tv);
    }


    public void adsClick(View view) {

        List<Ad> l = Aptoide.getAds(3);
        if (l == null || l.size() == 0) {
            tv.setText("ad response: empty");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Ad i : l) {
                sb.append("ad name: " + i.getName() + "\n");
            }
            tv.setText(sb.toString());
        }
    }

    public void searchClick(View view) {

        List<SearchResult> l = Aptoide.searchApps("facebook", "apps");
        if (l == null || l.size() == 0) {
            tv.setText("search response: empty");
        } else {
            StringBuilder sb = new StringBuilder();
            for (SearchResult i : l) {
                sb.append("search: app name: " + i.getName() + "\n");
            }
            tv.setText(sb.toString());
        }
    }

    public void appsClick(View view) {

        App l = Aptoide.getApp("cm.aptoide.pt", "apps");
        if (l == null) {
            tv.setText("app response: empty");
        } else {
            tv.setText("app name: " + l.getName());
        }
    }
}
