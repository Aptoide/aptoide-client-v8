package cm.aptoide.pt.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideosFragment extends NavigationTrackFragment implements VideosContract.View{

  private RecyclerView list;
  private VideoAdapter adapter;
  private LinearLayout videosEmptyState;


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    adapter = new VideoAdapter(Collections.emptyList());
    return inflater.inflate(R.layout.fragment_videos, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.fragment_videos_list);
    videosEmptyState = (LinearLayout) view.findViewById(R.id.video_empty_state);

    list.setAdapter(adapter);
    list.setLayoutManager(new LinearLayoutManager(getContext()));

    //VideosRepository as getVideos()
    List<String> videosList = new ArrayList<>();
    for(int i = 0; i < 10; i++){
      String element = "Element nr.: " + i;
      videosList.add(element);
    }
    adapter.add(videosList);


  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void showVideos() {
    // TODO: 31/07/2018  
  }
}
