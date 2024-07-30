package telran.net;
import java.lang.Thread.State;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
public class TcpServer implements Runnable{
	static final int socketTimeOut = 1000;
	Protocol protocol;
	int port;
	boolean running = true;
	LinkedList<TcpClientServerSession> sessionsList = new LinkedList<>();
	
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	
	public void shutdown() {
		running = false;
	}
	
	public void run() {
		TcpClientServerSession session = null;
		try(ServerSocket serverSocket = new ServerSocket(port)){
			//TODO using ServerSocket method setSoTimeout 
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(socketTimeOut);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					session =
							new TcpClientServerSession(socket, protocol);
					sessionsList.add(session);
					session.start();
					System.out.println(sessionsList.size());
				} catch (SocketTimeoutException e) {
					
				}
				//TODO handling timeout exception
			}
			gracefulActiveSessionsShutdown();
				
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void gracefulActiveSessionsShutdown() {
		sessionsList.forEach(session -> session.shutdown());
	waitForAllSessionsTermination();

}
	
	private void waitForAllSessionsTermination() {
		while (!sessionsList.isEmpty()) {
			removeTerminatedThreads();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

	}

	private void removeTerminatedThreads() {
        Iterator<TcpClientServerSession> iterator = sessionsList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getState() == State.TERMINATED) {
                iterator.remove();
            }
        }
	}
	
	
}