package finder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of (regular expression-based) folder/file name/content filtering.
 * 
 * @author TAYLOJ10
 */
public class FileFilter implements IFileFilter {

	@Override
	public boolean accept(String fileTarget, String fileName, boolean matchAgainstLeafNodeOnly, String findTextInFiles,
			boolean caseInsensitiveSearch, boolean acceptArchiveFilesImmediately, InputStream inputStream) throws Exception {
		boolean result = false;
		String entry = fileTarget.replaceAll("\\\\", "/");
		if (matchAgainstLeafNodeOnly == true) {
			if (entry.endsWith("/")) entry = entry.substring(0, entry.lastIndexOf("/"));
			if (entry.indexOf("/") >= 0) entry = entry.substring(entry.lastIndexOf("/") + 1);
		}
		if (acceptArchiveFilesImmediately == true && Finder.isArchiveFile(fileTarget)) {
			result = true;
		} else if (findTextInFiles == null && entry.matches(fileName)) {
			result = true;
		} else if (findTextInFiles != null && entry.matches(fileName)) {
			try {
				if (inputStream == null) {
					inputStream = new FileInputStream(fileTarget);
				}
				byte[] bytes = IOUtils.toByteArray(inputStream);
				String content = new String(bytes, "UTF-8");
				Pattern pattern = null;
				if (caseInsensitiveSearch == true) {
					pattern = Pattern.compile(findTextInFiles, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
				} else {
					pattern = Pattern.compile(findTextInFiles);
				}
				result = pattern.matcher(content).find();
			} finally {
				if (inputStream != null && inputStream instanceof FileInputStream) {
					try {
						inputStream.close();
					} catch (Exception ex) {
						System.err.println("Unable to close (File)InputStream - " + inputStream);
					}
				}
			}
		}
		return result;
	}

}
