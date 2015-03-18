package group4asu.ambience;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RuleBase extends ActionBarActivity {

    private ListView rulesList;
    ArrayList<String> completeRulesList = new ArrayList<String>();
    String tempVal, operVal, lightVal, blindVal;
    Button addRule, refreshList;
    Spinner tempList, operatorList, lightList, blindList;
    HashMap<String, Object> hashingValues = new HashMap<String, Object>();
    HashMap<String, Object> deletingValues = new HashMap<String, Object>();
    int sendIndex = 0;
    String serverURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_base);
        Intent intent = getIntent();
        new SendJSONRequestRules().execute();

        Intent intentURL = getIntent();
        Bundle serverId = getIntent().getExtras();
        if(serverId!=null)
        {
            serverURL = serverId.getString("serverip");
        }


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

        rulesList = (ListView) findViewById(R.id.ruleList);

        addRule = (Button) findViewById(R.id.addButton);
        addRule.setOnClickListener(addRuleHandler);

        refreshList = (Button) findViewById(R.id.refreshB);
        refreshList.setOnClickListener(refreshRuleList);

    }

    View.OnClickListener refreshRuleList = new View.OnClickListener(){
        public void onClick(View tButton) {
            new  SendJSONRequestRules().execute();
        }
    };


    View.OnClickListener addRuleHandler = new View.OnClickListener(){
        public void onClick(View tButton) {
            tempVal = tempList.getSelectedItem().toString();
            hashingValues.put("ambientTemp", tempVal);
            lightVal = lightList.getSelectedItem().toString();
            hashingValues.put("ambientLight", lightVal);
            operVal = operatorList.getSelectedItem().toString();
            hashingValues.put("connector", operVal);
            blindVal = blindList.getSelectedItem().toString();
            hashingValues.put("blindPosition", blindVal);


            //methodName = "getTemp";
            new  SendJSONRequestAddRules().execute();
        }
    };

    public void populateList(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                completeRulesList);
        rulesList.setAdapter(arrayAdapter);

        rulesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sendindex = String.valueOf(i);
                navigate(sendindex,(String) rulesList.getItemAtPosition(i));
            }
        });
    }

    public void navigate(String index, String rule){
        //Toast.makeText(this,"navigate",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,elementActivity.class);
        intent.putExtra("index",index);
        intent.putExtra("rule",rule);
        intent.putExtra("ip", serverURL);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rule_base, menu);
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

    class SendJSONRequestRules extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverURL;
            String length="";
            String methodCall = "getRules";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            completeRulesList = JSONHandler.testJSONRequestList(serverAddress, methodCall);
            return String.valueOf(completeRulesList.size());
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            populateList();
        }
    }

    class SendJSONRequestAddRules extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverURL;
            String length="";
            String methodCall = "addRule";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequestUpdateRule(serverAddress, methodCall, hashingValues);
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            new SendJSONRequestRules().execute();
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
            new SendJSONRequestRules().execute();
        }
    }

}


