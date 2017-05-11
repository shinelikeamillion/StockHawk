package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.DetailActivity;

public class StockWidgetProvider extends AppWidgetProvider{

    public static final String ACTION_VIEW_DETAIL = "com.stock.udacity.stockwidget.action.view";
    public static final String ACTION_UPDATE = "com.stock.udacity.stockwidget.action.update";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {

            RemoteViews stocksView = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

            Intent stockService = new Intent(context, RemoteStockService.class);
            stocksView.setRemoteAdapter(R.id.ls_stocks, stockService);

            Intent stockIntent = new Intent();
            stockIntent.setAction(ACTION_VIEW_DETAIL);
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

        if (action.equals(ACTION_VIEW_DETAIL)) {

            String symbol = intent.getExtras().getString(Contract.Quote.COLUMN_SYMBOL);
            intent.setData(Contract.Quote.makeUriForStock(symbol));
            intent.setClass(context, DetailActivity.class);
            context.startActivity(intent);

        } else if (action.equals(ACTION_UPDATE)) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ls_stocks);
        }

        super.onReceive(context, intent);
    }
}
