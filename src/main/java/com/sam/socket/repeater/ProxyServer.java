package com.sam.socket.repeater;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ProxyServer {

	private final static Logger log = Logger.getLogger(ProxyServer.class);

	/**
	 * runs a single-threaded proxy server on the specified local port. It never
	 * returns.
	 */
	public static void runServer(String host, int remoteport, int localport)
			throws IOException {
		// Create a ServerSocket to listen for connections with
		ServerSocket ss = new ServerSocket(localport);

		while (true) {
			Socket client = null, server = null;

			// Wait for a connection on the local port
			client = ss.accept();


			// Make a connection to the real server.
			// If we cannot connect to the server, send an error to the
			// client, disconnect, and continue waiting for connections.
			try {
				server = new Socket(host, remoteport);
				new Handler(server, client).start();
			} catch (IOException e) {
				PrintWriter out = new PrintWriter( client.getOutputStream());
				String error = "Proxy server cannot connect to " + host + ":"+ remoteport + ":\n" + e + "\n";
				log.error(error, e);
				out.print(error);
				out.flush();
				if(client!=null){
					client.close();
				}				
				if(server!=null){
					server.close();
				}
				continue;
			}
			
			log.info("[Client connected]" + client);
		}
		
			
	}
}
