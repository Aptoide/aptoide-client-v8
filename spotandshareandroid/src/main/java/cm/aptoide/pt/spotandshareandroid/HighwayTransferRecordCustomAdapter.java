package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

/**
 * Created by filipegoncalves on 12-08-2016.
 */
public class HighwayTransferRecordCustomAdapter extends BaseAdapter {

  private Context context;
  private ViewHolder viewHolder;
  private List<HighwayTransferRecordItem> listOfItems;
  private MyOnClickListenerToInstall myOnClickListenerToInstall;
  private MyOnClickListenerToDelete myOnClickListenerToDelete;
  private HighwayTransferRecordView.TransferRecordListener listener;

  public HighwayTransferRecordCustomAdapter(Context context,
      List<HighwayTransferRecordItem> listOfItems) {
    this.context = context;
    this.listOfItems = listOfItems;
  }

  public void setListener(HighwayTransferRecordView.TransferRecordListener listener) {
    this.listener = listener;
  }

  @Override public int getCount() {
    return listOfItems.size();
  }

  @Override public HighwayTransferRecordItem getItem(int position) {

    return listOfItems.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    viewHolder = null;
    if (convertView == null) {

      viewHolder = new ViewHolder();
      int type = getItemViewType(position);
      LayoutInflater layoutInflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      if (type == 0) {
        convertView =
            layoutInflater.inflate(R.layout.highway_transf_record_recv_row, parent, false);
        viewHolder.transfRecRecvRowIcons =
            (RelativeLayout) convertView.findViewById(R.id.transfRecRecvRowIcons);
        viewHolder.transfRecRecvRowDeletedMessage =
            (LinearLayout) convertView.findViewById(R.id.transfRecRecvRowDeletedMessage);
        viewHolder.deleteFile = (Button) convertView.findViewById(R.id.transfRecRowDelete);
        viewHolder.installButton = (Button) convertView.findViewById(R.id.transfRecRowInstall);
      } else {//type=1 - enviado
        convertView =
            layoutInflater.inflate(R.layout.highway_transf_record_sent_row, parent, false);

        viewHolder.reSendButton = (Button) convertView.findViewById(R.id.transfRecReSendButton);
      }

      viewHolder.appImageIcon = (ImageView) convertView.findViewById(R.id.transfRecRowImage);
      viewHolder.appNameLabel = (TextView) convertView.findViewById(R.id.transfRecRowText);
      viewHolder.appVersionLabel = (TextView) convertView.findViewById(R.id.transfRecRowAppVersion);
      viewHolder.senderInfo = (TextView) convertView.findViewById(R.id.apkOrigin);
      viewHolder.rowImageLayout =
          (LinearLayout) convertView.findViewById(R.id.transfRecRowImageLayout);
      viewHolder.appInfoLayout = (RelativeLayout) convertView.findViewById(R.id.appInfoLayout);

      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    if (listOfItems.get(position).isReceived()) {
      viewHolder.senderInfo.setText(R.string.youReceived);

      myOnClickListenerToInstall = new MyOnClickListenerToInstall(position);
      viewHolder.installButton.setOnClickListener(myOnClickListenerToInstall);

      myOnClickListenerToDelete = new MyOnClickListenerToDelete(position);
      viewHolder.deleteFile.setOnClickListener(myOnClickListenerToDelete);

      if (listOfItems.get(position).isDeleted()) {
        viewHolder.transfRecRecvRowIcons.setVisibility(View.GONE);
        viewHolder.transfRecRecvRowDeletedMessage.setVisibility(View.VISIBLE);
      } else {
        viewHolder.transfRecRecvRowIcons.setVisibility(View.VISIBLE);
        viewHolder.transfRecRecvRowDeletedMessage.setVisibility(View.GONE);
      }
    } else {

      if (listOfItems.get(position).isNeedReSend()) {
        viewHolder.reSendButton.setVisibility(View.VISIBLE);
        ReSendListener reSendListener = new ReSendListener(position);
        viewHolder.reSendButton.setOnClickListener(reSendListener);
        if (listOfItems.get(position).getFromOutside().equals("outside")) {
          viewHolder.senderInfo.setText(R.string.reSendOutside);
        } else {
          viewHolder.senderInfo.setText(R.string.reSendError);
        }
        viewHolder.senderInfo.setTextColor(context.getResources().getColor(R.color.errorRed));
      } else {
        viewHolder.reSendButton.setVisibility(View.GONE);
        viewHolder.senderInfo.setTextColor(context.getResources().getColor(R.color.grey));
        if (listOfItems.get(position).isSent()) {
          viewHolder.senderInfo.setText(R.string.youSent);
        } else {
          viewHolder.senderInfo.setText(R.string.youAreSending);
        }
      }
    }
    viewHolder.appNameLabel.setText(listOfItems.get(position).getAppName());
    viewHolder.appImageIcon.setImageDrawable(listOfItems.get(position).getIcon());
    viewHolder.appVersionLabel.setText(listOfItems.get(position).getVersionName());

    //        convertView.setTag(viewHolder);

    return convertView;
  }

  @Override public int getItemViewType(int position) {

    if (listOfItems.get(position).isReceived()) {
      return 0;
    } else {
      return 1;
    }
  }

  @Override public int getViewTypeCount() {
    return 2;
  }

  public void clearListOfItems() {
    if (listOfItems != null) {
      listOfItems.clear();
    }
  }

  public void clearListOfItems(List<HighwayTransferRecordItem> list) {
    if (listOfItems != null && list != null) {
      listOfItems.removeAll(list);
    }
  }

  public void addTransferedItem(HighwayTransferRecordItem item) {
    listOfItems.add(item);
    notifyDataSetChanged();
  }

  public void updateItem(int positionToUpdate, boolean isSent, boolean needReSend) {
    if (positionToUpdate < listOfItems.size()) {
      listOfItems.get(positionToUpdate).setNeedReSend(needReSend);
      listOfItems.get(positionToUpdate).setSent(isSent);
    }
  }

  public static class ViewHolder {
    TextView senderInfo;
    ImageView appImageIcon;
    TextView appNameLabel;
    TextView appVersionLabel;
    Button installButton;
    LinearLayout rowImageLayout;//layout c a
    RelativeLayout appInfoLayout;//layout com o nome da app e versao
    Button deleteFile;
    Button reSendButton;
    RelativeLayout transfRecRecvRowIcons;
    LinearLayout transfRecRecvRowDeletedMessage;
  }

  class MyOnClickListenerToInstall implements View.OnClickListener {
    private final int position;

    public MyOnClickListenerToInstall(int position) {
      this.position = position;
    }

    public void onClick(View v) {

      if (listener != null) {
        listener.onInstallApp(getItem(position));
      }
    }
  }

  class MyOnClickListenerToDelete implements View.OnClickListener {
    private final int position;

    public MyOnClickListenerToDelete(int position) {
      this.position = position;
    }

    public void onClick(View v) {

      if (listener != null) {
        listener.onDeleteApp(getItem(position));
      }
    }
  }

  class ReSendListener implements View.OnClickListener {
    private final int position;

    public ReSendListener(int position) {
      this.position = position;
    }

    public void onClick(View v) {

      if (listener != null) {
        listener.onReSendApp(getItem(position), position);
      }
    }
  }
}
