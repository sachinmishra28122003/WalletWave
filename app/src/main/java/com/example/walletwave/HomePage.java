package com.example.walletwave;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    private TextView tvbalance, tvExpense, tvIncome, tvName;
    private DBHelper db;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    PieChart pie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);  // You can keep this if needed
        setContentView(R.layout.activity_home_page);
        pie = findViewById(R.id.pieChart);
        // Initialize views here
        tvbalance = findViewById(R.id.tvBalance);
        tvExpense = findViewById(R.id.tvExpense);
        tvIncome = findViewById(R.id.tvIncome);
        tvName = findViewById(R.id.tvGreeting);


        db = new DBHelper(this);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        loadUserData();

        // Button to go to Transactions activity
        Button transactionStore = findViewById(R.id.btnAddTransaction);
        transactionStore.setOnClickListener(v -> {
            Intent i = new Intent(HomePage.this, Transactions.class);
            // Extract only the number from income string (e.g., "Income: ₹5000" -> "5000")
            String incomeText = tvIncome.getText().toString().replaceAll("[^\\d.]", "");
            i.putExtra("income", incomeText);
            i.putExtra("name", tvName.getText().toString());
            i.putExtra("Expense", tvExpense.getText().toString());
            i.putExtra("Balance", tvbalance.getText().toString());
            startActivity(i);
        });

        // Button to go to Transaction History activity
        Button transactionHistory = findViewById(R.id.btnHistory);
        transactionHistory.setOnClickListener(v -> {
            Intent i = new Intent(HomePage.this, TransactionHistory.class);
            startActivity(i);
        });
    }

    private void loadUserData() {
        String uid = auth.getCurrentUser().getUid();

        usersRef.child(uid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("name").getValue(String.class);
                String income = snapshot.child("income").getValue(String.class);

                double incomeDouble = 0.0;
                try {
                    incomeDouble = Double.parseDouble(income);
                } catch (Exception e) {
                    incomeDouble = 0.0;
                }

                double expenseDouble = 0.0;
                try {
                    expenseDouble = Double.parseDouble(db.getBalance());
                } catch (Exception e) {
                    expenseDouble = 0.0;
                }

                double balanceDouble = incomeDouble - expenseDouble;

                tvName.setText("Hi " + name);
                tvIncome.setText( String.format("%.2f", incomeDouble));
                tvExpense.setText("Expense: ₹" + String.format("%.2f", expenseDouble));
                tvbalance.setText("Balance: ₹" + String.format("%.2f", balanceDouble));
                showPieChart();
            } else {
                Toast.makeText(HomePage.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(HomePage.this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSummary() {
        // If you want to update UI summary dynamically (e.g., after returning from transaction screen)
        tvIncome = findViewById(R.id.tvIncome);
        String incomeStr = tvIncome.getText().toString(); // keep digits and dot
        String expenseStr = db.getBalance();

        double income = 0.0;
        double expense = 0.0;

        try {
            income = Double.parseDouble(incomeStr);
        } catch (NumberFormatException e) {
            income = 0.0;
        }

        try {
            expense = Double.parseDouble(expenseStr);
        } catch (NumberFormatException e) {
            expense = 0.0;
        }

        double balance = income - expense;

        tvIncome.setText(  String.format("%.2f", income));
        tvExpense.setText("Expense: ₹" + String.format("%.2f", expense));
        tvbalance.setText("Balance: ₹" + String.format("%.2f", balance));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();  // Refresh the balance and expense whenever HomePage resumes
        showPieChart(); // Update Pie chart
    }

    // Pie Chart

    private void showPieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        // Use existing db instance (not new one)
        Map<String, Float> type = new HashMap<>();

        float income = 0f;
        float expense = 0f;

        try {
            income = Float.parseFloat(tvIncome.getText().toString());
        } catch (NumberFormatException e) {
            income = 0f;
        }

        try {
            expense = Float.parseFloat(db.getBalance());
        } catch (NumberFormatException e) {
            expense = 0f;
        }

        float balance = income - expense;

        type.put("Income", income);
        type.put("Expense", expense);
        type.put("Balance", balance);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));

        for (String t : type.keySet()) {
            pieEntries.add(new PieEntry(type.get(t), t));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        pie.setData(pieData);
        pie.invalidate(); // refresh
    }


}
