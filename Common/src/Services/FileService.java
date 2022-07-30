package Services;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileService {
	public static List<String> GetFilesFromPath(String path) {
		File directoryPath = new File(path);
		if(directoryPath.isDirectory()) {
			return Arrays.asList(directoryPath.list());
		}
		
		return null;
	}
	
	public static File GetFileFromFolder(String folderPath, String fileName) {
		File file = Paths.get(folderPath, fileName).toFile();
		return file;
	}
}
