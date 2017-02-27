package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import java.util.ArrayList;

/**
 * Created by jdandrade on 13/02/2017.
 */
public class SyncResultAdapter extends RecyclerView.Adapter<SyncResultAdapter.ViewHolder> {
  private final SyncResultFragment.ContactItemListener mItemListener;
  private ArrayList<Contact> mContacts;

  public SyncResultAdapter(ArrayList<Contact> contacts,
      SyncResultFragment.ContactItemListener itemListener) {
    setList(contacts);
    this.mItemListener = itemListener;
  }

  public void setList(ArrayList<Contact> contacts) {
    this.mContacts = contacts;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View contactView = inflater.inflate(R.layout.addressbook_synced_contacts_item, parent, false);

    return new ViewHolder(contactView, mItemListener);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Contact contact = mContacts.get(position);

    ImageLoader.loadWithShadowCircleTransform(contact.getStore().getAvatar(), holder.mMainIcon);
    ImageLoader.loadWithShadowCircleTransform(contact.getPerson().getAvatar(),
        holder.mSecondaryIcon);
    holder.mStoreName.setText(contact.getStore().getName());
    holder.mUserName.setText(contact.getPerson().getName());
  }

  @Override public int getItemCount() {
    return mContacts.size();
  }

  private Contact getItem(int position) {
    return mContacts.get(position);
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final SyncResultFragment.ContactItemListener mItemListener;

    ImageView mMainIcon;
    ImageView mSecondaryIcon;
    TextView mStoreName;
    TextView mUserName;

    ViewHolder(View contactView, SyncResultFragment.ContactItemListener itemListener) {
      super(contactView);
      this.mItemListener = itemListener;
      mMainIcon = (ImageView) contactView.findViewById(R.id.main_icon);
      mSecondaryIcon = (ImageView) contactView.findViewById(R.id.secondary_icon);
      mStoreName = (TextView) contactView.findViewById(R.id.store_name);
      mUserName = (TextView) contactView.findViewById(R.id.user_name);
    }

    @Override public void onClick(View view) {
      int position = getAdapterPosition();
      Contact contact = getItem(position);
      mItemListener.onContactClick(contact);
    }
  }
}
