package cfrase49.nuclieyemapstest.APICalls.Put;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//TODO: Better error handling
public abstract class APIPutCall extends AsyncTask<PutCallInput, String, Integer> {
    @Override
    protected Integer doInBackground(PutCallInput... params) {
        PutCallInput input = params[0];
        int responseCode;

        try {
            URL url = new URL(input.url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(input.payload);
            writer.flush();
            writer.close();
            os.close();
            responseCode = conn.getResponseCode();
        }
        catch(Exception e){
            e.printStackTrace();
            responseCode = -1;
        }

        return responseCode;
    }

    @Override
    abstract protected void onPostExecute(Integer callResult);
}
