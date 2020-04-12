package com.quakearts.test.classpathscanner;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import com.quakearts.classannotationscanner.ClasspathScanner;
import com.quakearts.classannotationscanner.DefaultFilter;
import com.quakearts.classannotationscanner.listener.ClassAnnotationScanningListener;

public class TestClasspathScanner {
	
	@Test
	public void testScan() {
		TestClassAnnotationScanningListener listener = new TestClassAnnotationScanningListener();
		ClasspathScanner scanner = new ClasspathScanner();
		scanner.addAnnotationListener(listener);
		
		scanner.scan();
		assertTrue(listener.hasRun);
	}
	
	@Test
	public void testFilterScan() {
		TestClassAnnotationScanningListener listener = new TestClassAnnotationScanningListener();
		ClasspathScanner scanner = new ClasspathScanner(new DefaultFilter(new String[]{"com.quakearts.test.classpathscanner"}));
		scanner.addAnnotationListener(listener);
		scanner.scan();
		assertFalse(listener.hasRun);
		
		listener = new TestClassAnnotationScanningListener();
		scanner = new ClasspathScanner();
		scanner.addAnnotationListener(listener);
		scanner.setFilter(new DefaultFilter(new String[]{"com.quakearts.test.classpathscanner"}));
		scanner.scan();
		assertFalse(listener.hasRun);
	}
	
	@Test
	public void testWithURLCLassloader() throws Exception {
		URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("test-lib"+File.separator+"commons-beanutils-1.9.2.jar").toURI().toURL(),
				new File("test-lib"+File.separator+"commons-collections-3.2.2.jar").toURI().toURL()});
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);
			TestClassAnnotationScanningListener listener = new TestClassAnnotationScanningListener();
			ClasspathScanner scanner = new ClasspathScanner();
			scanner.addAnnotationListener(listener);
			
			scanner.scan();
			assertTrue(listener.hasRun);
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}
	
}

class TestClassAnnotationScanningListener implements ClassAnnotationScanningListener{

	boolean hasRun;
	
	@Override
	public String[] getAnnotationsToListenFor() {
		return new String[]{TestAnnotation.class.getName()};
	}

	@Override
	public void handle(String className, String annotation) {
		hasRun = true;
		assertTrue(className.equals(TestClass.class.getName()));
	}
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface TestAnnotation{	
}

@TestAnnotation
class TestClass{
}