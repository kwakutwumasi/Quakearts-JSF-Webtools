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
package com.quakearts.classannotationscanner.exception;

/**A subclass of {@link RuntimeException} to be thrown when scanning errors occur. Can be caught by applications
 * that can recover from such errors; in all other uses cases, as this is a 
 * @author Kwaku Twumasi Afriyie (kwaku.twumasi@quakearts.com)
 *
 */
public class ScannerRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7372531965898980265L;

	public ScannerRuntimeException() {
		super();
	}

	public ScannerRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScannerRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScannerRuntimeException(String message) {
		super(message);
	}

	public ScannerRuntimeException(Throwable cause) {
		super(cause);
	}

}
