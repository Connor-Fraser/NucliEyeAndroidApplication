package cfrase49.nuclieyemapstest.APICalls.Get;

import android.util.JsonReader;

import java.io.IOException;
import java.io.Serializable;


public abstract class APIResult implements Serializable {
    abstract void parseJson(JsonReader reader)throws IOException;
    public String baseUrl = "http://salty-tor-14520.herokuapp.com/";
}
