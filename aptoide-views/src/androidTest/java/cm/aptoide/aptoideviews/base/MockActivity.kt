package cm.aptoide.aptoideviews.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.aptoideviews.R

class MockActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTheme(R.style.AppBaseTheme)
  }


}