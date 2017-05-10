package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.stockhawk.R;

public class StockWidgetProvider extends AppWidgetProvider{

    public static final String COLLECTION_VIEW_ACTION = "com.stock.udacity.CLLECTION_VIEW.ACTION";
    public static final String EXTRA_ITEM = "com.stock.udacity.stockwidget.EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {

            RemoteViews stocksView = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

            Intent stockService = new Intent(context, RemoteStockService.class);
            stocksView.setRemoteAdapter(R.id.ls_stocks, stockService);

            Intent stockIntent = new Intent();
            stockIntent.setAction(COLLECTION_VIEW_ACTION);
            stockIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, stockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            stocksView.setPendingIntentTemplate(R.id.ls_stocks, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, stocksView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (action.equals(COLLECTION_VIEW_ACTION)) {
            Toast.makeText(context, "clicked item ", Toast.LENGTH_SHORT).show();
        }

        super.onReceive(context, intent);
    }

    private void setRemoteAdapter(Context context, @NonNull RemoteViews views) {
        views.setRemoteAdapter(R.id.dialog_stock, new Intent(context, StockWidgetProvider.class));
    }
}
