package cfrase49.nuclieyemapstest.APICalls.Get;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.Date;

public class CurrentDataCallResults extends APIResult {
    public String url = baseUrl+"current/";
    public Date timestamp;
    public Double temp;
    public Double humidity;
    public Double co;
    public Double co2;
    public Double radiation;

    public void completeURL(int machineID){
        url=url+machineID;
    }

    public void parseJson(JsonReader reader) throws IOException {
        timestamp=null;
        temp=null;
        humidity=null;
        co=null;
        co2=null;
        radiation=null;

        reader.beginArray();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            JsonToken next = reader.peek();

            if(next != JsonToken.NULL){
                if(name.equals("data_timestamp")) {
                    long milliseconds = reader.nextLong() *1000;
                    timestamp = new Date(milliseconds);
                }
                else if(name.equals("temperature"))
                    temp = reader.nextDouble();
                else if(name.equals("humidity"))
                    humidity = reader.nextDouble();
                else if(name.equals("co"))
                    co = reader.nextDouble();
                else if(name.equals("co2"))
                    co2 = reader.nextDouble();
                else if(name.equals("radiation"))
                    radiation = reader.nextDouble();
                else
                    reader.skipValue();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.endArray();
    }
}
