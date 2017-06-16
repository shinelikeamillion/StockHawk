package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.utilities.Utilities;
import com.udacity.stockhawk.widget.StockWidgetProvider;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int STOCK_LOADER = 0;
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.tv_last_update_time)
    TextView tvLastUpdateTime;
    private StockAdapter adapter;


    public static final int MSG_WHAT_STOCK_NOT_EXIST = 0x01;
    public static final int MSG_WHAT_ADD_STOCK = 0X02;

    Handler addStockHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_WHAT_STOCK_NOT_EXIST: {
                    Toast.makeText(MainActivity.this, R.string.toast_found_not_exit, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_WHAT_ADD_STOCK:{

                    String symbol = String.valueOf(msg.obj);
                    if (networkUp()) {
                        swipeRefreshLayout.setRefreshing(true);
                    } else {
                        String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    PrefUtils.addStock(MainActivity.this, symbol);
                    QuoteSyncJob.syncImmediately(MainActivity.this);
                    break;
                }
            }
        }
    };

    @Override
    public void onClick(String symbol) {
        Timber.d("Symbol clicked: %s", symbol);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.setData(Contract.Quote.makeUriForStock(symbol));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new StockAdapter(this, this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        QuoteSyncJob.initialize(this);
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
        getSupportActionBar().setElevation(0);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(MainActivity.this, symbol);
                getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
            }
        }).attachToRecyclerView(stockRecyclerView);


        long lastUpdateTime = PreferenceManager.getDefaultSharedPreferences(this).getLong("last_update", 0);
        if (0 != lastUpdateTime) {
            tvLastUpdateTime.setText(String.format(getString(R.string.last_refresh_time), Utilities.getUpdateTimeName(this, lastUpdateTime)));
        }
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(this);

        if (!networkUp() && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!networkUp()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(this).size() == 0) {
            Timber.d("WHY ARE WE HERE");
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    public void button(View view) {
        new AddStockDialog().show(getFragmentManager(), "StockDialogFragment");
    }

    void addStock(final String symbol) {
        if (symbol != null && !symbol.isEmpty()) {

            new Thread(){
                @Override
                public void run() {
                    try {
                        Stock sk = YahooFinance.get(symbol);
                        if (sk == null ||
                                (sk != null && sk.getQuote().getPrice() == null)) {
                            Timber.e("there is a null");
                            addStockHandler.sendEmptyMessage(MSG_WHAT_STOCK_NOT_EXIST);
                        } else {
                            Message msg = addStockHandler.obtainMessage();
                            msg.what = MSG_WHAT_ADD_STOCK;
                            msg.obj = symbol;
                            msg.sendToTarget();
                        }
                    } catch (IOException e) {
                        Timber.e(e, "get stock error");
                        e.printStackTrace();
                    }

                }
            }.start();

        } else {

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);

        if (data.getCount() != 0) {
            error.setVisibility(View.GONE);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putLong("last_update", System.currentTimeMillis()).apply();
            sendBroadcast(new Intent().setAction(StockWidgetProvider.ACTION_UPDATE));
        }
        adapter.setCursor(data);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }
}
