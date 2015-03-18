package group4asu.ambience;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Objects;


public class elementActivity extends ActionBarActivity {
    String rule;
    String index;
    Button delete, update;
    Spinner tempList, operatorList, lightList, blindList;
    TextView rule_view;
    String serverURL;
    HashMap<String, Object> hashingValues = new HashMap<String, Object>();
    HashMap<String, Object> deletingValues = new HashMap<String, Object>();
    String tempVal, lightVal, operVal, blindVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            index = extras.getString("index");
            rule = extras.getString("rule");
            serverURL = extras.getString("ip");
        }
        rule_view = (TextView) findViewById(R.id.rule_text);
        tempList = (Spinner) findViewById(R.id.tempSpinner);
        ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(this,
                R.array.tempList, android.R.layout.simple_spinner_item);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tempList.setAdapter(tempAdapter);
        operatorList = (Spinner) findViewById(R.id.operatorSpinner);
        ArrayAdapter<CharSequence> operatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.operAndOrList, android.R.layout.simple_spinner_item);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatorList.setAdapter(operatorAdapter);

        lightList = (Spinner) findViewById(R.id.lightSpinner);
        ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(this,
                R.array.intensityList, android.R.layout.simple_spinner_item);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lightList.setAdapter(lightAdapter);

        blindList = (Spinner) findViewById(R.id.blindSpinner);
        ArrayAdapter<CharSequence> blindAdapter = ArrayAdapter.createFromResource(this,
                R.array.blindPosList, android.R.layout.simple_spinner_item);
        blindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blindList.setAdapter(blindAdapter);
        rule_view.setText(rule);

        update = (Button) findViewById(R.id.button_update);
        update.setOnClickListener(updateHandler);

        delete = (Button) findViewById(R.id.button_delete);
        delete.setOnClickListener(deleteHandler);
    }

    View.OnClickListener updateHandler = new View.OnClickListener(){
        public void onClick(View tButton) {

            hashingValues.put("index", index);
            tempVal = tempList.getSelectedItem().toString();
            hashingValues.put("ambientTemp", tempVal);
            lightVal = lightList.getSelectedItem().toString();
            hashingValues.put("ambientLight", lightVal);
            operVal = operatorList.getSelectedItem().toString();
            hashingValues.put("connector", operVal);
            blindVal = blindList.getSelectedItem().toString();
            hashingValues.put("blindPosition", blindVal);

            //methodName = "getTemp";
            new  SendJSONRequestUpdateRules().execute();
        }
    };

    View.OnClickListener deleteHandler = new View.OnClickListener(){
        public void onClick(View tButton) {

            deletingValues.put("index", index);

            //methodName = "getTemp";
            new  SendJSONRequestDelete().execute();
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_element, menu);
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

    class SendJSONRequestUpdateRules extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverURL;
            String length="";
            String methodCall = "editRule";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequestUpdateRule(serverAddress, methodCall, hashingValues);
            //System.out.println("vwow "+completeRulesList.size());
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {

            finish();
        }
    }

    class SendJSONRequestDelete extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverURL;
            String length="";
            String methodCall = "deleteRule";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;

            response_txt = JSONHandler.testJSONRequestUpdateRule(serverAddress, methodCall, deletingValues);
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {

            finish();
        }
    }


}
