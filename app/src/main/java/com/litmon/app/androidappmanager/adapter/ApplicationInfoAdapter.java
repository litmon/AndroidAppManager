package com.litmon.app.androidappmanager.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.litmon.app.androidappmanager.R;

import java.text.DateFormat;

/**
 * Created by fukuo on 2015/09/07.
 */
public class ApplicationInfoAdapter extends ArrayAdapter<ApplicationInfo> {

    PackageManager pm;

    public ApplicationInfoAdapter(Context context){
        super(context, 0);
        pm = context.getPackageManager();
    }

    public ApplicationInfoAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApplicationInfo item = getItem(position);

        if(convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_item_app_info, null);
        }

        TextView packageName = (TextView)convertView.findViewById(R.id.packageName);
        TextView appName = (TextView)convertView.findViewById(R.id.appName);
        TextView firstInstallTime = (TextView)convertView.findViewById(R.id.firstInstallTime);


        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);

        packageName.setText(item.packageName);
        appName.setText(pm.getApplicationLabel(item));
        icon.setImageDrawable(pm.getApplicationIcon(item));
        try {
            firstInstallTime.setText(android.text.format.DateFormat.format("yyyy/MM/dd", pm.getPackageInfo(item.packageName, PackageManager.GET_META_DATA).firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
