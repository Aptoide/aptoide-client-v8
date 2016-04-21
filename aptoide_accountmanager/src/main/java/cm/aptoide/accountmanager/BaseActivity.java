package cm.aptoide.accountmanager;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by trinkes on 4/18/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getActivityTitle());
    }

    protected abstract String getActivityTitle();

    abstract
    @LayoutRes
    int getLayoutId();
}
