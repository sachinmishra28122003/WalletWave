package com.example.walletwave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Transactions extends AppCompatActivity {
    // Saving Transaction
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions);

        // extracting intent data
        Intent intent = getIntent();
        String Income = intent.getStringExtra("income");
        String name = intent.getStringExtra("name");
        String Expense = intent.getStringExtra("Expense");
        String Balance = intent.getStringExtra("Balance");
        TextView greet = findViewById(R.id.tvGreeting);
        TextView income = findViewById(R.id.tvIncome);
        TextView expense = findViewById(R.id.tvExpense);
        TextView balance = findViewById(R.id.tvBalance);
        greet.setText(name);
        income.setText(Income);
        expense.setText(Expense);
        balance.setText(Balance);
        DBHelper db  = new DBHelper(this);

        // Save Transaction on sqlLite
        Button store = findViewById(R.id.btnSaveTransaction);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText date = findViewById(R.id.etDate);
                EditText time = findViewById(R.id.etTime);
                EditText amount = findViewById(R.id.etAmount);
                EditText reason = findViewById(R.id.etReason);
                Spinner type = findViewById(R.id.spType);
                Spinner category = findViewById(R.id.spCategory);
                Spinner payMethod = findViewById(R.id.spPaymentMethod);

                // Calculate balance safely (example assumes Income is a string representing a number)
                double income = Double.parseDouble(Income);
                double amountEntered = Double.parseDouble(amount.getText().toString());
                double balance = income - amountEntered;
                String balanceStr = String.format("%.2f", balance);  // Convert to String

                boolean isInserted = db.insertData(
                        date.getText().toString(),
                        time.getText().toString(),
                        amount.getText().toString(),
                        reason.getText().toString(),
                        type.getSelectedItem().toString(),
                        category.getSelectedItem().toString(),
                        payMethod.getSelectedItem().toString(),
                        "",                  // Note (optional)
                        balanceStr
                );


                if (isInserted) {
                    Toast.makeText(getApplicationContext(), "Transaction added successfully!", Toast.LENGTH_SHORT).show();

                    // Reset form fields
                    date.setText("");
                    time.setText("");
                    amount.setText("");
                    reason.setText("");
                    type.setSelection(0);
                    category.setSelection(0);
                    payMethod.setSelection(0);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add transaction.", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}