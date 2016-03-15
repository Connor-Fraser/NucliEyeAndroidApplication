package cfrase49.nuclieyemapstest.APICalls.Get;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public abstract class APIGetCall extends AsyncTask<String, String, APIResult> {
    private APIResult result;
    private Context contextOfCall;
    public final static String FAILED_STRING = "Web Server Inaccesible";

    public APIGetCall(APIResult input, Context context){
        result = input;
        contextOfCall = context;
    }

    @Override
    protected APIResult doInBackground(String... params) {

        String urlString = params[0]; // URL to call

        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

            result.parseJson(reader);
            in.close();

        } catch (Exception e) {

            onFailure();
            System.out.println(e.getMessage());
        }

        return result;
    }

    private void onFailure(){
        Toast failToast = Toast.makeText(contextOfCall, APIGetCall.FAILED_STRING, Toast.LENGTH_LONG);
        failToast.show();
    }

    protected void onPostExecute(APIResult result) {
        return;
    }
}
