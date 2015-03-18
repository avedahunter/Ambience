package edu.rit.csci759.jsonrpc.server;

//The JSON-RPC 2.0 Base classes that define the 
//JSON-RPC 2.0 protocol messages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
//The JSON-RPC 2.0 server framework package
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import edu.rit.csci759.fuzzylogic.RspiBlinds;
import edu.rit.csci759.rspi.RpiIndicatorImpl;



public class JsonRPCServer {
	
	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 8080;
	//private static int reqCount=0;
	private static RpiIndicatorImpl objRpi;
	private static RspiBlinds objBlinds;
	private static Map<InetAddress, Integer>listOfMobDevices;

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for a dealing with a single client
	 * and broadcasting its messages.
	 */
	private static class Handler extends Thread{
		
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Dispatcher dispatcher;
		//private int local_count;

		/**
		 * Constructs a handler thread, squirreling away the socket.
		 * All the interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;

			// Create a new JSON-RPC 2.0 request dispatcher
			this.dispatcher =  new Dispatcher();

			// Register the "echo", "getDate" and "getTime" handlers with it
			dispatcher.register(new JsonHandler.EchoHandler());
			dispatcher.register(new JsonHandler.DateTimeHandler());
			dispatcher.register(new JsonHandler.LightAndTempHandler());
		}

		/**
		 * Services this thread's client by repeatedly requesting a
		 * screen name until a unique one has been submitted, then
		 * acknowledges the name and registers the output stream for
		 * the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			try {
								
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// read request
				String line;
				line = in.readLine();
				//System.out.println(line);
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
				boolean isPost = line.startsWith("POST");
				int contentLength = 0;
				while (!(line = in.readLine()).equals("")) {
					//System.out.println(line);
					raw.append('\n' + line);
					if (isPost) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line.substring(contentHeader.length()));
						}
					}
				}
				StringBuilder body = new StringBuilder();
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}
				System.out.println(body.toString());
				JSONRPC2Request request = JSONRPC2Request.parse(body.toString());
				
				//If the device is connecting for the first time then add it to the map of connected devices
				if(request.getMethod().equalsIgnoreCase("echo")){
					
					InetAddress tempIP = socket.getInetAddress();
					//int port = Integer.parseInt(request.getNamedParams().get("servicePort").toString() );
					
					//Acquire the lock the list of devices before modifying it as it's also accessed by the parallel temp monitor thread
					//and also the threads that are initiated by the newer requests
					synchronized (listOfMobDevices) {
						if(!listOfMobDevices.containsKey(tempIP))
							listOfMobDevices.put(tempIP, 8000);
					}
					
					System.out.println("Client with IP address: "+ socket.getInetAddress() + " connected to the Pi");
				}
				
				//If client decides to disconnect from the Pi
				if(request.getMethod().equalsIgnoreCase("disconnect")){
					
					//Acquire the lock the list of devices before modifying it as it's also accessed by the parallel temp monitor thread
					//and also the threads that are initiated by the newer requests					
					synchronized (listOfMobDevices) {
						listOfMobDevices.remove(socket.getInetAddress());
						System.out.println("Client with IP address: "+ socket.getInetAddress() + " disconnected");
					}

				}
				
				// Else continue with processing the request
				else{	 			
				JSONRPC2Response resp = dispatcher.process(request, null);

					// send response
					out.write("HTTP/1.1 200 OK\r\n");
					out.write("Content-Type: application/json\r\n");
					out.write("\r\n");
					out.write(resp.toJSONString());
					// do not in.close();
					out.flush();
					out.close();
					socket.close();
				}
				
			} catch (IOException e) {
				System.out.println(e);
			} catch (JSONRPC2ParseException e) {
				e.printStackTrace();
			} finally {
				
				try {
					socket.close();
				} catch (IOException e) {
				}
				
			}
		}

	
	}
	
	public static RpiIndicatorImpl getObjRpi() {
		return objRpi;
	}
	
	public static Map<InetAddress, Integer> returnListOfConnectedDevices(){
		return listOfMobDevices;
	}
	
	public static RspiBlinds getRspiBlindsObject(){
		return  objBlinds;
	}
	
	
	public static void main(String [] args)throws Exception {

		System.out.println("Server started.");
		
		//Initialize variables
		objRpi = new RpiIndicatorImpl();	
		System.out.println("RsPi pins initialized");
		
		objBlinds =  new RspiBlinds(objRpi);
		System.out.println("Blinds object initialized");
		
		//Thread to monitor temperature
		TempMonitor objRunnableTempMonitor = new TempMonitor(objRpi,objBlinds,objRpi.read_temperature());
		new Thread(objRunnableTempMonitor).start();	
		
		ServerSocket listener = new ServerSocket(PORT);
		
		//Maintain a map of connected devices
		listOfMobDevices = new HashMap<InetAddress, Integer>();
		
		try {
			
			Socket tempSocket;
			while (true) {
				tempSocket= listener.accept();
				new Handler(tempSocket).start();
			}
			
		} finally {
			
			listener.close();
			objRpi.GPIOshutDown();
		}
		
	}

}	
	
	

	
	
	
