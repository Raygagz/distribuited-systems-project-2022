package Server;

import java.net.Socket;

public class ServerThread extends Thread {
	private Socket Socket = null;
	
	public ServerThread(Socket socket) {
		this.Socket = socket;
	}

	public void run() {
		
	}
}
