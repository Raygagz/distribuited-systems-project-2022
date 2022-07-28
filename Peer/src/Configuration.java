import java.nio.file.Paths;

public class Configuration {
	public static final String FILES_RELATIVE_PATH = Paths.get(System.getProperty("user.dir"), "files").toAbsolutePath().toString();
}
