package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer extends Thread {
	private final ServerSocket ServerSocket;
	private final String FileFolderPath;
	
	public PeerServer(InetAddress address, int port, String fileFolderPath) throws IOException {
		ServerSocket = new ServerSocket(port, 0, address);
		FileFolderPath = fileFolderPath;
	}
	
	public void run() {
		try {
			while (true) {
				Socket socket;
				socket = ServerSocket.accept();
				RequestHandlerThread thread = new RequestHandlerThread(socket, FileFolderPath);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
