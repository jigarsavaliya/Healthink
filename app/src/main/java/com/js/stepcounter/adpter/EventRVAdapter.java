
package com.js.stepcounter.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.js.stepcounter.GpsAdapterCallBack;
import com.js.stepcounter.R;
import com.js.stepcounter.fragment.HistoryFragment;
import com.js.stepcounter.model.EventItem;
import com.js.stepcounter.model.GpsTrackerModel;
import com.js.stepcounter.model.HeaderModel;
import com.js.stepcounter.model.ListEvent;
import com.js.stepcounter.utils.Logger;
import com.js.stepcounter.utils.constant;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<GpsTrackerModel> gpsTrackerModelArrayList = new ArrayList<>();
    private List<ListEvent> items;
    HistoryFragment activity;
    private GpsAdapterCallBack myAdapterListener;
    Calendar rightNow;

    public EventRVAdapter(HistoryFragment historyFragment, List<ListEvent> items) {
        activity = historyFragment;
        this.items = items;
        rightNow = Calendar.getInstance();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mTvType, mTvMile, mTvDuration, mTvKcal, mTvSteps;
        AppCompatCheckBox mCbDeleteItem;
        ImageView mivmap;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tvType);
            mTvMile = itemView.findViewById(R.id.tvMile);
            mTvDuration = itemView.findViewById(R.id.tvDuration);
            mTvKcal = itemView.findViewById(R.id.tvKcal);
            mTvSteps = itemView.findViewById(R.id.tvSteps);
            mCbDeleteItem = itemView.findViewById(R.id.cbDeleteItem);
            mivmap = itemView.findViewById(R.id.ivmap);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHeaderDate;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderDate = itemView.findViewById(R.id.tvHeader);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListEvent.TYPE_HEADER: {
                View itemView = inflater.inflate(R.layout.row_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case ListEvent.TYPE_ITEM: {
                View itemView = inflater.inflate(R.layout.item_location_history, parent, false);
                return new ItemViewHolder(itemView);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        Logger.e(viewType + "viewType");
        switch (viewType) {
            case ListEvent.TYPE_HEADER: {
                HeaderModel header = (HeaderModel) items.get(position);
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

                String[] newdate = header.getDate().split(" ");

                if (rightNow.get(Calendar.DATE) == Integer.parseInt(newdate[0])) {
                    headerHolder.tvHeaderDate.setText("Today");
                } else if (rightNow.get(Calendar.DATE) - 1 == Integer.parseInt(newdate[0])) {
                    headerHolder.tvHeaderDate.setText("Yesterday");
                } else {
                    headerHolder.tvHeaderDate.setText(header.getDate() + "");
                }
                break;
            }
            case ListEvent.TYPE_ITEM: {
                EventItem event = (EventItem) items.get(position);
                ItemViewHolder itemHolder = (ItemViewHolder) holder;

                itemHolder.mTvType.setText(event.getEventModel().getAction());
                itemHolder.mTvMile.setText(event.getEventModel().getDistance() + " Mile");
                itemHolder.mTvDuration.setText(event.getEventModel().getDuration());
                itemHolder.mTvKcal.setText(event.getEventModel().getCalories() + " Kcal");
                itemHolder.mTvSteps.setText(event.getEventModel().getStep() + " Steps");
//        viewHolder.mCbDeleteItem.setChecked(false);

                if (constant.IsLocationHistoryDelete) {
                    itemHolder.mCbDeleteItem.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.mCbDeleteItem.setVisibility(View.GONE);
                }
                itemHolder.mCbDeleteItem.setChecked(false);

                itemHolder.mCbDeleteItem.setOnClickListener(view -> {
                    if (!gpsTrackerModelArrayList.contains(event.getEventModel())) {
                        gpsTrackerModelArrayList.add(event.getEventModel());
//                Logger.e("count" + gpsTrackerModelArrayList.size());
                    } else {
                        gpsTrackerModelArrayList.remove(event.getEventModel());
                    }
//            Logger.e("count" + gpsTrackerModelArrayList.size());
                    myAdapterListener.onMethodCallback(gpsTrackerModelArrayList);
                });


                Glide.with(activity).load("http://maps.google.com/maps/api/staticmap?center=" +
                        event.getEventModel().getSlatitude() +
                        "," +
                        event.getEventModel().getSlogtitude() +
                        "&zoom=15&size=200x200&sensor=false" +
                        "&markers=color:red%7Clabel:C%" +
                        event.getEventModel().getElatitude() +
                        "," +
                        event.getEventModel().getElongtitude())
                        .centerCrop()
                        .into(itemHolder.mivmap);

                break;
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }

    }

    @Override
    public int getItemViewType(int position) {
//        Logger.e(items.get(position).getType() + "type");
        return items.get(position).getType();
    }

    public void setMyAdapterListener(GpsAdapterCallBack adapterCallback) {
        this.myAdapterListener = adapterCallback;
    }

    public void updatelist(List<ListEvent> gpsTrackerModelArrayList) {
        items = gpsTrackerModelArrayList;
    }
}