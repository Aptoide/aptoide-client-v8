package cm.aptoide.pt.profile.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.dialogs.AptoideDialog
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.toolbar.TopBar
import cm.aptoide.pt.settings.presentation.settingsRoute
import cm.aptoide.pt.theme.grey
import cm.aptoide.pt.theme.pinkishOrange

const val myProfileRoute = "myProfile"

fun NavGraphBuilder.myProfileScreen(
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
) = composable(myProfileRoute) {
  val myProfileTitle = "My Account"
  MyProfileScreen(
    title = myProfileTitle,
    navigate = navigate,
    navigateBack = navigateBack,
  )
}

@Composable
fun MyProfileScreen(
  title: String,
  navigate: (String) -> Unit,
  navigateBack: () -> Unit,
) {
  val uriHandler = LocalUriHandler.current

  val userProfile = userProfileData("myProfileUserData").first
  val userImageUri = userProfile.userImage
    .takeIf { it.isNotBlank() }
    ?.let { Uri.parse(it) }

  val username = userProfile.username.ifEmpty { "Guest" }
  val userJoinedData = userProfile.joinedData.ifEmpty { "Unknown" } // TODO
  val userStoreName = userProfile.userStore.ifEmpty { "Unknown" } // TODO
  var openLogoutDialog by remember { mutableStateOf(false) }

  if (openLogoutDialog)
    LogoutDialog(
      onPositiveClick = {
        // TODO Logout Account
        openLogoutDialog = false
      },
      onDismissDialog = { openLogoutDialog = false }
    )

  Column(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    TopBar(title, navigateBack)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      MyProfileHeader(
        username = username,
        imageUri = userImageUri,
        userJoinedData = userJoinedData,
        editImageOnClick = { navigate(editProfileRoute) }
      )
      MyStoreCard(
        userStoreName = userStoreName,
        onClick = { /*TODO*/ }
      )
      SettingsCard(onClick = { navigate(settingsRoute) })
      AptoideProductsCard(openLink = { uriHandler.openUri(it) })
      FAQsCard(onClick = { uriHandler.openUri("https://pt.aptoide.com/company/faq") })
      LogoutCard(onClick = { openLogoutDialog = true })
    }
  }
}

@Composable
private fun MyProfileHeader(
  username: String,
  imageUri: Uri?,
  userJoinedData: String,
  editImageOnClick: () -> Unit,
) {
  val imageVector = rememberVectorPainter(AppTheme.icons.NoImageIcon)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, bottom = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    AptoideAsyncImage(
      data = imageUri,
      contentDescription = "My Profile Image",
      placeholder = imageVector,
      error = imageVector,
      modifier = Modifier
        .clickable(onClick = editImageOnClick)
        .size(64.dp)
        .clip(CircleShape)
        .border(
          width = 4.dp,
          color = AppTheme.colors.imageIconBackground,
          shape = CircleShape,
        )
    )

    Column(
      modifier = Modifier
        .height(64.dp)
        .weight(1f),
      verticalArrangement = Arrangement
        .spacedBy(5.dp, Alignment.CenterVertically)
    ) {
      Text(
        text = "Hi, I'm $username",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Visible,
        maxLines = 2,
        style = AppTheme.typography.medium_S
      )

      Text(
        text = "Joined Aptoide in $userJoinedData",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.medium_XXS
      )
    }

    Text(
      text = "EDIT",
      modifier = Modifier
        .padding(end = 16.dp)
        .clickable(onClick = editImageOnClick),
      textAlign = TextAlign.Start,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = AppTheme.typography.medium_XS,
      color = pinkishOrange
    )
  }
}

@Composable
private fun MyStoreCard(
  userStoreName: String,
  onClick: () -> Unit,
) {
  SmallProfileCard(
    title = "My Store",
    imageVector = Icons.Outlined.Book,
    contentDescription = "MyStoreIcon",
    onClick = onClick
  ) {
    Text(
      text = userStoreName,
      modifier = Modifier.padding(end = 10.dp),
      textAlign = TextAlign.End,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = AppTheme.typography.medium_XS,
      color = Color.Gray
    )
  }
}

@Composable
private fun SettingsCard(onClick: () -> Unit) {
  SmallProfileCard(
    title = "Settings",
    imageVector = Icons.Outlined.Settings,
    contentDescription = "SettingsIcon",
    onClick = onClick
  )
}

