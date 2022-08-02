package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerServer extends Thread {
	public final ServerSocket ServerSocket;
	private final String FileFolderPath;
	
	public PeerServer(InetAddress address, int port, String fileFolderPath) throws IOException {
		ServerSocket = new ServerSocket(port, 0, address);
		FileFolderPath = fileFolderPath;
	}
	
	public void run() {
		try {
			while (!ServerSocket.isClosed()) {
				Socket socket;
				// Listens to peers trying to connect over TCP
				socket = ServerSocket.accept();
				
				// Open a new thread for the connection
				RequestHandlerThread thread = new RequestHandlerThread(socket, FileFolderPath);
				thread.start();
			}
		} catch (SocketException e) {
			// In case of an interruption (Peer leaving the network)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
