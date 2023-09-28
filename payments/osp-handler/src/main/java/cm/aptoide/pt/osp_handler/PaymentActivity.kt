package cm.aptoide.pt.osp_handler

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

  private val uri by lazy { intent?.data }

  private var navController: NavHostController? = null

  @Inject
  lateinit var contentProvider: PaymentScreenContentProvider

  @OptIn(ExperimentalAnimationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberAnimatedNavController()
        .also { this.navController = it }
      contentProvider.content(navController, uri)
    }
  }
}
