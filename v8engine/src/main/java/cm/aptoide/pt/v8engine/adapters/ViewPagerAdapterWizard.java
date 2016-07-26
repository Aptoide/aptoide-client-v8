package cm.aptoide.pt.v8engine.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageOneFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageThreeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.WizardPageTwoFragment;

/**
 * Created by jdandrade on 18-07-2016.
 * This class determines how many pages exist and which fragment to display for each page.
 */
public class ViewPagerAdapterWizard extends FragmentPagerAdapter {
    public static final int NUMBER_OF_WIZARD_PAGES = 3;

    public ViewPagerAdapterWizard(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUMBER_OF_WIZARD_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        switch(position){
            case 0:
                fragment = WizardPageOneFragment.newInstance();
                break;
            case 1:
                fragment = WizardPageTwoFragment.newInstance();
                break;
            case 2:
                fragment = WizardPageThreeFragment.newInstance();
                break;
        }
        return fragment;
    }

}