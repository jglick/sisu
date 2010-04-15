/**
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.guice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.spi.ServiceRegistry;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import aQute.bnd.main.bnd;

import com.googlecode.guice.bundle.OSGiTestActivator;

/**
 * Run various tests inside one or more OSGi containers.
 * 
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
public class OSGiContainerTest
    extends TestCase {

  // build properties passed from Ant
  static final String VERSION = System.getProperty("version", "snapshot");
  static final String BUILD_DIR = System.getProperty("build.dir", "build");
  static final String LIB_DIR = System.getProperty("lib.dir", "lib");

  static final String BUILD_DIST_DIR = BUILD_DIR + "/dist";
  static final String BUILD_TEST_DIR = BUILD_DIR + "/test";

  static final String GUICE_JAR = BUILD_DIST_DIR + "/guice-" + VERSION + ".jar";

  // dynamically build test bundles
  @Override protected void setUp()
      throws Exception {

    // verify properties
    assertTrue(new File(BUILD_DIR).isDirectory());
    assertTrue(new File(LIB_DIR).isDirectory());
    assertTrue(new File(GUICE_JAR).isFile());

    Properties instructions = new Properties();

    // javax.inject is an API bundle --> export the full API
    instructions.setProperty("Export-Package", "javax.inject.*");
    buildBundle("javax.inject", instructions, LIB_DIR + "/javax.inject.jar");
    instructions.clear();

    // aopalliance is an API bundle --> export the full API
    instructions.setProperty("Export-Package", "org.aopalliance.*");
    buildBundle("aopalliance", instructions, LIB_DIR + "/aopalliance.jar");
    instructions.clear();

    // strict imports to make sure test bundle only has access to these packages
    instructions.setProperty("Import-Package", "org.osgi.framework,org.aopalliance.intercept,"
        + "com.google.inject(|.binder|.matcher|.name);version=\"[1,2)\"");

    // test bundle should only contain the local test classes, nothing else
    instructions.setProperty("Bundle-Activator", OSGiTestActivator.class.getName());
    instructions.setProperty("Private-Package", OSGiTestActivator.class.getPackage().getName());
    buildBundle("osgitests", instructions, BUILD_TEST_DIR);
    instructions.clear();
  }

  // build an OSGi bundle at runtime
  private static void buildBundle(String name, Properties instructions, String classpath)
      throws IOException {

    // write BND instructions to temporary test directory
    String bndFileName = BUILD_TEST_DIR + '/' + name + ".bnd";
    OutputStream os = new BufferedOutputStream(new FileOutputStream(bndFileName));
    instructions.store(os, "BND instructions");
    os.close();

    // assemble bundle, use -failok switch to avoid early exit
    bnd.main(new String[]{"-failok", "build", "-classpath", classpath, bndFileName});
  }

  public void testGuiceWorksInOSGiContainer()
      throws Throwable {

    // ask framework to clear cache on startup
    Properties properties = new Properties();
    properties.setProperty("org.osgi.framework.storage", BUILD_TEST_DIR + "/bundle.cache");
    properties.setProperty("org.osgi.framework.storage.clean", "onFirstInit");

    // test each available OSGi framework in turn
    Iterator<FrameworkFactory> f = ServiceRegistry.lookupProviders(FrameworkFactory.class);
    while (f.hasNext()) {
      Framework framework = f.next().newFramework(properties);

      framework.start();
      BundleContext systemContext = framework.getBundleContext();

      // load all the necessary bundles and start the OSGi test bundle
      systemContext.installBundle("reference:file:" + BUILD_TEST_DIR + "/aopalliance.jar");
      systemContext.installBundle("reference:file:" + BUILD_TEST_DIR + "/javax.inject.jar");
      systemContext.installBundle("reference:file:" + GUICE_JAR);
      systemContext.installBundle("reference:file:" + BUILD_TEST_DIR + "/osgitests.jar").start();

      framework.stop();
    }
  }
}