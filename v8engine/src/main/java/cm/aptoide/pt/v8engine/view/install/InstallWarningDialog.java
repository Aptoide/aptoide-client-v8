/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/08/2016.
 */

package cm.aptoide.pt.v8engine.view.install;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import lombok.Getter;

// FIXME: 27/2/2017 convert this into a class that extends "BaseDialog"
public class InstallWarningDialog {

  private final boolean trustedVersionAvailable;
  private final Malware.Rank rank;
  @Getter private AlertDialog dialog;
  private Button trustedAppButton;
  private Button proceedButton;

  @SuppressLint("InflateParams")
  public InstallWarningDialog(Malware.Rank rank, boolean trustedVersionAvailable, Context ctx,
      View.OnClickListener installHandler, View.OnClickListener searchTrustedHandler) {

    this.trustedVersionAvailable = trustedVersionAvailable;
    this.rank = rank;

    View contentView = LayoutInflater.from(ctx).inflate(R.layout.dialog_install_warning, null);
    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    builder.setView(contentView);

    proceedButton = (Button) contentView.findViewById(R.id.dialog_install_warning_proceed_button);
    proceedButton.setOnClickListener(new ViewOnClickListenerComposite(installHandler, onDestroy()));

    trustedAppButton =
        (Button) contentView.findViewById(R.id.dialog_install_warning_trusted_app_button);
    trustedAppButton.setOnClickListener(
        new ViewOnClickListenerComposite(searchTrustedHandler, onDestroy()));

    setRank(contentView);
    setTextBadges(contentView);
    setTrustedAppButton(contentView);

    dialog = builder.create();
  }

  public View.OnClickListener onDestroy() {
    return v -> {
      proceedButton.setOnClickListener(null);
      trustedAppButton.setOnClickListener(null);
      proceedButton = null;
      trustedAppButton = null;
      dialog.dismiss();
      dialog = null;
    };
  }

  private void setRank(View contentView) {
    TextView badge = (TextView) contentView.findViewById(R.id.dialog_install_warning_rank_text);
    if (rank == null || rank == Malware.Rank.UNKNOWN) {
      badge.setText(R.string.unknown);
      badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_badge_unknown, 0, 0, 0);
    } else if (rank == Malware.Rank.WARNING) {
      badge.setText(R.string.warning);
      badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_badge_warning, 0, 0, 0);
    } else if (rank == Malware.Rank.CRITICAL) {
      badge.setText(R.string.critical);
      badge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_badge_critical, 0, 0, 0);
    }
  }

  private void setTextBadges(View contentView) {
    // We need a placeholder for the span image in order to avoid it from disappearing in case
    // it is the last character in the line. It happens in when device orientation changes.
    final String placeholder = "[placeholder]";
    final String stringTextTemp = contentView.getContext()
        .getString(R.string.dialog_install_warning_credibility_text, placeholder);
    final String stringText =
        stringTextTemp.replaceFirst("Aptoide", Application.getConfiguration().getMarketName());
    final SpannableString text = new SpannableString(stringText);

    final int placeholderIndex = stringText.indexOf(placeholder);
    final ImageSpan trustedBadge =
        new ImageSpan(contentView.getContext(), R.drawable.ic_badge_trusted);

    text.setSpan(trustedBadge, placeholderIndex, placeholderIndex + placeholder.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ((TextView) contentView.findViewById(R.id.dialog_install_warning_credibility_text)).setText(
        text);
  }

  public void setTrustedAppButton(View contentView) {
    final String topString;
    final String bottonString;
    if (trustedVersionAvailable) {
      topString = contentView.getContext()
          .getString(R.string.dialog_install_warning_get_trusted_version_button);
      bottonString = contentView.getContext()
          .getString(R.string.dialog_install_warning_trusted_version_button);
    } else {
      topString = contentView.getContext()
          .getString(R.string.dialog_install_warning_search_for_trusted_app_button);
      bottonString =
          contentView.getContext().getString(R.string.dialog_install_warning_trusted_app_button);
    }
    final int topStringLength = topString.length();
    final int bottonStringLength = bottonString.length();

    final Spannable span = new SpannableString(topString + "\n" + bottonString);
    span.setSpan(new StyleSpan(Typeface.BOLD), topStringLength,
        (topStringLength + bottonStringLength + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    trustedAppButton.setText(span);
  }

  private static class ViewOnClickListenerComposite implements View.OnClickListener {

    private final View.OnClickListener first;
    private final View.OnClickListener second;

    public ViewOnClickListenerComposite(View.OnClickListener first, View.OnClickListener second) {
      this.first = first;
      this.second = second;
    }

    @Override public void onClick(View v) {
      first.onClick(v);
      second.onClick(v);
    }
  }
}
