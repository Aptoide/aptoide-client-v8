package cm.aptoide.pt.shareappsandroid;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 12-08-2016.
 */
public class HighwayTransferRecordCustomAdapter extends BaseAdapter {

  private Context context;
  private ViewHolder viewHolder;
  private View view;
  //    private Activity activity;
  private List<HighwayTransferRecordItem> listOfItems;
  private String appName;
  private String filePath;
  private MyOnClickListenerToInstall myOnClickListenerToInstall;
  private MyOnClickListenerToDelete myOnClickListenerToDelete;
  private HighwayTransferRecordView.TransferRecordListener listener;
  private String versionName;
  private boolean resend;

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
      System.out.println("INSIDE THE GET VIEW TRANSF REC ADAPTER :  RECEIVED SOMETHING");
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
      System.out.println("INSIDE THE GET VIEW TRANSF REC ADAPTER :  SENT SOMETHING");
      System.out.println(
          "o bool need resend esta a  :!::!:! " + listOfItems.get(position).isNeedReSend());
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
      System.out.println("GETVIEW TYPE :  : ::  This was a received item");
      return 0;
    } else {
      System.out.println("Get View Type :: :: : THis was a sent item");
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

  public void clearListOfItems(ArrayList<HighwayTransferRecordItem> list) {
    if (listOfItems != null && list != null) {
      listOfItems.removeAll(list);
    }
  }

  public void addTransferedItem(HighwayTransferRecordItem item) {
    listOfItems.add(item);
    notifyDataSetChanged();
  }

  public void updateItem(int positionToReSend, boolean isSent, boolean needReSend) {
    listOfItems.get(positionToReSend).setNeedReSend(needReSend);
    listOfItems.get(positionToReSend).setSent(isSent);

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

      //
      //            appName=listOfItems.get(position).getAppName();
      //            filePath=listOfItems.get(position).getFilePath();
      //            versionName=listOfItems.get(position).getVersionName();
      //
      //
      //            if(filePath.equals("Could not read the original filepath")){
      ////                tmp=createErrorDialog(appName);
      //                transferRecordView.showInstallErrorDialog(appName);
      //            }else{
      //                System.out.println("I will install the app " + appName);
      //                transferRecordView.showDialogToInstall(appName, filePath);
      ////                tmp=createDialogToInstall();
      //            }
    }
  }

  //    public Dialog createDialogToInstall(){
  //
  //        String message = String.format(activity.getResources().getString(R.string.alertInstallApp),appName);
  //
  //        AlertDialog.Builder builder;
  //        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
  //            builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
  //        }else {
  //            builder = new AlertDialog.Builder(context);
  //        }
  //        builder.setTitle(activity.getResources().getString(R.string.alert));
  //        builder.setMessage(message);
  //        builder.setPositiveButton(activity.getResources().getString(R.string.install), new DialogInterface.OnClickListener() {
  //                    public void onClick(DialogInterface dialog, int id) {
  //                        System.out.println("PERSON PRESSED INSTALL  : TESTES TESTE TESTE TESTE ");
  //
  //                        System.out.println("I am going to install  this APP hehehe :  "+appName);
  //
  //                        transferRecordView.installApp(filePath);
  //                    }
  //                })
  //                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
  //                    public void onClick(DialogInterface dialog, int id) {
  //                        // User cancelled the dialog
  //                        System.out.println("TransferREcordsCustomAdapter : Person pressed the CANCEL BUTTON !!!!!!!! ");
  //                    }
  //                });
  //        return builder.create();
  //
  //    }

  //    public Dialog createErrorDialog(String name){
  //
  //        String message=String.format(activity.getResources().getString(R.string.errorAppVersionNew), name, name);
  //
  //        AlertDialog.Builder builder;
  //        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
  //            builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
  //        }else {
  //            builder = new AlertDialog.Builder(context);
  //        }
  //
  //        builder.setMessage(message);
  //        builder.setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
  //            public void onClick(DialogInterface dialog, int id) {
  //                System.out.println("Pressed OK in the error of the app version");
  //            }
  //        });
  //
  //        AlertDialog dialog = builder.create();
  //        return dialog;
  //    }

  class MyOnClickListenerToDelete implements View.OnClickListener {
    private final int position;

    public MyOnClickListenerToDelete(int position) {
      this.position = position;
    }

    public void onClick(View v) {

      if (listener != null) {
        listener.onDeleteApp(getItem(position));
      }

      //            appName=listOfItems.get(position).getAppName();
      //            filePath=listOfItems.get(position).getFilePath();
      //
      //
      //            Dialog deleteDialog =  createDialogToDelete(v, position);
      //            deleteDialog.show();

    }
  }

  //    public Dialog createDialogToDelete(final View v, final int position){
  //        String message = String.format(activity.getResources().getString(R.string.alertDeleteApp),appName);
  //        AlertDialog.Builder builder;
  //        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
  //            builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
  //        }else {
  //            builder = new AlertDialog.Builder(activity);
  //        }
  //
  //        builder.setTitle(activity.getResources().getString(R.string.alert));
  //        builder.setMessage(message);
  //        builder.setPositiveButton(activity.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
  //                    public void onClick(DialogInterface dialog, int id) {
  //                        System.out.println("PERSON PRESSED DELETE  : TESTES TESTE TESTE TESTE DELETE DELETE DELETE DELETE DELETE DELETE DELETE DELETE");
  //
  //                        System.out.println("I am going to DELETE  this APP hehehe :  "+appName);
  //
  //                        ((HighwayTransferRecordActivity)activity).deleteAppFile(filePath);
  //
  //                        listOfItems.get(position).setDeleted(true);
  //                        System.out.println("setted the position as deleted");
  //                        notifyDataSetChanged();
  //
  //
  //                    }
  //                })
  //                .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
  //                    public void onClick(DialogInterface dialog, int id) {
  //                        // User cancelled the dialog
  //                        System.out.println("TransferREcordsCustomAdapter : Person pressed the CANCEL BUTTON !!!!!!!! ");
  //                    }
  //                });
  //        return builder.create();
  //
  //    }

  class ReSendListener implements View.OnClickListener {
    private final int position;

    public ReSendListener(int position) {
      this.position = position;
    }

    public void onClick(View v) {

      if (listener != null) {
        listener.onReSendApp(getItem(position), position);
      }
      //call a method from the activity (transferRecordActivity to send the files.
      //            String filePathToReSend = listOfItems.get(position).getFilePath();
      //            String appName = listOfItems.get(position).getAppName();
      //            String packageName = listOfItems.get(position).getPackageName();
      //            Drawable imageIcon= listOfItems.get(position).getIcon();
      //            String origin=listOfItems.get(position).getFromOutside();
      //            System.out.println("TransferRecordAdapter : here is the filePathToResend :  "+filePathToReSend);
      //            List<App> list= new ArrayList<App>();
      //            App tmpItem = new App(imageIcon, appName, packageName, filePathToReSend, origin);
      //
      //            String obbsFilePath=((HighwayTransferRecordActivity) activity).checkIfHasObb(packageName);
      //            //add obb path
      //            tmpItem.setObbsFilePath(obbsFilePath);
      //
      //
      //            list.add(tmpItem);
      //
      //            System.out.println("RE-SEND !!!!!  : : : : trying to send the list ");
      //            System.out.println("the size of the list is : "+list.size());
      //
      //            ((HighwayTransferRecordActivity)activity).sendFiles(list, position) ;

    }
  }
}
