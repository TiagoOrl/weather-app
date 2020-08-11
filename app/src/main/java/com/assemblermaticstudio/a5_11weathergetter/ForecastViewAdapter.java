package com.assemblermaticstudio.a5_11weathergetter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class ForecastViewAdapter extends RecyclerView.Adapter<ForecastViewAdapter.ForecastViewHolder> {

    private ArrayList<ForecastDayItem> mForecastDayList;

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hour0, tv_hour1, tv_hour2, tv_hour3, tv_hour4, tv_hour5, tv_hour6, tv_hour7, tv_day;

        public ForecastViewHolder(View itemView){
            super(itemView);
            tv_hour0 = itemView.findViewById(R.id.tv_weather);
            tv_hour1 = itemView.findViewById(R.id.tv_hour1);
            tv_hour2 = itemView.findViewById(R.id.tv_hour2);
            tv_hour3 = itemView.findViewById(R.id.tv_hour3);
            tv_hour4 = itemView.findViewById(R.id.tv_hour4);
            tv_hour5 = itemView.findViewById(R.id.tv_hour5);
            tv_hour6 = itemView.findViewById(R.id.tv_hour6);
            tv_hour7 = itemView.findViewById(R.id.tv_hour7);
            tv_day = itemView.findViewById(R.id.tv_day);
        }
    }

    public ForecastViewAdapter(ArrayList<ForecastDayItem> forecastDayItemList){
        this.mForecastDayList = forecastDayItemList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_day_item, parent, false);
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(v);
        return forecastViewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        ForecastDayItem forecastDayItem = mForecastDayList.get(position);

        holder.tv_day.setText(forecastDayItem.s_day);
        holder.tv_hour0.setText(forecastDayItem.s_hour0);
        holder.tv_hour1.setText(forecastDayItem.s_hour1);
        holder.tv_hour2.setText(forecastDayItem.s_hour2);
        holder.tv_hour3.setText(forecastDayItem.s_hour3);
        holder.tv_hour4.setText(forecastDayItem.s_hour4);
        holder.tv_hour5.setText(forecastDayItem.s_hour5);
        holder.tv_hour6.setText(forecastDayItem.s_hour6);
        holder.tv_hour7.setText(forecastDayItem.s_hour7);
    }

    @Override
    public int getItemCount() {
        return mForecastDayList.size();
    }
}
