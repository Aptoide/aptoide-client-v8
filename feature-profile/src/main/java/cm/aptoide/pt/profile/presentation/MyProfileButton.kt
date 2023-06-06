package cm.aptoide.pt.profile.presentation

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.theme.AppTheme

@Composable
fun MyProfileButton(onClick: () -> Unit) {
  val userProfile = userProfileData("myProfileUserData").first
  val userImage = userProfile.userImage
    .takeIf { it.isNotBlank() }
    ?.let { Uri.parse(it) }
  val imageVector = rememberVectorPainter(AppTheme.icons.NoImageIcon)


  IconButton(
    modifier = Modifier.fillMaxHeight(),
    onClick = onClick
  ) {
    AptoideAsyncImage(
      data = userImage,
      contentDescription = "My Profile",
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .border(
          width = 2.dp,
          color = AppTheme.colors.imageIconBackground,
          shape = CircleShape
        ),
      placeholder = imageVector,
      error = imageVector,
    )
  }
}
