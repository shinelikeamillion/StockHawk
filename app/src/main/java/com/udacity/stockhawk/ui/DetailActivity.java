package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockHistories;
import com.udacity.stockhawk.Utilities;
import com.udacity.stockhawk.data.Contract.Quote;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri stockUri;

    private LineChartView chart;
    private LineChartData data;

    private boolean hasAxes = true; // 是否有坐标轴
    private boolean hasAxesName = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private boolean isFilled = false;
    private ValueShape valueShape = ValueShape.CIRCLE;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;

    private int numberOfPoints;

    private String historyData;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        context = this;

        chart = (LineChartView) findViewById(R.id.lineChart);

        stockUri = getIntent().getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(100, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (stockUri != null) {
            return new CursorLoader(
                    context,
                    stockUri,
                    Quote.QUOTE_COLUMNS,
                    null,
                    null,
                    null
            );
        } else {

            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()
                && historyData == null) {

            historyData = data.getString(Quote.POSITION_HISTORY);
            Timber.d("eee", historyData);
            setUpLineChart();
        }
    }

    private void setUpLineChart() {
        StockHistories stockHistories;
        Line line;
        stockHistories = Utilities.getFormattedStockHistory(historyData);
        numberOfPoints = stockHistories.histories.size();

        line = new Line(stockHistories.histories);
        line.setColor(ChartUtils.COLORS[2]);
        line.setShape(ValueShape.CIRCLE);
        line.setStrokeWidth(1);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(true);
        line.setPointRadius(2);

        if (pointsHaveDifferentColor){
            line.setPointColor(ChartUtils.COLORS[(2 + 1) % ChartUtils.COLORS.length]);
        }

        List<Line> lines = new ArrayList<>();
        lines.add(line);
        data = new LineChartData(lines);

        if (hasAxes) {

            List<AxisValue> axisXValues = new ArrayList<>();
            List<AxisValue> axisYValues = new ArrayList<>();

            int i = 0;
            float b = (stockHistories.maxPrice - stockHistories.minPrice) / 15;
            for (PointValue pointValue : stockHistories.histories) {
                float x = pointValue.getX();

                String date = Utilities.formatDate(stockHistories.referenceTime+x);
                String price = Utilities.formatPrice(i * b + stockHistories.minPrice);

                axisXValues.add(i, new AxisValue(x).setLabel(date));
                axisYValues.add(i, new AxisValue(i * b).setLabel(price));

                i++;
            }

            Axis axisX = new Axis(axisXValues);
            axisX.setMaxLabelChars(8);
            Axis axisY = new Axis(axisYValues);

            if (hasAxesName) {
                axisX.setName("date");
                axisY.setName("price");
            }

            axisY.setHasLines(true);

            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setLineChartData(data);

        setViewPort(stockHistories);
    }

    private void setViewPort(StockHistories stockHistories) {
        float b = (stockHistories.maxPrice - stockHistories.minPrice) / 15;

        Viewport v = chart.getMaximumViewport();

        v.set(v.left, stockHistories.maxPrice - stockHistories.minPrice + 2*b , v.right, 0);
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
