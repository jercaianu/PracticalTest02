package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ajercaianu on 5/18/17.
 */

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String word;
    private TextView queryText;

    private Socket socket;

    public ClientThread(String address, int port, String word, TextView queryText) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.queryText = queryText;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            Log.e(Constants.TAG, "[CLIENT THREAD] Here");

            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(word);
            printWriter.flush();
            String result;

            while ((result = bufferedReader.readLine()) != null) {
                final String finalresult = result;
                queryText.post(new Runnable() {
                    @Override
                    public void run() {
                        queryText.setText(finalresult);
                    }
                });
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}