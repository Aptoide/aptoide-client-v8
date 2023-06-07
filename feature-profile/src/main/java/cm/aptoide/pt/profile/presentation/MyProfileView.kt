package cm.aptoide.pt.profile.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
  val userJoinedData = userProfile.joinedData.ifEmpty { "January 2000" } // TODO
  val userStoreName = userProfile.userStore
  var openLogoutDialog by remember { mutableStateOf(false) }

  MyProfileView(
    onBackPressed = { navigateBack() },
    title = title,
    username = username,
    imageUri = userImageUri,
    userJoinedData = userJoinedData,
    userStoreName = userStoreName,
    editImageOnClick = { navigate(editProfileRoute) },
    settingsOnClick = { navigate(settingsRoute) },
    logoutShouldOpenDialog = openLogoutDialog,
    logoutDialogOnClick = { openLogoutDialog = it },
    openLink = { uriHandler.openUri(it) }
  )
}

@Preview
@Composable
fun MyProfileView(
  onBackPressed: () -> Unit = {},
  title: String = "My Account",
  username: String = "Guest",
  imageUri: Uri? = null,
  userJoinedData: String = "unknown",
  userStoreName: String? = "My Store",
  editImageOnClick: () -> Unit = {},
  settingsOnClick: (String) -> Unit = {},
  logoutShouldOpenDialog: Boolean = false,
  logoutDialogOnClick: (Boolean) -> Unit = {},
  openLink: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    TopBar(title, onBackPressed)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      MyProfileHeader(
        username = username,
        imageUri = imageUri,
        userJoinedData = userJoinedData,
        editImageOnClick = editImageOnClick
      )
      MyStoreCard(userStoreName = userStoreName)
      SettingsCard(settingsOnClick)
      AptoideProductsCard(openLink)
      FAQsCard(openLink)
      LogoutCard(
        logoutShouldOpenDialog = logoutShouldOpenDialog,
        logoutDialogOnClick = logoutDialogOnClick
      )
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
        .wrapContentWidth()
        .clickable(onClick = editImageOnClick),
      textAlign = TextAlign.Start,
      overflow = TextOverflow.Ellipsis,
      maxLines = 1,
      style = AppTheme.typography.medium_XS,
      color = pinkishOrange
    )
    Spacer(modifier = Modifier.width(1.dp))
  }
}

@Composable
private fun MyStoreCard(userStoreName: String?) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable { },
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Image(
        imageVector = Icons.Outlined.Book,
        colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
        contentDescription = "MyStoreIcon",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(28.dp)
      )

      Text(
        text = "My Store",
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.medium_M
      )

      userStoreName?.let {
        Text(
          text = it,
          modifier = Modifier.wrapContentWidth(),
          textAlign = TextAlign.End,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_XS,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.size(10.dp))
      }
    }
  }
}

@Composable
private fun SettingsCard(navigate: (String) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable { navigate("settings") },
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Image(
        imageVector = Icons.Outlined.Settings,
        colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
        contentDescription = "SettingsIcon",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(28.dp)
      )

      Text(
        text = "Settings",
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.medium_M
      )
    }
  }
}

@Composable
private fun AptoideProductsCard(openLinkFunc: (String) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Image(
          imageVector = AppTheme.icons.AptoideIcon,
          contentDescription = "AptoideIcon",
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .fillMaxHeight()
            .size(24.dp)
        )

        Text(
          text = "Aptoide Products",
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_M
        )
      }

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable {
              openLinkFunc("https://tv.aptoide.com/")
            },
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
              imageVector = AppTheme.icons.AptoideTVIcon,
              contentDescription = "Aptoide TV",
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .wrapContentHeight()
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp))
            )

            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "Aptoide TV",
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = AppTheme.typography.medium_S,
              )
              Text(
                modifier = Modifier.wrapContentHeight(),
                text = "The best solution" +
                    "for your Set Top Box and Smart TV",
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Visible,
                maxLines = 2,
                style = AppTheme.typography.medium_XS,
                color = grey
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable {
              openLinkFunc(
                "https://aptoide-uploader.pt.aptoide.com/app"
              )
            },
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
              imageVector = AppTheme.icons.AptoideUploaderIcon,
              contentDescription = "Aptoide Uploader",
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .wrapContentHeight()
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp))
            )

            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement
                .spacedBy(4.dp, Alignment.CenterVertically)
            ) {
              Text(
                text = "Aptoide Uploader",
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = AppTheme.typography.medium_S,
              )
              Text(
                modifier = Modifier.wrapContentHeight(),
                text = "Perfect tool to get " +
                    "your favorite apps in Aptoide store",
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Visible,
                maxLines = 2,
                style = AppTheme.typography.medium_XS,
                color = grey
              )
            }
          }
        }

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

          IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = {
              openLinkFunc(
                "https://www.facebook.com/aptoide"
              )
            }
          ) {
            Image(
              imageVector = AppTheme.icons.FacebookIcon,
              contentDescription = "FacebookIcon",
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
          IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = {
              openLinkFunc(
                "https://twitter.com/Aptoide"
              )
            }
          ) {
            Image(
              imageVector = AppTheme.icons.TwitterIcon,
              contentDescription = "TwitterIcon",
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
          IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = {
              openLinkFunc(
                "https://www.instagram.com/aptoideappstore/"
              )
            }
          ) {
            Image(
              imageVector = AppTheme.icons.InstagramIcon,
              contentDescription = "InstagramIcon",
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
      }
    }
  }
}

@Composable
fun FAQsCard(openLinkFunc: (String) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable { openLinkFunc("https://pt.aptoide.com/company/faq") },
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Image(
          imageVector = Icons.Outlined.HelpOutline,
          colorFilter = ColorFilter.tint(AppTheme.colors.onBackground),
          contentDescription = "FAQIcon",
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(28.dp)
        )

        Text(
          text = "FAQs",
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_M
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Find all the answers you need!",
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.medium_XS,
          color = grey
        )
      }
    }
  }
}

@Composable
fun LogoutCard(
  logoutShouldOpenDialog: Boolean = false,
  logoutDialogOnClick: (Boolean) -> Unit = {}
) {
  if (logoutShouldOpenDialog)
    AptoideDialog(
      title = "Logout Account",
      positiveText = "confirm",
      onPositiveClicked = {
        // TODO Logout Account
        logoutDialogOnClick(false)
      },
      onDismissDialog = { logoutDialogOnClick(false) }
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

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable(onClick = { logoutDialogOnClick(true) }),
    elevation = 10.dp,
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Image(
        imageVector = Icons.Filled.Logout,
        colorFilter = ColorFilter.tint(AppTheme.colors.primary),
        contentDescription = "LogOutIcon",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(28.dp)
      )

      Text(
        text = "Log Out",
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.medium_M,
        color = AppTheme.colors.primary
      )
    }
  }
}
