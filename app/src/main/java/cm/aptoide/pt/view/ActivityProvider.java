package cm.aptoide.pt.view;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @deprecated use specific navigator for each presenter/lifecycle manager instead. Inside those
 * navigators instantiate the proper fragment or activity.
 */
@Deprecated public interface ActivityProvider {
  Class<? extends AppCompatActivity> getMainActivityFragmentClass();
}
