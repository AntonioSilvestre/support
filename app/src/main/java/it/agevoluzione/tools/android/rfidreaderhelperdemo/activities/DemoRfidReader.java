package it.agevoluzione.tools.android.rfidreaderhelperdemo.activities;

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
import it.agevoluzione.tools.android.rfidreaderhelperdemo.R;
import it.agevoluzione.tools.android.utils.AnimatorUtils;

public class DemoRfidReader extends AppCompatActivity {

    private ReaderHelper readerHelper;

    private Button button1;
    private Button button2;
    private TextView testo;

//    @Override
//    protected void onStop() {
//        super.onStop();
//        readerHelper.stopReading();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        readerHelper.close(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rfidreader);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        Bind View
        button1 = findViewById(R.id.connection_button_1);
        button2 = findViewById(R.id.esempio1_textview_1);

        testo = findViewById(R.id.testo);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (null != tag) {
                    switch (tag) {
                        case "Request":
                            readerHelper.recheckPresence(DemoRfidReader.this);
                            break;
                        case "Connect":
                            try {
                                readerHelper.connect(DemoRfidReader.this, new ReaderConfiguratorImpl());
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
                            readerHelper.disconnect(DemoRfidReader.this);
                            break;
                    }
                }
            }
        };

        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);



//        Create Reader
        readerHelper = new ReaderHelper();
        readerHelper.setLifecycleOwner(this);
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
                                button1.setText("Non Inizialized!");
                                button1.setEnabled(false);
//                                AnimatorUtils.changeText(button1, "Non Inizialized!").start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.INITIALIZED:
                                button1.setEnabled(false);
                                button1.setText("No Device!");
//                                AnimatorUtils.changeText(button1, "No Device!").start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.USB_DEVICE_ATTACHED_NOT_AUTHORIZED:
                                button1.setText("Request");
                                button1.setTag("Request");
                                button1.setEnabled(true);
//                                AnimatorUtils.changeText(button1, "Request").start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.USB_DEVICE_AUTHORIZATION_GRANT:
                                button1.setText("Connect");
                                button1.setTag("Connect");
                                button1.setEnabled(true);
//                                AnimatorUtils.changeText(button1, "Connect").start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.READER_CONNECTING:
                                button1.setText("Connecting...");
                                button1.setEnabled(false);
//                                AnimatorUtils.changeText(button1, "Connecting...").start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.READER_CONNECTED:
                                button1.setText("Configuring...");
                                button1.setEnabled(false);
//                                AnimatorUtils.changeText(button1, "Configuring...").start();
//                                AnimatorUtils.fadeOut(button2).start();
                                button2.setVisibility(View.GONE);
                                testo.setText("");
                                break;
                            case ReaderHelper.READER_CONFIGURED:
                                button1.setText("Read");
                                button1.setEnabled(true);
//                                AnimatorUtils.changeText(button1, "Read").start();
                                button1.setTag("Read");

                                button2.setEnabled(true);
                                button2.setVisibility(View.VISIBLE);
                                button2.setText("Disconnect");
//                                AnimatorUtils.changeText(button2, "Disconnect").start();
                                button2.setTag("Disconnect");
                                testo.setText("");
                                break;
                            case ReaderHelper.READING:
                                button1.setEnabled(true);
                                button1.setText("Stop");
//                                AnimatorUtils.changeText(button1, "Stop").start();
                                button1.setTag("Stop");

                                button2.setEnabled(true);
                                button2.setVisibility(View.VISIBLE);
                                button2.setText("Disconnect");
//                                AnimatorUtils.changeText(button2, "Disconnect").start();
                                button2.setTag("Disconnect");
                                testo.setText("Reading...");
                                break;
                        }
                    }

                });

            }
        });

        readerHelper.setErrorListener(new ReaderHelper.ErrorListener() {
            @Override
            public void onError(final Throwable throwable) {
                Log.e("DemoRfidReader", "ERR", throwable);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoRfidReader.this,"ERR: "+throwable.getMessage(),Toast.LENGTH_LONG).show();
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
