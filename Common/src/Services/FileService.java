package Services;

import java.io.File;

public class FileService {
	public static String[] getFilesFromPath(String path) {
		File directoryPath = new File(path);
		if(directoryPath.isDirectory()) {
			return directoryPath.list();
		}
		
		return null;
	}
}
