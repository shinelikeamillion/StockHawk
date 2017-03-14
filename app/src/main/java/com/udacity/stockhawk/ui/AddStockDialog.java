package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment {

    @BindView(R.id.dialog_stock)
    EditText stock;

    public static final int MSG_WHAT_STOCK_NOT_EXIST = 0x11;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addStock();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        final String stockSymbol = stock.getText().toString();
        final Activity parent = getActivity();

        new Thread(){
            @Override
            public void run() {
                try {
                    Stock sk = YahooFinance.get(stockSymbol);
                    if (sk.getQuote().getPrice() == null) {
                        Timber.e("there is a null");
                        stock.clearComposingText();
                        ((MainActivity) parent).addStockHandler.sendEmptyMessage(MSG_WHAT_STOCK_NOT_EXIST);
                    }
                } catch (IOException e) {
                    Timber.e(e, "get stock error");
                    e.printStackTrace();
                }

            }
        }.start();

//        if (parent instanceof MainActivity) {
//            ((MainActivity) parent).addStock(stockSymbol);
//        }

        dismissAllowingStateLoss();
    }


}
