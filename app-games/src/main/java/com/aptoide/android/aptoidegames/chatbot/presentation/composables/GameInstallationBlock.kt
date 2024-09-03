package com.aptoide.android.aptoidegames.chatbot.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
import coil.compose.rememberAsyncImagePainter
import com.aptoide.android.aptoidegames.appview.buildAppViewRouteBySource
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun GameInstallationBlock(appInfo: GameContext, navigateTo: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Palette.Secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = appInfo.icon),
                contentDescription = "${appInfo.name} Icon",
                modifier = Modifier
                    .size(64.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = appInfo.name,
                style = AGTypography.BodyBold,
                fontSize = 18.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            Button(onClick = {
                navigateTo(
                    buildAppViewRouteBySource(appInfo.packageName.toPackageNameParam(), false)
                )
            }) {
                Text(
                    text = "Install",
                    style = AGTypography.BodyBold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}