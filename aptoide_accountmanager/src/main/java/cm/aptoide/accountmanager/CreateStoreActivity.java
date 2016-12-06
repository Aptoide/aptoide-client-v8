package cm.aptoide.accountmanager;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by pedroribeiro on 29/11/16.
 */

public class CreateStoreActivity extends PermissionsBaseActivity {

  private Toolbar mToolbar;
  private Button mCreateStore;
  private Button mSkip;
  private RelativeLayout mStoreAvatar;
  private TextView mTakePictureText;
  private TextView mHeader;
  private TextView mChooseNameTitle;
  private EditText mStoreName;

  private String aptoideStoreAvatar = "aptoide_user_store_avatar.png";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    bindViews();
    editViews();
    setupToolbar();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_store_title);
  }

  @Override int getLayoutId() {
    return R.layout.fragment_create_store;
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mCreateStore = (Button) findViewById(R.id.create_store_action);
    mSkip = (Button) findViewById(R.id.create_store_skip);
    mStoreAvatar = (RelativeLayout) findViewById(R.id.create_store_image_action);
    mTakePictureText = (TextView) findViewById(R.id.create_store_take_picture_text);
    mStoreName = (EditText) findViewById(R.id.create_store_name);
    mHeader = (TextView) findViewById(R.id.create_store_header_textview);
    mChooseNameTitle = (TextView) findViewById(R.id.create_store_choose_name_title);
  }

  private void editViews() {
    mHeader.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_header, "Aptoide"));
    mChooseNameTitle.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_name, "Aptoide"));
  }
}
