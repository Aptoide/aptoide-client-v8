package cm.aptoide.pt.v8engine.view.addressbook;

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
  private ArrayList<Contact> mContacts;
  private Context mContext;

  public SyncResultAdapter(ArrayList<Contact> contacts, Context context) {
    setList(contacts);
    this.mContext = context;
  }

  public void setList(ArrayList<Contact> contacts) {
    this.mContacts = contacts;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View contactView = inflater.inflate(R.layout.addressbook_synced_contacts_item, parent, false);

    return new ViewHolder(contactView);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Contact contact = mContacts.get(position);

    if (contact.getStore() != null) {
      ImageLoader.with(mContext)
          .loadWithShadowCircleTransform(contact.getStore().getAvatar(), holder.mMainIcon);
      holder.mStoreName.setText(contact.getStore().getName());
    }

    if (contact.getPerson() != null) {
      ImageLoader.with(mContext)
          .loadWithShadowCircleTransform(contact.getPerson().getAvatar(), holder.mSecondaryIcon);
      holder.mUserName.setText(contact.getPerson().getName());
    }
  }

  @Override public int getItemCount() {
    return mContacts.size();
  }

  private Contact getItem(int position) {
    return mContacts.get(position);
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    ImageView mMainIcon;
    ImageView mSecondaryIcon;
    TextView mStoreName;
    TextView mUserName;

    ViewHolder(View contactView) {
      super(contactView);
      mMainIcon = (ImageView) contactView.findViewById(R.id.main_icon);
      mSecondaryIcon = (ImageView) contactView.findViewById(R.id.secondary_icon);
      mStoreName = (TextView) contactView.findViewById(R.id.store_name);
      mUserName = (TextView) contactView.findViewById(R.id.user_name);
    }
  }
}
