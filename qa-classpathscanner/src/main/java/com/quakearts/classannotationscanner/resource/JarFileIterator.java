/*******************************************************************************
* Copyright (C) 2016 Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kwaku Twumasi-Afriyie <kwaku.twumasi@quakearts.com> - initial API and implementation
 ******************************************************************************/
package com.quakearts.classannotationscanner.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.quakearts.classannotationscanner.Filter;
import com.quakearts.classannotationscanner.exception.ScannerRuntimeException;

/**Implementation of {@link ResourceInputStreamIterator} that iterates over class files in a jar archive
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public final class JarFileIterator implements ResourceInputStreamIterator {

    private JarInputStream jarInputStream;

    private JarEntry next;

    private Filter filter;

    private boolean start = true;

    private boolean closed = false;

    /**Constructs a resource iterator using the given jar file
     * @param file The {@link File} object. Must point to a valid jar file
     * @param filter The {@link Filter} object to use to skip files
     * @throws IOException
     */
    public JarFileIterator(File file, Filter filter) throws IOException {
        this(new FileInputStream(file), filter);
    }

    /**Constructs a resource iterator using the given input stream
     * @param is The {@link InputStream}
     * @param filter The {@link Filter} object to use to skip files
     * @throws IOException
     */
    public JarFileIterator(InputStream is, Filter filter) throws IOException {
        this.filter = filter;
        jarInputStream = new JarInputStream(is);
    }

    /**Move the {@link JarInputStream} to the next entry
     * 
     */
    private void setNext() {
    	start = true;
        try {
            if (next != null) {
            	jarInputStream.closeEntry();
            }
            next = null;

            do {
                next = jarInputStream.getNextJarEntry();
            } while (next != null && (next.isDirectory() || (filter == null || !filter.accepts(next.getName()))));

            if (next == null) {
                close();
            }
        } catch (IOException e) {
            throw new ScannerRuntimeException("failed to browse jar", e);
        }
    }

    /* (non-Javadoc)
     * @see com.quakearts.classpathscanner.resource.ResourceIterator#next()
     */
    @Override
    public InputStream next() {
        if (closed || (next == null && !start)) {
            return null;
        }
        setNext();
        if (next == null) {
            return null;
        }
        return new JarInputStreamWrapper(jarInputStream);
    }

    /* (non-Javadoc)
     * @see com.quakearts.classpathscanner.resource.ResourceIterator#close()
     */
    @Override
    public void close() {
        try {
            closed = true;
            jarInputStream.close();
        } catch (IOException ioe) {
        	//Ignore
        }
    }

    /**Wrapper class for {@link JarInputStream}
     * @author animesh.kumar
     *
     */
    static class JarInputStreamWrapper extends InputStream {

        // input stream object which is wrapped
    	private InputStream is;

        /**Constructs the wrapper using the given {@link InputStream}
         * @param is The {@link InputStream}
         */
        public JarInputStreamWrapper(InputStream is) {
            this.is = is;
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException {
            return is.read();
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#read(byte[])
         */
        @Override
        public int read(byte[] bytes) throws IOException {
            return is.read(bytes);
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] bytes, int i, int i1) throws IOException {
            return is.read(bytes, i, i1);
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#skip(long)
         */
        @Override
        public long skip(long l) throws IOException {
            return is.skip(l);
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#available()
         */
        @Override
        public int available() throws IOException {
            return is.available();
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() throws IOException {
            // DO Nothing
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#mark(int)
         */
        @Override
        public synchronized void mark(int i) {
        	is.mark(i);
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#reset()
         */
        @Override
        public synchronized void reset() throws IOException {
            is.reset();
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#markSupported()
         */
        @Override
        public boolean markSupported() {
            return is.markSupported();
        }
    }
}
