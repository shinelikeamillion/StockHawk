package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;

/**
 * Created by 10000_hours on 2017/5/10.
 */

public class StockRemoteViewFactory implements RemoteViewsFactory {


    private List<Stock> mStocks = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;

    public StockRemoteViewFactory (Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        for (int i = 0; i < 10; i++) {
            Stock stock = new Stock("hah");
            stock.setName("hah"+i);
            mStocks.add(stock);
        }
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        mStocks.clear();
    }

    @Override
    public int getCount() {
        return mStocks.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.stock_item);

        remoteViews.setTextViewText(R.id.name, mStocks.get(i).getName());
        remoteViews.setTextViewText(R.id.symbol_and_exchange_name, mStocks.get(i).getName());
        remoteViews.setTextViewText(R.id.price, mStocks.get(i).getName());
        remoteViews.setTextViewText(R.id.change, mStocks.get(i).getName());

        Bundle bundle = new Bundle();
        bundle.putInt(StockWidgetProvider.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();

        fillInIntent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(R.id.stock_item, fillInIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
