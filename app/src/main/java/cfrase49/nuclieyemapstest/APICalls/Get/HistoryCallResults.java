package cfrase49.nuclieyemapstest.APICalls.Get;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryCallResults extends APIResult{
    public String url = baseUrl+"history/";
    public String metric;
    public ArrayList<DataPoint> dataPoints;

    public void completeURL(int machineID, String metric){
        url=url+machineID+"/"+metric;
        this.metric = metric;
    }

    public void parseJson(JsonReader reader) throws IOException {
        dataPoints = new ArrayList<DataPoint>();
        Calendar calendar = Calendar.getInstance();

        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();
            DataPoint temp = new DataPoint();
            while (reader.hasNext()) {
                String name = reader.nextName();
                JsonToken next = reader.peek();
                if(next != JsonToken.NULL){
                    if(name.equals("historic_date")) {
                        String stringDate = reader.nextString();
                        int year = Integer.valueOf(stringDate.substring(0, 4));
                        int month = Integer.valueOf(stringDate.substring(5, 7));
                        int day = Integer.valueOf(stringDate.substring(8, 10));
                        calendar.set(year,month-1,day,0,0,0);

                        temp.dataDate = calendar.getTime();
                    }
                    else if(name.contains("low")){
                        temp.metricLow = reader.nextDouble();
                    }
                    else if(name.contains("average")){
                        temp.metricAvg = reader.nextDouble();
                    }
                    else if(name.contains("high")){
                        temp.metricHigh = reader.nextDouble();
                    }
                    else
                        reader.skipValue();
                }
                else {
                    reader.skipValue();
                }
            }
            dataPoints.add(temp);
            reader.endObject();
        }
        reader.endArray();
        return;
    }

    public class DataPoint{
        public Double metricLow;
        public Double metricAvg;
        public Double metricHigh;
        public Date dataDate;

        public DataPoint(){
            metricLow = null;
            metricAvg = null;
            metricHigh = null;
            dataDate = null;
        }
    }
}
