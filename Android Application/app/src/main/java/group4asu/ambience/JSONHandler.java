package group4asu.ambience;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONHandler {

    public static String testJSONRequest(String server_URL_text, String method){
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        String returnString="false";
        Log.d("ServerURL", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            System.out.println(server_URL_text);
            server_URL_text = server_URL_text+":8080";
            serverURL = new URL("http://"+server_URL_text);

        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


        // Once the client session object is created, you can use to send a series
        // of JSON-RPC 2.0 requests and notifications to it.

        // Sending an example "getTime" request:
        // Construct new request

        int requestID = 0;
        HashMap<String,Object> tempMap = new HashMap<String, Object>();
        tempMap.put("servicePort", new Integer(4000));
        new JSONRPC2Request("",tempMap,requestID);
        JSONRPC2Request request = new JSONRPC2Request(method, requestID);

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);
            returnString = response.getResult().toString();
        } catch (JSONRPC2SessionException e) {

            Log.e("error", e.getMessage().toString());
            returnString = "false";
            // handle exception...
        }

        // Print response result / error
        //if (response.indicatesSuccess())
            //Log.d("debug", response.getResult().toString());
        //else
            //Log.e("error", response.getError().getMessage().toString());

        return returnString;

    }

    public static ArrayList<String> testJSONRequestList(String server_URL_text, String method){
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        String returnString="false";
        ArrayList<String> returnRuleList = new ArrayList<String>();
        Log.d("ServerURL", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            System.out.println(server_URL_text);
            server_URL_text = server_URL_text+":8080";
            serverURL = new URL("http://"+server_URL_text);

        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


        // Once the client session object is created, you can use to send a series
        // of JSON-RPC 2.0 requests and notifications to it.

        // Sending an example "getTime" request:
        // Construct new request

        int requestID = 0;
        HashMap<String,Object> tempMap = new HashMap<String, Object>();
        tempMap.put("servicePort", new Integer(4000));
        new JSONRPC2Request("",tempMap,requestID);
        JSONRPC2Request request = new JSONRPC2Request(method, requestID);

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);
            returnRuleList = (ArrayList<String>) response.getResult();
            //returnString = response.getResult().toString();
        } catch (JSONRPC2SessionException e) {
            Log.e("error", e.getMessage().toString());
            returnRuleList .add("emptyList");
            // handle exception...
        }

        // Print response result / error
        //if (response.indicatesSuccess())
        //Log.d("debug", response.getResult().toString());
        //else
        //Log.e("error", response.getError().getMessage().toString());

        return returnRuleList;

    }

    public static String testJSONRequestUpdateRule(String server_URL_text, String method, HashMap<String, Object> valueMap){
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        String returnString="false";
        Log.d("ServerURL", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            System.out.println(server_URL_text);
            server_URL_text = server_URL_text+":8080";
            serverURL = new URL("http://"+server_URL_text);

        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


        // Once the client session object is created, you can use to send a series
        // of JSON-RPC 2.0 requests and notifications to it.

        // Sending an example "getTime" request:
        // Construct new request

        int requestID = 0;
        HashMap<String,Object> tempMap = new HashMap<String, Object>();
        tempMap.put("servicePort", new Integer(4000));
        new JSONRPC2Request("",tempMap,requestID);
        JSONRPC2Request request = new JSONRPC2Request(method, valueMap, requestID);

        // Send request
        JSONRPC2Response response = null;

        try {
            response = mySession.send(request);
            returnString = response.getResult().toString();
        } catch (JSONRPC2SessionException e) {
            Log.e("error", e.getMessage().toString());
            returnString="false";
            // handle exception...
        }

        // Print response result / error
        //if (response.indicatesSuccess())
        //Log.d("debug", response.getResult().toString());
        //else
        //Log.e("error", response.getError().getMessage().toString());

        return returnString;

    }

}
