package com.orbit.week2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Calendar;

/** Small offline store for the Week 4 Android prototype. */
final class OrbitStore extends SQLiteOpenHelper {
    OrbitStore(Context context) { super(context, "orbit-prototype.db", null, 6); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE records (record_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, category TEXT NOT NULL, description TEXT, amount_minor_units INTEGER, created_at INTEGER NOT NULL, due_at INTEGER NOT NULL DEFAULT 0, is_paid INTEGER NOT NULL DEFAULT 0, reminder_at INTEGER NOT NULL DEFAULT 0, reminder_enabled INTEGER NOT NULL DEFAULT 0, attachment_uri TEXT, attachment_name TEXT, purchase_at INTEGER NOT NULL DEFAULT 0, recurrence TEXT NOT NULL DEFAULT 'None', last_paid_at INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE shopping_items (item_id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT NOT NULL, is_completed INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_records_due_at ON records(due_at)");
        seedRecord(db, "Wireless headphones", "Purchase", "Receipt saved · Warranty ends 14 Dec 2026", 849900L);
        seedRecord(db, "Electricity bill", "Bill", "Monthly bill · Due 23 Oct 2026", 124000L);
        seedRecord(db, "Cloud storage", "Subscription", "Renews 26 Oct 2026 · Monthly", 19900L);
        seedRecord(db, "Apartment agreement", "Document", "Lease document · Updated 2 Sep 2026", 0L);
        seedRecord(db, "Headphones warranty", "Warranty", "Linked to Wireless headphones · Ends 14 Dec 2026", 0L);
        seedDueDate(db, "Electricity bill", 5); seedDueDate(db, "Cloud storage", 9); seedDueDate(db, "Headphones warranty", 45);
        setSeedRecurrence(db,"Electricity bill","Monthly"); setSeedRecurrence(db,"Cloud storage","Monthly");
        addItem(db, "Dish soap", false); addItem(db, "Notebook", false); addItem(db, "Light bulbs", false); addItem(db, "Oat milk", true);
    }
    private void seedRecord(SQLiteDatabase db, String title, String category, String description, long amount) {
        ContentValues v = new ContentValues(); v.put("title", title); v.put("category", category); v.put("description", description); v.put("amount_minor_units", amount); v.put("created_at", System.currentTimeMillis()); db.insert("records", null, v);
    }
    private void seedDueDate(SQLiteDatabase db, String title, int daysFromNow) {
        ContentValues v = new ContentValues(); v.put("due_at", System.currentTimeMillis() + daysFromNow * 24L * 60L * 60L * 1000L); db.update("records", v, "title=?", new String[]{title});
    }
    private void setSeedRecurrence(SQLiteDatabase db,String title,String recurrence) { ContentValues v=new ContentValues(); v.put("recurrence",recurrence); db.update("records",v,"title=?",new String[]{title}); }
    private void addItem(SQLiteDatabase db, String name, boolean done) { ContentValues v = new ContentValues(); v.put("item_name", name); v.put("is_completed", done ? 1 : 0); v.put("created_at", System.currentTimeMillis()); db.insert("shopping_items", null, v); }
    long saveRecord(String title, String category, String description, long amount, long dueAt, long reminderAt, boolean reminderEnabled, String attachmentUri, String attachmentName, long purchaseAt, String recurrence) {
        ContentValues v = new ContentValues(); v.put("title", title); v.put("category", category); v.put("description", description); v.put("amount_minor_units", amount); v.put("created_at", System.currentTimeMillis()); v.put("due_at", dueAt); v.put("reminder_at", reminderEnabled ? reminderAt : 0L); v.put("reminder_enabled", reminderEnabled ? 1 : 0); v.put("attachment_uri", attachmentUri); v.put("attachment_name", attachmentName); v.put("purchase_at", purchaseAt); v.put("recurrence", recurrence); return getWritableDatabase().insert("records", null, v);
    }
    boolean updateRecord(long id, String title, String category, String description, long amount, long dueAt, long reminderAt, boolean reminderEnabled, String attachmentUri, String attachmentName, long purchaseAt, String recurrence) {
        ContentValues v = new ContentValues(); v.put("title", title); v.put("category", category); v.put("description", description); v.put("amount_minor_units", amount); v.put("due_at", dueAt); v.put("reminder_at", reminderEnabled ? reminderAt : 0L); v.put("reminder_enabled", reminderEnabled ? 1 : 0); v.put("attachment_uri", attachmentUri); v.put("attachment_name", attachmentName); v.put("purchase_at", purchaseAt); v.put("recurrence", recurrence);
        return getWritableDatabase().update("records", v, "record_id=?", new String[]{String.valueOf(id)}) == 1;
    }
    long recordAmount(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT amount_minor_units FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() ? c.getLong(0) : 0L; } finally { c.close(); }
    }
    long recordDueAt(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT due_at FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() ? c.getLong(0) : 0L; } finally { c.close(); }
    }
    long recordReminderAt(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT reminder_at FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() ? c.getLong(0) : 0L; } finally { c.close(); }
    }
    boolean recordReminderEnabled(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT reminder_enabled FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() && c.getInt(0) == 1; } finally { c.close(); }
    }
    String recordAttachmentUri(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT attachment_uri FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() ? c.getString(0) : null; } finally { c.close(); }
    }
    String recordAttachmentName(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT attachment_name FROM records WHERE record_id=?", new String[]{String.valueOf(id)});
        try { return c.moveToFirst() ? c.getString(0) : null; } finally { c.close(); }
    }
    long recordPurchaseAt(long id) { Cursor c=getReadableDatabase().rawQuery("SELECT purchase_at FROM records WHERE record_id=?",new String[]{String.valueOf(id)}); try{return c.moveToFirst()?c.getLong(0):0L;}finally{c.close();} }
    String recordRecurrence(long id) { Cursor c=getReadableDatabase().rawQuery("SELECT recurrence FROM records WHERE record_id=?",new String[]{String.valueOf(id)}); try{return c.moveToFirst()?c.getString(0):"None";}finally{c.close();} }
    Cursor activeReminders() { return getReadableDatabase().rawQuery("SELECT record_id,title,reminder_at FROM records WHERE reminder_enabled=1 AND reminder_at>0", null); }
    Cursor obligations(String category) {
        if (category == null || category.equals("All")) return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE category IN ('Bill','Subscription') ORDER BY CASE WHEN due_at=0 THEN 1 ELSE 0 END,due_at ASC", null);
        return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE category=? ORDER BY CASE WHEN due_at=0 THEN 1 ELSE 0 END,due_at ASC", new String[]{category});
    }
    Cursor upcoming(String category) {
        if (category == null || category.equals("All")) return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE due_at>0 ORDER BY due_at ASC", null);
        String normalized = category.equals("Bills") ? "Bill" : category.equals("Subscriptions") ? "Subscription" : category;
        return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE due_at>0 AND category=? ORDER BY due_at ASC", new String[]{normalized});
    }
    long markPaid(long id) {
        Cursor c=getReadableDatabase().rawQuery("SELECT due_at,recurrence,reminder_at,reminder_enabled FROM records WHERE record_id=?",new String[]{String.valueOf(id)}); long due=0,reminderAt=0; String recurrence="None"; boolean reminderEnabled=false; try{if(c.moveToFirst()){due=c.getLong(0);recurrence=c.getString(1);reminderAt=c.getLong(2);reminderEnabled=c.getInt(3)==1;}}finally{c.close();}
        if(!"None".equals(recurrence)&&due>0){Calendar next=Calendar.getInstance();next.setTimeInMillis(due);int months="Monthly".equals(recurrence)?1:"Quarterly".equals(recurrence)?3:"Yearly".equals(recurrence)?12:0;while(next.getTimeInMillis()<=System.currentTimeMillis()){if("Weekly".equals(recurrence))next.add(Calendar.DAY_OF_YEAR,7);else if(months>0)next.add(Calendar.MONTH,months);else break;} if(next.getTimeInMillis()>System.currentTimeMillis()){long nextDue=next.getTimeInMillis(),nextReminder=reminderEnabled&&reminderAt>0?nextDue+(reminderAt-due):0L;boolean keepReminder=reminderEnabled&&nextReminder>System.currentTimeMillis();ContentValues v=new ContentValues();v.put("due_at",nextDue);v.put("is_paid",0);v.put("last_paid_at",System.currentTimeMillis());v.put("reminder_at",keepReminder?nextReminder:0L);v.put("reminder_enabled",keepReminder?1:0);getWritableDatabase().update("records",v,"record_id=?",new String[]{String.valueOf(id)});return nextDue;}}
        ContentValues v=new ContentValues();v.put("is_paid",1);v.put("last_paid_at",System.currentTimeMillis());getWritableDatabase().update("records",v,"record_id=?",new String[]{String.valueOf(id)});return 0L;
    }
    void setPaid(long id, boolean paid) { ContentValues v = new ContentValues(); v.put("is_paid", paid ? 1 : 0); getWritableDatabase().update("records", v, "record_id=?", new String[]{String.valueOf(id)}); }
    Cursor records(String query, String category) {
        String q = "%" + (query == null ? "" : query.trim()) + "%";
        if (category != null && !category.isEmpty() && !category.equals("All")) return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE (title LIKE ? OR description LIKE ?) AND category = ? ORDER BY created_at DESC", new String[]{q,q,category});
        return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE title LIKE ? OR description LIKE ? ORDER BY created_at DESC", new String[]{q,q});
    }
    Cursor recordById(long id) { return getReadableDatabase().rawQuery("SELECT record_id,title,category,description,amount_minor_units,due_at,is_paid,reminder_at,reminder_enabled,recurrence,purchase_at FROM records WHERE record_id=?",new String[]{String.valueOf(id)}); }
    Cursor items(boolean completed) { return getReadableDatabase().rawQuery("SELECT item_id,item_name,is_completed FROM shopping_items WHERE is_completed=? ORDER BY item_id DESC", new String[]{completed ? "1" : "0"}); }
    void addItem(String name) { addItem(getWritableDatabase(), name, false); }
    void completeItem(long id, boolean completed) { ContentValues v=new ContentValues(); v.put("is_completed",completed?1:0); getWritableDatabase().update("shopping_items",v,"item_id=?",new String[]{String.valueOf(id)}); }
    void clearCompleted() { getWritableDatabase().delete("shopping_items","is_completed=1",null); }
    void deleteRecord(long id) { getWritableDatabase().delete("records","record_id=?",new String[]{String.valueOf(id)}); }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { db.execSQL("ALTER TABLE records ADD COLUMN due_at INTEGER NOT NULL DEFAULT 0"); db.execSQL("ALTER TABLE records ADD COLUMN is_paid INTEGER NOT NULL DEFAULT 0"); seedDueDate(db, "Electricity bill", 5); seedDueDate(db, "Cloud storage", 9); seedDueDate(db, "Headphones warranty", 45); }
        if (oldVersion < 3) { db.execSQL("ALTER TABLE records ADD COLUMN reminder_at INTEGER NOT NULL DEFAULT 0"); db.execSQL("ALTER TABLE records ADD COLUMN reminder_enabled INTEGER NOT NULL DEFAULT 0"); }
        if (oldVersion < 4) { db.execSQL("ALTER TABLE records ADD COLUMN attachment_uri TEXT"); db.execSQL("ALTER TABLE records ADD COLUMN attachment_name TEXT"); }
        if (oldVersion < 5) { db.execSQL("ALTER TABLE records ADD COLUMN purchase_at INTEGER NOT NULL DEFAULT 0"); db.execSQL("ALTER TABLE records ADD COLUMN recurrence TEXT NOT NULL DEFAULT 'None'"); db.execSQL("ALTER TABLE records ADD COLUMN last_paid_at INTEGER NOT NULL DEFAULT 0"); setSeedRecurrence(db,"Electricity bill","Monthly"); setSeedRecurrence(db,"Cloud storage","Monthly"); }
        if (oldVersion < 6) db.execSQL("CREATE INDEX IF NOT EXISTS idx_records_due_at ON records(due_at)");
    }
}
