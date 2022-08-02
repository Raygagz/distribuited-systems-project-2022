package Services;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class FileService {
	public static ArrayList<String> GetFilesFromPath(String path) {
		File directoryPath = new File(path);
		
		if(directoryPath.isDirectory()) {
			return new ArrayList<String>(Arrays.asList(directoryPath.list()));
		}
		
		return null;
	}
	
	public static File GetFileFromFolder(String folderPath, String fileName) {
		// TODO: File not found exception
		File file = Paths.get(folderPath, fileName).toFile();
		return file;
	}
}
