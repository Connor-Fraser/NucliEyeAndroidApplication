package cfrase49.nuclieyemapstest.APICalls.Get;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;

public class MachineCallResults extends APIResult {
    //The '1' is the user ID that this app utilizes
    public String url = baseUrl+"machines/1";
    public ArrayList<Machine> machines;

    public void parseJson(JsonReader reader) throws IOException {
        machines = new ArrayList<Machine>();

        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();
            Machine temp = new Machine();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("machine_id"))
                    temp.machineID = reader.nextInt();
                else if (name.equals("latitude"))
                    temp.lat = reader.nextDouble();
                else if (name.equals("longitude"))
                    temp.lon = reader.nextDouble();
                else
                    reader.skipValue();
            }
            machines.add(temp);
            reader.endObject();
        }
        reader.endArray();
        return;
    }

    public class Machine {
        public int machineID;
        public double lat;
        public double lon;
    }
}
