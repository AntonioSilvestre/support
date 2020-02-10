package it.agevoluzione.tools.android.rfidreaderhelperdemo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.agevoluzione.tools.android.rfidreaderhelperdemo.R;
import it.agevoluzione.tools.android.utils.AnimatorUtils;

public class DemoAnimator extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_animator);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        esempio1();
        esempio2();
        esempio3();


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

    private void esempio1() {
        RadioGroup radioGroup = findViewById(R.id.esempio1_ragio_group_1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView view = findViewById(R.id.esempio1_textview_1);
                switch (checkedId) {
                    case R.id.radio_0:
                        AnimatorUtils.changeBackgroudColor(view, Color.TRANSPARENT).start();
                        break;
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
        RadioGroup radioGroup2 = findViewById(R.id.esempio1_ragio_group_2);
        final TextView view = findViewById(R.id.esempio1_textview_1);
        final int origColor = view.getCurrentTextColor();
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_0_2:
                        AnimatorUtils.changeTextColor(view, origColor).start();
                        break;
                    case R.id.radio_1_2:
                        AnimatorUtils.changeTextColor(view, Color.BLACK).start();
                        break;
                    case R.id.radio_2_2:
                        AnimatorUtils.changeTextColor(view, Color.WHITE).start();
                        break;
                    case R.id.radio_3_2:
                        AnimatorUtils.changeTextColor(view, Color.YELLOW).start();
                        break;
                    case R.id.radio_4_2:
                        AnimatorUtils.changeTextColor(view, Color.BLUE).start();
                        break;
                    case R.id.radio_5_2:
                        AnimatorUtils.changeTextColor(view, Color.RED).start();
                        break;
                }
            }
        });
    }

    private void esempio2() {
        RadioGroup radioGroup = findViewById(R.id.esempio2_ragio_group_1);
        final TextView view = findViewById(R.id.esempio2_textview_1);
        final String orginal = view.getText().toString();
        final int origColor = view.getCurrentTextColor();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_0:
                        AnimatorUtils.changeTextColor(view, origColor).start();
                        break;
                    case R.id.radio_1:
                        AnimatorUtils.changeTextColor(view, Color.BLACK).start();
                        break;
                    case R.id.radio_2:
                        AnimatorUtils.changeTextColor(view, Color.WHITE).start();
                        break;
                    case R.id.radio_3:
                        AnimatorUtils.changeTextColor(view, Color.YELLOW).start();
                        break;
                    case R.id.radio_4:
                        AnimatorUtils.changeTextColor(view, Color.BLUE).start();
                        break;
                    case R.id.radio_5:
                        AnimatorUtils.changeTextColor(view, Color.RED).start();
                        break;
                }
            }
        });

        RadioGroup radioGroup2 = findViewById(R.id.esempio2_ragio_group_2);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_0_2:
                        AnimatorUtils.changeText(view, "").start();
                        break;
                    case R.id.radio_1_2:
                        AnimatorUtils.changeText(view, "Home").start();
                        break;
                    case R.id.radio_2_2:
                        AnimatorUtils.changeText(view, "House").start();
                        break;
                    case R.id.radio_3_2:
                        AnimatorUtils.changeText(view,"Houses" ).start();
                        break;
                    case R.id.radio_4_2:
                        AnimatorUtils.changeText(view, "Hi there, whats up").start();
                        break;
                    case R.id.radio_5_2:
                        AnimatorUtils.changeText(view, "Hi there").start();
                        break;
                }
            }
        });
    }

    private void esempio3() {

    }


}
