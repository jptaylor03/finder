package finder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Find Files In Zip, Jar, War, Ear And Nested Archives.
 * <p>
 *  NOTE: The specified directory/archive is searched for folder/file names matching the regular expression.
 *  Zip files (and zip-alikes) are also searched including nested zips.
 * </p>
 * 
 * @author http://www.dzone.com/snippets/find-files-zip-jar-war-ear-and
 * @author James P. Taylor
 */
public class Finder {

	/**
	 * Supported archive file extensions.
	 */
	public static String[] ARCHIVE_FILE_EXTENSIONS = { ".zip", ".jar", ".war", ".ear" };

	/**
	 * Concrete IFileFilter implementation.
	 */
	private static IFileFilter fileFilter = new FileFilter();

	/**
	 * Main entry point for this class (for testing purposes).
	 * <p>
	 *  NOTE: Accepts these arguments:
	 *  <ol>
	 *   <li>[String] Directory/archive name (required)</li>
	 *   <li>[String] Regular expression to match against file name (required)</li>
	 *   <li>[Boolean] Whether to match only against the leaf-most node of each path (optional, defaults to false)</li>
	 *   <li>[String] Regular expression to match against file content (optional, defaults to null)</li>
	 *   <li>[Boolean] Whether to ignore case while searching file content (optional, defaults to false)</li>
	 *  </ol>
	 * </p>
	 * 
	 * @param args String array of command-line arguments.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 2) {
			System.out.println("Syntax: java -jar finder.jar (path to search) (regular expression for entries to find) [match against leaf node only] [regular expression for text to find] [whether search should ignore case]\nExample: java -jar finder.jar C:\\temp\\myEAR.ear .*[.]properties true label[.]required false\nExample: java -jar finder.jar c:/temp/myFolder .*[.]log false Exception true");
			return;
		}
		final String  path            = args[0];
		final String  filename        = args[1];
		final boolean leafNodeOnly    = args.length >= 3?Boolean.parseBoolean(args[2]):false;
		final String  findInFiles     = args.length >= 4?args[3]:null;
		final boolean caseInsensitive = args.length >= 5?Boolean.parseBoolean(args[4]):false;

		if (path == null || "".equals(path.trim()) || filename == null || "".equals(filename.trim())) {
			System.out.println("Syntax...\njava " + Finder.class.getName() + " (path to search) (regular expression for entries to find) [match against leaf node only] [regular expression for text to find] [whether search should ignore case]");
			return;
		}
		Finder finder = new Finder();
		List<String> result = finder.execute(path, filename, leafNodeOnly, findInFiles, caseInsensitive);
		for (String entry : result) {
			System.out.println(entry);
		}
	}

	/**
	 * Perform the actual search/find.
	 * 
	 * @param path String containing the path to search.
	 * @param filename String containing the regular expression to match against.
	 * @param leafNodeOnly Boolean indicating whether to only match against each path's leaf-most node (versus match against the entire path).
	 * @param findInFiles String containing the text to find (optional).
	 * @param caseInsensitive Boolean indicating whether the findInFiles search should ignore case.
	 * @return List&lt;String&gt; containing the matching file(s).
	 * @throws Exception
	 */
	public List<String> execute(String path, final String filename, final boolean leafNodeOnly, final String findInFiles, final boolean caseInsensitive) throws Exception {
		List<String> result = new ArrayList<String>();
		
		System.out.println("Searching '" + path + "' for" + (leafNodeOnly?" LEAFNODE-ONLY":"") + " entries named like '" + filename + "'" + (findInFiles!=null?" and containing '" + findInFiles + "'" + (caseInsensitive?" (CASE INSENSITIVE)":""):"") + "...");

		List<File> matchingFiles = new ArrayList<File>();
		findFile(new File(path), filename, leafNodeOnly, findInFiles, caseInsensitive, true, matchingFiles);

		List<String> matchingNestedFileTargets = new ArrayList<String>();
		for (Iterator<File> it = matchingFiles.iterator(); it.hasNext();) {
			File matchingFile = (File) it.next();
			String matchingFileTargets = matchingFile + "";
			if (fileFilter.accept(matchingFileTargets, filename, leafNodeOnly, findInFiles, caseInsensitive, false, null)) {
				matchingNestedFileTargets.add(matchingFileTargets);
			}
			if (isArchiveFile(matchingFile.getName())) {
				InputStream inputStream = new FileInputStream(matchingFile);
				findArchiveFile(matchingFileTargets, filename, leafNodeOnly, findInFiles, caseInsensitive, false, inputStream, matchingNestedFileTargets);
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception ex) {
					System.out.println("Unable to close (File)InputStream: " + ex.getMessage());
				}
			}
		}

