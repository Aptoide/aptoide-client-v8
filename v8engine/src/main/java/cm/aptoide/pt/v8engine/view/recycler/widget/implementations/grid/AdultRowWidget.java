package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.dialog.AdultDialog;
import cm.aptoide.pt.v8engine.fragment.BaseLoaderFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AdultRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 11-07-2016.
 */
public class AdultRowWidget extends Widget<AdultRowDisplayable> {

  private SwitchCompat adultSwitch;
  private boolean shouldITrackNextChange = true;

  public AdultRowWidget(View itemView) {
    super(itemView);
  }

  @Partners @Override protected void assignViews(View itemView) {
    adultSwitch = (SwitchCompat) itemView.findViewById(R.id.adult_content);
  }

  @Override public void bindView(AdultRowDisplayable displayable) {
    adultSwitch.setOnCheckedChangeListener(null);
    adultSwitch.setChecked(SecurePreferences.isAdultSwitchActive());
    adultSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

      if (isChecked) {
        AdultDialog.buildAreYouAdultDialog(getContext(), (dialog, which) -> {
          if (which == DialogInterface.BUTTON_POSITIVE) {
            //						adultSwitch.setChecked(true);
            AptoideAccountManager.updateMatureSwitch(true);

            FragmentManager supportFragmentManager = getContext().getSupportFragmentManager();
            ((BaseLoaderFragment) supportFragmentManager.getFragments()
                .get(supportFragmentManager.getBackStackEntryCount())).load(true, true, null);
          }
        }, dialog1 -> {
          shouldITrackNextChange = false;
          adultSwitch.setChecked(false);
        }).show();
      } else {
        if (shouldITrackNextChange) {
          Logger.d(this.getClass().getName(), "FLURRY TESTING HOME : LOCK ADULT CONTENT");
          Analytics.AdultContent.lock();
        } else {
          shouldITrackNextChange = true;
        }
        FragmentManager supportFragmentManager = getContext().getSupportFragmentManager();
        ((BaseLoaderFragment) supportFragmentManager.getFragments()
            .get(supportFragmentManager.getBackStackEntryCount())).load(true, true, null);
        AptoideAccountManager.updateMatureSwitch(false);
      }
    });
  }
}
