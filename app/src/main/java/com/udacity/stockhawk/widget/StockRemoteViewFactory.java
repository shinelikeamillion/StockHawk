package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utilities.Utilities;

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
    Cursor mCursor;

    public StockRemoteViewFactory (Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        // doing nothing～
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS,
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        mStocks.clear();
    }

    @Override
    public int getCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null
                || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.stock_item);

        remoteViews.setTextViewText(R.id.name, mCursor.getString(Contract.Quote.POSITION_STOCK_NAME));

        remoteViews.setTextViewText(R.id.symbol_and_exchange_name,
                String.format(mContext.getResources().getString(R.string.symbol_and_exchange_name),
                        mCursor.getString(Contract.Quote.POSITION_SYMBOL),
                        mCursor.getString(Contract.Quote.POSITION_EXCHANGE_NAME)));

        remoteViews.setTextViewText(R.id.price, String.format(mContext.getString(R.string.price),

                Utilities.getDollarFormat().format(mCursor.getFloat(Contract.Quote.POSITION_PRICE))));

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        String change = Utilities.getDollarFormatWithPlus().format(rawAbsoluteChange);
        String percentage = Utilities.getPercentageFormat().format(percentageChange / 100);

        remoteViews.setTextViewText(R.id.change, String.format(mContext.getResources().getString(R.string.change),
                change,
                percentage));

        if (rawAbsoluteChange > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                remoteViews.setTextColor(R.id.change, ContextCompat.getColor(mContext, R.color.material_green_900));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                remoteViews.setTextColor(R.id.change, ContextCompat.getColor(mContext, R.color.material_red_700));
            }
        }

        Bundle bundle = new Bundle();
        int symbolColumn = mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
        bundle.putString(Contract.Quote.COLUMN_SYMBOL, mCursor.getString(symbolColumn));
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
