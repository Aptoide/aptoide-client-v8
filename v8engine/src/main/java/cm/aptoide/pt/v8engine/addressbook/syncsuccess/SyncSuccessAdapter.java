package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import java.util.ArrayList;

/**
 * Created by jdandrade on 13/02/2017.
 */
public class SyncSuccessAdapter extends RecyclerView.Adapter<SyncSuccessAdapter.ViewHolder> {
  private final SyncSuccessFragment.ContactItemListener mItemListener;
  private ArrayList<Contact> mContacts;

  public SyncSuccessAdapter(ArrayList<Contact> contacts,
      SyncSuccessFragment.ContactItemListener itemListener) {
    setList(contacts);
    this.mItemListener = itemListener;
  }

  public void setList(ArrayList<Contact> contacts) {
    this.mContacts = contacts;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View contactView = inflater.inflate(R.layout.timeline_follow_user, parent, false);

    return new ViewHolder(contactView, mItemListener);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {

  }

  @Override public int getItemCount() {
    return mContacts.size();
  }

  public Contact getItem(int position) {
    return mContacts.get(position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final SyncSuccessFragment.ContactItemListener mItemListener;

    public ViewHolder(View contactView, SyncSuccessFragment.ContactItemListener itemListener) {
      super(contactView);
      this.mItemListener = itemListener;
    }

    @Override public void onClick(View view) {
      int position = getAdapterPosition();
      Contact contact = getItem(position);
      mItemListener.onContactClick(contact);
    }
  }
}
