package panda.io.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import panda.io.IOCase;

/**
 * Useful utilities for working with file filters. It provides access to all file filter
 * implementations in this package so you don't have to import every class you use.
 */
public class FileFilters {

	/**
	 * FileFilterUtils is not normally instantiated.
	 */
	public FileFilters() {
	}

	// -----------------------------------------------------------------------

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting array is
	 * a subset of the original file list that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link Set} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * Set&lt;File&gt; allFiles = ...
	 * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
	 *     FileFilterUtils.suffixFileFilter(".java"));
	 * </pre>
	 * 
	 * @param filter the filter to apply to the set of files.
	 * @param files the array of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static File[] filter(IOFileFilter filter, File... files) {
		if (filter == null) {
			throw new IllegalArgumentException("file filter is null");
		}
		if (files == null) {
			return new File[0];
		}
		List<File> acceptedFiles = new ArrayList<File>();
		for (File file : files) {
			if (file == null) {
				throw new IllegalArgumentException("file array contains null");
			}
			if (filter.accept(file)) {
				acceptedFiles.add(file);
			}
		}
		return acceptedFiles.toArray(new File[acceptedFiles.size()]);
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting array is
	 * a subset of the original file list that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link Set} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * Set&lt;File&gt; allFiles = ...
	 * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
	 *     FileFilterUtils.suffixFileFilter(".java"));
	 * </pre>
	 * 
	 * @param filter the filter to apply to the set of files.
	 * @param files the array of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static File[] filter(IOFileFilter filter, Iterable<File> files) {
		List<File> acceptedFiles = filterList(filter, files);
		return acceptedFiles.toArray(new File[acceptedFiles.size()]);
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting list is a
	 * subset of the original files that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link List} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * List&lt;File&gt; filesAndDirectories = ...
	 * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
	 *     FileFilterUtils.directoryFileFilter());
	 * </pre>
	 * 
	 * @param filter the filter to apply to each files in the list.
	 * @param files the collection of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static List<File> filterList(IOFileFilter filter, Iterable<File> files) {
		return filter(filter, files, new ArrayList<File>());
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting list is a
	 * subset of the original files that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link List} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * List&lt;File&gt; filesAndDirectories = ...
	 * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
	 *     FileFilterUtils.directoryFileFilter());
	 * </pre>
	 * 
	 * @param filter the filter to apply to each files in the list.
	 * @param files the collection of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static List<File> filterList(IOFileFilter filter, File... files) {
		File[] acceptedFiles = filter(filter, files);
		return Arrays.asList(acceptedFiles);
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting set is a
	 * subset of the original file list that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link Set} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * Set&lt;File&gt; allFiles = ...
	 * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
	 *     FileFilterUtils.suffixFileFilter(".java"));
	 * </pre>
	 * 
	 * @param filter the filter to apply to the set of files.
	 * @param files the collection of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static Set<File> filterSet(IOFileFilter filter, File... files) {
		File[] acceptedFiles = filter(filter, files);
		return new HashSet<File>(Arrays.asList(acceptedFiles));
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects. The resulting set is a
	 * subset of the original file list that matches the provided filter.
	 * </p>
	 * <p>
	 * The {@link Set} returned by this method is not guaranteed to be thread safe.
	 * </p>
	 * 
	 * <pre>
	 * Set&lt;File&gt; allFiles = ...
	 * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
	 *     FileFilterUtils.suffixFileFilter(".java"));
	 * </pre>
	 * 
	 * @param filter the filter to apply to the set of files.
	 * @param files the collection of files to apply the filter to.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	public static Set<File> filterSet(IOFileFilter filter, Iterable<File> files) {
		return filter(filter, files, new HashSet<File>());
	}

	/**
	 * <p>
	 * Applies an {@link IOFileFilter} to the provided {@link File} objects and appends the accepted
	 * files to the other supplied collection.
	 * </p>
	 * 
	 * <pre>
	 * List&lt;File&gt; files = ...
	 * List&lt;File&gt; directories = FileFilterUtils.filterList(files,
	 *     FileFilterUtils.sizeFileFilter(FileUtils.FIFTY_MB), 
	 *         new ArrayList&lt;File&gt;());
	 * </pre>
	 * 
	 * @param filter the filter to apply to the collection of files.
	 * @param files the collection of files to apply the filter to.
	 * @param acceptedFiles the list of files to add accepted files to.
	 * @param <T> the type of the file collection.
	 * @return a subset of <code>files</code> that is accepted by the file filter.
	 * @throws IllegalArgumentException if the filter is {@code null} or <code>files</code> contains
	 *             a {@code null} value.
	 */
	private static <T extends Collection<File>> T filter(IOFileFilter filter, Iterable<File> files, T acceptedFiles) {
		if (filter == null) {
			throw new IllegalArgumentException("file filter is null");
		}
		if (files != null) {
			for (File file : files) {
				if (file == null) {
					throw new IllegalArgumentException("file collection contains null");
				}
				if (filter.accept(file)) {
					acceptedFiles.add(file);
				}
			}
		}
		return acceptedFiles;
	}

	/**
	 * Returns a filter that returns true if the filename starts with the specified text.
	 * 
	 * @param prefix the filename prefix
	 * @return a prefix checking filter
	 * @see PrefixFileFilter
	 */
	public static IOFileFilter prefixFileFilter(String... prefix) {
		return new PrefixFileFilter(prefix);
	}

	/**
	 * Returns a filter that returns true if the filename starts with the specified text.
	 * 
	 * @param caseSensitivity how to handle case sensitivity, null means case-sensitive
	 * @param prefix the filename prefix
	 * @return a prefix checking filter
	 * @see PrefixFileFilter
	 */
	public static IOFileFilter prefixFileFilter(IOCase caseSensitivity, String... prefix) {
		return new PrefixFileFilter(prefix, caseSensitivity);
	}

	/**
	 * Returns a filter that returns true if the file path starts with the specified text.
	 * 
	 * @param prefix the file path prefix
	 * @return a prefix checking filter
	 * @see PrefixPathFilter
	 */
	public static IOFileFilter prefixPathFilter(String... prefix) {
		return new PrefixPathFilter(prefix);
	}

	/**
	 * Returns a filter that returns true if the file path starts with the specified text.
	 * 
	 * @param caseSensitivity how to handle case sensitivity, null means case-sensitive
	 * @param prefix the filename prefix
	 * @return a prefix checking filter
	 * @see PrefixPathFilter
	 */
	public static IOFileFilter prefixPathFilter(IOCase caseSensitivity, String... prefix) {
		return new PrefixPathFilter(prefix, caseSensitivity);
	}

	/**
	 * Returns a filter that returns true if the filename ends with the specified text.
	 * 
	 * @param suffix the filename suffix
	 * @return a suffix checking filter
	 * @see SuffixFileFilter
	 */
	public static IOFileFilter suffixFileFilter(String... suffix) {
		return new SuffixFileFilter(suffix);
	}

	/**
	 * Returns a filter that returns true if the filename ends with the specified text.
	 * 
	 * @param suffix the filename suffix
	 * @param caseSensitivity how to handle case sensitivity, null means case-sensitive
	 * @return a suffix checking filter
	 * @see SuffixFileFilter
	 */
	public static IOFileFilter suffixFileFilter(IOCase caseSensitivity, String... suffix) {
		return new SuffixFileFilter(suffix, caseSensitivity);
	}

	/**
	 * Returns a filter that returns true if the filename ends with the specified text.
	 * 
	 * @param suffix the file path suffix
	 * @return a suffix checking filter
	 * @see SuffixPathFilter
	 */
	public static IOFileFilter suffixPathFilter(String... suffix) {
		return new SuffixPathFilter(suffix);
	}

	/**
	 * Returns a filter that returns true if the filename ends with the specified text.
	 * 
	 * @param suffix the file path suffix
	 * @param caseSensitivity how to handle case sensitivity, null means case-sensitive
	 * @return a suffix checking filter
	 * @see SuffixFileFilter
	 */
	public static IOFileFilter suffixPathFilter(IOCase caseSensitivity, String... suffix) {
		return new SuffixPathFilter(suffix, caseSensitivity);
	}

	/**
	 * Returns a filter that returns true if the filename matches the specified text.
	 * 
	 * @param name the filename
	 * @return a name checking filter
	 * @see NameFileFilter
	 */
	public static IOFileFilter nameFileFilter(String name) {
		return new NameFileFilter(name);
	}

	/**
	 * Returns a filter that returns true if the filename matches the specified text.
	 * 
	 * @param name the filename
	 * @param caseSensitivity how to handle case sensitivity, null means case-sensitive
	 * @return a name checking filter
	 * @see NameFileFilter
	 */
	public static IOFileFilter nameFileFilter(String name, IOCase caseSensitivity) {
		return new NameFileFilter(name, caseSensitivity);
	}

	/**
	 * Returns a filter that checks if the file is a directory.
	 * 
	 * @return file filter that accepts only directories and not files
	 * @see DirectoryFileFilter#DIRECTORY
	 */
	public static IOFileFilter directoryFileFilter() {
		return DirectoryFileFilter.DIRECTORY;
	}

	/**
	 * Returns a filter that checks if the file is a file (and not a directory).
	 * 
	 * @return file filter that accepts only files and not directories
	 * @see FileFileFilter#FILE
	 */
	public static IOFileFilter fileFileFilter() {
		return FileFileFilter.FILE;
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns a filter that ANDs the specified filters.
	 * 
	 * @param filters the IOFileFilters that will be ANDed together.
	 * @return a filter that ANDs the specified filters
	 * @throws IllegalArgumentException if the filters are null or contain a null value.
	 * @see AndFileFilter
	 */
	public static IOFileFilter and(IOFileFilter... filters) {
		return new AndFileFilter(toList(filters));
	}

	/**
	 * Returns a filter that ORs the specified filters.
	 * 
	 * @param filters the IOFileFilters that will be ORed together.
	 * @return a filter that ORs the specified filters
	 * @throws IllegalArgumentException if the filters are null or contain a null value.
	 * @see OrFileFilter
	 */
	public static IOFileFilter or(IOFileFilter... filters) {
		return new OrFileFilter(toList(filters));
	}

	/**
	 * Create a List of file filters.
	 * 
	 * @param filters The file filters
	 * @return The list of file filters
	 * @throws IllegalArgumentException if the filters are null or contain a null value.
	 */
	public static List<IOFileFilter> toList(IOFileFilter... filters) {
		if (filters == null) {
			throw new IllegalArgumentException("The filters must not be null");
		}
		List<IOFileFilter> list = new ArrayList<IOFileFilter>(filters.length);
		for (int i = 0; i < filters.length; i++) {
			if (filters[i] == null) {
				throw new IllegalArgumentException("The filter[" + i + "] is null");
			}
			list.add(filters[i]);
		}
		return list;
	}

	/**
	 * Returns a filter that NOTs the specified filter.
	 * 
	 * @param filter the filter to invert
	 * @return a filter that NOTs the specified filter
	 * @see NotFileFilter
	 */
	public static IOFileFilter notFileFilter(IOFileFilter filter) {
		return new NotFileFilter(filter);
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns a filter that always returns true.
	 * 
	 * @return a true filter
	 * @see TrueFileFilter#TRUE
	 */
	public static IOFileFilter trueFileFilter() {
		return TrueFileFilter.TRUE;
	}

	/**
	 * Returns a filter that always returns false.
	 * 
	 * @return a false filter
	 * @see FalseFileFilter#FALSE
	 */
	public static IOFileFilter falseFileFilter() {
		return FalseFileFilter.FALSE;
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns an <code>IOFileFilter</code> that wraps the <code>FileFilter</code> instance.
	 * 
	 * @param filter the filter to be wrapped
	 * @return a new filter that implements IOFileFilter
	 * @see DelegateFileFilter
	 */
	public static IOFileFilter asFileFilter(FileFilter filter) {
		return new DelegateFileFilter(filter);
	}

	/**
	 * Returns an <code>IOFileFilter</code> that wraps the <code>FilenameFilter</code> instance.
	 * 
	 * @param filter the filter to be wrapped
	 * @return a new filter that implements IOFileFilter
	 * @see DelegateFileFilter
	 */
	public static IOFileFilter asFileFilter(FilenameFilter filter) {
		return new DelegateFileFilter(filter);
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns a filter that returns true if the file was last modified after the specified cutoff
	 * time.
	 * 
	 * @param cutoff the time threshold
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(long cutoff) {
		return new AgeFileFilter(cutoff);
	}

	/**
	 * Returns a filter that filters files based on a cutoff time.
	 * 
	 * @param cutoff the time threshold
	 * @param newer if false, older files get accepted, if false, newer
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(long cutoff, boolean newer) {
		return new AgeFileFilter(cutoff, newer);
	}

	/**
	 * Returns a filter that returns true if the file was last modified after the specified cutoff
	 * date.
	 * 
	 * @param cutoffDate the time threshold
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(Date cutoffDate) {
		return new AgeFileFilter(cutoffDate);
	}

	/**
	 * Returns a filter that filters files based on a cutoff date.
	 * 
	 * @param cutoffDate the time threshold
	 * @param newer if false, older files get accepted, if false, newer
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(Date cutoffDate, boolean newer) {
		return new AgeFileFilter(cutoffDate, newer);
	}

	/**
	 * Returns a filter that returns true if the file was last modified after the specified
	 * reference file.
	 * 
	 * @param cutoffReference the file whose last modification time is usesd as the threshold age of
	 *            the files
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(File cutoffReference) {
		return new AgeFileFilter(cutoffReference);
	}

	/**
	 * Returns a filter that filters files based on a cutoff reference file.
	 * 
	 * @param cutoffReference the file whose last modification time is usesd as the threshold age of
	 *            the files
	 * @param newer if false, older files get accepted, if false, newer
	 * @return an appropriately configured age file filter
	 * @see AgeFileFilter
	 */
	public static IOFileFilter ageFileFilter(File cutoffReference, boolean newer) {
		return new AgeFileFilter(cutoffReference, newer);
	}

	// -----------------------------------------------------------------------
	/**
	 * Returns a filter that returns true if the file is bigger than a certain size.
	 * 
	 * @param threshold the file size threshold
	 * @return an appropriately configured SizeFileFilter
	 * @see SizeFileFilter
	 */
	public static IOFileFilter sizeFileFilter(long threshold) {
		return new SizeFileFilter(threshold);
	}

	/**
	 * Returns a filter that filters based on file size.
	 * 
	 * @param threshold the file size threshold
	 * @param acceptLarger if true, larger files get accepted, if false, smaller
	 * @return an appropriately configured SizeFileFilter
	 * @see SizeFileFilter
	 */
	public static IOFileFilter sizeFileFilter(long threshold, boolean acceptLarger) {
		return new SizeFileFilter(threshold, acceptLarger);
	}

	/**
	 * Returns a filter that accepts files whose size is &gt;= minimum size and &lt;= maximum size.
	 * 
	 * @param minSizeInclusive the minimum file size (inclusive)
	 * @param maxSizeInclusive the maximum file size (inclusive)
	 * @return an appropriately configured IOFileFilter
	 * @see SizeFileFilter
	 */
	public static IOFileFilter sizeRangeFileFilter(long minSizeInclusive, long maxSizeInclusive) {
		IOFileFilter minimumFilter = new SizeFileFilter(minSizeInclusive, true);
		IOFileFilter maximumFilter = new SizeFileFilter(maxSizeInclusive + 1L, false);
		return new AndFileFilter(minimumFilter, maximumFilter);
	}

	/**
	 * Returns a filter that accepts files that begin with the provided magic number.
	 * 
	 * @param magicNumber the magic number (byte sequence) to match at the beginning of each file.
	 * @return an IOFileFilter that accepts files beginning with the provided magic number.
	 * @throws IllegalArgumentException if <code>magicNumber</code> is {@code null} or the empty
	 *             String.
	 * @see MagicNumberFileFilter
	 */
	public static IOFileFilter magicNumberFileFilter(String magicNumber) {
		return new MagicNumberFileFilter(magicNumber);
	}

	/**
	 * Returns a filter that accepts files that contains the provided magic number at a specified
	 * offset within the file.
	 * 
	 * @param magicNumber the magic number (byte sequence) to match at the provided offset in each
	 *            file.
	 * @param offset the offset within the files to look for the magic number.
	 * @return an IOFileFilter that accepts files containing the magic number at the specified
	 *         offset.
	 * @throws IllegalArgumentException if <code>magicNumber</code> is {@code null} or the empty
	 *             String, or if offset is a negative number.
	 * @see MagicNumberFileFilter
	 */
	public static IOFileFilter magicNumberFileFilter(String magicNumber, long offset) {
		return new MagicNumberFileFilter(magicNumber, offset);
	}

	/**
	 * Returns a filter that accepts files that begin with the provided magic number.
	 * 
	 * @param magicNumber the magic number (byte sequence) to match at the beginning of each file.
	 * @return an IOFileFilter that accepts files beginning with the provided magic number.
	 * @throws IllegalArgumentException if <code>magicNumber</code> is {@code null} or is of length
	 *             zero.
	 * @see MagicNumberFileFilter
	 */
	public static IOFileFilter magicNumberFileFilter(byte[] magicNumber) {
		return new MagicNumberFileFilter(magicNumber);
	}

	/**
	 * Returns a filter that accepts files that contains the provided magic number at a specified
	 * offset within the file.
	 * 
	 * @param magicNumber the magic number (byte sequence) to match at the provided offset in each
	 *            file.
	 * @param offset the offset within the files to look for the magic number.
	 * @return an IOFileFilter that accepts files containing the magic number at the specified
	 *         offset.
	 * @throws IllegalArgumentException if <code>magicNumber</code> is {@code null}, or contains no
	 *             bytes, or <code>offset</code> is a negative number.
	 * @see MagicNumberFileFilter
	 */
	public static IOFileFilter magicNumberFileFilter(byte[] magicNumber, long offset) {
		return new MagicNumberFileFilter(magicNumber, offset);
	}

	// -----------------------------------------------------------------------
	/* Constructed on demand and then cached */
	private static final IOFileFilter cvsFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter("CVS")));

	/* Constructed on demand and then cached */
	private static final IOFileFilter svnFilter = notFileFilter(and(directoryFileFilter(), nameFileFilter(".svn")));

	/**
	 * Decorates a filter to make it ignore CVS directories. Passing in {@code null} will return a
	 * filter that accepts everything except CVS directories.
	 * 
	 * @param filter the filter to decorate, null means an unrestricted filter
	 * @return the decorated filter, never null
	 */
	public static IOFileFilter makeCVSAware(IOFileFilter filter) {
		if (filter == null) {
			return cvsFilter;
		}
		else {
			return and(filter, cvsFilter);
		}
	}

	/**
	 * Decorates a filter to make it ignore SVN directories. Passing in {@code null} will return a
	 * filter that accepts everything except SVN directories.
	 * 
	 * @param filter the filter to decorate, null means an unrestricted filter
	 * @return the decorated filter, never null
	 */
	public static IOFileFilter makeSVNAware(IOFileFilter filter) {
		if (filter == null) {
			return svnFilter;
		}
		else {
			return and(filter, svnFilter);
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Decorates a filter so that it only applies to directories and not to files.
	 * 
	 * @param filter the filter to decorate, null means an unrestricted filter
	 * @return the decorated filter, never null
	 * @see DirectoryFileFilter#DIRECTORY
	 */
	public static IOFileFilter makeDirectoryOnly(IOFileFilter filter) {
		if (filter == null) {
			return DirectoryFileFilter.DIRECTORY;
		}
		return new AndFileFilter(DirectoryFileFilter.DIRECTORY, filter);
	}

	/**
	 * Decorates a filter so that it only applies to files and not to directories.
	 * 
	 * @param filter the filter to decorate, null means an unrestricted filter
	 * @return the decorated filter, never null
	 * @see FileFileFilter#FILE
	 */
	public static IOFileFilter makeFileOnly(IOFileFilter filter) {
		if (filter == null) {
			return FileFileFilter.FILE;
		}
		return new AndFileFilter(FileFileFilter.FILE, filter);
	}

}
