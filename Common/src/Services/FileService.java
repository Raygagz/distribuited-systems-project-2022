package Services;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileService {
	public static List<String> getFilesFromPath(String path) {
		File directoryPath = new File(path);
		if(directoryPath.isDirectory()) {
			return Arrays.asList(directoryPath.list());
		}
		
		return null;
	}
}
