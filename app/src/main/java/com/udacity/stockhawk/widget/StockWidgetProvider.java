package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

public class StockWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int widgetId : appWidgetIds) {

            RemoteViews stocksView = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            appWidgetManager.updateAppWidget(widgetId, stocksView);
        }
    }

    private void setRemoteAdapter(Context context, @NonNull RemoteViews views) {
        views.setRemoteAdapter(R.id.dialog_stock, new Intent(context, StockWidgetProvider.class));
    }
}
