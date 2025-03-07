import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.drawables.icons.getClose
import com.aptoide.android.aptoidegames.drawables.icons.getGames
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun ConversationHistoryBox(
  conversationInfo: ConversationInfo,
  onClick: (String) -> Unit,
  isSelected: Boolean,
  onDelete: (String) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .then(
        if (isSelected)
          Modifier.background(Palette.GreyDark)
        else
          Modifier
      )
  ) {
    Row(
      modifier = Modifier
        .height(56.dp)
        .fillMaxWidth()
        .clickable {
          onClick(conversationInfo.id)
        },
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        modifier = Modifier
          .padding(16.dp)
          .size(24.dp),
        imageVector = getGames(Palette.Black, Palette.Grey),
        contentDescription = null
      )
      Text(
        text = if (conversationInfo.title.isNullOrEmpty()) conversationInfo.firstMessage else conversationInfo.title,
        style = AGTypography.DescriptionGames,
        color = Palette.GreyLight,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier
          .weight(1f)
          .padding(vertical = 16.dp)
      )
      Icon(
        imageVector = getClose(Palette.White),
        tint = Palette.GreyLight,
        contentDescription = null,
        modifier = Modifier
          .padding(start = 8.dp, end = 8.dp)
          .clickable {
            onDelete(conversationInfo.id)
          }
          .minimumInteractiveComponentSize()
          .size(16.dp)
      )
    }
  }
}
