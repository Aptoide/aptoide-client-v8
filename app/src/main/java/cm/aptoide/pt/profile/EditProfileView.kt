package cm.aptoide.pt.profile

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.orangeGradient
import cm.aptoide.pt.aptoide_ui.theme.shapes
import cm.aptoide.pt.aptoide_ui.theme.textWhite
import cm.aptoide.pt.profile.data.model.UserProfile
import cm.aptoide.pt.profile.presentation.userProfileData
import cm.aptoide.pt.theme.GradientButton
import cm.aptoide.pt.toolbar.NavigationTopBar
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

const val editProfileRoute = "editProfile"
private const val IMAGE_DIR = "UserImageDir"

fun NavGraphBuilder.editProfileScreen(
  navigateBack: () -> Unit,
  showSnack: (String) -> Unit,
) = staticComposable(editProfileRoute) {
  val editProfileTitle = "My Account"
  EditProfileScreen(
    title = editProfileTitle,
    navigateBack = navigateBack,
    showSnack = showSnack
  )
}

@Composable
fun EditProfileScreen(
  title: String,
  navigateBack: () -> Unit,
  showSnack: (String) -> Unit,
) {
  val (userProfile, setUserProfile) = userProfileData(key = "editProfileUserData")
  val keyboardFocus = LocalFocusManager.current
  val context = LocalContext.current

  val storedUsername = userProfile.username
  val storedUserImageUri = userProfile.userImage
    .takeIf { it.isNotBlank() }
    ?.let { Uri.parse(it) }

  var username by remember { mutableStateOf(storedUsername) }
  var imageUri: Uri? by remember { mutableStateOf(storedUserImageUri) }
  var userImageSupport by remember { mutableStateOf(storedUserImageUri) }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent(),
    onResult = { uri: Uri? ->
      uri?.let {
        imageUri = setupGalleryImage(context, uri)
        userImageSupport = imageUri
      }
    }
  )

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
    onResult = { success ->
      if (success) imageUri = setupGalleryImage(context, userImageSupport)
    }
  )

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      if (isGranted) {
        val uri = getImageUri(context)
        userImageSupport = uri
        cameraLauncher.launch(uri)
      } else {
        showSnack("Camera Permission Denied")
      }
    }
  )

  EditProfileView(
    title = title,
    onBackPressed = { navigateBack() },

    username = username,
    imageUri = imageUri,
    usernameOnClick = {
      username = it
    },
    userImageOnClick = {
      setupUserImage(
        context = context,
        cameraPermissionLauncher = cameraPermissionLauncher,
        galleryLauncher = galleryLauncher,
        cameraLauncher = cameraLauncher,
      ) { userImageSupport = it }
    },
    onKeyboardDone = { keyboardFocus.clearFocus() },
    isSubmitEnabled = {
      username.isNotEmpty() && (
        username != storedUsername ||
          imageUri != storedUserImageUri)
    },
    submitOnClick = {
      setUserProfile(
        UserProfile(
          username = username,
          userImage = imageUri.toString(),
          joinedData = userProfile.joinedData,
          userStore = userProfile.userStore,
        )
      )

      showSnack("Profile Saved Successfully")
      navigateBack()
    }
  )
}

@Preview
@Composable
fun EditProfileView(
  title: String = "My Account",
  onBackPressed: () -> Unit = {},

  username: String = "",
  imageUri: Uri? = null,
  usernameOnClick: (String) -> Unit = {},
  userImageOnClick: () -> Unit = {},
  onKeyboardDone: () -> Unit = {},
  isSubmitEnabled: () -> Boolean = { true },
  submitOnClick: () -> Unit = {},
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    NavigationTopBar(title, onBackPressed)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      EditableUserImage(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 21.dp),
        imageUri = imageUri,
        userImageOnClick = userImageOnClick
      )

      Text(
        text = "Choose your nickname",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Visible,
        maxLines = 1,
        style = AppTheme.typography.regular_S,
        color = AppTheme.colors.onBackground,
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            color = AppTheme.colors.secondBackground,
            shape = shapes.large
          ),
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          TextField(
            value = username,
            onValueChange = usernameOnClick,
            textStyle = AppTheme.typography.regular_S,
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Words,
              imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onKeyboardDone() }),
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = 1.dp,
                color = AppTheme.colors.onBackground,
                shape = shapes.large
              )
              .background(
                color = AppTheme.colors.background,
                shape = shapes.large

              ),
            colors = TextFieldDefaults.textFieldColors(
              backgroundColor = Color.Transparent,
              placeholderColor = AppTheme.colors.greyText,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
              Text(
                text = "Type your username",
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = AppTheme.typography.regular_S,
                color = AppTheme.colors.greyText
              )
            },
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Image(
              imageVector = Icons.Outlined.Info,
              colorFilter = ColorFilter.tint(AppTheme.colors.greyText),
              contentDescription = "Info",
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
            )

            Text(
              text = "Your nickname appears when you rate, comment and share apps",
              modifier = Modifier.weight(1f),
              textAlign = TextAlign.Start,
              overflow = TextOverflow.Visible,
              maxLines = 2,
              style = AppTheme.typography.regular_XS,
              color = AppTheme.colors.greyText
            )
          }
        }
      }

      Spacer(modifier = Modifier.weight(2f))

      GradientButton(
        title = "SAVE",
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        gradient = orangeGradient,
        isEnabled = isSubmitEnabled(),
        style = AppTheme.typography.button_L,
        onClick = submitOnClick
      )
    }
  }
}

