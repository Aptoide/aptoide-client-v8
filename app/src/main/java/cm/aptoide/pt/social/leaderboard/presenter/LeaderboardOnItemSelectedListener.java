package cm.aptoide.pt.social.leaderboard.presenter;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetLeaderboardEntriesRequest;
import cm.aptoide.pt.social.leaderboard.data.Leaderboard;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntry;
import cm.aptoide.pt.social.leaderboard.data.LeaderboardEntryMapper;
import cm.aptoide.pt.social.leaderboard.view.LeaderboardAdapter;
import cm.aptoide.pt.social.leaderboard.view.LeaderboardView;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 10/12/17.
 */

public class LeaderboardOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

  private LeaderboardAdapter adapter;
  private PublishSubject<String> spinnerPublishSubject;


  public LeaderboardOnItemSelectedListener(LeaderboardAdapter adapter, PublishSubject<String> spinnerPublishSubject){
    super();
    this.adapter = adapter;
    this.spinnerPublishSubject = spinnerPublishSubject;
  }

  @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    adapter.updateLeaderboardEntries(Collections.EMPTY_LIST);
    spinnerPublishSubject.onNext(adapterView.getItemAtPosition(i).toString().toLowerCase());


  }

  @Override public void onNothingSelected(AdapterView<?> adapterView) {

  }
}