@Composable
private fun AptoideProductsCard(openLink: (String) -> Unit) {
  ProfileCard(
    title = "Aptoide Products",
    imageVector = AppTheme.icons.AptoideIcon,
    titleColor = AppTheme.colors.onBackground,
    imageColorFilter = null,
    contentDescription = "AptoideIcon",
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
      ProductSection(
        title = "Aptoide TV",
        description = "The best solution for your Set Top Box and Smart TV",
        imageVector = AppTheme.icons.AptoideTVIcon,
        contentDescription = "Aptoide TV",
        onClick = { openLink("https://tv.aptoide.com/") }
      )
      ProductSection(
        title = "Aptoide Uploader",
        description = "Perfect tool to get your favorite apps in Aptoide store",
        imageVector = AppTheme.icons.AptoideUploaderIcon,
        contentDescription = "Aptoide Uploader",
        onClick = { openLink("https://aptoide-uploader.pt.aptoide.com/app") }
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy((-4).dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Follow us",
          modifier = Modifier.padding(end = 12.dp),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_M
        )
        AptoideSocialsIcon(
          imageVector = AppTheme.icons.FacebookIcon,
          contentDescription = "FacebookIcon",
          onClick = {
            openLink(
              "https://www.facebook.com/aptoide"
            )
          }
        )
        AptoideSocialsIcon(
          imageVector = AppTheme.icons.TwitterIcon,
          contentDescription = "TwitterIcon",
          onClick = {
            openLink(
              "https://twitter.com/Aptoide"
            )
          }
        )
        AptoideSocialsIcon(
          imageVector = AppTheme.icons.InstagramIcon,
          contentDescription = "InstagramIcon",
          onClick = {
            openLink(
              "https://www.instagram.com/aptoideappstore/"
            )
          }
        )
      }
    }
  }
}

@Composable
fun FAQsCard(onClick: () -> Unit) {
  SmallProfileCard(
    title = "FAQs",
    description = "Find all the answers you need!",
    imageVector = Icons.Outlined.HelpOutline,
    contentDescription = "FAQIcon",
    onClick = onClick
  )
}

@Composable
fun LogoutCard(
  onClick: () -> Unit = {},
) {
  ProfileCard(
    title = "Log Out",
    imageVector = Icons.Filled.Logout,
    titleColor = AppTheme.colors.primary,
    imageColorFilter = ColorFilter.tint(AppTheme.colors.primary),
    contentDescription = "LogOutIcon",
    onClick = onClick,
  )
}

@Composable
private fun LogoutDialog(
  onPositiveClick: () -> Unit,
  onDismissDialog: () -> Unit,
) {
  AptoideDialog(
    title = "Logout Account",
    positiveText = "confirm",
    onPositiveClicked = onPositiveClick,
    onDismissDialog = onDismissDialog
  ) {
    Text(
      text = "Are you sure you want to log out from your account?",
      modifier = Modifier.fillMaxWidth(),
      textAlign = TextAlign.Start,
      overflow = TextOverflow.Ellipsis,
      maxLines = 3,
      style = AppTheme.typography.medium_S,
    )
  }
}

@Composable
fun SmallProfileCard(
  title: String,
  imageVector: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  description: String? = null,
  content: @Composable () -> Unit = {},
) {
  ProfileCard(
    title = title,
    imageVector = imageVector,
    titleColor = AppTheme.colors.onBackground,
    imageColorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
    contentDescription = contentDescription,
    onClick = onClick,
    smallContent = content
  ) {
    description?.let {
      Text(
        text = it,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.medium_XS,
        color = grey
      )
    }
  }
}

@Composable
private fun ProfileCard(
  title: String,
  imageVector: ImageVector,
  titleColor: Color = AppTheme.colors.onBackground,
  imageColorFilter: ColorFilter? = null,
  contentDescription: String = "",
  onClick: () -> Unit = {},
  smallContent: @Composable () -> Unit = {},
  content: @Composable () -> Unit = {},
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable(onClick = onClick),
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Image(
          imageVector = imageVector,
          colorFilter = imageColorFilter,
          contentDescription = contentDescription,
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(28.dp)
        )
        Text(
          text = title,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_M,
          color = titleColor
        )
        smallContent()
      }
      content()
    }
  }
}

@Composable
private fun ProductSection(
  title: String,
  description: String,
  imageVector: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(86.dp)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(5.dp))
      )
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = title,
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_S,
        )
        Text(
          text = description,
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Visible,
          maxLines = 2,
          style = AppTheme.typography.medium_XS,
          color = grey
        )
      }
    }
  }
}

@Composable
private fun AptoideSocialsIcon(
  imageVector: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
) {
  IconButton(
    modifier = Modifier.fillMaxHeight(),
    onClick = onClick
  ) {
    Image(
      imageVector = imageVector,
      contentDescription = contentDescription,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .border(
          width = 1.dp,
          color = grey,
          shape = CircleShape
        )
    )
  }
}
