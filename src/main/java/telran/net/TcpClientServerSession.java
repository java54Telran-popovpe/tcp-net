package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession implements Runnable {
	static final int socketTimeOut = 1000;
	static final int clientSessionTimeOut = 60_000;
	Socket socket;
	Protocol protocol;
	TcpServer tcpServer;
	boolean running = true;
	
	public void shutdown() {
		System.out.println("session stop signaled");
		running = false;
	}

	public TcpClientServerSession(Socket socket, Protocol protocol, TcpServer tcpServer) {
		this.socket = socket;
		this.protocol = protocol;
		this.tcpServer = tcpServer;
	}
	
	public void run() {
		try (BufferedReader receiver =
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())){
			socket.setSoTimeout(socketTimeOut);
			String line = null;
			//FIXME 
			int timeOutCounter = 0;
			while(running && timeOutCounter < clientSessionTimeOut) {
				try {
				if ( (line = receiver.readLine()) != null) {
					timeOutCounter = 0;
					String responseStr = protocol.getResponseWithJSON(line);
					sender.println(responseStr);
				}
				} catch ( SocketTimeoutException e ) {
					timeOutCounter += socketTimeOut;
				}
				running = tcpServer.running;
				
			}
			System.out.println("Session connection closed");
			socket.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}