@Composable
private fun EditableUserImage(
  modifier: Modifier,
  imageUri: Uri?,
  userImageOnClick: () -> Unit,
) {
  val imageVector = rememberVectorPainter(AppTheme.icons.NoImageIcon)

  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    AptoideAsyncImage(
      data = imageUri,
      contentDescription = "My Profile Image",
      placeholder = imageVector,
      error = imageVector,
      modifier = Modifier
        .clickable(onClick = userImageOnClick)
        .size(135.dp)
        .clip(CircleShape)
        .border(
          width = 4.dp,
          color = AppTheme.colors.iconBackground,
          shape = CircleShape
        )
    )

    Box(
      modifier = Modifier
        .width(32.dp)
        .height(3.dp)
        .offset(x = 64.dp, y = (-38).dp)
        .background(brush = orangeGradient)
    )

    Box(
      modifier = Modifier
        .width(16.dp)
        .height(8.dp)
        .offset(x = 85.dp, y = (-25).dp)
        .background(brush = orangeGradient)
    )

    Box(
      modifier = Modifier
        .width(11.dp)
        .height(8.dp)
        .offset(x = (-100).dp, y = (-10).dp)
        .background(brush = orangeGradient)
    )

    Box(
      modifier = Modifier
        .width(48.dp)
        .height(7.dp)
        .offset(x = (-78).dp, y = (15).dp)
        .background(brush = orangeGradient)
    )

    IconButton(
      onClick = userImageOnClick,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .size(30.dp)
        .offset(x = 45.dp)
        .background(
          color = AppTheme.colors.primary,
          shape = CircleShape
        ),
    ) {
      Image(
        imageVector = Icons.Outlined.PhotoCamera,
        colorFilter = ColorFilter.tint(textWhite),
        contentDescription = "My Profile Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

private fun setupUserImage(
  context: Context,
  cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
  galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
  cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
  setImageUri: (Uri) -> Unit,
) {
  val pictureDialog = AlertDialog.Builder(context).setTitle("Select Action")
  val pictureDialogItems = arrayOf(
    "Select photo from Gallery",
    "Capture photo from Camera"
  )

  pictureDialog.setItems(pictureDialogItems) { _, which ->
    when (which) {
      0 -> galleryLauncher.launch("image/*")

      1 -> takePhotoFromCamera(
        context = context,
        permissionLauncher = cameraPermissionLauncher,
        activityLauncher = cameraLauncher,
        setImageUri = setImageUri,
      )
    }
  }
  pictureDialog.show()
}

private fun takePhotoFromCamera(
  context: Context,
  permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
  activityLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
  setImageUri: (Uri) -> Unit,
) {
  val permission = Manifest.permission.CAMERA

  if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
    val uri = getImageUri(context)
    setImageUri(uri)
    activityLauncher.launch(uri)
  } else {
    permissionLauncher.launch(permission)
  }
}

fun setupGalleryImage(
  context: Context,
  uri: Uri?,
): Uri? {
  uri?.let {
    try {
      return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        val bitmap = MediaStore.Images.Media
          .getBitmap(context.contentResolver, it)
        saveImageToInternalStorage(context, bitmap)
      } else {
        val source = ImageDecoder.createSource(context.contentResolver, it)
        val bitmap = ImageDecoder.decodeBitmap(source)
        saveImageToInternalStorage(context, bitmap)
      }
    } catch (e: IOException) {
      Timber.e("Failed to load the image to gallery")
      e.printStackTrace()
    }
  }
  return uri
}

fun saveImageToInternalStorage(
  context: Context,
  bitmap: Bitmap,
): Uri {
  val wrapper = ContextWrapper(context.applicationContext)
  var file = wrapper.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
  file = File(file, "${UUID.randomUUID()}.jpg")
  try {
    val stream: OutputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
  } catch (e: IOException) {
    e.printStackTrace()
  }
  return Uri.parse(file.absolutePath)
}

fun getImageUri(context: Context): Uri {
  // 1
  val directory = File(context.cacheDir, "images")
  directory.mkdirs()
  // 2
  val file = File.createTempFile(
    "selected_image_",
    ".jpg",
    directory
  )
  // 3
  val authority = context.packageName + ".fileprovider"
  // 4
  return getUriForFile(
    context,
    authority,
    file,
  )
}
