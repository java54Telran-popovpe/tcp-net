package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread{
	static final int socketTimeOut = 1000;
	static final int clientSessionTimeOut = 60_000;
	Socket socket;
	Protocol protocol;
	boolean running = true;
	
	public void shutdown() {
		System.out.println("session stop signaled");
		running = false;
	}

	public TcpClientServerSession(Socket socket, Protocol protocol) {
		this.socket = socket;
		//TODO
		//using the method setSoTimeout and some solution for getting session to know about shutdown
		//you should stop the thread after shutdown command
		this.protocol = protocol;
	}
	public void run() {
		try (BufferedReader receiver =
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())){
			socket.setSoTimeout(socketTimeOut);
			String line = null;
			//FIXME 
			//figure out solution for exiting from the thread after shutdown
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
				
			}
			System.out.println("Session connection closed");
			//TODO handling SocketTimeoutException for exiting from the thread on two conditions
			//1. Shutdown has been performed
			//2. Thread exists in IDLE state more than 1 minute
			//exiting from the cycle should be followed by closing connection
			socket.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}