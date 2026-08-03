package com.aptoide.android.aptoidegames.appview

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.domain.Trust
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

/**
 * The transparency / trust panel — the product's "transparency inversion". Renders
 * the device-API trust signals (scan verdict, provenance, signer consistency).
 * Shown only when [Trust] is present, i.e. the aptoideGamesDev device path; on the
 * v7 path `app.trust` is null and nothing renders.
 */
@Composable
fun TrustPanel(trust: Trust?) {
  if (trust == null) return

  // Palette is a @Composable accessor, so colors are resolved here, not in helpers.
  val rows = mutableListOf<Pair<Color, String>>()
  trust.scanVerdict?.let { v ->
    val color = when (v.lowercase()) {
      "trusted" -> Palette.Primary
      "critical" -> Palette.Error
      else -> Palette.GreyLight
    }
    rows.add(color to stringResource(scanLabelRes(v)))
  }
  trust.provenance?.let { p ->
    provenanceLabelRes(p)?.let { res -> rows.add(Palette.GreyLight to stringResource(res)) }
  }
  trust.signerConsistency?.let { c ->
    val color = if (c.lowercase() == "different_signer") Palette.Error else Palette.Primary
    signerLabelRes(c)?.let { res -> rows.add(color to stringResource(res)) }
  }
  if (rows.isEmpty()) return

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 16.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = stringResource(R.string.appview_trust_title),
      style = AGTypography.BodyBold,
      color = Palette.White,
    )
    rows.forEach { (color, label) ->
      Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
          modifier = Modifier
            .size(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = AGTypography.Body, color = Palette.GreyLight)
      }
    }
  }
}

@StringRes
private fun scanLabelRes(verdict: String): Int = when (verdict.lowercase()) {
  "trusted" -> R.string.appview_trust_scan_trusted
  "warning" -> R.string.appview_trust_scan_warning
  "critical" -> R.string.appview_trust_scan_critical
  else -> R.string.appview_trust_scan_unknown
}

@StringRes
private fun provenanceLabelRes(provenance: String): Int? = when (provenance.lowercase()) {
  "developer_upload" -> R.string.appview_trust_provenance_developer
  "mirrored" -> R.string.appview_trust_provenance_mirrored
  else -> null
}

@StringRes
private fun signerLabelRes(consistency: String): Int? = when (consistency.lowercase()) {
  "consistent" -> R.string.appview_trust_signer_consistent
  "different_signer" -> R.string.appview_trust_signer_different
  else -> null
}
