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
package com.quakearts.classannotationscanner.listener;

import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

/**Interface for {@link AnnotationScanningListener}s that need access to the {@link ClassFile} object.
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public interface ClassAnnotationObjectScanningListener extends AnnotationScanningListener {
	/**Handle processing for the discovered class
	 * @param clazz The {@link ClassFile}
	 * @param annotation The {@link Annotation}
	 */
	void handle(ClassFile clazz, Annotation annotation);
}
