package com.example.walletwave;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private ArrayList<DataModel> dataList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.rvTransactionHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        dataList = dbHelper.fetch();

        adapter = new TransactionAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
    }
}
