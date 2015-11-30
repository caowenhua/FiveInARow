package me.fiveinarow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.fiveinarow.R;

/**
 * Created by caowenhua on 2015/11/30.
 */
public class DeviceAdapter extends BaseAdapter {

    private List<String> list;
    private Context context;

    public DeviceAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.tv, null);
        }
        ((TextView)(convertView)).setText(list.get(position));
        return convertView;
    }
}
