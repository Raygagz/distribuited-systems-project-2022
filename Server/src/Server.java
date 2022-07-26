import java.net.ServerSocket;
import java.net.Socket;

class Server {

	public Socket s;

	public static void main (String args[]) throws Exception {
		ServerSocket serverSocket = new ServerSocket(9000);
		
		while (true) {
			System.out.println("Waiting for connection at port 9000.");
			Socket s = serverSocket.accept();
			System.out.println("Connection established from " + s.getInetAddress());

			ServiceThread thread = new ServiceThread(s);
			thread.start();
		}
	}
}
