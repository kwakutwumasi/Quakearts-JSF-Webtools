package com.quakearts.classannotationscanner.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.quakearts.classannotationscanner.Filter;
import com.quakearts.classannotationscanner.exception.ScannerRuntimeException;

/**Implementation of {@link ResourceInputStreamIterator} that iterates over class files in a folder
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public class ClassFileIterator implements ResourceInputStreamIterator {

    private List<File> files;
    
    private File rootDir;

    private int index = 0;

    /**Constructs a resource file iterator using the given directory
     * @param file The {@link File} object of the directory. Must be a directory
     * @param filter The {@link Filter} object to use to skip files
     */
    public ClassFileIterator(File file, Filter filter) {
        files = new ArrayList<File>();
        if(file.isDirectory())
        	rootDir = file;
        else
        	throw new ScannerRuntimeException("ClassFileIterator requires a directory to traverse");
        
        try {
        	init(files, file, filter);
        } catch (Exception e) {
            throw new ScannerRuntimeException(e);
        }
    }

    /**Recursively iterate over files to scan subdirectories
     * @param list
     * @param dir
     * @param filter
     * @throws Exception
     */
    private void init(List<File> list, File dir, Filter filter) throws Exception {
        File[] files = dir.listFiles();
        for (File file:files) {
            if (file.isDirectory()) {
            	init(list, file, filter);
            } else {
            	String fileName = file.getAbsolutePath().substring(rootDir.getAbsolutePath().length());
            	
                if (filter == null || filter.accepts(fileName)) {
                    list.add(file);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.quakearts.classpathscanner.resource.ResourceIterator#next()
     */
    @Override
    public final InputStream next() {
        if (index >= files.size()){
            return null;
        }
        File fp = (File) files.get(index++);
        try {
            return new FileInputStream(fp);
        } catch (FileNotFoundException e) {
            throw new ScannerRuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.quakearts.classpathscanner.resource.ResourceIterator#close()
     */
    @Override
    public void close() {
    }
}
