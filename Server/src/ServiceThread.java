import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServiceThread extends Thread {
	private Socket connection;
	
	public ServiceThread(Socket node) {
		this.connection = node;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String input = br.readLine();

			DataOutputStream output = new DataOutputStream(connection.getOutputStream());

			while (input.compareTo("") != 0) {
				output.writeBytes(input.toUpperCase() + "\n");
				input = br.readLine();
			}
			
			output.writeBytes("See ya later!");
			connection.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
