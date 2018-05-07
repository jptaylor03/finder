package finder;

import java.io.InputStream;

/**
 * API for (regular expression-based) folder/file name/content filtering.
 * 
 * @author TAYLOJ10
 */
interface IFileFilter {

	/**
	 * Determine whether a file should be included in the search results.
	 * 
	 * @param path String containing the path/file to interrogate.
	 * @param fileName String containing a regular expression to compare against the folder/file name.
	 * @param matchAgainstLeafNodeOnly Boolean indicating whether to compare against the leaf-most node of the path/file.
	 * @param findTextInFiles String containing (optional) regular expression to search for within each file.
	 * @param caseInsensitiveSearch Boolean indicating whether to ignore case when searching for text within the file.
	 * @param acceptArchiveFilesImmediately Boolean indicating whether to automatically accept archive files immediately.
	 * @param inputStream InputStream for accessing the contents of the specified file.
	 * @return Boolean indicating whether the file should be included in the search results.
	 * @throws Exception
	 */
	public abstract boolean accept(String path, String fileName, boolean matchAgainstLeafNodeOnly, String findTextInFiles,
			boolean caseInsensitiveSearch, boolean acceptArchiveFilesImmediately, InputStream inputStream) throws Exception;

}