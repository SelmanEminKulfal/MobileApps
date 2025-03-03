package com.example.weatherapp;

import android.app.PendingIntent;
import android.app.WidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.widget.AppWidgetManager;
import androidx.widget.AppWidgetProvider;

public class WeatherWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Widget güncelleme işlemi
        for (int appWidgetId : appWidgetIds) {
            // Widget görünümünü oluştur
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Widget'a tıklanabilirlik ekleyelim (örneğin, tıklandığında uygulama açılacak)
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Widget verisini güncelleme veya hata işlemleri eklenebilir
        if (intent.getAction().equals("com.example.weatherapp.UPDATE_WEATHER")) {
            Toast.makeText(context, "Hava durumu güncelleniyor...", Toast.LENGTH_SHORT).show();
        }
    }
}
