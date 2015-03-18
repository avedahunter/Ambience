package group4asu.ambience;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.widget.Toast;
import android.widget.ToggleButton;


public class Home extends ActionBarActivity implements FireDialogFragment.Communicator{

    private WebView mywebview;
    Button btn_send_request;
    private String serverUrl = "";
    private String methodName = "";
    Button tempR;
    Button intensityR;
    Button makeConnection;
    Button goToRuleB;
    TextView showTemp, tempMeter;
    TextView showIntensity, lightMeter;
    TextView detailsPi;
    String buttonClick = "";
    ToggleButton onOff;
    String outputVal = "";
    //TextView showTemp, showIntensity;
    boolean firstFlag = false;
    String ip="";
    boolean on;
    public String checkConnection="false";
    RelativeLayout layout;
    boolean check=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setBackgroundColor(Color.rgb(210, 210, 210));
        tempR  = (Button) findViewById(R.id.tempRefresh);
        intensityR = (Button) findViewById(R.id.intenRefresh);
        makeConnection = (Button) findViewById(R.id.enterIP);
        goToRuleB = (Button) findViewById(R.id.gotoRules);
        onOff = (ToggleButton) findViewById(R.id.toggledata);
        //TextView showTemp = (TextView) findViewById(R.id.textTemp);
        //TextView showIntensity = (TextView) findViewById(R.id.textInten);
        showTemp = (TextView) findViewById(R.id.textTemp);
        showIntensity = (TextView) findViewById(R.id.textInten);
        tempMeter = (TextView) findViewById(R.id.tempMeter);
        lightMeter = (TextView) findViewById(R.id.lightMeter);
        detailsPi = (TextView) findViewById(R.id.detailsPi);
        detailsPi.setTextColor(Color.rgb(162,162,162));

