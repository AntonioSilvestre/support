package it.agevoluzione.tools.android.rfidreaderhelperdemo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import it.agevoluzione.tools.android.rfidreaderhelper.ReaderConfiguratorImpl;
import it.agevoluzione.tools.android.rfidreaderhelper.ReaderHelper;
import it.agevoluzione.tools.android.rfidreaderhelperdemo.R;
import it.agevoluzione.tools.android.utils.AnimatorUtils;

public class DemoAnimator extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_animator);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        esemptio1();

    }

    private void esemptio1() {
        RadioGroup radioGroup = findViewById(R.id.esempio1_ragio_group_1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView view = findViewById(R.id.esempio1_textview_1);
                switch (checkedId) {
                    case R.id.radio_1:
                        AnimatorUtils.changeBackgroudColor(view, Color.BLACK).start();
                        break;
                    case R.id.radio_2:
                        AnimatorUtils.changeBackgroudColor(view, Color.WHITE).start();
                        break;
                    case R.id.radio_3:
                        AnimatorUtils.changeBackgroudColor(view, Color.YELLOW).start();
                        break;
                    case R.id.radio_4:
                        AnimatorUtils.changeBackgroudColor(view, Color.BLUE).start();
                        break;
                    case R.id.radio_5:
                        AnimatorUtils.changeBackgroudColor(view, Color.RED).start();
                        break;
                }
            }
        });


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
