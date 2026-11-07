package com.orbit.week2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

/** Schedules local reminder notifications without a server or paid service. */
final class OrbitReminderScheduler {
    static final String CHANNEL_ID = "orbit_reminders";

    static void schedule(Context context, long recordId, String title, long atMillis) {
        if (atMillis <= System.currentTimeMillis()) return;
        ensureChannel(context);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms != null) alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent(context, recordId, title));
    }

    static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Orbit reminders", NotificationManager.IMPORTANCE_DEFAULT));
    }

    static void cancel(Context context, long recordId) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = pendingIntent(context, recordId, null);
        if (alarms != null) alarms.cancel(pending);
        pending.cancel();
    }

    static void syncAll(Context context, OrbitStore store, boolean enabled) {
        Cursor c=store.activeReminders();
        try { while(c.moveToNext()) { long id=c.getLong(0); if(enabled)schedule(context,id,c.getString(1),c.getLong(2)); else cancel(context,id); } }
        finally { c.close(); }
    }

    private static PendingIntent pendingIntent(Context context, long recordId, String title) {
        Intent intent = new Intent(context, OrbitReminderReceiver.class).setAction("com.orbit.week2.REMINDER." + recordId).putExtra("record_id", recordId);
        if (title != null) intent.putExtra("title", title);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(context, (int) recordId, intent, flags);
    }
}
