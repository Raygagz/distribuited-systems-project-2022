package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer extends Thread {
	private ServerSocket ServerSocket = null;
	
	public PeerServer(InetAddress address, int port) throws IOException {
		ServerSocket = new ServerSocket(port, 0, address);
	}
	
	public void run() {
		try {
			while (true) {
				Socket socket;
				socket = ServerSocket.accept();
				ServerThread thread = new ServerThread(socket);
				thread.start();
			}
		} catch (Exception e) {
		}
	}
}
