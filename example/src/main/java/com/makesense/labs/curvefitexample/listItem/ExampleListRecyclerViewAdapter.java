package com.makesense.labs.curvefitexample.listItem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makesense.labs.curvefitexample.R;

import java.util.List;

public class ExampleListRecyclerViewAdapter extends RecyclerView.Adapter<ExampleListRecyclerViewAdapter.ActivityListViewHolder> {

    private final Context context;
    private final List<ActivityItem> activityItemList;
    private final RecyclerViewClickListener recyclerViewClickListener;


    public interface RecyclerViewClickListener {
        void onActivityItemClick(int position);
    }

    public ExampleListRecyclerViewAdapter(Context context,
                                   List<ActivityItem> list,
                                   RecyclerViewClickListener listener) {
        this.context = context;
        this.activityItemList = list;
        this.recyclerViewClickListener = listener;
    }

    @NonNull
    @Override
    public ActivityListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_list_activity_item, viewGroup, false);
        return new ActivityListViewHolder(view, recyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListViewHolder viewHolder, int index) {
        viewHolder.activityNameTextView.setText(getActivityItemList().get(index).getActivityName());
    }

    @Override
    public int getItemCount() {
        return activityItemList.size();
    }

    static class ActivityListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RecyclerViewClickListener listener;
        TextView activityNameTextView;

        ActivityListViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            activityNameTextView = itemView.findViewById(R.id.activityNameTextView);
        }

        @Override
        public void onClick(View v) {
            listener.onActivityItemClick(getAdapterPosition());
        }
    }

    private List<ActivityItem> getActivityItemList() {
        return activityItemList;
    }
}
