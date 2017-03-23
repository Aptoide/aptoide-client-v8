package cm.aptoide.pt.v8engine.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 18-07-2016.
 * This class determines how many pages exist and which fragment to display for each page.
 */
public class DumbEagerFragmentPagerAdapter extends FragmentStatePagerAdapter {

  private final ArrayList<Fragment> fragments = new ArrayList<>();

  public DumbEagerFragmentPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  public void attachFragments(List<Fragment> fragments) {
    this.fragments.addAll(fragments);
  }

  @Override public int getCount() {
    return fragments.size();
  }

  @Override public Fragment getItem(int position) {
    if (position >= fragments.size()) {
      throw new IllegalArgumentException("Item index is bigger than item count.");
    }
    return fragments.get(position);
  }
}
