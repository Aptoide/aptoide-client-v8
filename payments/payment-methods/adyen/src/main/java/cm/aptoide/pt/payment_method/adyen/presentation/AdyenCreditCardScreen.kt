package cm.aptoide.pt.payment_method.adyen.presentation

import android.view.LayoutInflater
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.payment_method.adyen.R
import cm.aptoide.pt.payment_method.adyen.credit_card.CreditCardPaymentMethod
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.adyen.checkout.card.CardView
import kotlin.random.Random

@PreviewAll
@Composable
fun AdyenCreditCardScreenPreview() {
  Column(
    modifier = Modifier.background(color = Color.White)
  ) {
    AdyenCreditCardGameInfo(
      imageUrl = "",
      gameName = "Lords Mobile",
      price = "76.62 USD",
      gems = "5000 gems",
    )
  }
}

@PreviewAll
@Composable
fun AdyenCreditCardButtonsPreview() {
  val onBuyClickEnabled = Random.nextBoolean()
  Column(
    modifier = Modifier.background(color = Color.White)
  ) {
    AdyenPaymentButtons(
      onBuyClick = { },
      onOtherPaymentMethodsClick = { },
      onBuyClickEnabled = onBuyClickEnabled,
    )
  }
}

@Composable
fun BuildAdyenCreditCardScreen(
  paymentMethod: CreditCardPaymentMethod,
) {
  val viewModel = hiltViewModel<AdyenCreditCardViewModel>()
  val onBuyClickEnabled by rememberSaveable { mutableStateOf(false) }

  AdyenCreditCardScreen(
    imageUrl = paymentMethod.productInfo.priceValue,
    gameName = paymentMethod.productInfo.title,
    price = paymentMethod.productInfo.priceValue,
    gems = paymentMethod.productInfo.sku,
    onBuyClickEnabled = onBuyClickEnabled,
    onBuyClick = { },
    onOtherPaymentMethodsClick = { },
  )
}

@Composable
private fun AdyenCreditCardScreen(
  imageUrl: String,
  gameName: String,
  price: String,
  gems: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
) {
  Column {
    AdyenCreditCardGameInfo(
      imageUrl = imageUrl,
      gameName = gameName,
      price = price,
      gems = gems
    )
    AdyenCreditCardView()
    AdyenPaymentButtons(
      onBuyClickEnabled = onBuyClickEnabled,
      onBuyClick = onBuyClick,
      onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
    )
  }
}

@Composable
fun AdyenPaymentButtons(
  modifier: Modifier = Modifier,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
) {
  Column(modifier = modifier) {
    OutlinedButton(
      enabled = onBuyClickEnabled,
      onClick = onBuyClick,
      modifier = Modifier.fillMaxWidth(),
      border = BorderStroke(0.dp, Color.Transparent),
      shape = RoundedCornerShape(50),
      colors = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFFFE6446),
        disabledBackgroundColor = Color(0xFFD2D2D2)
      ),
    ) {
      Text(
        text = "Buy",
        color = if (onBuyClickEnabled) Color.White else Color(0xFF939393)
      )
    }
    TextButton(
      onClick = onOtherPaymentMethodsClick,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "Other Payment Methods",
        color = Color(0xFFFE6446)
      )
    }
  }
}

@Composable
fun AdyenCreditCardGameInfo(
  modifier: Modifier = Modifier,
  imageUrl: String,
  gameName: String,
  price: String,
  gems: String,
) {
  val context = LocalContext.current
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(600)
        .transformations(RoundedCornersTransformation(16.dp.value))
        .build(),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .width(64.dp)
        .height(64.dp)
        .clip(RoundedCornerShape(16.dp)),
    )
    Column {
      Row(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = gameName,
          fontWeight = FontWeight(500),
          fontSize = 14.sp,
          lineHeight = 18.sp,
          color = Color(0xFF272727)
        )
        Text(
          text = price,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.End,
          fontWeight = FontWeight(500),
          fontSize = 14.sp,
          lineHeight = 18.sp,
          color = Color(0xFF272727)
        )
      }
      Text(
        text = gems,
        textAlign = TextAlign.End,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = Color(0xFF818181)
      )
    }
  }
}

@Composable
fun AdyenCreditCardView() {
  AndroidView(
    factory = { context ->
      val cardView = LayoutInflater.from(context)
        .inflate(R.layout.view_adyen_credit_card, null, false) as CardView

      // do whatever you want...
      cardView // return the view
    },
    update = { view ->
      // Update the view
    }
  )
}