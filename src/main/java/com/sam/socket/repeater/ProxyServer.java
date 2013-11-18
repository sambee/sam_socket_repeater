package com.sam.socket.repeater;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

		final byte[] request = new byte[1024];
//		byte[] reply = new byte[4096];
		byte[] reply = new byte[4096];
		while (true) {
			Socket client = null, server = null;
			try {
				// Wait for a connection on the local port
				client = ss.accept();

				final InputStream streamFromClient = client.getInputStream();
				final OutputStream streamToClient = client.getOutputStream();

				log.info("[Client connected]"
						+ client.getInetAddress().getHostName());

				// Make a connection to the real server.
				// If we cannot connect to the server, send an error to the
				// client, disconnect, and continue waiting for connections.
				try {
					server = new Socket(host, remoteport);
				} catch (IOException e) {
					PrintWriter out = new PrintWriter(streamToClient);
					out.print("Proxy server cannot connect to " + host + ":"
							+ remoteport + ":\n" + e + "\n");
					out.flush();
					client.close();
					continue;
				}

				// Get server streams.
				final InputStream streamFromServer = server.getInputStream();
				final OutputStream streamToServer = server.getOutputStream();

				// a thread to read the client's requests and pass them
				// to the server. A separate thread for asynchronous.
				Thread t = new Thread() {
					public void run() {
						int bytesRead;
						try {
							while ((bytesRead = streamFromClient.read(request)) != -1) {
								log.info("[Client to server]"
										+ new String(request, "gbk"));
								streamToServer.write(request, 0, bytesRead);
								streamToServer.flush();
							}
						} catch (IOException e) {
						}
						
						// the client closed the connection to us, so close our
						// connection to the server.
						try {
							streamToServer.close();
						} catch (IOException e) {
						}
					}
				};

				// Start the client-to-server request thread running
				t.start();

				// Read the server's responses
				// and pass them back to the client.
				int bytesRead;
				try {
					StringBuffer sb = new StringBuffer();
					while ((bytesRead = streamFromServer.read(reply)) != -1) {
						
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
				}

				// The server closed its connection to us, so we close our
				// connection to our client.
				streamToClient.close();
			} catch (IOException e) {
				System.err.println(e);
			} finally {
				try {
					if (server != null)
						server.close();
					if (client != null)
						client.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
