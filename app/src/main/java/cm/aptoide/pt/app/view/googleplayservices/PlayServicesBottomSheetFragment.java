package cm.aptoide.pt.app.view.googleplayservices;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cm.aptoide.pt.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PlayServicesBottomSheetFragment extends BottomSheetDialogFragment {

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.appview_google_services_dialog, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
  }
}
