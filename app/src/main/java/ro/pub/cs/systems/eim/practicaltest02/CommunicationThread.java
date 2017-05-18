package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by ajercaianu on 5/18/17.
 */

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");
            String word = bufferedReader.readLine();
            //String informationType = bufferedReader.readLine();


            Log.i(Constants.TAG, "[COMMUNICATION THREAD] WORD IS: " + word);
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(Constants.WEB_ADDR + word);
            Log.i(Constants.TAG, "Get query is: " + Constants.WEB_ADDR + word);
           // UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            //httpPost.setEntity(urlEncodedFormEntity);
            //ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpResponse resp = httpClient.execute(httpGet);
            //ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //String content = httpClient.execute(httpGet, responseHandler);
            HttpEntity httpGetEntity = resp.getEntity();
            //Log.i(Constants.TAG, pageSourceCode.toString());
            String data = EntityUtils.toString(httpGetEntity);
            JSONObject content = new JSONObject(data);
            JSONArray jsonArray = content.getJSONArray("RESULTS");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            //JSONObject currentObservation = content.getJSONObject("RESULTS");
            //String result = content.getString("RESULT");
           // String result = new String("result");
            String result = jsonObject.getString(new String("name"));
            Log.i(Constants.TAG, "RESULT IS: " + result);

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
