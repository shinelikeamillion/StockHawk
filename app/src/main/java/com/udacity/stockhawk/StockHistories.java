package com.udacity.stockhawk;

import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

public class StockHistories {

    public List<PointValue> histories;
    public float referenceTime;
    public float minPrice;
    public float maxPrice;

    public StockHistories (List<PointValue> histories, float referenceTime, float minPrice, float maxPrice) {
        this.histories = histories;
        this.referenceTime = referenceTime;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

}
