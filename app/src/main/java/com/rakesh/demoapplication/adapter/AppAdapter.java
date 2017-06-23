package com.rakesh.demoapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.pojo.AppList;

import java.util.Date;
import java.util.List;

public class AppAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<AppList> listStorage;
    private Context mContext;


    public AppAdapter(Context context, List<AppList> customizedListView) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        mContext = context;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.installed_app_listrow, parent, false);
            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.list_app_name);
            listViewHolder.textdurationListView = (TextView) convertView
                    .findViewById(R.id.list_app_duration);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.app_icon);
            listViewHolder.more = (ImageView) convertView.findViewById(R.id.more);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.textInListView.setText(listStorage.get(position).getName());
        listViewHolder.textdurationListView.setText(listStorage.get(position).getDuration());
        listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());
        listViewHolder.more.setTag(position);
        listViewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup(v);
            }
        });
        return convertView;
    }

    private void showpopup(View v) {
        final int position = (int) v.getTag();
        final AppList appinfo = listStorage.get(position);
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.getMenu().getItem(0).setTitle("Install " + DateTimeUtils.getTimeAgo(mContext,
                new Date(appinfo.getFirstinstall())));
        popup.getMenu().getItem(1).setTitle("Update " + DateTimeUtils.getTimeAgo(mContext,
                new Date(appinfo.getUpdatetime())));

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.three) {
                    Intent LaunchIntent = mContext.getPackageManager().
                            getLaunchIntentForPackage(appinfo.getPackagename());
                    mContext.startActivity(LaunchIntent);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private static class ViewHolder {
        TextView textInListView;
        TextView textdurationListView;
        ImageView imageInListView;
        ImageView more;
    }
}
