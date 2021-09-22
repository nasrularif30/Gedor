package com.movtech.gedor.model;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movtech.gedor.R;

import org.w3c.dom.Text;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter {
    List<DataHistory> dataHistoryList;

    public HistoryAdapter(List<DataHistory> dataHistoryList){
        this.dataHistoryList = dataHistoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        DataHistory dataHistory = dataHistoryList.get(position);
        viewHolderClass.tvId.setText(String.valueOf(dataHistory.getId()));
        viewHolderClass.tvActivity.setText(String.valueOf(dataHistory.getActivity()));
        viewHolderClass.tvWaktu.setText(dataHistory.getWaktu());
    }

    @Override
    public int getItemCount() {
        return dataHistoryList.size();
    }
    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView tvId, tvActivity, tvWaktu;
        public ViewHolderClass(@NonNull View v){
            super(v);
            tvId = v.findViewById(R.id.tv_id);
            tvActivity = v.findViewById(R.id.tv_activity);
            tvWaktu = v.findViewById(R.id.tv_time);
        }
    }
}
