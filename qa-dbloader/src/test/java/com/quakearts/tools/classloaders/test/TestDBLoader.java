package com.quakearts.tools.classloaders.test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

import org.junit.Test;

import com.quakearts.tools.classloaders.DBJarClassLoader;
import com.quakearts.tools.classloaders.hibernate.JarFile;
import com.quakearts.tools.classloaders.utils.EventType;
import com.quakearts.tools.classloaders.utils.FileStorageEvent;
import com.quakearts.tools.classloaders.utils.JarFileStorer;
import com.quakearts.webapp.hibernate.CurrentSessionContextHelper;
import com.quakearts.webapp.orm.DataStoreFactory;

public class TestDBLoader {
	
	@Test
	public void test() throws Exception {
		Stack<FileStorageEvent> events = new Stack<>(); 
		DBJarClassLoader classLoader = new DBJarClassLoader();
		
		JarFileStorer jarFileStorer = new JarFileStorer()
				.addListener(events::push);
		byte[] jarBytes;
		jarBytes = readFileBytes("test.jar");
		
		jarFileStorer.storeJarFile(jarBytes, "test.jar");
		DataStoreFactory.getInstance().getDataStore().flushBuffers();
		CurrentSessionContextHelper.closeOpenSessions();
		
		assertThat(events.size(), is(1));
		FileStorageEvent event = events.pop();
		assertThat(event.getType(), is(EventType.NEWFILE));
		assertThat(event.getJarFileEntry().getId(), is("test/TestClass.class"));
		assertThat(event.getJarFileEntry().getJarFile().getJarName(), is("test.jar"));
		assertThat(event.getJarFileEntry().getJarFile().getJarData(), is(jarBytes));
		
		try {
			Class.forName("test.TestClass", true, classLoader);
		} catch (Exception e) {
			fail("Unable to load class: "+e.getMessage());
		}
		
		jarFileStorer.storeJarFile(jarBytes, "testV2.jar");		
		assertThat(jarFileStorer.getSummary("<br />").contains("No files were stored.<br />"), is(true));
		DataStoreFactory.getInstance().getDataStore().flushBuffers();
		CurrentSessionContextHelper.closeOpenSessions();		
		assertThat(events.isEmpty(), is(true));
		
		jarBytes = readFileBytes("testV2.jar");
		jarFileStorer.storeJarFile(jarBytes, "testV2.jar");
		DataStoreFactory.getInstance().getDataStore().flushBuffers();
		CurrentSessionContextHelper.closeOpenSessions();
		
		URL url = classLoader
				.getResource("test/test.properties");
		assertThat(url != null, is(true));
		
		InputStream is = classLoader
				.getResourceAsStream("test/test.properties");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		assertThat(reader.readLine(), is("test=property"));

		assertThat(events.size(), is(1));
		event = events.pop();
		assertThat(event.getType(), is(EventType.NEWFILE));
		assertThat(event.getJarFileEntry().getId(), is("test/test.properties"));
		assertThat(event.getJarFileEntry().getJarFile().getJarName(), is("testV2.jar"));
		assertThat(event.getJarFileEntry().getJarFile().getJarData(), is(jarBytes));
		
		jarBytes = readFileBytes("testV3.jar");
		jarFileStorer.storeJarFile(jarBytes, "testV3.jar");
		DataStoreFactory.getInstance().getDataStore().flushBuffers();
		CurrentSessionContextHelper.closeOpenSessions();
		
		assertThat(events.size(), is(3));
		event = events.pop();
		assertThat(event.getType(), is(EventType.UPDATEDFILE));
		assertThat(event.getJarFileEntry().getId(), is("test/test.properties"));
		assertThat(event.getJarFileEntry().getJarFile().getJarName(), is("testV3.jar"));
		assertThat(event.getJarFileEntry().getJarFile().getJarData(), is(jarBytes));
		event = events.pop();
		assertThat(event.getType(), is(EventType.UPDATEDFILE));
		assertThat(event.getJarFileEntry().getId(), is("test/TestClass.class"));
		assertThat(event.getJarFileEntry().getJarFile().getJarName(), is("testV3.jar"));
		assertThat(event.getJarFileEntry().getJarFile().getJarData(), is(jarBytes));
		event = events.pop();
		assertThat(event.getType(), is(EventType.NEWFILE));
		assertThat(event.getJarFileEntry().getId(), is("test/TestInterface.class"));
		assertThat(event.getJarFileEntry().getJarFile().getJarName(), is("testV3.jar"));
		assertThat(event.getJarFileEntry().getJarFile().getJarData(), is(jarBytes));
		
		JarFile[] deleted = jarFileStorer.cleanOrphanJars();
		
		assertThat(deleted.length, is(2));
		assertThat(deleted[0].getJarName(), is("test.jar"));
		assertThat(deleted[1].getJarName(), is("testV2.jar"));
		CurrentSessionContextHelper.closeOpenSessions();
	}

	private byte[] readFileBytes(String fileName) throws IOException, FileNotFoundException {
		byte[] jarBytes;
		try(FileInputStream fis = new FileInputStream("testjars"+File.separator+fileName);){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			int read;
			while ((read = fis.read())!=-1) {
				stream.write(read);
			}
			jarBytes = stream.toByteArray();
		}
		return jarBytes;
	}
}
