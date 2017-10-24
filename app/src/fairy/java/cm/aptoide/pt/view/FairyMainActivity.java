/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import com.testfairy.TestFairy;

/**
 * Created by neuro on 06-05-2016.
 */
public class FairyMainActivity extends MainActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TestFairy.begin(this, getString(R.string.TEST_FAIRY_TOKEN));
    TestFairy.setUserId(((AptoideApplication) getApplicationContext()).getIdsRepository()
        .getUniqueIdentifier());
  }
}