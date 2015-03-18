package edu.rit.csci759.jsonrpc.server;

/**
* Demonstration of the JSON-RPC 2.0 Server framework usage. The request
* handlers are implemented as static nested classes for convenience, but in 
* real life applications may be defined as regular classes within their old 
* source files.
*
* @author Vladimir Dzhuvinov
* @version 2011-03-05
*/ 

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.fuzzylogic.RspiBlinds;
import edu.rit.csci759.rspi.RpiIndicatorImpl;

public class JsonHandler {
	
	// Implements a handler for an "echo" JSON-RPC method
	 public static class EchoHandler implements RequestHandler {
		

	     // Reports the method names of the handled requests
	     public String[] handledRequests() {
			
	         return new String[]{"echo"};
	     }
		
	      // Processes the requests
	      public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
				
	          if (req.getMethod().equals("echo")) {
					
	              // Echo first parameter
	              List params = (List)req.getParams();
		 
		        // Object input = params.get(0);
		        // System.out.println("INPUT IS REQID IS "+input);
		         return new JSONRPC2Response("true", req.getID());
	         } else {
		
	             // Method name not supported
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
		    }
	     }
	 }
	 	 
	 // Implements a handler for "getDate" and "getTime" JSON-RPC methods
	 // that return the current date and time
	 public static class DateTimeHandler implements RequestHandler {
		
	     // Reports the method names of the handled requests
		public String[] handledRequests() {
		
		    return new String[]{"getDate", "getTime"};
		}
		
		
		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
		
			String hostname="unknown";
			try {
				hostname=InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		    if (req.getMethod().equals("getDate")) {
		    
		        DateFormat df = DateFormat.getDateInstance();
			
			String date = df.format(new Date());
			
			return new JSONRPC2Response(hostname+" "+date, req.getID());

	         }
	         else if (req.getMethod().equals("getTime")) {
	        	
		        DateFormat df = DateFormat.getTimeInstance();
			
			String time = df.format(new Date());
			
			return new JSONRPC2Response(hostname+" "+time, req.getID());
	         }
		    else {
		    
		        // Method name not supported
			
			return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
	         }
	     }
	 }
	 
	 // Implements a handler for "getTemp" and "getTimeAmbientLight" and other JSON-RPC methods
	 // that return the current date and time	 
	 public static class LightAndTempHandler implements RequestHandler{
				
		@Override
		public String[] handledRequests() {
			return  new String[]{"getAmbientLight","getTemp","getBlindPosition","getRules","addRule","deleteRule","editRule"};
		}

		@Override
		public JSONRPC2Response process(JSONRPC2Request req,MessageContext ctx) {
		
			String hostname="unknown";
			RpiIndicatorImpl objRpi = JsonRPCServer.getObjRpi();
			
			try {
				hostname=InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			//Return ambient light
		    if (req.getMethod().equals("getAmbientLight")) {
		    	
		    	int ambientLight;
		    	synchronized (objRpi) {
			    	ambientLight = objRpi.read_ambient_light_intensity();
			    	System.out.println("AmbientLight: "+ ambientLight);					
				}
		    	
		    	return new JSONRPC2Response(hostname+" "+ambientLight, req.getID());

	         }
		    
		    //Return  Ambient temperature
	         else if (req.getMethod().equals("getTemp")) {

	        	 int ambientTemp;
	        	 synchronized (objRpi) {
	        		 ambientTemp = objRpi.read_temperature();
		        	 System.out.println("AmbientTemp: "+ ambientTemp);
				}
	        	 
	        	 return new JSONRPC2Response(hostname+" "+ambientTemp, req.getID());	
	         }
		    
		    //Get the existing get the existing blind's position
	         else if(req.getMethod().equals("getBlindPosition")){
	        	 RspiBlinds objRsPiBlinds = JsonRPCServer.getRspiBlindsObject();
	        	 String retStr;
	        	 synchronized (objRsPiBlinds) {
	        		 retStr = objRsPiBlinds.highlightAppropriateLED(objRsPiBlinds.getBlindPos());
				}
        	 
	        	return new JSONRPC2Response(hostname+" "+retStr, req.getID()); 
	         }
		    
		    //Get the existing fuzzy logic rules
	         else if(req.getMethod().equals("getRules")){
	        	 
	        	 RspiBlinds objRsPiBlinds = JsonRPCServer.getRspiBlindsObject();
	        	 ArrayList<String> arrOfRules;
	        	 
	        	 synchronized (objRsPiBlinds) {
	        		 arrOfRules = objRsPiBlinds.getFuzzyLogicRules();
				}
	        	return new JSONRPC2Response(arrOfRules, req.getID()); 
	        	 
	         }
		    
		    //Add rule to the existing fuzzy logic
	         else if(req.getMethod().equals("addRule")){
	        	 
	        	 RspiBlinds objRsPiBlinds = JsonRPCServer.getRspiBlindsObject();
	        	 RpiIndicatorImpl objRp = JsonRPCServer.getObjRpi();
	        	 String str;
	        	 synchronized (objRsPiBlinds) {
	        		 str = objRsPiBlinds.addRule(req, objRp);
				}
	        	 
	        	return new JSONRPC2Response(hostname +" New blind position after addition of the rule is: "+str, req.getID()); 
	        	 
	         }
		    
		    //Add rule to the existing fuzzy logic
	         else if(req.getMethod().equals("deleteRule")){
	        	 
	        	 RspiBlinds objRsPiBlinds = JsonRPCServer.getRspiBlindsObject();
	        	 String str;
	        	 synchronized (objRsPiBlinds) {
	        		 
	        		 RpiIndicatorImpl objRp = JsonRPCServer.getObjRpi();
	        		 synchronized (objRp) {
		        		 str = objRsPiBlinds.deleteRule(req, objRp);
					}

				}	        	 
	        	return new JSONRPC2Response(hostname +" New blind position after deletion of the rule is: "+str, req.getID()); 
	        	 
	         }
		    
		    //Edit existing rule
	         else if(req.getMethod().equals("editRule")){
	        	 
	        	 RspiBlinds objRsPiBlinds = JsonRPCServer.getRspiBlindsObject();
	        	 String str;
	        	 synchronized (objRsPiBlinds) {
	        		 
	        		 RpiIndicatorImpl objRp = JsonRPCServer.getObjRpi();
	        		 synchronized (objRp) {
		        		 str = objRsPiBlinds.editRule(req, objRp);
					}

				}	        	 
	        	return new JSONRPC2Response(hostname +" "+str, req.getID()); 
	        	 
	         }
	        	 
		    else {
		    
		        // Method name not supported
		    	return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
	         }
	     }

		}

	 
	 
}
