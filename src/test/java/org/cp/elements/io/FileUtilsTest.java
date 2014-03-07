/*
 * Copyright (c) 2011-Present. Codeprimate, LLC and authors.  All Rights Reserved.
 * <p/>
 * This software is licensed under the Codeprimate End User License Agreement (EULA).
 * This software is proprietary and confidential in addition to an intellectual asset
 * of the aforementioned authors.
 * <p/>
 * By using the software, the end-user implicitly consents to and agrees to be in compliance
 * with all terms and conditions of the EULA.  Failure to comply with the EULA will result in
 * the maximum penalties permissible by law.
 * <p/>
 * In short, this software may not be reverse engineered, reproduced, copied, modified
 * or distributed without prior authorization of the aforementioned authors, permissible
 * and expressed only in writing.  The authors grant the end-user non-exclusive, non-negotiable
 * and non-transferable use of the software "as is" without expressed or implied WARRANTIES,
 * EXTENSIONS or CONDITIONS of any kind.
 * <p/>
 * For further information on the software license, the end user is encouraged to read
 * the EULA @ ...
 */

package org.cp.elements.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.cp.elements.test.CommonBaseTestSuite;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * The FileUtilsTest class is a test suite of test cases testing the contract and functionality of the FileUtils class.
 * <p/>
 * @author John J. Blum
 * @see org.cp.elements.io.FileUtils
 * @see org.cp.elements.test.CommonBaseTestSuite
 * @see org.junit.Assert
 * @see org.junit.Test
 */
public class FileUtilsTest extends CommonBaseTestSuite {

  @Test
  public void testGetNameWithoutExtension() {
    File file = new File("/absolute/path/to/myFile.ext");
    String filename = FileUtils.getNameWithoutExtension(file);

    assertEquals("myFile", filename);

    file = new File("FileUtilsTest.java");
    filename = FileUtils.getNameWithoutExtension(file);

    assertEquals("FileUtilsTest", filename);

    file = new File("relative/path/to/someFile");
    filename = FileUtils.getNameWithoutExtension(file);

    assertEquals("someFile", filename);

    file = new File("FileUtils");
    filename = FileUtils.getNameWithoutExtension(file);

    assertEquals("FileUtils", filename);
  }

  @Test(expected = NullPointerException.class)
  public void testGetNameWithoutExtensionUsingNull() {
    try {
      FileUtils.getNameWithoutExtension(null);
    }
    catch (NullPointerException expected) {
      assertEquals("The File to get the name of without extension cannot be null!", expected.getMessage());
      throw expected;
    }
  }

  @Test
  public void testIsDirectory() {
    assertTrue(FileUtils.isDirectory(new File(System.getProperty("java.io.tmpdir"))));
    assertTrue(FileUtils.isDirectory(new File(System.getProperty("user.home"))));
    assertTrue(FileUtils.isDirectory(new File(System.getProperty("user.dir"))));
    assertFalse(FileUtils.isDirectory(new File(System.getProperty("user.dir"), "non_existing_directory/")));
    assertFalse(FileUtils.isDirectory(new File(System.getProperty("user.dir"), "cp-elements-1.0.0.SNAPSHOT.jar")));
    assertFalse(FileUtils.isDirectory(new File(System.getProperty("user.dir"), "non_existing_file.ext")));
    assertFalse(FileUtils.isDirectory(null));
  }

  @Test
  public void testIsExisting() {
    assertTrue(FileUtils.isExisting(new File(System.getProperty("user.home"))));
    assertTrue(FileUtils.isExisting(new File(getBuildOutputDirectoryAsWorkingDirectory(),
      "classes/org/cp/elements/io/FileUtils.class")));
    assertFalse(FileUtils.isExisting(new File("/path/to/non_existing/pathname")));
    assertFalse(FileUtils.isExisting(null));
  }

  @Test
  public void testIsFile() {
    assertTrue(FileUtils.isFile(new File(getBuildOutputDirectoryAsWorkingDirectory(),
      "classes/org/cp/elements/io/FileUtils.class")));
    assertFalse(FileUtils.isFile(new File(System.getProperty("user.dir"))));
    assertFalse(FileUtils.isFile(new File("/path/to/non_existing/file.ext")));
    assertFalse(FileUtils.isFile(new File("/path/to/non_existing/directory/")));
  }

  @Test
  public void testTryGetCanonicalFileElseGetAbsoluteFile() throws Exception {
    final File mockFile = EasyMock.createMock(File.class);

    EasyMock.expect(mockFile.getCanonicalFile()).andReturn(mockFile);
    EasyMock.expectLastCall().once();
    EasyMock.replay(mockFile);

    assertSame(mockFile, FileUtils.tryGetCanonicalFileElseGetAbsoluteFile(mockFile));

    EasyMock.verify(mockFile);
  }

  @Test
  public void testTryGetCanonicalFileElseGetAbsoluteFileWhenGetCanonicalFileThrowIOException() throws Exception {
    final File expectedMockAbsoluteFile = EasyMock.createMock(File.class);
    final File mockFile = EasyMock.createMock(File.class);

    EasyMock.expect(mockFile.getCanonicalFile()).andThrow(new IOException("io error!"));
    EasyMock.expect(mockFile.getAbsoluteFile()).andReturn(expectedMockAbsoluteFile);
    EasyMock.expectLastCall().once();
    EasyMock.replay(mockFile);

    assertSame(expectedMockAbsoluteFile, FileUtils.tryGetCanonicalFileElseGetAbsoluteFile(mockFile));

    EasyMock.verify(mockFile);
  }

}
