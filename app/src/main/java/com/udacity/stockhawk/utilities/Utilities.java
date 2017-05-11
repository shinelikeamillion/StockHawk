package com.udacity.stockhawk.utilities;

import com.udacity.stockhawk.StockHistories;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PointValue;

public class Utilities {

    /**
     * example : 1480050000000:60.4453453\n148005002340:61.44234345
     * @param historyStr
     * @return
     */
    public static StockHistories getFormattedStockHistory (String historyStr) {

        List<PointValue> pointValues = new ArrayList<>();
        List<Float> timeData = new ArrayList<>();
        List<Float> stockPrice = new ArrayList<>();
        String[] dataPairs = historyStr.split("\n");

        Float minPrice = Float.valueOf(dataPairs[0].split(":")[1]);
        Float maxPrice = minPrice;
        Float price;
        for (String pair : dataPairs) {
            String[] entry = pair.split(":");
            timeData.add(Float.valueOf(entry[0]));
            price = Float.valueOf(entry[1]);
            stockPrice.add(Float.valueOf(entry[1]));

            if (price < minPrice) {
                minPrice = price;
            }
            if (price > maxPrice) {
                maxPrice = price;
            }

        }
        float b = (maxPrice - minPrice) / 15;

        Collections.reverse(timeData);
        Collections.reverse(stockPrice);

        Float referenceTime = timeData.get(0);
        for (int i = 0; i < timeData.size(); i++) {
            pointValues.add(new PointValue(timeData.get(i) - referenceTime, stockPrice.get(i) - minPrice + b ));
        }

        return new StockHistories(pointValues, referenceTime, minPrice, maxPrice);
    }

    public static String formatDate (float date) {
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
        return format.format(date);
    }

    public static String formatPrice (float price) {
        return String.format("%.2f", price);
    }

    public static DecimalFormat getDollarFormatWithPlus() {

        DecimalFormat dollarFormatWithPlus;
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+");
        dollarFormatWithPlus.setNegativePrefix("-");

        return dollarFormatWithPlus;
    }

    public static DecimalFormat getDollarFormat() {

        DecimalFormat dollarFormat;
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormatSymbols symbols = dollarFormat.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        dollarFormat.setDecimalFormatSymbols(symbols);

        return dollarFormat;
    }

    public static DecimalFormat getPercentageFormat () {

        DecimalFormat percentageFormat;
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        return percentageFormat;
    }

}
