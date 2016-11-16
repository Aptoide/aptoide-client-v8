package cm.aptoide.pt.v8engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import cm.aptoide.pt.v8engine.remoteinstall.ReceiverDevice;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by franciscoaleixo on 22/08/2016.
 */
public class ReceiverDeviceAdapter extends ArrayAdapter<cm.aptoide.pt.v8engine.remoteinstall.ReceiverDevice> {

    private Context context;
    private List<ReceiverDevice> devices;
    private int resource;
    private String appId;

    public ReceiverDeviceAdapter(Context context, int resource, List<ReceiverDevice> devices) {
        super(context, resource, devices);
        this.context = context;
        this.resource = resource;
        this.devices = devices;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReceiverDevice app = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_remote_install, parent, false);
        }
        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceNameText);
        deviceName.setText(app.getDeviceName());

        return convertView;
    }

    @Override
    public void add(ReceiverDevice device){
        boolean replaced = false;
        for(int i = 0; i<devices.size(); i++){
            ReceiverDevice dvc = devices.get(i);
            if(dvc.isSameDevice(device)){
                devices.set(i, device);
                replaced = true;
                notifyDataSetChanged();
            }
        }
        if(!replaced){
            super.add(device);
        }
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
