package com.orbit.week2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Small offline store for the Week 4 Android prototype. */
final class OrbitStore extends SQLiteOpenHelper {
    OrbitStore(Context context) { super(context, "orbit-prototype.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE records (record_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, category TEXT NOT NULL, description TEXT, amount_minor_units INTEGER, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE shopping_items (item_id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT NOT NULL, is_completed INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL)");
        seedRecord(db, "Wireless headphones", "Purchase", "Receipt saved · Warranty ends 14 Dec 2026", 849900L);
        seedRecord(db, "Electricity bill", "Bill", "Monthly bill · Due 23 Oct 2026", 124000L);
        seedRecord(db, "Cloud storage", "Subscription", "Renews 26 Oct 2026 · Monthly", 19900L);
        seedRecord(db, "Apartment agreement", "Document", "Lease document · Updated 2 Sep 2026", 0L);
        seedRecord(db, "Headphones warranty", "Warranty", "Linked to Wireless headphones · Ends 14 Dec 2026", 0L);
        addItem(db, "Dish soap", false); addItem(db, "Notebook", false); addItem(db, "Light bulbs", false); addItem(db, "Oat milk", true);
    }
    private void seedRecord(SQLiteDatabase db, String title, String category, String description, long amount) {
        ContentValues v = new ContentValues(); v.put("title", title); v.put("category", category); v.put("description", description); v.put("amount_minor_units", amount); v.put("created_at", System.currentTimeMillis()); db.insert("records", null, v);
    }
    private void addItem(SQLiteDatabase db, String name, boolean done) { ContentValues v = new ContentValues(); v.put("item_name", name); v.put("is_completed", done ? 1 : 0); v.put("created_at", System.currentTimeMillis()); db.insert("shopping_items", null, v); }
    long saveRecord(String title, String category, String description, long amount) {
        ContentValues v = new ContentValues(); v.put("title", title); v.put("category", category); v.put("description", description); v.put("amount_minor_units", amount); v.put("created_at", System.currentTimeMillis()); return getWritableDatabase().insert("records", null, v);
    }
    Cursor records(String query, String category) {
        String q = "%" + (query == null ? "" : query.trim()) + "%";
        if (category != null && !category.isEmpty() && !category.equals("All")) return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units FROM records WHERE (title LIKE ? OR description LIKE ?) AND category = ? ORDER BY created_at DESC", new String[]{q,q,category});
        return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units FROM records WHERE title LIKE ? OR description LIKE ? ORDER BY created_at DESC", new String[]{q,q});
    }
    Cursor items(boolean completed) { return getReadableDatabase().rawQuery("SELECT item_id,item_name,is_completed FROM shopping_items WHERE is_completed=? ORDER BY item_id DESC", new String[]{completed ? "1" : "0"}); }
    void addItem(String name) { addItem(getWritableDatabase(), name, false); }
    void completeItem(String name, boolean completed) { ContentValues v=new ContentValues(); v.put("is_completed",completed?1:0); getWritableDatabase().update("shopping_items",v,"item_name=?",new String[]{name}); }
    void clearCompleted() { getWritableDatabase().delete("shopping_items","is_completed=1",null); }
    void deleteRecord(long id) { getWritableDatabase().delete("records","record_id=?",new String[]{String.valueOf(id)}); }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
