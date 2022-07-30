package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import Services.FileService;

public class RequestHandlerThread extends Thread {
	private final Socket ConnectionToClient;
	private final String FileFolderPath;
	
	public RequestHandlerThread(Socket socket, String fileFolderPath) {
		this.ConnectionToClient = socket;
		this.FileFolderPath = fileFolderPath;
	}

	public void run() {
		try {
			// Read fileName as a line
			BufferedReader br = new BufferedReader(new InputStreamReader(ConnectionToClient.getInputStream()));
			String fileName = br.readLine();

			// Start file sending procedure
			File file = FileService.GetFileFromFolder(FileFolderPath, fileName);
			int bytes = 0;
			FileInputStream fileInputStream = new FileInputStream(file);
			
			DataOutputStream dataOutputStream = new DataOutputStream(ConnectionToClient.getOutputStream());
			
			// Send File size
			dataOutputStream.writeLong(file.length());
			// Break file into chunks
			byte[] buffer = new byte[4*1024];
			while ((bytes=fileInputStream.read(buffer))!=-1){
	            dataOutputStream.write(buffer,0,bytes);
	            dataOutputStream.flush();
	        }
			fileInputStream.close();
			
			/* Send fileName capsLocked as a line
			DataOutputStream output = new DataOutputStream(ConnectionToClient.getOutputStream());

			output.writeBytes(fileName.toUpperCase() + "\n");
			*/

			ConnectionToClient.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void SendToClient(byte[] bytesToSend) throws IOException {
		DataOutputStream outputStream = new DataOutputStream(ConnectionToClient.getOutputStream());
		outputStream.write(bytesToSend, 0, bytesToSend.length);
	}
}
