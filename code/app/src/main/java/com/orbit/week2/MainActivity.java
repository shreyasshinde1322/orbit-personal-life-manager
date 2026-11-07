package com.orbit.week2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** Native Android prototype with local SQLite-backed records and shopping items. */
public class MainActivity extends Activity {
    private static final int REQUEST_ATTACHMENT = 4102;
    private static final int REQUEST_EXPORT = 4103;
    private static final int REQUEST_RESTORE = 4104;
    private static final int INK = Color.rgb(28, 43, 64);
    private static final int MUTED = Color.rgb(99, 112, 131);
    private static final int LINE = Color.rgb(220, 227, 236);
    private static final int PANEL = Color.rgb(245, 248, 252);
    private final java.util.List<String> history = new java.util.ArrayList<>();
    private LinearLayout root;
    private FrameLayout screenSlot;
    private TextView detailTitle;
    private String currentScreen = "home";
    private String selectedCategory = "All";
    private String insightsPeriod = "This month";
    private String currentRecordTitle = "Wireless headphones";
    private String currentRecordCategory = "Purchase", currentRecordDescription = "Receipt saved · Warranty ends 14 Dec 2026";
    private String prefilledCategory;
    private long currentRecordId = 1L;
    private long currentRecordDueAt;
    private long currentRecordPurchaseAt;
    private long currentRecordReminderAt;
    private boolean currentRecordReminderEnabled;
    private String currentRecordRecurrence = "None";
    private long currentRecordAmount;
    private boolean currentRecordPaid;
    private String formAttachmentUri, formAttachmentName;
    private Button attachmentButton;
    private boolean removeAttachmentOnSave;
    private long editingRecordId = -1L;
    private OrbitStore store;
    private Runnable searchRefresh;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        store = new OrbitStore(this);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        screenSlot = new FrameLayout(this);
        root.addView(screenSlot, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(bottomNavigation(), new LinearLayout.LayoutParams(-1, dp(62)));
        setContentView(root);
        if (!showRecordFromIntent(getIntent())) showScreen("home", false);
    }

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }
    private String formatDate(long millis) { return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date(millis)); }
    private String formatDateTime(long millis) { return new SimpleDateFormat("d MMM yyyy · h:mm a", Locale.getDefault()).format(new Date(millis)); }
    private boolean beforeToday(long millis) { Calendar today=Calendar.getInstance(); today.set(Calendar.HOUR_OF_DAY,0); today.set(Calendar.MINUTE,0); today.set(Calendar.SECOND,0); today.set(Calendar.MILLISECOND,0); return millis < today.getTimeInMillis(); }
    private GradientDrawable outline(int fill, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(fill); d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), stroke); return d;
    }
    private TextView text(String value, int size, boolean bold, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private void pad(View v, int left, int top, int right, int bottom) { v.setPadding(dp(left), dp(top), dp(right), dp(bottom)); }
    private void gap(LinearLayout parent, int height) { Space s = new Space(this); parent.addView(s, new LinearLayout.LayoutParams(1, dp(height))); }
    private TextView label(String value) { TextView t = text(value.toUpperCase(Locale.ROOT), 10, true, MUTED); t.setLetterSpacing(.09f); return t; }
    private TextView paragraph(String value) { TextView t = text(value, 13, false, MUTED); t.setGravity(Gravity.TOP); t.setLineSpacing(dp(2), 1); return t; }
    private Button button(String value, boolean primary, Runnable action) {
        Button b = new Button(this); b.setText(value); b.setTextSize(12); b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setMinHeight(dp(44)); b.setPadding(dp(12), 0, dp(12), 0);
        b.setTextColor(primary ? Color.WHITE : INK); b.setBackground(outline(primary ? INK : Color.WHITE, primary ? INK : LINE, 10));
        if (action != null) b.setOnClickListener(v -> action.run()); return b;
    }
    private TextView chip(String value, boolean selected, Runnable action) {
        TextView t = text(value, 11, selected, INK); t.setGravity(Gravity.CENTER); pad(t, 12, 0, 12, 0);
        t.setMinHeight(dp(36)); t.setBackground(outline(selected ? 0xffe9e9e9 : Color.WHITE, selected ? 0xff777777 : LINE, 22));
        if (action != null) t.setOnClickListener(v -> action.run()); return t;
    }
    private LinearLayout page(String eyebrow, String title, boolean back) {
        LinearLayout outer = column(); outer.setBackgroundColor(Color.WHITE);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(false);
        LinearLayout content = column(); pad(content, 18, 16, 18, 20);
        LinearLayout header = row();
        if (back) {
            TextView b = text("‹", 31, false, INK); b.setGravity(Gravity.CENTER); b.setBackground(outline(Color.WHITE, LINE, 11));
            header.addView(b, new LinearLayout.LayoutParams(dp(42), dp(42)));
            b.setOnClickListener(v -> goBack());
            LinearLayout titles = column(); pad(titles, 10, 0, 0, 0);
            titles.addView(label(eyebrow)); TextView h = text(title, 24, true, INK); titles.addView(h);
            header.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        } else {
            LinearLayout titles = column(); titles.addView(label(eyebrow)); TextView h = text(title, 24, true, INK); h.setLineSpacing(0, 1); titles.addView(h);
            header.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        }
        content.addView(header); gap(content, 8); scroll.addView(content); outer.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        outer.setTag(content); return outer;
    }
    private LinearLayout body(View page) { return (LinearLayout) page.getTag(); }
    private void section(LinearLayout p, String title) { gap(p, 20); p.addView(text(title, 15, true, INK)); gap(p, 6); }
    private LinearLayout card(LinearLayout parent) {
        LinearLayout c = column(); pad(c, 12, 12, 12, 12); c.setBackground(outline(PANEL, LINE, 12));
        parent.addView(c, new LinearLayout.LayoutParams(-1, -2)); gap(parent, 8); return c;
    }
    private void summary(LinearLayout parent, String number, String title, Runnable action) {
        LinearLayout c = column(); pad(c, 10, 12, 8, 12); c.setBackground(outline(PANEL, LINE, 11));
        c.addView(text(number, 18, true, INK)); c.addView(text(title, 10, false, MUTED));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(76), 1); lp.setMargins(0, 0, dp(7), 0); parent.addView(c, lp);
        if (action != null) c.setOnClickListener(v -> action.run());
    }
    private void summaries(LinearLayout p, String[][] values, Runnable[] actions) {
        LinearLayout r = row(); for (int i=0; i<values.length; i++) summary(r, values[i][0], values[i][1], actions == null ? null : actions[i]);
        p.addView(r);
    }
    private void listRow(LinearLayout p, String icon, String title, String detail, String status, String target) {
        LinearLayout r = row(); pad(r, 2, 9, 2, 9);
        TextView i = text(icon, 18, true, INK); i.setGravity(Gravity.CENTER); i.setBackground(outline(0xfff1f1f1, LINE, 9));
        r.addView(i, new LinearLayout.LayoutParams(dp(38), dp(38)));
        LinearLayout copy = column(); pad(copy, 10, 0, 4, 0); copy.addView(text(title, 13, true, INK)); copy.addView(text(detail, 10, false, MUTED));
        r.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        if (status != null && !status.isEmpty()) { TextView s = text(status, 9, false, MUTED); pad(s, 7, 4, 7, 4); s.setBackground(outline(Color.WHITE, LINE, 18)); r.addView(s); }
        else r.addView(text("›", 21, false, MUTED));
        p.addView(r); View divider = new View(this); divider.setBackgroundColor(0xffe6e6e6); p.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
        r.setOnClickListener(v -> showScreen(target, true));
    }
    private EditText edit(String hint, boolean multiline) {
        EditText e = new EditText(this); e.setTextSize(13); e.setTextColor(INK); e.setHintTextColor(MUTED); e.setHint(hint);
        e.setSingleLine(!multiline); e.setPadding(dp(11), dp(9), dp(11), dp(9)); e.setBackground(outline(Color.WHITE, 0xffbbbbbb, 8));
        if (multiline) { e.setGravity(Gravity.TOP); e.setMinHeight(dp(76)); }
        return e;
    }
    private void field(LinearLayout p, String title, View input, boolean required) {
        LinearLayout r = row(); TextView t = text(title, 12, true, INK); r.addView(t, new LinearLayout.LayoutParams(0, -2, 1));
        r.addView(text(required ? "Required" : "Optional", 10, false, MUTED)); p.addView(r); gap(p, 5);
        p.addView(input, new LinearLayout.LayoutParams(-1, input instanceof EditText && ((EditText) input).getMinHeight() > dp(50) ? -2 : dp(46))); gap(p, 13);
    }
    private void emptyState(LinearLayout p, String title, String detail, String cta, Runnable action) {
        LinearLayout c = card(p); c.setGravity(Gravity.CENTER); TextView icon = text("＋", 22, false, MUTED); icon.setGravity(Gravity.CENTER);
        icon.setBackground(outline(Color.WHITE, LINE, 30)); c.addView(icon, new LinearLayout.LayoutParams(dp(46), dp(46)));
        TextView h = text(title, 14, true, INK); h.setGravity(Gravity.CENTER); c.addView(h); TextView d = paragraph(detail); d.setGravity(Gravity.CENTER); c.addView(d);
        if (cta != null) { gap(c, 6); c.addView(button(cta, true, action)); }
    }
    private void go(String screen) { showScreen(screen, true); }
    private void showScreen(String screen, boolean push) {
        if (push && !currentScreen.equals(screen)) history.add(currentScreen);
        currentScreen = screen; screenSlot.removeAllViews();
        View view;
        switch (screen) {
            case "timeline": view = timelineScreen(); break;
            case "add": view = addRecordScreen(); break;
            case "library": view = libraryScreen(); break;
            case "bills": view = billsScreen(); break;
            case "shopping": view = shoppingScreen(); break;
            case "details": view = detailsScreen(); break;
            case "search": view = searchScreen(); break;
            case "insights": view = insightsScreen(); break;
            case "settings": view = settingsScreen(); break;
            default: view = homeScreen(); currentScreen = "home";
        }
        screenSlot.addView(view, new FrameLayout.LayoutParams(-1, -1));
        root.removeViewAt(1); root.addView(bottomNavigation(), new LinearLayout.LayoutParams(-1, dp(62)));
    }
    private void goBack() { if (currentScreen.equals("add")) { editingRecordId = -1L; prefilledCategory = null; removeAttachmentOnSave=false; formAttachmentUri=null; formAttachmentName=null; } if (!history.isEmpty()) { String previous = history.remove(history.size()-1); showScreen(previous, false); } else showScreen("home", false); }
    @Override public void onBackPressed() { goBack(); }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==REQUEST_EXPORT||requestCode==REQUEST_RESTORE)&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) { if(requestCode==REQUEST_EXPORT)exportBackup(data.getData());else confirmRestore(data.getData()); return; }
        if (requestCode != REQUEST_ATTACHMENT || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); }
        catch (SecurityException ignored) { Toast.makeText(this, "The selected file may only be available for this session", Toast.LENGTH_LONG).show(); }
        formAttachmentUri = uri.toString(); formAttachmentName = attachmentName(uri); removeAttachmentOnSave = false;
        if (attachmentButton != null) attachmentButton.setText(formAttachmentName == null ? "Selected file" : formAttachmentName);
    }
    private String attachmentName(Uri uri) {
        Cursor c = null;
        try { c = getContentResolver().query(uri, null, null, null, null); if (c != null && c.moveToFirst()) { int index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME); if (index >= 0) return c.getString(index); } }
        catch (Exception ignored) { }
        finally { if (c != null) c.close(); }
        return uri.getLastPathSegment();
    }
    private void chooseAttachment() {
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT); picker.addCategory(Intent.CATEGORY_OPENABLE); picker.setType("*/*");
        picker.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(picker, REQUEST_ATTACHMENT);
    }
    private void openAttachment(String attachmentUri) {
        try { Uri uri=Uri.parse(attachmentUri); String mime=getContentResolver().getType(uri); Intent viewer=new Intent(Intent.ACTION_VIEW); viewer.setDataAndType(uri,mime==null?"*/*":mime); viewer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); startActivity(Intent.createChooser(viewer,"Open attachment")); }
        catch (Exception error) { Toast.makeText(this,"No app can open this file",Toast.LENGTH_SHORT).show(); }
    }
    private void startBackupExport() { Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/octet-stream");i.putExtra(Intent.EXTRA_TITLE,"orbit-backup.db");startActivityForResult(i,REQUEST_EXPORT); }
    private void startBackupRestore() { Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/octet-stream");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivityForResult(i,REQUEST_RESTORE); }
    private void exportBackup(Uri destination) {
        try { store.close(); try(InputStream in=new FileInputStream(getDatabasePath("orbit-prototype.db"));OutputStream out=getContentResolver().openOutputStream(destination)){if(out==null)throw new java.io.IOException("Could not create the backup file");copy(in,out);} store=new OrbitStore(this);Toast.makeText(this,"Backup exported",Toast.LENGTH_LONG).show(); }
        catch(Exception e){store=new OrbitStore(this);Toast.makeText(this,"Backup could not be exported",Toast.LENGTH_LONG).show();}
    }
    private void confirmRestore(Uri source) { new AlertDialog.Builder(this).setTitle("Replace Orbit data?").setMessage("This replaces the records and shopping list on this device with the selected backup.").setNegativeButton("Cancel",(d,w)->d.dismiss()).setPositiveButton("Restore",(d,w)->restoreBackup(source)).show(); }
    private void restoreBackup(Uri source) {
        File temporary=null;
        try { temporary=File.createTempFile("orbit-restore-",".db",getCacheDir());try(InputStream in=getContentResolver().openInputStream(source);OutputStream out=new FileOutputStream(temporary)){if(in==null)throw new java.io.IOException("Backup could not be opened");copy(in,out);}
            byte[] signature=new byte[16];try(InputStream in=new FileInputStream(temporary)){int read=0;while(read<signature.length){int n=in.read(signature,read,signature.length-read);if(n<0)break;read+=n;}if(read<16||!new String(signature,java.nio.charset.StandardCharsets.US_ASCII).equals("SQLite format 3\u0000"))throw new java.io.IOException("This is not an Orbit database backup");}
            android.database.sqlite.SQLiteDatabase check=android.database.sqlite.SQLiteDatabase.openDatabase(temporary.getAbsolutePath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);try{check.rawQuery("SELECT record_id FROM records LIMIT 1",null).close();check.rawQuery("SELECT item_id FROM shopping_items LIMIT 1",null).close();}finally{check.close();}
            OrbitReminderScheduler.syncAll(this,store,false);store.close();File target=getDatabasePath("orbit-prototype.db");try(InputStream in=new FileInputStream(temporary);OutputStream out=new FileOutputStream(target)){copy(in,out);}store=new OrbitStore(this);store.getWritableDatabase();OrbitReminderScheduler.syncAll(this,store,preferences().getBoolean("reminders_enabled",true));showScreen("home",false);Toast.makeText(this,"Backup restored",Toast.LENGTH_LONG).show();
        } catch(Exception e){try{store.close();}catch(Exception ignored){}store=new OrbitStore(this);try{store.getWritableDatabase();}catch(Exception ignored){}Toast.makeText(this,"Backup could not be restored: "+e.getMessage(),Toast.LENGTH_LONG).show();}
        finally {if(temporary!=null)temporary.delete();}
    }
    private void copy(InputStream in,OutputStream out)throws java.io.IOException {byte[] buffer=new byte[8192];int count;while((count=in.read(buffer))!=-1)out.write(buffer,0,count);out.flush();}
    @Override protected void onNewIntent(Intent intent) { super.onNewIntent(intent); setIntent(intent); showRecordFromIntent(intent); }
    private boolean showRecordFromIntent(Intent intent) {
        if(intent==null||!intent.hasExtra("record_id"))return false; long id=intent.getLongExtra("record_id",-1L); if(id<1)return false;
        Cursor c=store.recordById(id); try { if(!c.moveToFirst())return false; currentRecordId=c.getLong(0);currentRecordTitle=c.getString(1);currentRecordCategory=c.getString(2);currentRecordDescription=c.getString(3)==null?"":c.getString(3);currentRecordAmount=c.getLong(4);currentRecordDueAt=c.getLong(5);currentRecordPaid=c.getInt(6)==1;currentRecordReminderAt=c.getLong(7);currentRecordReminderEnabled=c.getInt(8)==1;currentRecordRecurrence=c.getString(9);currentRecordPurchaseAt=c.getLong(10); } finally { c.close(); }
        history.clear(); showScreen("details",false); return true;
    }
    private LinearLayout bottomNavigation() {
        LinearLayout bar = row(); bar.setGravity(Gravity.CENTER); bar.setBackgroundColor(Color.WHITE);
        View line = new View(this); // use an outer wrapper so the navigation has a clear top boundary
        LinearLayout wrapper = column(); line.setBackgroundColor(LINE); wrapper.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
        String[][] tabs = {{"⌂", "Home", "home"}, {"◷", "Timeline", "timeline"}, {"▤", "Library", "library"}, {"↻", "Bills", "bills"}, {"▥", "Insights", "insights"}};
        for (String[] tab : tabs) {
            LinearLayout item = column(); item.setGravity(Gravity.CENTER); TextView symbol = text(tab[0], 18, false, currentScreen.equals(tab[2]) ? INK : MUTED); symbol.setGravity(Gravity.CENTER); item.addView(symbol);
            TextView name = text(tab[1], 9, currentScreen.equals(tab[2]), currentScreen.equals(tab[2]) ? INK : MUTED); name.setGravity(Gravity.CENTER); item.addView(name);
            bar.addView(item, new LinearLayout.LayoutParams(0, -1, 1)); item.setOnClickListener(v -> showScreen(tab[2], true));
        }
        wrapper.addView(bar, new LinearLayout.LayoutParams(-1, 0, 1)); return wrapper;
    }

    private View homeScreen() {
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY); String greeting=hour<12?"Good morning":hour<18?"Good afternoon":"Good evening";
        LinearLayout v = page("YOUR PERSONAL ORBIT", greeting, false); LinearLayout p = body(v);
        LinearLayout top = row(); top.addView(label(new SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(new Date())), new LinearLayout.LayoutParams(0, -2, 1));
        TextView alerts = text("♧", 20, false, INK); alerts.setGravity(Gravity.CENTER); top.addView(alerts, new LinearLayout.LayoutParams(dp(42), dp(42))); alerts.setOnClickListener(x -> go("timeline"));
        TextView settings = text("⚙", 20, false, INK); settings.setGravity(Gravity.CENTER); top.addView(settings, new LinearLayout.LayoutParams(dp(42), dp(42))); settings.setOnClickListener(x -> go("settings")); p.addView(top);
        p.addView(paragraph("A clear view of what’s coming up.")); gap(p, 12);
        int upcomingCount=0,dueSoonCount=0; Calendar today=Calendar.getInstance(); today.set(Calendar.HOUR_OF_DAY,0); today.set(Calendar.MINUTE,0); today.set(Calendar.SECOND,0); today.set(Calendar.MILLISECOND,0); long start=today.getTimeInMillis(), week=start+7L*24*60*60*1000, month=start+30L*24*60*60*1000;
        android.database.Cursor stats=store.upcoming("All"); try { while(stats.moveToNext()) { long due=stats.getLong(5); boolean paid=stats.getInt(6)==1; if(!paid&&!beforeToday(due)&&due<month) { upcomingCount++; if(due<week) dueSoonCount++; } } } finally { stats.close(); }
        android.database.Cursor taskCount=store.items(false); int openTasks=taskCount.getCount(); taskCount.close();
        summaries(p, new String[][]{{String.format(Locale.US,"%02d",upcomingCount),"Next 30 days"},{String.format(Locale.US,"%02d",dueSoonCount),"Due in 7 days"},{String.format(Locale.US,"%02d",openTasks),"Open tasks"}}, new Runnable[]{() -> go("timeline"), () -> go("bills"), () -> go("shopping")});
        section(p, "Next up"); LinearLayout next=column(); p.addView(next); populateHomeUpcoming(next,2);
        section(p, "Quick actions"); LinearLayout actions = row(); actions.addView(button("＋ Add Record", false, () -> go("add")), weight()); actions.addView(button("＋ Add Bill", false, () -> go("bills")), weight()); p.addView(actions);
        LinearLayout actions2 = row(); actions2.addView(button("▢ Shopping List", false, () -> go("shopping")), weight()); actions2.addView(button("⌕ Search", false, () -> go("search")), weight()); p.addView(actions2);
        section(p, "Recent records"); LinearLayout recent=column(); p.addView(recent); populateRecentRecords(recent,3);
        section(p, "Explore Orbit"); LinearLayout explore = row(); explore.addView(button("Library", false, () -> go("library")), weight()); explore.addView(button("Settings", false, () -> go("settings")), weight()); p.addView(explore);
        return v;
    }
    private void openRecord(long id,String title,String type,String description,long amount,long dueAt,boolean paid,long reminderAt,boolean reminderEnabled) {
        currentRecordId=id; currentRecordTitle=title; currentRecordCategory=type; currentRecordDescription=description==null?"":description; currentRecordAmount=amount; currentRecordDueAt=dueAt; currentRecordPaid=paid; currentRecordReminderAt=reminderAt; currentRecordReminderEnabled=reminderEnabled; go("details");
    }
    private void populateHomeUpcoming(LinearLayout destination,int max) {
        android.database.Cursor c=store.upcoming("All"); int shown=0;
        try { while(c.moveToNext()&&shown<max) { long due=c.getLong(5); boolean paid=c.getInt(6)==1; if(paid) continue; long id=c.getLong(0),amount=c.getLong(4),reminderAt=c.getLong(7); String title=c.getString(1),type=c.getString(2),desc=c.getString(3); boolean reminderEnabled=c.getInt(8)==1; String detail=type+(desc==null||desc.trim().isEmpty()?"":" · "+desc); if(amount>0)detail+=" · ₹"+String.format(Locale.US,"%.2f",amount/100.0); detail+=" · "+formatDate(due);
            listRow(destination,"◷",title,detail,beforeToday(due)?"Overdue":"Upcoming","details"); View item=destination.getChildAt(destination.getChildCount()-2); item.setOnClickListener(view->openRecord(id,title,type,desc,amount,due,false,reminderAt,reminderEnabled)); shown++;
        }} finally { c.close(); }
        if(shown==0) destination.addView(paragraph("No unpaid items have a due date yet."));
    }
    private void populateRecentRecords(LinearLayout destination,int max) {
        android.database.Cursor c=store.records("","All"); int shown=0;
        try { while(c.moveToNext()&&shown<max) { long id=c.getLong(0),amount=c.getLong(4),due=c.getLong(5),reminderAt=c.getLong(7); String title=c.getString(1),type=c.getString(2),desc=c.getString(3); boolean paid=c.getInt(6)==1,reminderEnabled=c.getInt(8)==1; String detail=type+(desc==null||desc.trim().isEmpty()?"":" · "+desc); if(amount>0)detail+=" · ₹"+String.format(Locale.US,"%.2f",amount/100.0);
            listRow(destination,"▤",title,detail,"","details"); View item=destination.getChildAt(destination.getChildCount()-2); item.setOnClickListener(view->openRecord(id,title,type,desc,amount,due,paid,reminderAt,reminderEnabled)); shown++;
        }} finally { c.close(); }
        if(shown==0) emptyState(destination,"No records yet","Add your first record to get started.","Add Record",()->go("add"));
    }
    private LinearLayout.LayoutParams weight() { LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1); lp.setMargins(0, 0, dp(6), dp(7)); return lp; }

    private View timelineScreen() {
        LinearLayout v = page("YOUR SCHEDULE", "Timeline", false); LinearLayout p = body(v); chips(p, new String[]{"All","Bills","Subscriptions","Documents","Warranties"});
        LinearLayout entries=column(); p.addView(entries); populateTimeline(entries,selectedCategory);
        if(entries.getChildCount()==0) emptyState(p,"Nothing scheduled", "Add a due date to a bill, subscription, document, or warranty to see it on your timeline.",null,null);
        return v;
    }
    private void populateTimeline(LinearLayout destination, String category) {
        android.database.Cursor c=store.upcoming(category); String activeDate="";
        try { while(c.moveToNext()) {
            long id=c.getLong(0), amount=c.getLong(4), due=c.getLong(5), reminderAt=c.getLong(7); String title=c.getString(1), type=c.getString(2), desc=c.getString(3); boolean paid=c.getInt(6)==1, reminderEnabled=c.getInt(8)==1; String date=formatDate(due);
            if(!date.equals(activeDate)){ section(destination,date); activeDate=date; }
            String detail=type+(desc==null||desc.trim().isEmpty()?"":" · "+desc); if(amount>0) detail+=" · ₹"+String.format(Locale.US,"%.2f",amount/100.0);
            listRow(destination,"◷",title,detail,paid?"Paid":beforeToday(due)?"Overdue":"Upcoming","details");
            View entry=destination.getChildAt(destination.getChildCount()-2); entry.setOnClickListener(view->{currentRecordId=id;currentRecordTitle=title;currentRecordCategory=type;currentRecordDescription=desc==null?"":desc;currentRecordDueAt=due;currentRecordAmount=amount;currentRecordPaid=paid;currentRecordReminderAt=reminderAt;currentRecordReminderEnabled=reminderEnabled;go("details");});
        }} finally { c.close(); }
    }
    private void chips(LinearLayout p, String[] labels) {
        LinearLayout r = row(); r.setGravity(Gravity.CENTER_VERTICAL); r.setPadding(0, dp(8), 0, dp(8));
        for (String s : labels) { TextView c = chip(s, selectedCategory.equals(s), () -> {
                selectedCategory = s;
                for (int i=0; i<r.getChildCount(); i++) { TextView item=(TextView)r.getChildAt(i); boolean active=s.equals(item.getTag()); item.setBackground(outline(active ? 0xffe9e9e9 : Color.WHITE, active ? 0xff777777 : LINE, 22)); item.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL); }
                if (searchRefresh != null && currentScreen.equals("search")) searchRefresh.run();
            }); c.setTag(s);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2); lp.setMargins(0, 0, dp(6), 0); r.addView(c, lp); }
        HorizontalScrollViewCompat.add(this, p, r);
    }
    private View addRecordScreen() {
        boolean editing = editingRecordId > 0;
        formAttachmentUri = editing && !removeAttachmentOnSave ? store.recordAttachmentUri(editingRecordId) : null;
        formAttachmentName = editing && !removeAttachmentOnSave ? store.recordAttachmentName(editingRecordId) : null;
        LinearLayout v = page(editing ? "UPDATE SAVED INFORMATION" : "SAVE SOMETHING IMPORTANT", editing ? "Edit Record" : "Add Record", true); LinearLayout p = body(v);
        Spinner category = new Spinner(this); android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Purchase", "Bill", "Subscription", "Warranty", "Document", "Note"}); category.setAdapter(adapter);
        if (editing) { int position = adapter.getPosition(currentRecordCategory); if (position >= 0) category.setSelection(position); }
        else if (prefilledCategory != null) { int position = adapter.getPosition(prefilledCategory); if (position >= 0) category.setSelection(position); }
        field(p, "Category", category, true); EditText name = edit("e.g. Wireless headphones", false); if (editing) name.setText(currentRecordTitle); field(p, "Record name", name, true);
        EditText notes = edit("Add helpful details", true); if (editing) notes.setText(currentRecordDescription); field(p, "Notes", notes, false);
        long[] purchaseAt={editing?store.recordPurchaseAt(editingRecordId):0L}; Button purchaseDate=button(purchaseAt[0]==0?"Choose date":formatDate(purchaseAt[0]),false,null);
        purchaseDate.setOnClickListener(view->{Calendar c=Calendar.getInstance();if(purchaseAt[0]>0)c.setTimeInMillis(purchaseAt[0]);new DatePickerDialog(this,(picker,year,month,day)->{Calendar selected=Calendar.getInstance();selected.set(year,month,day,12,0,0);selected.set(Calendar.MILLISECOND,0);purchaseAt[0]=selected.getTimeInMillis();purchaseDate.setText(formatDate(purchaseAt[0]));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        field(p,"Purchase date",purchaseDate,false);
        long[] dueAt = {editing ? store.recordDueAt(editingRecordId) : 0L};
        Button dueDate = button(dueAt[0] == 0 ? "Choose date" : formatDate(dueAt[0]), false, null);
        dueDate.setOnClickListener(view -> { Calendar c = Calendar.getInstance(); if (dueAt[0] > 0) c.setTimeInMillis(dueAt[0]); new DatePickerDialog(this, (picker, year, month, day) -> { Calendar selected = Calendar.getInstance(); selected.set(year, month, day, 12, 0, 0); selected.set(Calendar.MILLISECOND, 0); dueAt[0] = selected.getTimeInMillis(); dueDate.setText(formatDate(dueAt[0])); }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show(); });
        field(p, "Due / warranty / renewal date", dueDate, false);
        Spinner recurrence=new Spinner(this); android.widget.ArrayAdapter<String> recurrenceAdapter=new android.widget.ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"None","Weekly","Monthly","Quarterly","Yearly"}); recurrence.setAdapter(recurrenceAdapter); int recurrencePosition=recurrenceAdapter.getPosition(editing?store.recordRecurrence(editingRecordId):"None"); recurrence.setSelection(Math.max(0,recurrencePosition));
        LinearLayout recurrenceField=column(); field(recurrenceField,"Repeat bill / subscription",recurrence,false); p.addView(recurrenceField);
        Runnable updateRecurrenceVisibility=()->{String type=category.getSelectedItem().toString();boolean allowed=type.equals("Bill")||type.equals("Subscription");recurrenceField.setVisibility(allowed?View.VISIBLE:View.GONE);if(!allowed)recurrence.setSelection(0);};
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onItemSelected(AdapterView<?> parent,View view,int position,long id){updateRecurrenceVisibility.run();}public void onNothingSelected(AdapterView<?> parent){}}); updateRecurrenceVisibility.run();
        EditText amount = edit("₹ 0.00", false); if (editing) { long cents = store.recordAmount(editingRecordId); if (cents > 0) amount.setText(String.format(Locale.US, "%.2f", cents / 100.0)); } field(p, "Amount", amount, false);
        section(p, "Receipt or document"); LinearLayout attachmentRow=row(); attachmentButton=button(formAttachmentName==null?"Choose a file":formAttachmentName,false,this::chooseAttachment); attachmentRow.addView(attachmentButton,new LinearLayout.LayoutParams(0,-2,1));
        if(formAttachmentUri!=null) attachmentRow.addView(button("Remove",false,()->{formAttachmentUri=null;formAttachmentName=null;removeAttachmentOnSave=true;showScreen("add",false);})); p.addView(attachmentRow);
        section(p, "Reminder"); Switch reminder = new Switch(this); reminder.setText("Enable reminder"); reminder.setChecked(editing && store.recordReminderEnabled(editingRecordId)); p.addView(reminder);
        long[] reminderAt = {editing ? store.recordReminderAt(editingRecordId) : 0L};
        Button reminderTime = button(reminderAt[0] == 0 ? "Choose date and time" : formatDateTime(reminderAt[0]), false, null);
        reminderTime.setOnClickListener(view -> { Calendar current = Calendar.getInstance(); if (reminderAt[0] > 0) current.setTimeInMillis(reminderAt[0]); new DatePickerDialog(this, (picker, year, month, day) -> { Calendar selected = Calendar.getInstance(); selected.set(year, month, day); new TimePickerDialog(this, (timePicker, hour, minute) -> { selected.set(Calendar.HOUR_OF_DAY, hour); selected.set(Calendar.MINUTE, minute); selected.set(Calendar.SECOND, 0); selected.set(Calendar.MILLISECOND, 0); reminderAt[0] = selected.getTimeInMillis(); reminderTime.setText(formatDateTime(reminderAt[0])); }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false).show(); }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show(); });
        field(p, "Reminder date and time", reminderTime, false);
        gap(p, 8); LinearLayout actions = row(); actions.addView(button("Cancel", false, () -> { if (editing) editingRecordId = -1L; goBack(); }), weight());
        actions.addView(button(editing ? "Save Changes" : "Save Record", true, () -> {
            String title = name.getText().toString().trim();
            if (title.isEmpty()) { name.setError("Please enter a record name"); name.requestFocus(); return; }
            String selectedRecurrence=recurrence.getSelectedItem().toString(); if(!selectedRecurrence.equals("None")&&dueAt[0]==0){dueDate.setError("Set a next due date for a recurring item");dueDate.requestFocus();return;}
            if (reminder.isChecked() && reminderAt[0] <= System.currentTimeMillis()) { reminderTime.setError("Choose a reminder time in the future"); reminderTime.requestFocus(); return; }
            long minor = 0; String amountText = amount.getText().toString().replace("₹", "").replace(",", "").trim();
            if (!amountText.isEmpty()) { try { BigDecimal value = new BigDecimal(amountText); if (value.signum() < 0 || value.scale() > 2) throw new NumberFormatException(); minor = value.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact(); } catch (Exception invalid) { amount.setError("Enter a non-negative amount with up to 2 decimal places"); amount.requestFocus(); return; } }
            currentRecordTitle = title; currentRecordCategory = category.getSelectedItem().toString(); currentRecordDescription = notes.getText().toString().trim();
            currentRecordDueAt = dueAt[0];
            currentRecordPurchaseAt=purchaseAt[0]; currentRecordRecurrence=(currentRecordCategory.equals("Bill")||currentRecordCategory.equals("Subscription"))?selectedRecurrence:"None";
            currentRecordAmount = minor;
            currentRecordReminderAt = reminder.isChecked() ? reminderAt[0] : 0L; currentRecordReminderEnabled = reminder.isChecked();
            if (editing) { currentRecordId = editingRecordId; if (!store.updateRecord(editingRecordId, currentRecordTitle, currentRecordCategory, currentRecordDescription, minor, dueAt[0], currentRecordReminderAt, currentRecordReminderEnabled, formAttachmentUri, formAttachmentName,purchaseAt[0],currentRecordRecurrence)) { Toast.makeText(this, "Record could not be updated", Toast.LENGTH_SHORT).show(); return; } }
            else { currentRecordId = store.saveRecord(currentRecordTitle, currentRecordCategory, currentRecordDescription, minor, dueAt[0], currentRecordReminderAt, currentRecordReminderEnabled, formAttachmentUri, formAttachmentName,purchaseAt[0],currentRecordRecurrence); currentRecordPaid = false; }
            if (currentRecordReminderEnabled && preferences().getBoolean("reminders_enabled",true)) OrbitReminderScheduler.schedule(this, currentRecordId, currentRecordTitle, currentRecordReminderAt);
            else OrbitReminderScheduler.cancel(this, currentRecordId);
            removeAttachmentOnSave = false;
            editingRecordId = -1L; Toast.makeText(this, editing ? "Changes saved" : "Saved on this device", Toast.LENGTH_SHORT).show(); go("details");
            prefilledCategory = null;
        }), weight()); p.addView(actions);
        return v;
    }
    private View libraryScreen() {
        LinearLayout v = page("YOUR SAVED INFORMATION", "Library", false); LinearLayout p = body(v);
        p.addView(button("⌕  Search records", false, () -> go("search")));
        section(p, "Categories"); String[][] categories = {{"▤","Document","Documents"},{"▧","Purchase","Purchases"},{"◷","Warranty","Warranties"},{"≡","Note","Notes"},{"↻","Subscription","Subscriptions"},{"₹","Bill","Bills"}};
        for (String[] c : categories) { android.database.Cursor countCursor=store.records("",c[1]); int count=countCursor.getCount(); countCursor.close(); int before=p.getChildCount(); listRow(p,c[0],c[2],count+ (count==1?" record":" records"),"","search"); p.getChildAt(before).setOnClickListener(view->{selectedCategory=c[1];go("search");}); }
        section(p, "All records · Recent"); LinearLayout recordsList = column(); p.addView(recordsList); populateRecords(recordsList, "", "All");
        p.addView(button("＋ Add Record", true, () -> go("add"))); return v;
    }
    private View billsScreen() {
        LinearLayout v = page("RECURRING OBLIGATIONS", "Bills & subscriptions", false); LinearLayout p = body(v);
        String category = selectedCategory.equals("Bills") ? "Bill" : selectedCategory.equals("Subscriptions") ? "Subscription" : "All";
        android.database.Cursor c = store.obligations(category); long total = 0, nextDue = 0; int count = 0;
        try { while (c.moveToNext()) { total += c.getLong(4); long due = c.getLong(5); if (nextDue == 0 && due > 0) nextDue = due; count++; } } finally { c.close(); }
        summaries(p, new String[][]{{"₹"+String.format(Locale.US,"%.2f",total/100.0),"Saved obligations"},{nextDue == 0 ? "—" : formatDate(nextDue),"Next due date"},{String.format(Locale.US,"%02d",count),"Active items"}}, null);
        chips(p, new String[]{"All", "Bills", "Subscriptions"});
        section(p, "Saved bills and subscriptions"); LinearLayout rows = column(); p.addView(rows); populateObligations(rows, category);
        if (rows.getChildCount() == 0) emptyState(p, "No bills or subscriptions yet", "Add one with an amount and due date to see it here.", null, null);
        p.addView(paragraph("Orbit records payment status. It does not make payments.")); gap(p, 8);
        p.addView(button("＋ Add Bill / Subscription", true, () -> { prefilledCategory="Bill"; go("add"); })); return v;
    }
    private void populateObligations(LinearLayout destination, String category) {
        android.database.Cursor c = store.obligations(category);
        try { while (c.moveToNext()) {
            long id=c.getLong(0), amount=c.getLong(4), due=c.getLong(5), reminderAt=c.getLong(7); String title=c.getString(1), type=c.getString(2), desc=c.getString(3), recurrence=c.getString(9); boolean paid=c.getInt(6)==1, reminderEnabled=c.getInt(8)==1;
            String detail=type+(desc==null||desc.trim().isEmpty()?"":" · "+desc); if(!"None".equals(recurrence))detail+=" · "+recurrence; if(due>0) detail += " · Due "+formatDate(due); if(amount>0) detail += " · ₹"+String.format(Locale.US,"%.2f",amount/100.0);
            listRow(destination,type.equals("Bill")?"₹":"↻",title,detail,paid?"Paid":due==0?"No date":(beforeToday(due)?"Overdue":"Due"),"details");
            View item=destination.getChildAt(destination.getChildCount()-2); item.setOnClickListener(view->{currentRecordId=id;currentRecordTitle=title;currentRecordCategory=type;currentRecordDescription=desc==null?"":desc;currentRecordDueAt=due;currentRecordAmount=amount;currentRecordPaid=paid;currentRecordReminderAt=reminderAt;currentRecordReminderEnabled=reminderEnabled;go("details");});
            destination.addView(button(paid?"Mark as Unpaid":"Mark as Paid",false,()->{
                if(paid){store.setPaid(id,false);Toast.makeText(this,"Marked unpaid",Toast.LENGTH_SHORT).show();}
                else {long nextDue=store.markPaid(id);if(nextDue>0){long nextReminder=reminderAt>0?nextDue+(reminderAt-due):0L;if(reminderEnabled&&preferences().getBoolean("reminders_enabled",true)&&nextReminder>System.currentTimeMillis())OrbitReminderScheduler.schedule(this,id,title,nextReminder);else OrbitReminderScheduler.cancel(this,id);Toast.makeText(this,"Next due · "+formatDate(nextDue),Toast.LENGTH_SHORT).show();}else{OrbitReminderScheduler.cancel(this,id);Toast.makeText(this,"Marked paid",Toast.LENGTH_SHORT).show();}}
                showScreen("bills",false);
            }));
        }} finally { c.close(); }
    }
    private View shoppingScreen() {
        LinearLayout v = page("A SIMPLE TO-DO LIST", "Shopping List", false); LinearLayout p = body(v); EditText input = edit("Add an item…", false);
        LinearLayout add = row(); add.addView(input, new LinearLayout.LayoutParams(0, dp(46), 1)); add.addView(button("Add", true, () -> { String value = input.getText().toString().trim(); if (!value.isEmpty()) { store.addItem(value); input.setText(""); showScreen("shopping", false); } })); p.addView(add);
        section(p, "To buy"); LinearLayout openList = column(); populateShopping(openList, false); p.addView(openList);
        section(p, "Completed"); LinearLayout doneList = column(); populateShopping(doneList, true); p.addView(doneList);
        gap(p, 10); p.addView(button("Clear completed", false, () -> { store.clearCompleted(); showScreen("shopping", false); }));
        if (openList.getChildCount() == 0 && doneList.getChildCount() == 0) emptyState(p, "Your list is clear", "Add items as you think of them.", null, null);
        return v;
    }
    private void populateShopping(LinearLayout p, boolean done) { android.database.Cursor c = store.items(done); try { while (c.moveToNext()) checkRow(p, c.getLong(0), c.getString(1), done); } finally { c.close(); } }
    private void checkRow(LinearLayout p, long id, String item, boolean done) {
        CheckBox cb = new CheckBox(this); cb.setText(item); cb.setTextSize(13); cb.setTextColor(done ? MUTED : INK); cb.setButtonTintList(android.content.res.ColorStateList.valueOf(0xff555555)); cb.setChecked(done); cb.setMinHeight(dp(48)); p.addView(cb);
        cb.setOnCheckedChangeListener((button, checked) -> { store.completeItem(id, checked); showScreen("shopping", false); });
    }
    private View detailsScreen() {
        LinearLayout v = page(currentRecordCategory.toUpperCase(Locale.ROOT)+" · SAVED RECORD", "Record Details", true); LinearLayout p = body(v);
        TextView state = text("▧  " + currentRecordCategory + "                                  " + (currentRecordPaid ? "Paid" : "Saved"), 13, true, INK); pad(state, 12, 12, 12, 12); state.setBackground(outline(PANEL, LINE, 11)); p.addView(state);
        long purchaseAt=store.recordPurchaseAt(currentRecordId); String recurrence=store.recordRecurrence(currentRecordId);
        section(p, "Record information"); LinearLayout c = card(p); detailTitle = text(currentRecordTitle, 13, true, INK); c.addView(detailTitle); c.addView(text("Stored on this device", 12, false, MUTED)); if(currentRecordAmount>0) c.addView(text("Amount · ₹"+String.format(Locale.US,"%.2f",currentRecordAmount/100.0),12,true,INK)); if(purchaseAt>0) c.addView(text("Purchase date · "+formatDate(purchaseAt),12,false,MUTED)); if(currentRecordDueAt>0) c.addView(text("Next due date · "+formatDate(currentRecordDueAt),12,false,MUTED)); if(!"None".equals(recurrence)) c.addView(text("Repeats · "+recurrence,12,false,MUTED)); c.addView(text(currentRecordDescription.isEmpty() ? "No notes added." : currentRecordDescription, 12, false, MUTED));
        section(p, "Attachment"); String attachmentUri=store.recordAttachmentUri(currentRecordId), attachmentName=store.recordAttachmentName(currentRecordId);
        if(attachmentUri==null||attachmentUri.isEmpty()) p.addView(paragraph("No file attached.")); else p.addView(button("Open · "+(attachmentName==null?"Attached file":attachmentName),false,()->openAttachment(attachmentUri)));
        section(p, "Reminder"); p.addView(paragraph(currentRecordReminderEnabled ? "Reminder scheduled · "+formatDateTime(currentRecordReminderAt) : "No reminder scheduled.")); p.addView(button("Edit reminder", false, () -> { editingRecordId=currentRecordId; go("add"); }));
        section(p, "Related records"); p.addView(paragraph("This warranty belongs to the headphones purchase.")); listRow(p, "◷", "Headphones warranty", "Warranty · Ends 14 Dec 2026", "", "details");
        section(p, "Actions"); LinearLayout buttons = row(); buttons.addView(button("Edit", false, () -> { editingRecordId = currentRecordId; go("add"); }), weight()); buttons.addView(button("Set Reminder", false, () -> { editingRecordId=currentRecordId; go("add"); }), weight()); buttons.addView(button("Delete", false, this::confirmDelete), weight()); p.addView(buttons); return v;
    }
    private void confirmDelete() {
        new AlertDialog.Builder(this).setTitle("Delete this record?").setMessage("This removes the saved record from this device.")
                .setNegativeButton("Cancel", (d,w) -> d.dismiss()).setPositiveButton("Delete record", (d,w) -> { OrbitReminderScheduler.cancel(this,currentRecordId); store.deleteRecord(currentRecordId); Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show(); showScreen("library", false); }).show();
    }
    private View searchScreen() {
        LinearLayout v = page("FIND A SAVED ITEM", "Search", true); LinearLayout p = body(v); EditText query = edit("Search records, notes…", false); p.addView(query);
        chips(p, new String[]{"All", "Purchase", "Bill", "Subscription", "Warranty", "Document", "Note"});
        section(p, "Recent searches"); p.addView(button("◷  warranty", false, () -> { query.setText("warranty"); })); p.addView(button("⌕  electricity", false, () -> query.setText("electricity")));
        section(p, "Results"); LinearLayout results = column(); p.addView(results);
        Runnable filter = () -> {
            results.removeAllViews(); populateRecords(results, query.getText().toString(), selectedCategory);
            if (results.getChildCount()==0) results.addView(paragraph("No matching records. Try another word or category."));
        };
        searchRefresh = filter;
        query.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){filter.run();} public void afterTextChanged(Editable e){} });
        filter.run(); p.addView(button("Clear query", false, () -> { query.setText(""); selectedCategory="All"; })); return v;
    }
    private void populateRecords(LinearLayout destination, String query, String category) {
        android.database.Cursor c=store.records(query,category); int count=0;
        try { while(c.moveToNext()) { long id=c.getLong(0); String title=c.getString(1), type=c.getString(2), description=c.getString(3); long amount=c.getLong(4), dueAt=c.getLong(5), reminderAt=c.getLong(7); boolean paid=c.getInt(6)==1, reminderEnabled=c.getInt(8)==1; String detail=type+" · "+(description==null?"":description); if(amount>0) detail += " · ₹"+String.format(Locale.US,"%.2f",amount/100.0); if(dueAt>0) detail += " · Due "+formatDate(dueAt);
            int before=destination.getChildCount(); listRow(destination,"▤",title,detail,"","details"); View row=destination.getChildAt(before); row.setOnClickListener(v->{currentRecordId=id;currentRecordTitle=title;currentRecordCategory=type;currentRecordDescription=description==null?"":description;currentRecordDueAt=dueAt;currentRecordAmount=amount;currentRecordPaid=paid;currentRecordReminderAt=reminderAt;currentRecordReminderEnabled=reminderEnabled;go("details");}); count++;
        }} finally { c.close(); }
    }
    private View insightsScreen() {
        LinearLayout v = page("YOUR SAVED OBLIGATIONS", "Insights", false); LinearLayout p = body(v);
        LinearLayout filters=row(); filters.setPadding(0,dp(8),0,dp(8));
        filters.addView(chip("This month",insightsPeriod.equals("This month"),()->{insightsPeriod="This month";showScreen("insights",false);}));
        filters.addView(chip("Next month",insightsPeriod.equals("Next month"),()->{insightsPeriod="Next month";showScreen("insights",false);})); p.addView(filters);
        Calendar start=Calendar.getInstance(); start.set(Calendar.DAY_OF_MONTH,1); start.set(Calendar.HOUR_OF_DAY,0); start.set(Calendar.MINUTE,0); start.set(Calendar.SECOND,0); start.set(Calendar.MILLISECOND,0); if(insightsPeriod.equals("Next month")) start.add(Calendar.MONTH,1); long selectedStart=start.getTimeInMillis(); Calendar finish=(Calendar)start.clone(); finish.add(Calendar.MONTH,1); long selectedEnd=finish.getTimeInMillis();
        long[] values={0,0,0}; android.database.Cursor c=store.obligations("All");
        try { while(c.moveToNext()) { long amount=c.getLong(4),due=c.getLong(5); boolean paid=c.getInt(6)==1; values[0]+=amount; if(!paid&&due>0&&beforeToday(due)) values[2]++; if(!paid&&due>=selectedStart&&due<selectedEnd) values[1]+=amount; } } finally { c.close(); }
        String periodLabel=insightsPeriod.equals("This month")?"Due this month":"Due next month";
        summaries(p,new String[][]{{formatMoney(values[0]),"Saved obligations"},{formatMoney(values[1]),periodLabel},{String.format(Locale.US,"%02d",values[2]),"Overdue unpaid"}},null);
        section(p,"Due amount by month"); LinearLayout chart=card(p); chart.setOrientation(LinearLayout.HORIZONTAL); chart.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); chart.setMinimumHeight(dp(160));
        long nextAmount=insightsPeriod.equals("This month")?sumDueInMonth(1):sumDueInMonth(2); long currentAmount=insightsPeriod.equals("This month")?values[1]:sumDueInMonth(1); long maxAmount=Math.max(currentAmount,nextAmount);
        addInsightBar(chart,currentAmount,maxAmount,insightsPeriod); addInsightBar(chart,nextAmount,maxAmount,insightsPeriod.equals("This month")?"Next month":"Following month");
        section(p,"Unpaid items · "+insightsPeriod); LinearLayout items=column(); p.addView(items); populateInsightItems(items,selectedStart,selectedEnd);
        if(items.getChildCount()==0) p.addView(paragraph("No unpaid bills or subscriptions are due during this period."));
        p.addView(paragraph("Totals use saved amounts, due dates, and recurrence schedules. Orbit does not make payments.")); p.addView(button("View bills and subscriptions",false,()->go("bills"))); return v;
    }
    private String formatMoney(long minor) { return "₹"+String.format(Locale.US,"%.2f",minor/100.0); }
    private long sumDueInMonth(int offset) { Calendar start=Calendar.getInstance(); start.set(Calendar.DAY_OF_MONTH,1); start.set(Calendar.HOUR_OF_DAY,0); start.set(Calendar.MINUTE,0); start.set(Calendar.SECOND,0); start.set(Calendar.MILLISECOND,0); start.add(Calendar.MONTH,offset); long from=start.getTimeInMillis(); Calendar end=(Calendar)start.clone(); end.add(Calendar.MONTH,1); long total=0; android.database.Cursor c=store.obligations("All"); try { while(c.moveToNext()) { long due=c.getLong(5); if(c.getInt(6)==0&&due>=from&&due<end.getTimeInMillis()) total+=c.getLong(4); } } finally { c.close(); } return total; }
    private void addInsightBar(LinearLayout chart,long amount,long max,String label) { LinearLayout group=column(); group.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL); TextView value=text(formatMoney(amount),10,true,INK); value.setGravity(Gravity.CENTER); group.addView(value); int h=max==0?dp(4):Math.max(dp(4),(int)(dp(100)*(double)amount/max)); View bar=new View(this); bar.setBackground(outline(0xff9aa8b8,0xff7d8998,5)); group.addView(bar,new LinearLayout.LayoutParams(dp(32),h)); TextView caption=text(label,10,false,MUTED); caption.setGravity(Gravity.CENTER); group.addView(caption); chart.addView(group,new LinearLayout.LayoutParams(0,dp(145),1)); }
    private void populateInsightItems(LinearLayout destination,long from,long to) { android.database.Cursor c=store.obligations("All"); try { while(c.moveToNext()) { long id=c.getLong(0),amount=c.getLong(4),due=c.getLong(5),reminderAt=c.getLong(7); String title=c.getString(1),type=c.getString(2),desc=c.getString(3); boolean paid=c.getInt(6)==1,reminderEnabled=c.getInt(8)==1; if(paid||due<from||due>=to)continue; String detail=type+" · "+formatDate(due)+(amount>0?" · "+formatMoney(amount):""); listRow(destination,type.equals("Bill")?"₹":"↻",title,detail,beforeToday(due)?"Overdue":"Upcoming","details"); View item=destination.getChildAt(destination.getChildCount()-2); item.setOnClickListener(view->openRecord(id,title,type,desc,amount,due,paid,reminderAt,reminderEnabled)); } } finally { c.close(); } }
    private View settingsScreen() {
        LinearLayout v = page("MAKE ORBIT YOURS", "Settings", false); LinearLayout p = body(v);
        section(p, "Notifications"); settingToggle(p,"Reminders","Alerts for dates saved on this device","reminders_enabled",true); settingToggle(p,"Quiet hours","Hold alerts overnight until 7:00 AM","quiet_hours",false);
        section(p, "Data"); p.addView(button("Export Orbit backup",false,this::startBackupExport)); p.addView(button("Restore from backup",false,this::startBackupRestore)); p.addView(paragraph("Backup includes records, shopping list, and attachment references. Original attached files stay in their current location."));
        section(p, "Privacy & security"); settingSwitch(p, "App lock", "Proposed · Not implemented", false); placeholder(p, "Privacy information", "Proposed information page");
        section(p, "Appearance"); Switch theme = new Switch(this); theme.setText("Use dark appearance (preview only)"); p.addView(theme);
        section(p, "About Orbit"); p.addView(paragraph("Personal Life Operations Hub\nVersion placeholder · 0.1 wireframe")); return v;
    }
    private void settingSwitch(LinearLayout p, String name, String detail, boolean checked) {
        LinearLayout r = row(); LinearLayout copy = column(); copy.addView(text(name,13,true,INK)); copy.addView(text(detail,10,false,MUTED)); r.addView(copy,new LinearLayout.LayoutParams(0,-2,1)); Switch s=new Switch(this); s.setChecked(checked); r.addView(s); p.addView(r); View d=new View(this); d.setBackgroundColor(0xffe5e5e5); p.addView(d,new LinearLayout.LayoutParams(-1,dp(1)));
    }
    private SharedPreferences preferences() { return getSharedPreferences("orbit_settings",MODE_PRIVATE); }
    private void settingToggle(LinearLayout p,String name,String detail,String key,boolean defaultValue) {
        LinearLayout r=row(); LinearLayout copy=column(); copy.addView(text(name,13,true,INK));copy.addView(text(detail,10,false,MUTED));r.addView(copy,new LinearLayout.LayoutParams(0,-2,1));Switch s=new Switch(this);s.setChecked(preferences().getBoolean(key,defaultValue));r.addView(s);p.addView(r);View divider=new View(this);divider.setBackgroundColor(0xffe5e5e5);p.addView(divider,new LinearLayout.LayoutParams(-1,dp(1)));
        s.setOnCheckedChangeListener((button,checked)->{preferences().edit().putBoolean(key,checked).apply();if("reminders_enabled".equals(key))OrbitReminderScheduler.syncAll(this,store,checked);});
    }
    private void placeholder(LinearLayout p, String title, String detail) { LinearLayout r=row(); LinearLayout copy=column(); copy.addView(text(title,13,true,INK)); copy.addView(text(detail,10,false,MUTED)); r.addView(copy,new LinearLayout.LayoutParams(0,-2,1)); r.addView(text("›",20,false,MUTED)); pad(r,0,12,0,12); p.addView(r); r.setOnClickListener(v->Toast.makeText(this,"Design placeholder",Toast.LENGTH_SHORT).show()); }
    private void searchAction() { go("search"); }
}

/** Small horizontal scroller helper, kept local to avoid adding a third-party UI dependency. */
final class HorizontalScrollViewCompat {
    static void add(Activity activity, LinearLayout parent, LinearLayout child) {
        android.widget.HorizontalScrollView scroll = new android.widget.HorizontalScrollView(activity);
        scroll.setHorizontalScrollBarEnabled(false); scroll.addView(child); parent.addView(scroll, new LinearLayout.LayoutParams(-1, -2));
    }
}
