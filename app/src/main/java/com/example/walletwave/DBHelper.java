package com.example.walletwave;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRANSACTIONS = "Transactions";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "amount TEXT NOT NULL, " +
                "reason TEXT, " +
                "type TEXT, " +              // income or expense
                "category TEXT, " +          // e.g. Food, Travel, Rent
                "paymentMethod TEXT, " +     // e.g. Cash, UPI, Card
                "note TEXT, " +              // optional user notes
                "balance TEXT NOT NULL" +    // current balance
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // Insert Data
    public boolean insertData(String date, String time, String amount, String reason, String type,
                              String category, String payMethod, String note, String balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("date", date);
        values.put("time", time);
        values.put("amount", amount);
        values.put("reason", reason);
        values.put("type", type);
        values.put("category", category);
        values.put("paymentMethod", payMethod);
        values.put("note", note);
        values.put("balance", balance);

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result != -1;
    }

    //  Export to CSV (Fixed)
    public void exportToCSV(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No transactions to export", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        // Create CSV header
        StringBuilder csvData = new StringBuilder();
        csvData.append("ID,Date,Time,Amount,Reason,Type,Category,PaymentMethod,Note,Balance\n");

        // Write rows
        while (cursor.moveToNext()) {
            csvData.append(cursor.getInt(cursor.getColumnIndexOrThrow("id"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("date"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("time"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("amount"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("reason"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("type"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("category"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("paymentMethod"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("note"))).append(",");
            csvData.append(cursor.getString(cursor.getColumnIndexOrThrow("balance"))).append("\n");
        }

        cursor.close();
        db.close();

        try {
            // Save CSV file to Downloads folder
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(path, "transactions.csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csvData.toString().getBytes());
            fos.close();

            Toast.makeText(context, "CSV saved to Downloads", Toast.LENGTH_LONG).show();

            // FileProvider setup (for Android 11+)
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/csv");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Open CSV File"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Calculate Expense Total
    public String getBalance() {
        double totalExpense = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(CAST(amount AS REAL)) FROM " + TABLE_TRANSACTIONS + " WHERE type = 'Expense'", null
        );

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return String.format("%.2f", totalExpense);
    }

    // Fetch all records
    public ArrayList<DataModel> fetch() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel> result = new ArrayList<>();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);

        if (cur != null && cur.moveToFirst()) {
            do {
                DataModel d = new DataModel();
                d.id = cur.getString(cur.getColumnIndexOrThrow("id"));
                d.date = cur.getString(cur.getColumnIndexOrThrow("date"));
                d.time = cur.getString(cur.getColumnIndexOrThrow("time"));
                d.amount = cur.getString(cur.getColumnIndexOrThrow("amount"));
                d.reason = cur.getString(cur.getColumnIndexOrThrow("reason"));
                d.type = cur.getString(cur.getColumnIndexOrThrow("type"));
                d.category = cur.getString(cur.getColumnIndexOrThrow("category"));
                d.paymentMethod = cur.getString(cur.getColumnIndexOrThrow("paymentMethod"));
                d.note = cur.getString(cur.getColumnIndexOrThrow("note"));
                d.balance = cur.getString(cur.getColumnIndexOrThrow("balance"));
                result.add(d);
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();
        return result;
    }
}
