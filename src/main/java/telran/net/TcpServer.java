package telran.net;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
public class TcpServer implements Runnable {
	static final int socketTimeOut = 1000;
	private static final int MAX_THREAD_N = 3;
	Protocol protocol;
	int port;
	boolean running = true;
	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD_N);
	
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
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(socketTimeOut);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					session =
							new TcpClientServerSession(socket, protocol, this);
					threadPoolExecutor.execute(session);
				} catch (SocketTimeoutException e) {
					
				}
			}
			gracefulActiveSessionsShutdown();
				
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void gracefulActiveSessionsShutdown() {
		threadPoolExecutor.getQueue().clear();
		threadPoolExecutor.shutdown();
		try {
			threadPoolExecutor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			
		}
	}
}
	
