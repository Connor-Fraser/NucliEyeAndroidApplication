package cfrase49.nuclieyemapstest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cfrase49.nuclieyemapstest.APICalls.Get.APIGetCall;
import cfrase49.nuclieyemapstest.APICalls.Get.APIResult;
import cfrase49.nuclieyemapstest.APICalls.Get.AlertsCallResults;
import cfrase49.nuclieyemapstest.APICalls.Get.CurrentDataCallResults;

//TODO: change data timer to 1-5 minutes onSTOP and prepare alert if needed (if better idea)
//TODO: add machine selector at top right if there is time
//TODO: make UI pretty
public class MachineDataActivity extends AppCompatActivity {
    public static String MACHINE_ID_STRING = "com.nuclieye.selectedmachine.ID_MESSAGE";
    public static String METRIC_STRING = "com.nuclieye.selectedmetric.CHOSEN_MESSAGE";
    public static String METRIC_THRESHOLD = "com.nuclieye.selectedmetricthreshold.MESSAGE";

    private int machineID;
    private final int CALL_INTERVAL = 5000;
    private Timer timer;
    private AlertsCallResults alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        machineID = intent.getIntExtra(MapsActivity.SELECTED_MACHINE, -1);
        TextView menu = (TextView) findViewById(R.id.machine_toolbar);
        menu.setText(String.format(" - Machine %d", machineID));
    }

    @Override
    protected void onStart() {
        //set up repeated calls for machine's current data
        timer = new Timer();
        repeatDataCall();

        //get user's alert thresholds for the selected machine
        getAlerts();
        super.onStart();
    }

    @Override
    protected void onStop() {
        timer.cancel();
        timer.purge();
        timer = null;
        super.onStop();
    }

    public void metricHistory(View v){
        String metric;
        double threshold;

        if(v.getId()==R.id.TempLayout) {
            metric = "temperature";
            threshold = alerts.tempAlert;
        }
        else if(v.getId()==R.id.HumidityLayout) {
            metric = "humidity";
            threshold = alerts.humidityAlert;
        }
        else if(v.getId()==R.id.CoLayout){
            metric = "co";
            threshold = alerts.coAlert;
        }
        else if(v.getId()==R.id.Co2Layout) {
            metric = "co2";
            threshold = alerts.co2Alert;
        }
        else if(v.getId()==R.id.RadiationLayout) {
            metric = "radiation";
            threshold = alerts.radiationAlert;
        }
        else {
            metric = "";
            threshold = -1;
        }
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        intent.putExtra(MACHINE_ID_STRING, machineID);
        intent.putExtra(METRIC_STRING, metric);
        intent.putExtra(METRIC_THRESHOLD, threshold);
        startActivity(intent);
    }

    private void getAlerts(){
        AlertsCallResults alertsCallResults = new AlertsCallResults();
        APIGetCall getAlertsCall = new AlertsAPIGetCall(alertsCallResults, getApplicationContext());
        alertsCallResults.completeURL(machineID);
        getAlertsCall.execute(alertsCallResults.url);
    }

    private void getCurrentData(){
        CurrentDataCallResults getCurrentData = new CurrentDataCallResults();
        APIGetCall getCurrentDataCall = new CurrentDataAPIGetCall(getCurrentData, getApplicationContext());
        getCurrentData.completeURL(machineID);
        getCurrentDataCall.execute(getCurrentData.url);
    }

    private void repeatDataCall() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    //get the machine's current data
                    getCurrentData();
                } catch (Exception e) {
                    Toast failToast = Toast.makeText(getApplicationContext(), APIGetCall.FAILED_STRING, Toast.LENGTH_SHORT);
                    failToast.show();
                }
            }
        }, 0, CALL_INTERVAL);
    }

    private void updateDataTextViews(CurrentDataCallResults currentDataResult){
        TextView temp;
        String textString;

        temp = (TextView) findViewById(R.id.TempValue);
        if(currentDataResult.temp != null) {
            textString = String.format("%.2f/", currentDataResult.temp);
            if (currentDataResult.temp >= alerts.tempAlert)
                temp.setTextColor(getResources().getColor(R.color.alert));
        }
        else{
            textString = getResources().getString(R.string.na_string);
        }
        temp.setText(textString);


        temp = (TextView) findViewById(R.id.HumidityValue);
        if(currentDataResult.humidity != null) {
            textString = String.format("%.2f/", currentDataResult.humidity);
            if (currentDataResult.humidity >= alerts.humidityAlert)
                temp.setTextColor(getResources().getColor(R.color.alert));
        }
        else{
            textString = getResources().getString(R.string.na_string);
        }
        temp.setText(textString);


        temp = (TextView) findViewById(R.id.CoValue);
        if(currentDataResult.co != null) {
            textString = String.format("%.2f/", currentDataResult.co);
            if (currentDataResult.co >= alerts.coAlert)
                temp.setTextColor(getResources().getColor(R.color.alert));
        }
        else{
            textString = getResources().getString(R.string.na_string);
        }
        temp.setText(textString);


        temp = (TextView) findViewById(R.id.Co2Value);
        if(currentDataResult.co2 != null) {
            textString = String.format("%.2f/", currentDataResult.co2);
            if (currentDataResult.co2 >= alerts.co2Alert)
                temp.setTextColor(getResources().getColor(R.color.alert));
        }
        else{
            textString = getResources().getString(R.string.na_string);
        }
        temp.setText(textString);


        temp = (TextView) findViewById(R.id.RadiationValue);
        if(currentDataResult.radiation != null) {
            textString = String.format("%.2f/", currentDataResult.radiation);
            if (currentDataResult.radiation >= alerts.radiationAlert)
                temp.setTextColor(getResources().getColor(R.color.alert));
        }
        else{
            textString = getResources().getString(R.string.na_string);
        }
        temp.setText(textString);

        temp = (TextView) findViewById(R.id.timestampValue);
        temp.setText(currentDataResult.timestamp.toString());
    }

    private void updateAlertTextViews(){

        TextView temp = (TextView)findViewById(R.id.TempThreshold);
        String textString = String.format("%.2f",alerts.tempAlert);
        textString += getResources().getString(R.string.temp_units);
        temp.setText(textString);

        temp = (TextView)findViewById(R.id.HumidityThreshold);
        textString = String.format("%.2f", alerts.humidityAlert);
        textString += getResources().getString(R.string.humidity_units);
        temp.setText(textString);

        temp = (TextView)findViewById(R.id.CoThreshold);
        textString = String.format("%.2f", alerts.coAlert);
        textString += getResources().getString(R.string.co_units);
        temp.setText(textString);

        temp = (TextView)findViewById(R.id.Co2Threshold);
        textString = String.format("%.2f", alerts.co2Alert);
        textString += getResources().getString(R.string.co2_units);
        temp.setText(textString);

        temp = (TextView)findViewById(R.id.RadiationThreshold);
        textString = String.format("%.2f", alerts.radiationAlert);
        textString += getResources().getString(R.string.radiation_units);
        temp.setText(textString);

    }

    private class AlertsAPIGetCall extends APIGetCall {
        public AlertsAPIGetCall(APIResult result, Context context){super(result, context);}

        @Override
        protected void onPostExecute(APIResult result) {
            alerts = (AlertsCallResults)result;
            updateAlertTextViews();
        }
    }

    private class CurrentDataAPIGetCall extends APIGetCall {
        public CurrentDataAPIGetCall(APIResult result, Context context){super(result, context);}

        @Override
        protected void onPostExecute(APIResult result) {
            CurrentDataCallResults currentDataResult = (CurrentDataCallResults)result;
            updateDataTextViews(currentDataResult);
        }
    }
}
