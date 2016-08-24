package cm.aptoide.pt.v8engine.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.MainActivityFragment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.adapters.ViewPagerAdapterWizard;


/**
 * Created by jandrade on 18-07-2016.
 * This Fragment inflates the Wizard layout and uses the ViewPagerAdapterWizard to inflate each Wizard Page.
 * It also manages swapping pages and UI changes (Indicator + skip/next arrow)
 */
public class BaseWizardViewerFragment extends Fragment {
    private ViewPager mViewPager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView(view);
        setUpListeners(view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wizard_layout, container, false);
    }

    private void setUpView(View view){
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        ViewPagerAdapterWizard viewPagerAdapterWizard = new ViewPagerAdapterWizard(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapterWizard);
        mViewPager.setCurrentItem(0);
    }

    private void setUpListeners(View view){
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.wizard_indicator_group);
        final TextView skip = (TextView) view.findViewById(R.id.wizard_skip_text);
        final LinearLayout nextIconSpace = (LinearLayout) view.findViewById(R.id.wizard_next_clickable_space);
        final ImageView nextIcon = (ImageView) view.findViewById(R.id.wizard_next_arrow_icon);

        nextIconSpace.setOnClickListener(view1 -> {
            if(mViewPager.getCurrentItem() < ViewPagerAdapterWizard.NUMBER_OF_WIZARD_PAGES-1) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }else{
                getActivity().onBackPressed();
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**
             * This method will be invoked when a new page becomes selected. Animation is not
             * necessarily complete.
             *
             * @param position Position index of the new selected page.
             */
            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        radioGroup.check(R.id.wizard_indicator_one);
                        break;
                    case 1:
                        skip.setVisibility(View.GONE);
                        nextIcon.setVisibility(View.VISIBLE);
                        radioGroup.check(R.id.wizard_indicator_two);
                        break;
                    case 2:
                        skip.setVisibility(View.VISIBLE);
                        nextIcon.setVisibility(View.GONE);
                        radioGroup.check(R.id.wizard_indicator_three);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}