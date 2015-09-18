package com.litmon.app.androidappmanager.adapter;

/**
 * Created by fukuo on 2015/09/14.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.litmon.app.androidappmanager.R;
import com.litmon.app.androidappmanager.model.AppData;

import java.util.List;

public class AppDataAdapter extends ArrayAdapter<AppData> {

    public AppDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    public AppDataAdapter(Context context, int resource, List<AppData> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_item_app_data, null);
        }

        AppData data = getItem(position);


        View tagContainer = convertView.findViewById(R.id.tagContainer);

        TextView appName = (TextView) convertView.findViewById(R.id.appName);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.appIcon);

        if (data.tag != null) {
            tagContainer.setBackgroundColor(data.tag.color.getColorCode());
        } else {
            tagContainer.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        try {
            appName.setText(data.getAppName(getContext()));
            description.setText(data.description);
            appIcon.setImageDrawable(data.getIcon(getContext()));

        }catch(Exception e){
            remove(data);
            data.delete();
        }

        return convertView;
    }
}