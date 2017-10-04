package cm.aptoide.pt.social.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by jdandrade on 04/10/2017.
 */

public class PostOverflowArrayAdapter<T> extends ArrayAdapter<String> {
  public PostOverflowArrayAdapter(@NonNull Context context, int textViewResourceId,
      @NonNull String[] objects) {
    super(context, textViewResourceId, objects);
  }

  @NonNull @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = super.getView(position, convertView, parent);
    TextView textView = (TextView) view.findViewById(android.R.id.text1);
    textView.setText("");
    return view;
  }
}
