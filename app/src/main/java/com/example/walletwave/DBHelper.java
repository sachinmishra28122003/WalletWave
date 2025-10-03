package com.example.walletwave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

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
                "balance TEXT NOT NULL" + // <--- last field, no comma needed
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop and recreate the table if DB version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

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
        return result != -1;  // if insert is successful, result is the row id (>0), else -1
    }


    public String getBalance() {
        double totalExpense = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(CAST(amount AS REAL)) FROM " + TABLE_TRANSACTIONS + " WHERE type = 'Expense'", null
        );

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0); // This is now the SUM value
        }

        cursor.close();

        // Return only the number string (no ₹, no label)
        return String.format("%.2f", totalExpense);
    }




    public ArrayList<DataModel> fetch() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel> result = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);

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
        } finally {
            if (cur != null) {
                cur.close();
            }
            db.close();
        }

        return result;
    }

}