        tempR.setOnClickListener(tempHandler);
        intensityR.setOnClickListener(intenHandler);
        //makeConnection.setOnClickListener(connectHandler);
        mywebview = (WebView) findViewById(R.id.mywebview);
        isEmptyApp();
    }

    public void isEmptyApp()
    {
        if(on){
            tempR.setVisibility(View.VISIBLE);
            showIntensity.setText("");
            intensityR.setVisibility(View.VISIBLE);
            goToRuleB.setVisibility(View.VISIBLE);
            showTemp.setText("");
            mywebview.setVisibility(View.VISIBLE);
            tempMeter.setText("Temperature");
            lightMeter.setText("Light Intensity");
            detailsPi.setText("Established connection! Enjoy adjusting your blinds.");
            initiateApp();
        }else {
            tempR.setVisibility(View.INVISIBLE);
            showIntensity.setText("");
            intensityR.setVisibility(View.INVISIBLE);
            goToRuleB.setVisibility(View.INVISIBLE);
            showTemp.setText("");
            tempMeter.setText("");
            lightMeter.setText("");
            mywebview.setVisibility(View.INVISIBLE);
            detailsPi.setText("Connection not established. Start the application.");
        }
    }

    public void goToRuleBase(View view){
        Intent intent = new Intent(this, RuleBase.class);
        intent.putExtra("serverip", serverUrl);
        startActivity(intent);
    }

    public void initiateApp(){
        //isEmptyApp();
        new SendJSONRequestTemp().execute();
        new SendJSONRequestIntensity().execute();
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebViewClient(new MyWVClient());
        String baseUrl = "http://www.wunderground.com/weather-forecast/";
        String appendZip = "14586";
        String url = baseUrl + appendZip;
        mywebview.loadUrl(url);
        tempR = (Button) findViewById(R.id.tempRefresh);
        intensityR = (Button) findViewById(R.id.intenRefresh);
        showTemp = (TextView) findViewById(R.id.textTemp);
        showIntensity = (TextView) findViewById(R.id.textInten);
        tempR.setOnClickListener(tempHandler);
        intensityR.setOnClickListener(intenHandler);
        new SendJSONRequestBlind().execute();
        //Button buttonclick = (Button) findViewById(R.id.button);
    }

    @Override
    public void onDialogMessage(String message) {
        if(message.equalsIgnoreCase("cancel"))
        {
            on = false;
            onOff.setChecked(false);
            layout.setBackgroundColor(Color.rgb(210, 210, 210));
            detailsPi.setTextColor(Color.rgb(162,162,162));
            System.out.println(on);
            check=false;
            isEmptyApp();
        }
        else {
            ip = message;
            on = true;
            Toast.makeText(this, "IP added", Toast.LENGTH_SHORT).show();
            detailsPi.setText("Trying to establish a connection! Please wait a few seconds......");
            onOff.setClickable(false);
            new SendJSONRequestEcho().execute();
            onOff.setClickable(true);
            check=true;
        }
    }

    public void onToggleClicked(View view){
        on = ((ToggleButton) view).isChecked();

        if(on){
            FragmentManager manager = getFragmentManager();
            FireDialogFragment fire = new FireDialogFragment();
            fire.show(manager,"Fire");
            Intent intent = new Intent(this, ServiceForNotification.class);
            check=true;
            //new SendJSONRequestRefreshContinously().execute();
            startService(intent);
            //isEmptyApp();
            //initiateApp();
        }
        else
        {
            onOff.setClickable(false);
            //detailsPi.setText("Trying to establish a connection! Please wait a few seconds......");
            layout.setBackgroundColor(Color.rgb(210, 210, 210));
            detailsPi.setTextColor(Color.rgb(162,162,162));
            onOff.setClickable(true);
            check=false;
            isEmptyApp();
            Intent intent = new Intent(this, ServiceForNotification.class);
            stopService(intent);
            new SendJSONRequestDisconnect().execute();
        }
        //checkConnectivity();
    }


    //unused method
    public void checkConnectivity(){
        if(on){
            initiateApp();
        }
        else
        {
            isEmptyApp();
            onOff.setChecked(false);
        }
    }

    View.OnClickListener tempHandler = new View.OnClickListener(){
        public void onClick(View tButton) {
            outputVal = "temp";
            //methodName = "getTemp";
            new SendJSONRequestTemp().execute();
        }
    };

    View.OnClickListener intenHandler = new View.OnClickListener(){
        public void onClick(View tButton) {
            outputVal = "intensity";
            //methodName = "getIntensity";
            new SendJSONRequestIntensity().execute();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    class SendJSONRequestIntensity extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverUrl;
            String methodCall = "getAmbientLight";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequest(serverAddress, methodCall);

            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            showIntensity.setText(response_txt.substring(12, response_txt.length()));
        }

    }

    class SendJSONRequestTemp extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverUrl;
            String methodCall = "getTemp";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequest(serverAddress, methodCall);

            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            showTemp.setText(response_txt.substring(12, response_txt.length()));
        }

    }

    class SendJSONRequestEcho extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = ip;
            serverUrl=serverAddress;
            String methodCall = "echo";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            onOff.setClickable(false);
            checkConnection = JSONHandler.testJSONRequest(serverAddress, methodCall);
            return checkConnection;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            onOff.setClickable(true);
            if(result.equalsIgnoreCase("true")){
                on=true;
                onOff.setChecked(true);
                isEmptyApp();
            }
            else
            {
                serverUrl="";
                on=false;
                onOff.setChecked(false);
                detailsPi.setText("Not able to establish connection. Try again.");
            }
        }

    }

    class SendJSONRequestBlind extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverUrl;
            String methodCall = "getBlindPosition";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequest(serverAddress, methodCall);

            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            if((response_txt.substring(12, response_txt.length())).equalsIgnoreCase("open")) {
                detailsPi.setText("The blinds are completely open. Have a look at the beautiful weather outside");
                detailsPi.setTextColor(Color.rgb(10,145,23));
                layout.setBackgroundColor(Color.rgb(117, 218, 127));
                showIntensity.setTextColor(Color.rgb(10,145,23));
                showTemp.setTextColor(Color.rgb(10,145,23));
                tempR.setTextColor(Color.rgb(10,145,23));
                intensityR.setTextColor(Color.rgb(10,145,23));
                tempR.setBackgroundColor(Color.rgb(97,195,107));
                intensityR.setBackgroundColor(Color.rgb(97,195,107));
                goToRuleB.setBackgroundColor(Color.rgb(97,195,107));
                goToRuleB.setTextColor(Color.rgb(10,145,23));

            }
            if((response_txt.substring(12, response_txt.length())).equalsIgnoreCase("half-open"))
            {
                detailsPi.setText("The blinds are partially open. It will get comfortable for you.");
                detailsPi.setTextColor(Color.rgb(137, 125, 18));
                layout.setBackgroundColor(Color.rgb(255,212,38));
                showIntensity.setTextColor(Color.rgb(137, 125, 18));
                showTemp.setTextColor(Color.rgb(137, 125, 18));
                tempR.setTextColor(Color.rgb(137, 125, 18));
                intensityR.setTextColor(Color.rgb(137, 125, 18));
                tempR.setBackgroundColor(Color.rgb(255,208,21));
                intensityR.setBackgroundColor(Color.rgb(255,208,21));
                goToRuleB.setBackgroundColor(Color.rgb(255,208,21));
                goToRuleB.setTextColor(Color.rgb(137, 125, 18));
            }
            if((response_txt.substring(12, response_txt.length())).equalsIgnoreCase("closed"))
            {
                detailsPi.setText("The blinds are closed. Your room is a comfortable place for you");
                detailsPi.setTextColor(Color.rgb(222, 50, 16));
                layout.setBackgroundColor(Color.rgb(242, 113, 87));
                showIntensity.setTextColor(Color.rgb(222, 50, 16));
                showTemp.setTextColor(Color.rgb(222, 50, 16));
                tempR.setTextColor(Color.rgb(222, 50, 16));
                intensityR.setTextColor(Color.rgb(222, 50, 16));
                tempR.setBackgroundColor(Color.rgb(242,113,87));
                intensityR.setBackgroundColor(Color.rgb(242,113,87));
                goToRuleB.setBackgroundColor(Color.rgb(242,113,87));
                goToRuleB.setTextColor(Color.rgb(222, 50, 16));

            }
        }

    }

    class SendJSONRequestRefreshContinously extends AsyncTask<Void, String, String> {
        String response_txt;
        String connection;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            while (check) {
                new SendJSONRequestBlind().execute();
                new SendJSONRequestIntensity().execute();
                new SendJSONRequestTemp().execute();
                long millis = 30000;
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "refresh";
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {

        }

    }

    class SendJSONRequestDisconnect extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverAddress = serverUrl;
            String methodCall = "disconnect";
            //String serverURL_text = serverUrl;
            //String request_method = methodName;
            response_txt = JSONHandler.testJSONRequest(serverAddress, methodCall);

            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {

        }

    }


}


class MyWVClient extends  WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}