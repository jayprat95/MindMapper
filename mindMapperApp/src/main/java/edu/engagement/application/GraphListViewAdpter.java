package edu.engagement.application;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.engagement.application.utils.GraphAnnotation;

/**
 * Created by IvenRee on 10/12/15.
 */
public class GraphListViewAdpter extends BaseAdapter {

    List<GraphAnnotation> mAnnotations;

    public GraphListViewAdpter(List<GraphAnnotation> annotations){
        this.mAnnotations = annotations;
    }
    @Override
    public int getCount() {
        return mAnnotations.size();
    }

    @Override
    public Object getItem(int position) {
        return mAnnotations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent,false);
            holder = new ViewHolder();
            Log.v("GraphListView","textview init");
            holder.annotationNumber = (TextView) convertView.findViewById(R.id.numberLabel);
            holder.annotationTime = (TextView) convertView.findViewById(R.id.timeLabel);
            holder.annotation = (TextView) convertView.findViewById(R.id.annotationLabel);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        GraphAnnotation annotation = mAnnotations.get(position);

        holder.annotationNumber.setText(annotation.getAnnotationNumber());
        holder.annotationTime.setText(annotation.getTime());
        holder.annotation.setText((annotation.getAnnotation()));

        return convertView;
    }


    private static class ViewHolder{
        TextView annotationNumber;
        TextView annotationTime;
        TextView annotation;
    }
}
