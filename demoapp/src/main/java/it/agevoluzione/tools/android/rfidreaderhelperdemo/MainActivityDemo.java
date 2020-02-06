package it.agevoluzione.tools.android.rfidreaderhelperdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.agevoluzione.tools.android.rfidreaderhelperdemo.activities.DemoAnimator;
import it.agevoluzione.tools.android.rfidreaderhelperdemo.activities.DemoDialogLoader;
import it.agevoluzione.tools.android.rfidreaderhelperdemo.activities.DemoRfidReader;

public class MainActivityDemo extends AppCompatActivity {


    ArrayAdapter<Class<? extends Activity>> adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_demo);

        ListView containerActivity = findViewById(R.id.activity_container);
        adapter = new ArrayAdapter<Class<? extends Activity>>(this, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Class classe = getItem(position);
                if (view instanceof TextView && null != classe) {
                    ((TextView) view).setText(classe.getSimpleName());
                    ((TextView) view).setGravity(Gravity.CENTER);
                }
                return view;
            }
        };
        fillAdapter(adapter);
        containerActivity.setAdapter(adapter);


        containerActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class<? extends Activity> classe = adapter.getItem(position);
                Intent intent = new Intent(MainActivityDemo.this, classe);
                MainActivityDemo.this.startActivity(intent);
            }
        });

    }

    private void fillAdapter(ArrayAdapter<Class<? extends Activity>> adapter) {
        adapter.add(DemoRfidReader.class);
        adapter.add(DemoAnimator.class);
        adapter.add(DemoDialogLoader.class);

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
