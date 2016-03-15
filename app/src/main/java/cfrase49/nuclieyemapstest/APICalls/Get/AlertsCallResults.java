package cfrase49.nuclieyemapstest.APICalls.Get;


import android.util.JsonReader;

import java.io.IOException;

public class AlertsCallResults extends APIResult {
    //the 1 is this apps user ID
    public String url = baseUrl+"alerts/1/";
    public Double tempAlert;
    public Double humidityAlert;
    public Double coAlert;
    public Double co2Alert;
    public Double radiationAlert;

    public void completeURL(int machineID){
        url=url+machineID;
    }

    public void parseJson(JsonReader reader) throws IOException {
        tempAlert=null;
        humidityAlert=null;
        coAlert=null;
        co2Alert=null;
        radiationAlert=null;

        reader.beginArray();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("temperature_threshold"))
                tempAlert = reader.nextDouble();
            else if(name.equals("humidity_threshold"))
                humidityAlert = reader.nextDouble();
            else if(name.equals("co_threshold"))
                coAlert = reader.nextDouble();
            else if(name.equals("co2_threshold"))
                co2Alert = reader.nextDouble();
            else if(name.equals("radiation_threshold"))
                radiationAlert = reader.nextDouble();
            else
                reader.skipValue();
        }
        reader.endObject();
        reader.endArray();
    }
}
