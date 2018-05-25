package com.forestsoftware.pos;

/**
 * Created by HP-PC on 3/1/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forestsoftware.pos.model.Content;

import java.util.ArrayList;
import java.util.List;


public class ContentAdapter extends BaseAdapter {

    private Content content;
    private List<Content> contentList;
    Context context;
    String filterContactName;

    public ContentAdapter(Context context, Content content) {

        super();
        this.context = context;
        this.content = content;
        contentList = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Content getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
//        return Long.parseLong(this.getItem(position));
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.content_item_custom, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.t1 = (TextView) convertView.findViewById(R.id.c1);
            viewHolder.t2 = (TextView) convertView.findViewById(R.id.c2);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.t1.setText(this.contentList.get(position).toString());
        viewHolder.t2.setText(this.contentList.get(position).toString());

        return convertView;
    }


    public static class ViewHolder {
        TextView t1, t2;
    }

}