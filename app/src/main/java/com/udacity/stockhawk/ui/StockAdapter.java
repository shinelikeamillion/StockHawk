package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private Cursor cursor;
    private StockAdapterOnClickHandler clickHandler;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);

        holder.name.setText(cursor.getString(Quote.POSITION_STOCK_NAME));

        holder.symbolAndExchangeName.setText(
                String.format(context.getResources().getString(R.string.symbol_and_exchange_name),
                cursor.getString(Quote.POSITION_SYMBOL),
                cursor.getString(Quote.POSITION_EXCHANGE_NAME)));

        holder.price.setText(String.format(context.getString(R.string.price),
                Utilities.getDollorFormat().format(cursor.getFloat(Quote.POSITION_PRICE))));


        float rawAbsoluteChange = cursor.getFloat(Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                holder.change.setTextColor(ContextCompat.getColor(context, R.color.material_green_900));
            }
        } else {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                holder.change.setTextColor(ContextCompat.getColor(context, R.color.material_red_700));
            }
        }

        String change = Utilities.getDollorFormatWithPlus().format(rawAbsoluteChange);
        String percentage = Utilities.getPercentageFormat().format(percentageChange / 100);

        holder.change.setText(String.format(context.getResources().getString(R.string.change),
                change,
                percentage));
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }


    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.symbol_and_exchange_name)
        TextView symbolAndExchangeName;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            clickHandler.onClick(cursor.getString(symbolColumn));
        }


    }
}
