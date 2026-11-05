package com.aptoide.android.aptoidegames.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Shape of a game's feature graphic and of everything drawn around it (border, scrim).
 * Brand-specific like [BrandPrimary]: Vanilla = rounded corners.
 */
internal val FeatureGraphicShape: Shape = RoundedCornerShape(24.dp)

/** Shape of the "Earn $X" badge drawn inside the feature graphic's corner. */
internal val RewardBadgeShape: Shape = RoundedCornerShape(8.dp)

/** Shape of the Play & Earn progress bars, same brand rule as [FeatureGraphicShape]. */
internal val ProgressBarShape: Shape = RoundedCornerShape(percent = 50)
