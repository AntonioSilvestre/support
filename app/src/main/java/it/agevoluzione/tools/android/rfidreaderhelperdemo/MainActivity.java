package it.agevoluzione.tools.android.rfidreaderhelperdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import it.agevoluzione.tools.android.rfidreaderhelper.ReaderConfiguratorImpl;
import it.agevoluzione.tools.android.rfidreaderhelper.ReaderHelper;

public class MainActivity extends AppCompatActivity {

    ReaderHelper readerHelper;

    Button button1;
    Button button2;
    TextView testo;

    @Override
    protected void onStop() {
        super.onStop();
        readerHelper.stopReading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        readerHelper.close(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        Bind View
        button1 = findViewById(R.id.connection_button_1);
        button2 = findViewById(R.id.connection_button_2);

        testo = findViewById(R.id.testo);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (null != tag) {
                    switch (tag) {
                        case "Request":
                            readerHelper.recheckPresence(MainActivity.this);
                            break;
                        case "Connect":
                            try {
                                readerHelper.connect(MainActivity.this, new ReaderConfiguratorImpl());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Read":
                            try {
                                readerHelper.startReading();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Stop":
                            readerHelper.stopReading();
                            break;
                        case "Disconnect":
                            readerHelper.disconnect(MainActivity.this);
                            break;
                    }
                }
            }
        };

        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);



//        Create Reader
        readerHelper = new ReaderHelper();
        readerHelper.setStatusListener(new ReaderHelper.StatusListener() {
            @Override
            public void onStatusChange(final int status) {
                Snackbar.make(toolbar, readerHelper.getStatusName(status),Snackbar.LENGTH_LONG).show();
                button1.setTag(null);
                button2.setTag(null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (status) {
                            case ReaderHelper.NOT_INITIALIZED:
                                testo.setText("");
                                button1.setText("Non Inizialized!");
                                button1.setEnabled(false);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.INITIALIZED:
                                testo.setText("");
                                button1.setEnabled(false);
                                button1.setText("No Device!");
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.USB_DEVICE_ATTACHED_NOT_AUTHORIZED:
                                testo.setText("");
                                button1.setText("Request");
                                button1.setTag("Request");
                                button1.setEnabled(true);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.USB_DEVICE_AUTHORIZATION_GRANT:
                                testo.setText("");
                                button1.setText("Connect");
                                button1.setTag("Connect");
                                button1.setEnabled(true);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.READER_CONNECTING:
                                testo.setText("");
                                button1.setText("Connecting...");
                                button1.setEnabled(false);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.READER_CONNECTED:
                                testo.setText("");
                                button1.setText("Configuring...");
                                button1.setEnabled(false);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.READER_CONFIGURED:
                                testo.setText("");
                                button1.setText("Read");
                                button1.setTag("Read");
                                button1.setEnabled(true);
                                button2.setVisibility(View.INVISIBLE);
                                break;
                            case ReaderHelper.READING:
                                testo.setText("Reading...");
                                button1.setText("Stop");
                                button1.setEnabled(true);
                                button1.setTag("Stop");
                                button2.setEnabled(true);
                                button2.setVisibility(View.VISIBLE);
                                button2.setText("Disconnect");
                                button2.setTag("Disconnect");
                                break;
                        }
                    }

                });

            }
        });

        readerHelper.setErrorListener(new ReaderHelper.ErrorListener() {
            @Override
            public void onError(final Throwable throwable) {
                Log.e("MainActivity", "ERR", throwable);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"ERR: "+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        readerHelper.setReaderListener(new ReaderHelper.ReaderListener() {
            @Override
            public void onRead(final com.thingmagic.TagReadData tagReadData) {
                testo.post(new Runnable() {
                    @Override
                    public void run() {
                        testo.append("\n"+tagReadData.epcString());
                    }
                });
            }
        });

        readerHelper.init(this);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
