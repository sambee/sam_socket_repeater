package com.sam.socket.repeater;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Handler extends Thread{
	
	private boolean isActive =true;
	
	InputStream streamFromClient;
	OutputStream streamToClient;
	InputStream streamFromServer;
	OutputStream streamToServer;	
	Socket server, client;
	
	final byte[] request = new byte[1024];
	byte[] reply = new byte[4096];
	
	private final static Logger log = Logger.getLogger(Handler.class);
	

	public Handler(Socket server, Socket client) throws IOException{
		//server.setSoTimeout(5000);
		log.info("[Server Timeout]" + server.getSoTimeout());
		this.server = server;
		this.client = client;
		this.streamFromClient = client.getInputStream();
		this.streamToClient = client.getOutputStream();	
		this.streamFromServer = server.getInputStream();
		this.streamToServer = server.getOutputStream();
	}
	
	@Override
	public void run(){

		// a thread to read the client's requests and pass them
		// to the server. A separate thread for asynchronous.
		new Thread() {
			public void run() {
				int bytesRead;
				StringBuffer sb = new StringBuffer();
				try {
					while ((bytesRead = streamFromClient.read(request)) != -1 && isActive) {
					
						if(bytesRead != 4096){
							log.info("     [Client to server]" + sb.length()  + " " + sb.toString());
							sb.delete(0, sb.length());
						}
						sb.append(new String(request, "gbk"));
						streamToServer.write(request, 0, bytesRead);
						streamToServer.flush();
					}
				} catch (IOException e) {
					//e.printStackTrace();
				}			
				
				
				// the client closed the connection to us, so close our connection to the server.
				try {
					streamToServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				isActive = false;
			}
		}.start();

		
		

		// Read the server's responses
		// and pass them back to the client.
		int bytesRead;
		try {
			StringBuffer sb = new StringBuffer();
			while ((bytesRead = streamFromServer.read(reply)) != -1 && isActive) {
				
				if(bytesRead != 4096){
					log.info("[Server to Client]" + sb.length()  + " " + sb.toString());
					sb.delete(0, sb.length());
				}
				sb.append(new String(reply, "gbk"));
				streamToClient.write(reply, 0, bytesRead);
				streamToClient.flush();
			}
			log.info(" ------ Exit -------");
		} catch (IOException e) {
			//e.printStackTrace();
		}

		// The server closed its connection to us, so we close our
		// connection to our client.
		try {
			streamToClient.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			try {
				if(server!=null){					
					server.close();
					server =null;
					log.info("[Server Closed]");
					
				}
				if(client!=null){
					client.close();
					client = null;
					log.info("[Client Closed]");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		

		
		isActive = false;
	}
	

}
