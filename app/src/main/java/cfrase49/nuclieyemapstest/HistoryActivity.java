package cfrase49.nuclieyemapstest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cfrase49.nuclieyemapstest.APICalls.Get.APIGetCall;
import cfrase49.nuclieyemapstest.APICalls.Get.APIResult;
import cfrase49.nuclieyemapstest.APICalls.Get.HistoryCallResults;
import cfrase49.nuclieyemapstest.APICalls.Put.APIPutCall;
import cfrase49.nuclieyemapstest.APICalls.Put.AlertsPutCallInput;

//TODO: Make Graph Prettier
//TODO: change graph axis when alert is moved
public class HistoryActivity extends AppCompatActivity {
    private String metric;
    private int machineID;
    private double threshold;
    private double newThreshold;
    private double maxValue;
    private LineChart historyChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        metric = intent.getStringExtra(MachineDataActivity.METRIC_STRING);
        machineID = intent.getIntExtra(MachineDataActivity.MACHINE_ID_STRING, -1);
        threshold = intent.getDoubleExtra(MachineDataActivity.METRIC_THRESHOLD, -1);
        newThreshold = threshold;

        EditText editThreshold = (EditText)findViewById(R.id.thresholdField);
        editThreshold.setText(String.format("%.2f", newThreshold));

        String menuText=" - " + metric.substring(0, 1).toUpperCase() + metric.substring(1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbar.getTitle() + menuText);

