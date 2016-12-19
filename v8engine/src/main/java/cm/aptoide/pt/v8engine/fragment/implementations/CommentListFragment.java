package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragment;

public class CommentListFragment extends GridRecyclerFragment {

  private static final String COMMENT_TYPE = "comment_type";
  private static final String ELEMENT_ID = "element_id";

  private CommentType commentType;
  private String elementId;

  public static Fragment newInstance(CommentType commentType, String elementId) {
    Bundle args = new Bundle();
    args.putString(ELEMENT_ID, elementId);
    args.putString(COMMENT_TYPE, commentType.name());

    CommentListFragment fragment = new CommentListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  
}
