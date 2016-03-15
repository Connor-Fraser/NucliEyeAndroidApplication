package cfrase49.nuclieyemapstest.APICalls.Put;

/**
 * Created by T420 on 3/8/2016.
 */
public class AlertsPutCallInput extends PutCallInput{
    String metric;
    int userID = 1;
    int machineID;
    Double newThreshold;

    public AlertsPutCallInput(String metric, int machineID, Double newThreshold){
        url = url+"alerts";
        this.metric = metric;
        this.machineID = machineID;
        this.newThreshold = newThreshold;
        encodePayload();
    }

    public void encodePayload(){
        payload = String.format("userID=%d&machineID=%d&metric=%s&threshold=%.2f",userID,machineID,metric,newThreshold);
    }
}
