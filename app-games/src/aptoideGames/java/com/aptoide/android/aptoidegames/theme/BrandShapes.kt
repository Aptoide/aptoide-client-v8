package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * Shape of a game's feature graphic and of everything drawn around it (border, scrim).
 * Brand-specific like [BrandPrimary]: Aptoide Games = square corners.
 */
internal val FeatureGraphicShape: Shape = RectangleShape

/** Shape of the "Earn $X" badge drawn inside the feature graphic's corner. */
internal val RewardBadgeShape: Shape = RectangleShape

/** Shape of the Play & Earn progress bars, same brand rule as [FeatureGraphicShape]. */
internal val ProgressBarShape: Shape = RectangleShape
