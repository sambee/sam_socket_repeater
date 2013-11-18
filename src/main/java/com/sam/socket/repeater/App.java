package com.sam.socket.repeater;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

/**
 * 
 *
 * App.java
 *
 * @author Sam Wong
 * @link http://user.qzone.qq.com/1557299538/main
 * @create: 2013年11月18日  
 * 
 * Modification
 * -------------------------------------------
 */
public class App {
	public static void main(String[] args) throws IOException {
		
		
			PropertyConfigurator.configure(
					Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));  
			
			System.getProperties().list(System.out);
			
			String host = "your remote host";
			int remoteport = 1668;
			int localport = 61668;
			
			if(args==null||args.length==0){
				System.out.println(
						 "\n ====================================================================="
						+ "\n Welcome use sam proxy, Please enter your command:"
					    + " Author: Sam Wong"
						+ " QQ:173482035"
					    + " URL: http://user.qzone.qq.com/1557299538/main"
						+ "  "
						+ "\n java -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar yourRemoteHost"
						+ "\n java -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar yourRemoteHost yourRemotePort "
						+ "\n java -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar yourRemoteHost yourRemotePort localPort"
						+ "\n ex"
						+ "\njava -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar 21cn.com"
						+ "\njava -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar 21cn.com 80"
						+ "\njava -com.sam.socket.repeater-0.0.1-SNAPSHOT.jar 21cn.com 80 1080"
						+ "\n =====================================================================");
				
				System.exit(-1);
			}
			if(args!=null && args.length>0){
				host = args[0];
			}
			if(args!=null && args.length>1){
				remoteport = Integer.parseInt(args[1]);
			}
			if(args!=null && args.length>2){
				localport = Integer.parseInt(args[2]);
			}
			
		
			// Print a start-up message
			System.out.println(
					String.format(
			"Starting proxy for %s:%s on port %s" , host , remoteport, localport));
			ProxyServer.runServer(host, remoteport, localport);
	}

}
