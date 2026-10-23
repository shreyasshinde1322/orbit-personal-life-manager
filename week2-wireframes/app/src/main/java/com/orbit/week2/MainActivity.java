package com.orbit.week2;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Native Android, low-fidelity screen flow. All values and actions are demonstration data. */
public class MainActivity extends Activity {
    private static final int INK = Color.rgb(42, 42, 42);
    private static final int MUTED = Color.rgb(103, 103, 103);
    private static final int LINE = Color.rgb(213, 213, 213);
    private static final int PANEL = Color.rgb(247, 247, 247);
    private final List<String> history = new ArrayList<>();
    private final List<String> shoppingItems = new ArrayList<>(Arrays.asList("Dish soap", "Notebook", "Light bulbs"));
    private final List<String> completedItems = new ArrayList<>(Arrays.asList("Oat milk"));
    private LinearLayout root;
    private FrameLayout screenSlot;
    private TextView detailTitle;
    private String currentScreen = "home";
    private String selectedCategory = "All";
    private String currentRecordTitle = "Wireless headphones";
    private Runnable searchRefresh;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
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
        showScreen("home", false);
    }

    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }
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
    private void goBack() { if (!history.isEmpty()) { String previous = history.remove(history.size()-1); showScreen(previous, false); } else showScreen("home", false); }
    @Override public void onBackPressed() { goBack(); }
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
        LinearLayout v = page("ORBIT · ILLUSTRATIVE DATA", "Good morning, Alex", false); LinearLayout p = body(v);
        LinearLayout top = row(); top.addView(label("FRIDAY, 23 OCTOBER"), new LinearLayout.LayoutParams(0, -2, 1));
        TextView alerts = text("♧", 20, false, INK); alerts.setGravity(Gravity.CENTER); top.addView(alerts, new LinearLayout.LayoutParams(dp(42), dp(42))); alerts.setOnClickListener(x -> go("timeline"));
        TextView settings = text("⚙", 20, false, INK); settings.setGravity(Gravity.CENTER); top.addView(settings, new LinearLayout.LayoutParams(dp(42), dp(42))); settings.setOnClickListener(x -> go("settings")); p.addView(top);
        p.addView(paragraph("A clear view of what’s coming up.")); gap(p, 12);
        summaries(p, new String[][]{{"04","Upcoming"},{"02","Due soon"},{"03","Open tasks"}}, new Runnable[]{() -> go("timeline"), () -> go("bills"), () -> go("shopping")});
        section(p, "Next up"); listRow(p, "◷", "Electricity bill", "Bill · ₹1,240 · Due today", "Due", "details"); listRow(p, "↻", "Cloud storage renewal", "Subscription · ₹199 / month · 26 Oct", "Soon", "details");
        section(p, "Quick actions"); LinearLayout actions = row(); actions.addView(button("＋ Add Record", false, () -> go("add")), weight()); actions.addView(button("＋ Add Bill", false, () -> go("bills")), weight()); p.addView(actions);
        LinearLayout actions2 = row(); actions2.addView(button("▢ Shopping List", false, () -> go("shopping")), weight()); actions2.addView(button("⌕ Search", false, () -> go("search")), weight()); p.addView(actions2);
        section(p, "Recent records"); listRow(p, "▧", "Headphones receipt", "Purchase · Added 18 Oct", "", "details");
        section(p, "Explore Orbit"); LinearLayout explore = row(); explore.addView(button("Library", false, () -> go("library")), weight()); explore.addView(button("Settings", false, () -> go("settings")), weight()); p.addView(explore);
        section(p, "Empty state preview"); emptyState(p, "Your Orbit starts here", "Add a record or reminder to see your day take shape.", "Add your first record", () -> go("add"));
        return v;
    }
    private LinearLayout.LayoutParams weight() { LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1); lp.setMargins(0, 0, dp(6), dp(7)); return lp; }

    private View timelineScreen() {
        LinearLayout v = page("YOUR SCHEDULE", "Timeline", false); LinearLayout p = body(v); chips(p, new String[]{"All","Bills","Subscriptions","Documents","Warranties"});
        section(p, "Today · 23 Oct"); listRow(p, "◷", "Electricity bill", "Bill · ₹1,240 · Due today", "Overdue", "details");
        section(p, "Tomorrow · 24 Oct"); listRow(p, "◷", "Return library book", "Reminder · Personal", "Upcoming", "details");
        section(p, "This week"); listRow(p, "↻", "Cloud storage renewal", "Subscription · ₹199 · 26 Oct", "Upcoming", "details"); listRow(p, "✓", "Water bill", "Bill · 21 Oct", "Completed", "details");
        section(p, "Later"); listRow(p, "◷", "Headphones warranty ends", "Warranty · 14 Dec", "Upcoming", "details");
        return v;
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
        LinearLayout v = page("SAVE SOMETHING IMPORTANT", "Add Record", true); LinearLayout p = body(v);
        Spinner category = new Spinner(this); android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Purchase", "Bill", "Subscription", "Warranty", "Document", "Note"}); category.setAdapter(adapter);
        field(p, "Category", category, true); EditText name = edit("e.g. Wireless headphones", false); field(p, "Record name", name, true);
        EditText notes = edit("Add helpful details", true); field(p, "Notes", notes, false);
        field(p, "Purchase date", button("18 Oct 2026 · Choose date", false, null), false);
        field(p, "Due / warranty / renewal date", button("14 Dec 2026 · Choose date", false, null), false);
        EditText amount = edit("₹ 0.00", false); field(p, "Amount", amount, false);
        section(p, "Receipt or document"); p.addView(button("▧  Attachment placeholder · Add file", false, () -> Toast.makeText(this, "Attachment placeholder", Toast.LENGTH_SHORT).show()));
        section(p, "Reminder"); Switch reminder = new Switch(this); reminder.setText("Enable reminder · Optional"); reminder.setChecked(true); p.addView(reminder);
        field(p, "Reminder date and time", button("7 Dec 2026 · 9:00 AM", false, null), false);
        gap(p, 8); LinearLayout actions = row(); actions.addView(button("Cancel", false, this::goBack), weight());
        actions.addView(button("Save Record", true, () -> { if (name.getText().toString().trim().isEmpty()) { name.setError("Please enter a record name"); name.requestFocus(); } else { currentRecordTitle = name.getText().toString().trim(); Toast.makeText(this, "Saved for this preview only", Toast.LENGTH_SHORT).show(); go("details"); } }), weight()); p.addView(actions);
        return v;
    }
    private View libraryScreen() {
        LinearLayout v = page("YOUR SAVED INFORMATION", "Library", false); LinearLayout p = body(v);
        p.addView(button("⌕  Search records", false, () -> go("search")));
        section(p, "Categories"); String[][] categories = {{"▤","Documents","4 records"},{"▧","Purchases","8 records"},{"◷","Warranties","3 records"},{"≡","Notes","5 records"},{"↻","Subscriptions","2 records"}};
        for (String[] c : categories) listRow(p, c[0], c[1], c[2], "", c[1].equals("Subscriptions") ? "bills" : "details");
        p.addView(button("Sort: Recent⌄", false, () -> Toast.makeText(this, "Sort options placeholder", Toast.LENGTH_SHORT).show()));
        section(p, "All records · Recent"); listRow(p, "▧", "Headphones receipt", "Purchase · 18 Oct 2026 · ₹8,499", "", "details"); listRow(p, "◷", "Headphones warranty", "Warranty · Expires 14 Dec 2026", "", "details"); listRow(p, "▤", "Apartment agreement", "Document · Updated 2 Sep 2026", "", "details");
        section(p, "Empty state preview"); emptyState(p, "No saved records yet", "Keep receipts, documents and useful dates together.", "＋ Add Record", () -> go("add"));
        p.addView(button("＋ Add Record", true, () -> go("add"))); return v;
    }
    private View billsScreen() {
        LinearLayout v = page("RECURRING OBLIGATIONS", "Bills & subscriptions", false); LinearLayout p = body(v);
        summaries(p, new String[][]{{"₹2,439*","Monthly total"},{"26 Oct","Next payment"},{"05","Active items"}}, null);
        p.addView(text("*Illustrative sample amount", 10, false, MUTED)); chips(p, new String[]{"All", "Bills", "Subscriptions"});
        section(p, "Upcoming"); listRow(p, "₹", "Electricity", "Bill · Monthly · Due 23 Oct · ₹1,240", "Due", "details"); p.addView(button("Mark as Paid", false, () -> Toast.makeText(this, "Payment status updated in this demo", Toast.LENGTH_SHORT).show()));
        listRow(p, "↻", "Cloud storage", "Subscription · Monthly · Renews 26 Oct · ₹199", "Upcoming", "details");
        listRow(p, "₹", "Internet service", "Bill · Monthly · Due 30 Oct · ₹1,000", "Upcoming", "details");
        p.addView(paragraph("Orbit records status for this demonstration. It does not make payments.")); gap(p, 8);
        p.addView(button("＋ Add Bill / Subscription", true, () -> go("add"))); return v;
    }
    private View shoppingScreen() {
        LinearLayout v = page("A SIMPLE TO-DO LIST", "Shopping List", false); LinearLayout p = body(v); EditText input = edit("Add an item…", false);
        LinearLayout add = row(); add.addView(input, new LinearLayout.LayoutParams(0, dp(46), 1)); add.addView(button("Add", true, () -> { String value = input.getText().toString().trim(); if (!value.isEmpty()) { shoppingItems.add(value); input.setText(""); showScreen("shopping", false); } })); p.addView(add);
        section(p, "To buy"); LinearLayout openList = column(); for (String item : shoppingItems) checkRow(openList, item, false); p.addView(openList);
        section(p, "Completed"); LinearLayout doneList = column(); for (String item : completedItems) checkRow(doneList, item, true); p.addView(doneList);
        gap(p, 10); p.addView(button("Clear completed", false, () -> { completedItems.clear(); showScreen("shopping", false); }));
        if (shoppingItems.isEmpty() && completedItems.isEmpty()) emptyState(p, "Your list is clear", "Add items as you think of them.", null, null);
        return v;
    }
    private void checkRow(LinearLayout p, String item, boolean done) {
        CheckBox cb = new CheckBox(this); cb.setText(item); cb.setTextSize(13); cb.setTextColor(done ? MUTED : INK); cb.setButtonTintList(android.content.res.ColorStateList.valueOf(0xff555555)); cb.setChecked(done); cb.setMinHeight(dp(48)); p.addView(cb);
        cb.setOnCheckedChangeListener((button, checked) -> { if (checked) { shoppingItems.remove(item); if (!completedItems.contains(item)) completedItems.add(item); } else { completedItems.remove(item); if (!shoppingItems.contains(item)) shoppingItems.add(item); } showScreen("shopping", false); });
    }
    private View detailsScreen() {
        LinearLayout v = page("PURCHASE · SAVED RECORD", "Record Details", true); LinearLayout p = body(v);
        TextView state = text("▧  Purchase                                  Active", 13, true, INK); pad(state, 12, 12, 12, 12); state.setBackground(outline(PANEL, LINE, 11)); p.addView(state);
        section(p, "Record information"); LinearLayout c = card(p); detailTitle = text(currentRecordTitle, 13, true, INK); c.addView(detailTitle); c.addView(text("Purchase date     18 October 2026", 12, false, MUTED)); c.addView(text("Amount                 ₹8,499", 12, false, MUTED)); c.addView(text("Notes                   Wireless headphones, black.", 12, false, MUTED)); c.addView(text("Warranty ends     14 December 2026", 12, false, MUTED));
        section(p, "Attachment"); p.addView(button("▤  Receipt_oct18.pdf · Attachment placeholder", false, null));
        section(p, "Reminder"); p.addView(paragraph("Warranty expiry · 7 December 2026, 9:00 AM")); p.addView(button("Edit reminder", false, () -> go("settings")));
        section(p, "Related records"); p.addView(paragraph("This warranty belongs to the headphones purchase.")); listRow(p, "◷", "Headphones warranty", "Warranty · Ends 14 Dec 2026", "", "details");
        section(p, "Actions"); LinearLayout buttons = row(); buttons.addView(button("Edit", false, () -> go("add")), weight()); buttons.addView(button("Set Reminder", false, () -> go("settings")), weight()); buttons.addView(button("Delete", false, this::confirmDelete), weight()); p.addView(buttons); return v;
    }
    private void confirmDelete() {
        new AlertDialog.Builder(this).setTitle("Delete this record?").setMessage("This only simulates deletion in the wireframe preview.")
                .setNegativeButton("Cancel", (d,w) -> d.dismiss()).setPositiveButton("Delete record", (d,w) -> { Toast.makeText(this, "Deletion simulated", Toast.LENGTH_SHORT).show(); goBack(); }).show();
    }
    private View searchScreen() {
        selectedCategory = "All";
        LinearLayout v = page("FIND A SAVED ITEM", "Search", true); LinearLayout p = body(v); EditText query = edit("Search records, notes…", false); p.addView(query);
        chips(p, new String[]{"All", "Purchase", "Bill", "Warranty", "Document"});
        section(p, "Recent searches"); p.addView(button("◷  warranty", false, () -> { query.setText("warranty"); })); p.addView(button("⌕  electricity", false, () -> query.setText("electricity")));
        section(p, "Results"); LinearLayout results = column(); p.addView(results);
        Runnable filter = () -> {
            results.removeAllViews(); String q = query.getText().toString().toLowerCase(Locale.ROOT).trim();
            String[][] data = {{"Wireless headphones","Purchase","Receipt · 18 Oct 2026 · ₹8,499"},{"Electricity bill","Bill","Due 23 Oct 2026 · ₹1,240"},{"Headphones warranty","Warranty","Ends 14 Dec 2026 · linked to purchase"},{"Apartment agreement","Document","Updated 2 Sep 2026"},{"Cloud storage","Subscription","Renews 26 Oct · ₹199 / month"}};
            boolean found = false; for (String[] item : data) if ((q.isEmpty() || (item[0]+item[1]+item[2]).toLowerCase(Locale.ROOT).contains(q)) && (selectedCategory.equals("All") || item[1].equals(selectedCategory))) { listRow(results, "▤", item[0], item[1]+" · "+item[2], "", "details"); found = true; }
            if (!found) { TextView empty = paragraph("No matching records. Try another word or category."); results.addView(empty); }
        };
        searchRefresh = filter;
        query.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){filter.run();} public void afterTextChanged(Editable e){} });
        filter.run(); p.addView(button("Clear query", false, () -> { query.setText(""); selectedCategory="All"; })); return v;
    }
    private View insightsScreen() {
        LinearLayout v = page("A QUICK LOOK AHEAD", "Insights", false); LinearLayout p = body(v); chips(p, new String[]{"This month", "Next month"});
        p.addView(text("Illustrative figures · Not calculated from user data", 10, false, MUTED)); gap(p, 8);
        summaries(p, new String[][]{{"₹2,439","Monthly recurring"},{"03","Upcoming payments"},{"01","Overdue items"}}, null);
        section(p, "Recurring costs · Illustrative"); LinearLayout chart = card(p); chart.setOrientation(LinearLayout.HORIZONTAL); chart.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); chart.setMinimumHeight(dp(150));
        int[] heights = {55, 86, 112, 75}; String[] months = {"Aug","Sep","Oct","Nov"};
        for (int i=0; i<heights.length; i++) { LinearLayout g = column(); g.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); View bar = new View(this); bar.setBackground(outline(i==2 ? 0xff888888 : 0xffc4c4c4, 0xff999999, 4)); g.addView(bar, new LinearLayout.LayoutParams(dp(28), dp(heights[i]))); TextView m=text(months[i],9,false,MUTED); m.setGravity(Gravity.CENTER); g.addView(m); chart.addView(g, new LinearLayout.LayoutParams(0, dp(142), 1)); }
        section(p, "Upcoming renewals"); listRow(p, "↻", "Cloud storage", "Subscription · 26 Oct · ₹199", "", "details"); listRow(p, "◷", "Headphones warranty", "Warranty · 14 Dec", "", "details"); p.addView(button("View bills and subscriptions", false, () -> go("bills"))); return v;
    }
    private View settingsScreen() {
        LinearLayout v = page("MAKE ORBIT YOURS", "Settings", false); LinearLayout p = body(v);
        section(p, "Notifications"); settingSwitch(p, "Reminders", "Alerts for important dates", true); settingSwitch(p, "Quiet hours", "Pause alerts overnight", false); listRow(p, "◷", "Reminder timing", "Default: one day before", "", "settings");
        section(p, "Data"); placeholder(p, "Backup data", "Design placeholder · Not connected"); placeholder(p, "Restore data", "Design placeholder · Not connected");
        section(p, "Privacy & security"); settingSwitch(p, "App lock", "Proposed · Not implemented", false); placeholder(p, "Privacy information", "Proposed information page");
        section(p, "Appearance"); Switch theme = new Switch(this); theme.setText("Use dark appearance (preview only)"); p.addView(theme);
        section(p, "About Orbit"); p.addView(paragraph("Personal Life Operations Hub\nVersion placeholder · 0.1 wireframe")); return v;
    }
    private void settingSwitch(LinearLayout p, String name, String detail, boolean checked) {
        LinearLayout r = row(); LinearLayout copy = column(); copy.addView(text(name,13,true,INK)); copy.addView(text(detail,10,false,MUTED)); r.addView(copy,new LinearLayout.LayoutParams(0,-2,1)); Switch s=new Switch(this); s.setChecked(checked); r.addView(s); p.addView(r); View d=new View(this); d.setBackgroundColor(0xffe5e5e5); p.addView(d,new LinearLayout.LayoutParams(-1,dp(1)));
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
