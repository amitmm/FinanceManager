package edu.illinois.financemanager.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.illinois.financemanager.EmailValidator;

/**
 * Abstract super class for Activities that communicate to a server using a JSONObject
 */
public abstract class AccountActivity extends MenuActivity {

    protected static String urlString;
    protected EmailValidator emailValidator;
    protected String activityName;

    protected static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Callback function called by TransmitPostTask's onPostExecute
     *
     * @param response - JSONObject containing the server's response
     */
    public abstract void taskCallBack(JSONObject response);

    /**
     * AsyncTask that sends an HTTP post using a JSONObject and returns a JSONObject
     * A subclass of AccountActivity handles the onPostExecute by implementing the taskCallBack function
     */
    public class TransmitPostTask extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            try {
                // Simulate network access.
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);

                StringEntity entity = new StringEntity(params[0].toString(), HTTP.UTF_8);
                entity.setContentType("application/json");

                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpPost httpPost = new HttpPost(urlString);
                httpPost.setEntity(entity);

                HttpResponse response;
                response = client.execute(httpPost);

                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity);

                return new JSONObject(result);
            } catch (IOException e) {
                Log.d(activityName, "IO Exception" + e);
                return null;
            } catch (JSONException je) {
                Log.d(activityName, "JSON Exception " + je);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject responseData) {
            taskCallBack(responseData);
        }
    }

}
