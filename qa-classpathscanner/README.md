# qa-classpathscanner

A classpath scanner inspired by [Annovention](http://code.google.com/p/annovention/) developed by Animesh Kumar.

##### Requirements
* Java 11+
* Maven

##### Quick Start

Add the QuakeArts.com maven repository

```
<repositories>
    <repository>
        <id>YOUR-PROJECT-NAME-mvn-repo</id>
        <url>https://raw.github.com/kwakutwumasi/Quakearts-Webtools/mvn-repo/</url>
    </repository>
</repositories>

```

then add the dependency

```
<dependency>
	<groupId>com.quakearts.webtools</groupId>
	<artifactId>qa-classpathscanner</artifactId>
	<version>2.0.0</version>
</dependency>

```

To use the classpath scanner, first implement one of _com.quakearts.classannotationscanner.listener.ClassAnnotationObjectScanningListener_ or _com.quakearts.classannotationscanner.listener.ClassAnnotationScanningListener_. The _ClassAnnotationObjectScanningListener_ passes the _javassist.bytecode.ClassFile_ to the listener, providing a way to manipulate the class object. The _ClassAnnotationScanningListener_ returns the class name only.
The listener can then be instantiated and passed to the _com.quakearts.classannotationscanner.ClasspathScanner_ instance. It will be called whenever an annotation listed by the '_String[] getAnnotationsToListenFor()_' method of the listener contains one of the annotations found in the class file.
<br />
Libraries can be skipped by setting a filter on the _ClasspathScanner_ instance. This is done using the '_public ClasspathScanner(Filter filter)_' constructor. The default filter is _com.quakearts.classannotationscanner.DefaultFilter_. The _public ClasspathScanner(String... packageName)_ constructor can also be used to specify a narrow set of packages to scan.