package telran.net;

import java.net.*;

public class TcpServer {
	Protocol protocol;
	int port;

	public TcpServer(int port, Protocol protocol) {
		this.port = port;
		this.protocol = protocol;
	}

	public void run() {
			try (ServerSocket serverSocket = new ServerSocket()) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				TcpClientServerSession clientServerSession = new TcpClientServerSession(clientSocket, protocol);
				clientServerSession.run();
				clientSocket.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
