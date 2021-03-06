/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cp.elements.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.Optional;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NullSafe;

/**
 * The IOUtils class provides basic input and output utility operations.
 *
 * @author John J. Blum
 * @see java.io.ByteArrayInputStream
 * @see java.io.ByteArrayOutputStream
 * @see java.io.Closeable
 * @see java.io.InputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutputStream
 * @see java.io.OutputStream
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class IOUtils {

  protected static final int DEFAULT_BUFFER_SIZE = 32768;

  /**
   * Attempts to close the {@link Closeable} object, ignoring any {@link IOException} that may occur as a result
   * of the close operation.
   *
   * @param obj the {@link Closeable} object who's {@code close} method will be called.
   * @return a boolean value indicating if the close operation was successful or not.
   * @see java.io.Closeable
   */
  @NullSafe
  public static boolean close(Closeable obj) {

    if (obj != null) {
      try {
        obj.close();
        return true;
      }
      catch (IOException ignore) {
      }
    }

    return false;
  }

  /**
   * Copies the contents of the source {@link InputStream} to the target {@link OutputStream}.
   *
   * @param in the source {@link InputStream} to copy bytes from.
   * @param out the target {@link OutputStream} to copy bytes to.
   * @throws IOException if the copy operation results in an I/O error.
   * @see java.io.InputStream
   * @see java.io.OutputStream
   */
  public static void copy(InputStream in, OutputStream out) throws IOException {

    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

    for (int length = in.read(buffer); length > 0; length = in.read(buffer)) {
      out.write(buffer, 0, length);
      out.flush();
    }
  }

  /**
   * Performs a Input/Output (I/O) operation in a safe manner, handling any {@link IOException} that may be thrown
   * while performing the IO.
   *
   * @param operation I/O operation to perform.
   * @return a boolean value indicating whether the I/O operation was successful or not.
   * @see org.cp.elements.io.IOUtils.IoExceptionThrowingOperation
   */
  public static boolean doSafeIo(IoExceptionThrowingOperation operation) {

    try {
      operation.doIo();
      return true;
    }
    catch (IOException ignore) {
      return false;
    }
  }

  /**
   * Deserializes the given byte array back into an {@link Object} of the desired {@link Class} type.
   *
   * @param <T> class type of the object deserialized from the given bytes.
   * @param serializedObjectBytes an array containing the bytes of a serialized object.
   * @return a {@link java.io.Serializable} object from the array of bytes.
   * @throws ClassNotFoundException if the class type of the serialized object cannot be resolved.
   * @throws IOException if an I/O error occurs during the deserialization process.
   * @throws NullPointerException if the serialized object byte array is null.
   * @see #deserialize(byte[], ClassLoader)
   * @see #serialize(Object)
   * @see java.io.ByteArrayInputStream
   * @see java.io.ObjectInputStream
   * @see java.io.Serializable
   */
  @SuppressWarnings("unchecked")
  public static <T> T deserialize(byte[] serializedObjectBytes) throws ClassNotFoundException, IOException {

    ObjectInputStream in = null;

    try {
      in = new ObjectInputStream(new ByteArrayInputStream(serializedObjectBytes));
      return (T) in.readObject();
    }
    finally {
      close(in);
    }
  }

  /**
   * Deserializes the given byte array back into an {@link Object} of a desired {@link Class} type that is resolvable
   * with the given {@link ClassLoader}.
   *
   * @param <T> class type of the object deserialized from the given bytes.
   * @param serializedObjectBytes an array containing the bytes of a serialized object.
   * @param classLoader the Java {@link ClassLoader} used to resolve the {@link Class} type
   * of the serialized {@link Object}.
   * @return a {@link java.io.Serializable} object from the array of bytes.
   * @throws ClassNotFoundException if the class type of the serialized object cannot be resolved
   * by the specified Java {@link ClassLoader}.
   * @throws IOException if an I/O error occurs while deserializing the object from the array of bytes.
   * @throws NullPointerException if the serialized object byte array is null.
   * @see #deserialize(byte[])
   * @see #serialize(Object)
   * @see IOUtils.ClassLoaderObjectInputStream
   * @see java.lang.ClassLoader
   * @see java.io.ByteArrayInputStream
   * @see java.io.ObjectInputStream
   * @see java.io.Serializable
   */
  @SuppressWarnings("unchecked")
  public static <T> T deserialize(byte[] serializedObjectBytes, ClassLoader classLoader)
      throws ClassNotFoundException, IOException {

    ObjectInputStream in = null;

    try {
      in = new ClassLoaderObjectInputStream(new ByteArrayInputStream(serializedObjectBytes), classLoader);
      return (T) in.readObject();
    }
    finally {
      close(in);
    }
  }

  /**
   * Serializes the given {@link java.io.Serializable} object into an array fo bytes.
   *
   * @param obj the {@link java.io.Serializable} object to serialize into an array of bytes.
   * @return the byte array of the serialized object.
   * @throws IOException if an I/O error occurs during the serialization process.
   * @see #deserialize(byte[])
   * @see java.io.ByteArrayOutputStream
   * @see java.io.ObjectOutputStream
   * @see java.io.Serializable
   */
  public static byte[] serialize(Object obj) throws IOException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    ObjectOutputStream objOut = null;

    try {
      objOut = new ObjectOutputStream(out);
      objOut.writeObject(obj);
      objOut.flush();

      return out.toByteArray();
    }
    finally {
      close(objOut);
    }
  }

  /**
   * Reads the contents of the specified {@link InputStream} into a byte array.
   *
   * @param in the {@link InputStream} to read content from.
   * @return a byte array containing the contents of the given {@link InputStream}.
   * @throws IOException if an I/O error occurs while reading the {@link InputStream}.
   * @throws NullPointerException if the {@link InputStream} source is null.
   * @see java.io.ByteArrayOutputStream
   * @see java.io.InputStream
   */
  @NullSafe
  public static byte[] toByteArray(InputStream in) throws IOException {

    Assert.notNull(in, "The InputStream to read bytes from cannot be null");

    ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());

    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

    int bytesRead;

    while ((bytesRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, bytesRead);
      out.flush();
    }

    return out.toByteArray();
  }

  /**
   * The ClassLoaderObjectInputStream class is a {@link ObjectInputStream} implementation that resolves
   * the {@link Class} type of the {@link Object} being deserialized with the specified Java {@link ClassLoader}.
   *
   * @see java.lang.ClassLoader
   * @see java.lang.Thread#getContextClassLoader()
   * @see java.io.ObjectInputStream
   */
  protected static class ClassLoaderObjectInputStream extends ObjectInputStream {

    private final ClassLoader classLoader;

    protected ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {

      super(in);

      this.classLoader = Optional.ofNullable(classLoader)
        .orElseGet(() -> Thread.currentThread().getContextClassLoader());
    }

    protected ClassLoader getClassLoader() {
      return this.classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass descriptor) throws ClassNotFoundException , IOException {
      return Class.forName(descriptor.getName(), false, getClassLoader());
    }
  }

  @FunctionalInterface
  public interface IoExceptionThrowingOperation {
    void doIo() throws IOException;
  }
}
