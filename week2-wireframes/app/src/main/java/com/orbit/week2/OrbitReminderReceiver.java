package com.orbit.week2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import java.util.Calendar;

/** Delivers Orbit reminders and restores scheduled alarms after device restart. */
public final class OrbitReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            restoreReminders(context);
            return;
        }
        long recordId = intent.getLongExtra("record_id", -1L);
        if (recordId < 0) return;
        android.content.SharedPreferences settings=context.getSharedPreferences("orbit_settings",Context.MODE_PRIVATE);
        if(!settings.getBoolean("reminders_enabled",true))return;
        if(settings.getBoolean("quiet_hours",false)){
            Calendar now=Calendar.getInstance(); int hour=now.get(Calendar.HOUR_OF_DAY);
            if(hour>=22||hour<7){Calendar resume=(Calendar)now.clone();resume.set(Calendar.HOUR_OF_DAY,7);resume.set(Calendar.MINUTE,0);resume.set(Calendar.SECOND,0);resume.set(Calendar.MILLISECOND,0);if(hour>=22)resume.add(Calendar.DAY_OF_YEAR,1);OrbitReminderScheduler.schedule(context,recordId,intent.getStringExtra("title"),resume.getTimeInMillis());return;}
        }
        showNotification(context, recordId, intent.getStringExtra("title"));
    }

    private void restoreReminders(Context context) {
        OrbitStore store = new OrbitStore(context);
        try { OrbitReminderScheduler.syncAll(context,store,context.getSharedPreferences("orbit_settings",Context.MODE_PRIVATE).getBoolean("reminders_enabled",true)); }
        finally { store.close(); }
    }

    private void showNotification(Context context, long recordId, String title) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(OrbitReminderScheduler.CHANNEL_ID, "Orbit reminders", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alerts for dates saved in Orbit");
            manager.createNotificationChannel(channel);
        }
        Intent open = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("record_id",recordId);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) recordId, open, flags);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(context, OrbitReminderScheduler.CHANNEL_ID)
                : new Notification.Builder(context).setPriority(Notification.PRIORITY_DEFAULT);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Orbit reminder")
                .setContentText(title == null || title.trim().isEmpty() ? "You have an upcoming item" : title)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        try { manager.notify((int) recordId, builder.build()); }
        catch (SecurityException ignored) { /* The user has notifications disabled for Orbit. */ }
    }
}
