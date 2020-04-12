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
package com.quakearts.classannotationscanner;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.quakearts.classannotationscanner.listener.ClassAnnotationObjectScanningListener;
import com.quakearts.classannotationscanner.listener.ClassAnnotationScanningListener;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

/**Base class for {@link Scanner} implementations. Should be subclassed by all other implementations for ease of use
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public abstract class ScannerImplBase implements Scanner {

    private final Map<String, Set<ClassAnnotationScanningListener>> classAnnotationListeners =
        new HashMap<>();

    private final Map<String, Set<ClassAnnotationObjectScanningListener>> classAnnotationObjectListeners =
    	new HashMap<>();

    static final Logger log = Logger.getLogger("Scanner");
    
    /* (non-Javadoc)
	 * @see com.quakearts.classpathscanner.Scanner#addAnnotationListener(com.quakearts.classpathscanner.listener.ClassAnnotationScanningListener)
	 */
    @Override
	public final Scanner addAnnotationListener (ClassAnnotationScanningListener listener) {
        addAnnotationListener (classAnnotationListeners, listener, listener.getAnnotationsToListenFor());
        return this;
    }

    /* (non-Javadoc)
	 * @see com.quakearts.classpathscanner.Scanner#addAnnotationListener(com.quakearts.classpathscanner.listener.ClassAnnotationObjectScanningListener)
	 */
    @Override
	public final Scanner addAnnotationListener (ClassAnnotationObjectScanningListener listener) {
    	addAnnotationListener (classAnnotationObjectListeners, listener, listener.getAnnotationsToListenFor());
        return this;
    }

    private <L> void addAnnotationListener (Map<String, Set<L>> map, L listener, String... annotations) {
        if (null == annotations || annotations.length == 0) {
            throw new IllegalArgumentException(listener.getClass() + " has no supporting Annotations. Check method supportedAnnotations");
        }

        for (String annotation : annotations) {
            Set<L> listeners = map.computeIfAbsent(annotation, key-> new HashSet<>());
            listeners.add(listener);
        }
    }

    /**Process an {@link InputStream} resource. Loads a {@link ClassFile} and passes it 
     * to {@link #scanAndNotifyForClassAnnotations(ClassFile)}
     * @param is
     * @throws IOException
     */
    protected void processInputStream(InputStream is) throws IOException{
    	 DataInputStream dstream = new DataInputStream(new BufferedInputStream(is));
         try {
             ClassFile classFile = new ClassFile(dstream);
             scanAndNotifyForClassAnnotations (classFile);
         } finally {
              dstream.close();
              is.close();
         }
    }
    
    /**Loop through Annotations and find listeners to process Annotations, if any
     * @param classFile {@link ClassFile} object to search for annotations
     */
    protected void scanAndNotifyForClassAnnotations(ClassFile classFile) {
        Set<Annotation> annotations = new HashSet<>();

		AnnotationsAttribute visibleA = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if (visibleA != null) annotations.addAll(Arrays.asList(visibleA.getAnnotations()));

        for (Annotation annotation : annotations) {
			Set<ClassAnnotationScanningListener> listeners = classAnnotationListeners.get(annotation.getTypeName());
			if (null != listeners) {
    			for (ClassAnnotationScanningListener listener : listeners) {
    				listener.handle(classFile.getName(), annotation.getTypeName());
    			}
			}

			Set<ClassAnnotationObjectScanningListener> objectListeners = classAnnotationObjectListeners.get(annotation.getTypeName());
			if (null != objectListeners) {
				for (ClassAnnotationObjectScanningListener listener : objectListeners) {
					listener.handle(classFile, annotation);
				}
			}
        }
    }

}
