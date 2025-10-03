package com.example.walletwave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final ArrayList<DataModel> transactionList;
    private final Context context;

    public TransactionAdapter(Context context, ArrayList<DataModel> list) {
        this.context = context;
        this.transactionList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReason, tvAmount, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        DataModel model = transactionList.get(position);

        holder.tvReason.setText(model.category);
        holder.tvAmount.setText("₹" + model.amount);
        holder.tvDate.setText(model.date + " " + model.time);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
