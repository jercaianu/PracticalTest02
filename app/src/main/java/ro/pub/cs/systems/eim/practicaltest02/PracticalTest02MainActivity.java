package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private ServerThread serverThread;
    private EditText queryText;
    private Button startButton;
    private Button queryButton;
    private TextView queryDisplay;
    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private StartQueryClickListener startQueryClickListener = new StartQueryClickListener();
    private class StartQueryClickListener implements Button.OnClickListener {
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            ClientThread cthread = new ClientThread(new String("127.0.0.1"), Integer.parseInt(serverPort), queryText.getText().toString(),
                    queryDisplay);
            cthread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port);
        queryText = (EditText)findViewById(R.id.query_text);
        startButton = (Button)findViewById(R.id.start_server);
        queryButton = (Button)findViewById(R.id.start_query);
        queryDisplay = (TextView)findViewById(R.id.result_text);

        startButton.setOnClickListener(connectButtonClickListener);
        queryButton.setOnClickListener(startQueryClickListener);
    }
}