        historyChart = (LineChart) findViewById(R.id.chart);
        historyChart.setBackgroundColor(getResources().getColor(R.color.chartBackground));
        historyChart.setDescriptionColor(R.color.chartDescription);
        historyChart.setDescription("Summary of " + metric + " over last 30 days");
        historyChart.setDescriptionTextSize(15);
        historyChart.getLegend().setTextSize(13);
        historyChart.setNoDataText("No Data is Available");
        getHistory();
    }

    public void updateThreshold(View v){
        EditText newValueView = (EditText)findViewById(R.id.thresholdField);

        try {
            newThreshold = Double.parseDouble(newValueView.getText().toString());
            if (newThreshold != threshold) {
                AlertsPutCallInput in = new AlertsPutCallInput(metric, machineID, newThreshold);
                APIPutCall call = new AlertsPutCall();
                call.execute(in);
            }
        }
        catch(NumberFormatException e){
            newValueView.setText(String.format("%.2f", threshold));
            newThreshold = threshold;
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public void getHistory(){
        HistoryCallResults historyCallResults = new HistoryCallResults();
        APIGetCall getHistoryCall = new HistoryAPIGetCall(historyCallResults, getApplicationContext());
        historyCallResults.completeURL(machineID, metric);
        getHistoryCall.execute(historyCallResults.url);
    }

    private class HistoryAPIGetCall extends APIGetCall {
        public HistoryAPIGetCall(APIResult result, Context context){super(result, context);}

        @Override
        protected void onPostExecute(APIResult result) {

            maxValue = 0;
            HistoryCallResults historyCallResults = (HistoryCallResults)result;

            DateFormat dateFormat = new SimpleDateFormat("MMM-dd");
            Calendar thirtyAgo = Calendar.getInstance();
            thirtyAgo.add(Calendar.DATE, -30);
            thirtyAgo.set(Calendar.HOUR, 0);
            thirtyAgo.set(Calendar.MINUTE, 0);
            thirtyAgo.set(Calendar.SECOND, 0);
            thirtyAgo.set(Calendar.MILLISECOND, 0);

            ArrayList<Entry> lowEntries = new ArrayList<Entry>();
            ArrayList<Entry> avgEntries = new ArrayList<Entry>();
            ArrayList<Entry> highEntries = new ArrayList<Entry>();
            ArrayList<Entry> alert = new ArrayList<Entry>();

            alert.add(new Entry((float) threshold, 0));
            alert.add(new Entry((float) threshold, 29));

            for(HistoryCallResults.DataPoint data:historyCallResults.dataPoints){
                //xVar is never more than 30, so conversion is safe
                Date dataDate = data.dataDate;

                int xVar = Integer.parseInt(String.valueOf(getDateDiff(thirtyAgo.getTime(),dataDate, TimeUnit.DAYS)));

                if(data.metricLow != null){
                    Entry lowEntry = new Entry(data.metricLow.floatValue(), xVar);
                    lowEntries.add(lowEntry);
                }
                if(data.metricAvg != null){
                    Entry avgEntry = new Entry(data.metricAvg.floatValue(), xVar);
                    avgEntries.add(avgEntry);
                }
                if(data.metricHigh != null){
                    Entry highEntry = new Entry(data.metricHigh.floatValue(), xVar);
                    highEntries.add(highEntry);

                    if(data.metricHigh > maxValue)
                        maxValue = data.metricHigh;
                }
            }

            LineDataSet lowSet = new LineDataSet(lowEntries, "Daily Low");
            lowSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lowSet.setLineWidth(3);
            lowSet.setCircleRadius(5);
            lowSet.setColor(getResources().getColor(R.color.lowChartColour));
            lowSet.setCircleColor(getResources().getColor(R.color.lowChartColour));
            lowSet.setDrawCubic(true);

            LineDataSet avgSet = new LineDataSet(avgEntries, "Daily Average");
            avgSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            avgSet.setLineWidth(3);
            avgSet.setCircleRadius(5);
            avgSet.setColor(getResources().getColor(R.color.avgChartColour));
            avgSet.setCircleColor(getResources().getColor(R.color.avgChartColour));
            avgSet.setDrawCubic(true);

            LineDataSet highSet = new LineDataSet(highEntries, "Daily High");
            highSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            highSet.setLineWidth(3);
            highSet.setCircleRadius(5);
            highSet.setColor(getResources().getColor(R.color.highChartColour));
            highSet.setCircleColor(getResources().getColor(R.color.highChartColour));
            highSet.setDrawCubic(true);

            LineDataSet alertSet = new LineDataSet(alert, "Alert Threshold");
            alertSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            alertSet.setLineWidth(5);
            alertSet.setCircleRadius(0);
            alertSet.setColor(getResources().getColor(R.color.alertChartColour));
            alertSet.setDrawValues(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(lowSet);
            dataSets.add(avgSet);
            dataSets.add(highSet);
            dataSets.add(alertSet);

            ArrayList<String> xVals = new ArrayList<String>();
            for(int i=0; i<30; i++){
                String dateString = dateFormat.format(thirtyAgo.getTime());
                thirtyAgo.add(Calendar.DATE, 1);
                xVals.add(dateString);
            }

            LineData data = new LineData(xVals, dataSets);
            historyChart.setData(data);
            updateAxis();

            Button updateAlertButton = (Button)findViewById(R.id.changeButton);
            updateAlertButton.setEnabled(true);
        }
    }

    private void updateAxis(){
        YAxis axis = historyChart.getAxisLeft();
        if(maxValue > threshold)
            axis.setAxisMaxValue((float)(maxValue+(0.1*maxValue)));
        else
            axis.setAxisMaxValue((float)(threshold+(0.1*threshold)));
        historyChart.notifyDataSetChanged();
        historyChart.invalidate();
    }

    private class AlertsPutCall extends APIPutCall {
        @Override
        protected void onPostExecute(Integer callResult) {
            if(callResult == -1){
                Toast failToast = Toast.makeText(getApplicationContext(), "Unable to update threshold", Toast.LENGTH_SHORT);
                failToast.show();
            }
            else {
                threshold = newThreshold;
                EditText editThreshold = (EditText)findViewById(R.id.thresholdField);
                editThreshold.setText(String.format("%.2f", newThreshold));

                Toast successToast = Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT);
                successToast.show();

                historyChart.getLineData().getDataSetByLabel("Alert Threshold", true).getEntryForXIndex(0).setVal((float) newThreshold);
                historyChart.getLineData().getDataSetByLabel("Alert Threshold", true).getEntryForXIndex(29).setVal((float) newThreshold);

                updateAxis();
            }
        }
    }
}
