package com.quakearts.tools.classloaders.hibernate;

// Generated 17-Mar-2014 00:13:24 by Hibernate Tools 3.4.0.CR1

/**
 * JarFileEntry generated by hbm2java
 */
public class JarFileEntry implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1662876000076587796L;
	private String id;
	private long jarId;
	private JarFile jarFile;

	public JarFileEntry() {
	}

	public JarFileEntry(String id, JarFile jarFile) {
		this.id = id;
		this.jarFile = jarFile;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JarFile getJarFile() {
		return this.jarFile;
	}

	public void setJarFile(JarFile jarFile) {
		this.jarFile = jarFile;
	}

	public long getJarId() {
		return jarId;
	}

	public void setJarId(long jarId) {
		this.jarId = jarId;
	}

	public String getEntryClassName() {
		if (this.id != null) {
			return this.id.replace(".class", "").replaceAll("[\\\\/$]", ".");
		}
		return "";
	}
}