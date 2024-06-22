package com.aptoide.android.aptoidegames.feature_payments

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.getAppName
import cm.aptoide.pt.extensions.runPreviewable
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.emptyProductInfoData
import com.appcoins.payments.manager.presentation.rememberProductInfo
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@PreviewDark
@Composable
private fun PurchaseInfoRowPortraitPreview() {
  AptoideTheme {
    PurchaseInfoRow(
      buyingPackage = "any"
    )
  }
}

@PreviewLandscapeDark
@Composable
private fun PurchaseInfoRowLandscapePreview() {
  AptoideTheme {
    PurchaseInfoRow(
      buyingPackage = "any"
    )
  }
}

@Composable
fun currentProductInfo(): ProductInfoData? = runPreviewable(
  preview = { emptyProductInfoData },
  real = { rememberProductInfo() }
)

@Composable
fun PurchaseInfoRow(
  buyingPackage: String,
  modifier: Modifier = Modifier,
) {
  val productInfo = currentProductInfo()

  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> {
      PurchaseInfoRowLandscape(
        modifier = modifier,
        buyingPackage = buyingPackage,
        productName = productInfo?.title,
        price = productInfo?.run { "$priceValue $priceCurrency" },
      )
    }

    else -> {
      PurchaseInfoRowPortrait(
        modifier = modifier,
        buyingPackage = buyingPackage,
        productName = productInfo?.title,
        price = productInfo?.run { "$priceValue $priceCurrency" },
      )
    }
  }
}

@Composable
private fun PurchaseInfoRowPortrait(
  buyingPackage: String,
  productName: String?,
  price: String?,
  modifier: Modifier = Modifier,
) {
  val localContext = LocalContext.current
  val appIcon = localContext.getAppIconDrawable(buyingPackage)
  val appName = localContext.getAppName(buyingPackage)

  Row(
    modifier = modifier
      .clearAndSetSemantics { contentDescription = "$appName $productName $price" },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AppImage(appIcon = appIcon)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Row(modifier = Modifier.padding(bottom = 8.dp)) {
        AppNameText(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          appName = appName
        )
        price?.let {
          PriceText(
            modifier = Modifier.align(Alignment.Top),
            price = price
          )
        } ?: PurchaseSkeleton(modifier = Modifier.align(Alignment.Top))
      }
      productName?.let {
        ProductNameText(productName = it)
      } ?: PurchaseSkeleton()
    }
  }
}

@Composable
private fun PurchaseInfoRowLandscape(
  buyingPackage: String,
  productName: String?,
  price: String?,
  modifier: Modifier = Modifier,
) {
  val localContext = LocalContext.current
  val appIcon = localContext.getAppIconDrawable(buyingPackage)
  val appName = localContext.getAppName(buyingPackage)

  Row(
    modifier = modifier
      .clearAndSetSemantics { contentDescription = "$appName $productName $price" },
    verticalAlignment = Alignment.CenterVertically
  ) {
    AppImage(appIcon = appIcon)
    Column(
      modifier = Modifier.padding(start = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      AppNameText(appName = appName)
      productName?.let {
        ProductNameText(productName = it)
      } ?: PurchaseSkeleton()
      price?.let {
        PriceText(modifier = Modifier.padding(top = 8.dp), price = price)
      } ?: PurchaseSkeleton(modifier = Modifier.padding(top = 8.dp))
    }
  }
}

@Composable
private fun AppImage(
  appIcon: Drawable?,
  modifier: Modifier = Modifier,
) {
  AptoideAsyncImage(
    modifier = modifier.size(64.dp),
    data = appIcon,
    contentDescription = null,
  )
}

@Composable
private fun AppNameText(
  appName: String,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier,
    text = appName,
    maxLines = 1,
    style = AGTypography.DescriptionGames,
    overflow = TextOverflow.Ellipsis,
    color = Palette.Black
  )
}

@Composable
private fun ProductNameText(
  productName: String,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier,
    text = productName,
    maxLines = 1,
    style = AGTypography.Body,
    overflow = TextOverflow.Ellipsis,
    color = Palette.Black
  )
}

@Composable
private fun PriceText(
  price: String,
  modifier: Modifier = Modifier,
) {
  Text(
    modifier = modifier,
    text = price,
    style = AGTypography.InputsS,
    overflow = TextOverflow.Ellipsis,
    color = Palette.Black,
    maxLines = 1,
  )
}

@Composable
fun PurchaseSkeleton(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .size(width = 64.dp, height = 16.dp)
      .background(Palette.GreyLight)
  )
}