		for (Iterator<String> it = matchingNestedFileTargets.iterator(); it.hasNext();) {
			result.add(it.next());
		}
		System.out.println("Found " + matchingNestedFileTargets.size() + (leafNodeOnly?" LEAFNODE-ONLY":"") + " entries in '" + path + "' named like '" + filename + "'" + (findInFiles!=null?" and containing '" + findInFiles + "'" + (caseInsensitive?" (CASE INSENSITIVE)":""):"") + ".");
		
		return result;
	}

	/**
	 * Search the specified folder/file for matches.
	 * 
	 * @param path File identifying the current folder/file to search.
	 * @param fileName String containing the regex to match against folder/file name.
	 * @param matchAgainstLeafNodeOnly Boolean indicating whether to match against leaf nodes only.
	 * @param findTextInFiles String containing the (optional) regex to match against file contents.
	 * @param caseInsensitiveSearch Boolean indicating whether to ignore case while searching.
	 * @param acceptArchiveFilesImmediately Boolean indicating whether to automatically accept zip files.
	 * @param results List&lt;File&gt; of resulting matches.
	 * @throws Exception
	 */
	private static void findFile(File path, String fileName, boolean matchAgainstLeafNodeOnly, String findTextInFiles, boolean caseInsensitiveSearch, boolean acceptArchiveFilesImmediately, List<File> results) throws Exception {
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				findFile(files[i], fileName, matchAgainstLeafNodeOnly, findTextInFiles, caseInsensitiveSearch, acceptArchiveFilesImmediately, results);
			}
		} else if (fileFilter.accept(path + "", fileName, matchAgainstLeafNodeOnly, findTextInFiles, caseInsensitiveSearch, acceptArchiveFilesImmediately, null)) {
			results.add(path);
		}
	}

	/**
	 * Search the specified archive file for matches.
	 * 
	 * @param path  String containing the current path inside the archive being searched.
	 * @param fileName  String containing the regex to match against folder/file name.
	 * @param matchAgainstLeafNodeOnly  Boolean indicating whether to match against leaf nodes only.
	 * @param findTextInFiles  String containing the (optional) regex to match against file contents.
	 * @param caseInsensitiveSearch Boolean indicating whether to ignore case while searching.
	 * @param acceptArchiveFilesImmediately  Boolean indicating whether to automatically accept zip files.
	 * @param inputStream InputStream for the current archive file to search.
	 * @param results  List&lt;File&gt; of resulting matches.
	 * @throws Exception
	 */
	private static void findArchiveFile(String path, String fileName, boolean matchAgainstLeafNodeOnly, String findTextInFiles, boolean caseInsensitiveSearch, boolean acceptArchiveFilesImmediately, InputStream inputStream, List<String> results) throws Exception {
		ZipEntry zipEntry = null;
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			if (fileFilter.accept(zipEntry.getName(), fileName, matchAgainstLeafNodeOnly, findTextInFiles, caseInsensitiveSearch, acceptArchiveFilesImmediately, zipInputStream)) {
				results.add(path + "!" + zipEntry);
			}
			if (isArchiveFile(zipEntry.getName())) {
				findArchiveFile(path + "!" + zipEntry, fileName, matchAgainstLeafNodeOnly, findTextInFiles, caseInsensitiveSearch, acceptArchiveFilesImmediately, zipInputStream, results);
			}
		}
	}

	/**
	 * Whether the specified folder/file name ends with one of the supported archive file extensions.
	 * 
	 * @param fileTarget String containing a folder/file name.
	 * @return Boolean indicating whether the specified folder/file name ends with one of the supported archive file extensions.
	 */
	public static boolean isArchiveFile(String fileTarget) {
		for (int i = 0; i < ARCHIVE_FILE_EXTENSIONS.length; i++) {
			if (fileTarget.toLowerCase().endsWith(ARCHIVE_FILE_EXTENSIONS[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
