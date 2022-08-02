package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import Communication.Messages;
import Communication.Sizes;
import Services.FileService;

public class RequestHandlerThread extends Thread {
	public final Socket ConnectionToClient;
	private final String FileFolderPath;
	
	public RequestHandlerThread(Socket socket, String fileFolderPath) {
		this.ConnectionToClient = socket;
		this.FileFolderPath = fileFolderPath;
	}

	public void run() {
		try {
			OutputStream os = ConnectionToClient.getOutputStream();
			DataOutputStream outputWriter = new DataOutputStream(os);

			// Read fileName as a line
			BufferedReader br = new BufferedReader(new InputStreamReader(ConnectionToClient.getInputStream()));
			String fileName = br.readLine();
			File file = FileService.GetFileFromFolder(FileFolderPath, fileName);
			
			// Denies download if the peer doesn't have the file or randomly with a 50% chance
			if(!file.exists() || Math.random() < 0.5) {
				outputWriter.writeBytes(Messages.DownloadDenied + "\n");
			} else {
				outputWriter.writeBytes(Messages.DownloadAllowed + "\n");
				// Start file sending procedure
				int bytes = 0;
				FileInputStream fileInputStream = new FileInputStream(file);
				
				DataOutputStream dataOutputStream = new DataOutputStream(ConnectionToClient.getOutputStream());
				
				// Send File size
				dataOutputStream.writeLong(file.length());
				// Break file into chunks
				byte[] buffer = new byte[Sizes.TCPPacketSize];
				while ((bytes=fileInputStream.read(buffer))!=-1){
		            dataOutputStream.write(buffer,0,bytes);
		            dataOutputStream.flush();
		        }
				fileInputStream.close();
			}

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
