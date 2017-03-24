package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RemoteStockService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        return new RemoteStockService(this.getApplicationContext(), intent);
    }
}
