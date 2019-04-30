package com.droidtech.novel.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droidtech.novel.R;

import java.util.ArrayList;

public class ChapterAdapter extends BaseAdapter {

    private float size;
    private int color;
    private ArrayList<Chapter> data = new ArrayList<>();

    public void addData(Chapter chapter) {
        this.data.add(chapter);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void setSize(float size) {
        this.size = size;
        notifyDataSetChanged();
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_read_activity_chapter, viewGroup, false);
            holder.mTitle = view.findViewById(R.id.chapter_title);
            holder.mContent = view.findViewById(R.id.chapter_content);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Chapter chapter = data.get(position);
        if (chapter != null && !TextUtils.isEmpty(chapter.title)) {
            holder.mTitle.setText(chapter.title);
            holder.mContent.setText(chapter.content);
        }

        if (size > 12) {
            holder.mContent.setTextSize(size);
            holder.mTitle.setTextSize(size);
        }

        if (color != 0) {
            holder.mContent.setTextColor(color);
            holder.mTitle.setTextColor(color);
        }

        return view;
    }

    static class ViewHolder {
        public TextView mTitle;
        public TextView mContent;
    }

    public static class Chapter {
        public String title;
        public String content;
    }
}
