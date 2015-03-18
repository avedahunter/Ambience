package edu.rit.csci759.jsonrpc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import edu.rit.csci759.fuzzylogic.RspiBlinds;
import edu.rit.csci759.rspi.RpiIndicatorImpl;

public class TempMonitor implements Runnable{
	
	private RpiIndicatorImpl objRpi;
	private RspiBlinds objBlind;
	private int initalTemp, tempDelta;

	private PrintWriter out;
	
	TempMonitor(RpiIndicatorImpl _objRpi, RspiBlinds _objBlind, int _initalTemp){
		
		objRpi = _objRpi;
		initalTemp=_initalTemp;
		objBlind = _objBlind;
		tempDelta = 2;

	}
	
	public void setTempChangeThreshold(int diff){
		this.tempDelta = diff;
	}
	
	@Override
	public void run() {
		
		int currentTemp;

		System.out.println("Temp monitor started. InitialTemp: "+ initalTemp);
		
		while(true){
			
			//Synchronization is required as a handler thread also accesses the RsPi object parallel
			synchronized (objRpi) {
				
				currentTemp = objRpi.read_temperature();
				
		        //Check difference in temperature 
				if(Math.abs(initalTemp - currentTemp ) >= tempDelta){
					
					initalTemp = currentTemp;
					System.out.println("Temp has changed by "+ tempDelta +" degrees. Current Temp: "+ currentTemp);
					
					synchronized (objBlind) {
						
						objBlind.setBlindPos( objBlind.evaluateFuzzylogic(objRpi.read_ambient_light_intensity(), initalTemp) );
					}
					try{
						
						//Send notification to each of the clients in the map
						Map<InetAddress, Integer>tempMap = JsonRPCServer.returnListOfConnectedDevices();
						Set<InetAddress> objKeySet;
						
						synchronized (tempMap){ 
							objKeySet = tempMap.keySet();
						}

						int clientPort;
						Socket objSocket;
						
						for(InetAddress ipAddr : objKeySet){

							clientPort = JsonRPCServer.returnListOfConnectedDevices().get(ipAddr);
							
							System.out.println("Client addr: "+ipAddr + " Port num: "+clientPort  );
							objSocket = new Socket(ipAddr, clientPort);
							out = new PrintWriter(objSocket.getOutputStream(), true);
							
							DateFormat objDtFormat= new SimpleDateFormat("HH:mm:ss");
							JSONRPC2Response objResp = new JSONRPC2Response( objDtFormat.format(new Date().getTime() )  +", "+ currentTemp);
							
							// send response
							out.write("HTTP/1.1 200 OK\r\n");
							out.write("Content-Type: application/json\r\n");
							out.write("\r\n");
							out.write(objResp.toJSONString());
							
							out.flush();
							out.close();
							objSocket.close();
						}				

					}
					catch(IOException objEx){
						System.out.println(objEx.getMessage());
					}
				
					try{
						Thread.sleep(500);
					}catch (InterruptedException e){
						System.out.println("Temp monitor interuppted");
					}
					
					
					
				}
			}
			
		}
		
	}
	
}