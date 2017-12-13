package com.example.muraseyuuichirou.myimagewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.widget.RemoteViews;

import java.util.Random;

import static com.example.muraseyuuichirou.myimagewidget.R.id.image0;

/**
 * Implementation of App Widget functionality.
 */
public class MyImageWidget extends AppWidgetProvider {

    public static final String URI_SCHEME = "myimagewidget";

    public void onReceive(Context context, Intent intent) {
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(intent.getAction())) {
            deleteAlarm(context, intent);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            if (!URI_SCHEME.equals(intent.getScheme())) {
                setAlarm(context, intent);
            } else {
                doRroc(context, intent);
            }
        }
    }

    private void doRroc(Context context, Intent intent) {
        PowerManager pm = (PowerManager)context.getSystemService(context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            if (!pm.isScreenOn()) return;
        } else {
            if (!pm.isInteractive()) return;
        }
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            updateAppWidget(context, manager, appWidgetId);
        }
    }

    private void setAlarm(Context context,Intent intent){
        int intArr[] = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        for(int appWidgetId : intArr){
            long interval = 3;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval * 1000 * 60, PendingIntent.getBroadcast(context,0, buildAlarmIntent(context,appWidgetId),PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    private Intent buildAlarmIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(URI_SCHEME + "://update/" + appWidgetId));
        return intent;
    }

    private void deleteAlarm(Context context, Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, buildAlarmIntent(context, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        final int[] IMAGES = {
                R.drawable.char0, R.drawable.char1, R.drawable.char2, R.drawable.char3, R.drawable.char4, R.drawable.char5, R.drawable.char6, R.drawable.char7, R.drawable.char8, R.drawable.char9
        };
        final int VIEWS = image0;

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_image_widget);

        Random rnd = new Random();
        int r = rnd.nextInt(10) + 1;
        views.setImageViewResource(VIEWS, IMAGES[r]);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
