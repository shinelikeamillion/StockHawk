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
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.utilities.CircularRevealAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddStockDialog extends DialogFragment {

    @BindView(R.id.dialog_stock)
    EditText stock;

    private CircularRevealAnimation mCircularRevealAnimation;

    private void initAnim (final View animView) {
        mCircularRevealAnimation = new CircularRevealAnimation();

        stock.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                stock.getViewTreeObserver().removeOnPreDrawListener(this);
                mCircularRevealAnimation.show(animView.getRootView());
                return true;
            }
        });
    }

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

                        if (stock.getText().toString().isEmpty()) {

                            Toast.makeText(getActivity(), R.string.toast_require_name, Toast.LENGTH_SHORT).show();
                        } else {

                            addStock();
                        }
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        initAnim(custom);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        Activity parent = getActivity();

        if (parent instanceof MainActivity) {
            ((MainActivity) parent).addStock(stock.getText().toString());
        }

        dismissAllowingStateLoss();
    }
}
