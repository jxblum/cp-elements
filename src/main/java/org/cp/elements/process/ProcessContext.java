/*
 * Copyright 2016 Author or Authors.
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

package org.cp.elements.process;

import static java.lang.ProcessBuilder.Redirect;
import static java.util.Arrays.asList;
import static org.cp.elements.util.ArrayUtils.nullSafeArray;
import static org.cp.elements.util.CollectionUtils.nullSafeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cp.elements.io.FileSystemUtils;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.NullSafe;
import org.cp.elements.util.Environment;

/**
 * The {@link ProcessContext} class captures details about the operating environment (context)
 * in which a {@link Process} is running.
 *
 * @author John Blum
 * @see java.io.File
 * @see java.lang.Process
 * @see java.lang.ProcessBuilder.Redirect
 * @see org.cp.elements.util.Environment
 * @since 1.0.0
 */
@SuppressWarnings("unused")
// TODO refactor and split class into process context information and process configuration
public class ProcessContext {

  /**
   * Factory method used to construct an instance of {@link ProcessContext} initialized with
   * the given {@link Process} object.
   *
   * @param process {@link Process} object from which the context is constructed and captured.
   * @return a new instance of {@link ProcessContext} initialized with the given {@link Process}.
   * @throws IllegalArgumentException if {@link Process} is {@literal null}.
   * @see #ProcessContext(Process)
   * @see java.lang.Process
   */
  public static ProcessContext newProcessContext(Process process) {
    return new ProcessContext(process);
  }

  private boolean inheritIO;
  private boolean redirectErrorStream;

  private Environment environment;

  private File directory;

  private List<String> commandLine = Collections.emptyList();

  private final Process process;

  private Redirect error;
  private Redirect input;
  private Redirect output;

  private String username;

  /**
   * Constructs a new instance of {@link ProcessContext} initialized with the given {@link Process} object
   * for which this {@link ProcessContext} is about.
   *
   * @param process {@link Process} for which the context is captured.
   * @throws IllegalArgumentException if {@link Process} is {@literal null}.
   * @see java.lang.Process
   */
  protected ProcessContext(Process process) {
    Assert.notNull(process, "Process cannot be null");
    this.process = process;
  }

  /**
   * Returns the command-line used to execute the {@link Process} wrapped by this context.
   *
   * @return a {@link List} of {@link String Strings} constituting the command used to execute the {@link Process}.
   * @see #ranWith(String...)
   * @see #ranWith(List)
   */
  public List<String> getCommandLine() {
    return Collections.unmodifiableList(this.commandLine);
  }

  /**
   * Returns the file system directory in which the {@link Process} is running.
   *
   * @return a {@link File} object reference to the file system directory in which the {@link Process} is running.
   * @see java.io.File
   * @see #ranIn(File)
   */
  public File getDirectory() {
    return this.directory;
  }

  /**
   * Returns the {@link Environment} variable configuration used when launching the {@link Process}.
   *
   * @return the {@link Environment} variable configuration used when launching the {@link Process}.
   * @see org.cp.elements.util.Environment
   * @see #usingEnvironmentVariables()
   * @see #using(Environment)
   */
  public Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Returns the destination of the {@link Process Process's} standard error stream.
   *
   * @return a {@link Redirect} indicating the destination of the {@link Process Process's} standard error stream.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #redirectError(Redirect)
   */
  public Redirect getError() {
    return this.error;
  }

  /**
   * Returns the source of the {@link Process Process's} standard input stream.
   *
   * @return a {@link Redirect} indicating the source of the {@link Process Process's} standard input stream.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #redirectInput(Redirect)
   */
  public Redirect getInput() {
    return this.input;
  }

  /**
   * Returns the destination of the {@link Process Process's} standard output stream.
   *
   * @return a {@link Redirect} indicating the destination of the {@link Process Process's} standard output stream.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #redirectOutput(Redirect)
   */
  public Redirect getOutput() {
    return this.output;
  }

  /**
   * Returns a reference to the {@link Process} wrapped by this context.
   *
   * @return the {@link Process} object wrapped by this context.
   * @see java.lang.Process
   */
  public Process getProcess() {
    return this.process;
  }

  /**
   * Determines whether the {@link Process Process's} standard error stream is being redirected (merged)
   * into the {@link Process Process's} standard output stream.
   *
   * @return a boolean value indicating whether the {@link Process Process's} standard error stream is being merged
   * into its standard output stream.
   * @see #redirectErrorStream(boolean)
   */
  public boolean isRedirectingErrorStream() {
    return this.redirectErrorStream;
  }

  /**
   * Returns the name of the user used to run the {@link Process}.
   *
   * @return a {@link String} containing name of the user used to run the {@link Process}.
   * @see #ranBy(String)
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Determines whether the {@link Process sub-process} wrapped by this context inherits its IO
   * from its {@link Process parent}.
   *
   * @return a boolean value indicating whether the {@link Process sub-process} inherits its IO
   * from its {@link Process parent}.
   * @see #inheritIO(boolean)
   */
  public boolean inheritsIO() {
    return this.inheritIO;
  }

  /**
   * Sets whether the {@link Process sub-process} wrapped by this context inherits it's IO
   * from it's {@link Process parent}.
   *
   * @param inheritIO boolean value indicating whether the {@link Process sub-process} wrapped by this context
   * inherits IO from it's {@link Process parent}.
   * @return this {@link ProcessContext}.
   * @see #inheritsIO()
   */
  public ProcessContext inheritIO(boolean inheritIO) {
    this.inheritIO = inheritIO;
    return this;
  }

  /**
   * Sets the name of the user used to run the {@link Process}.
   *
   * @param username name of the user used to run the {@link Process}.
   * @return this {@link ProcessContext}.
   * @see #getUsername()
   */
  public ProcessContext ranBy(String username) {
    this.username = username;
    return this;
  }

  /**
   * Sets the file system directory in which the {@link Process} represented by this context is running.
   *
   * @param directory {@link File} object referencing the file system directory in which the {@link Process} is running.
   * @return this {@link ProcessContext}.
   * @throws IllegalArgumentException if {@link File} is not a valid directory
   * @see java.io.File
   */
  public ProcessContext ranIn(File directory) {
    Assert.isTrue(FileSystemUtils.isDirectory(directory), "[%s] must be a valid directory", directory);
    this.directory = directory;
    return this;
  }

  /**
   * Sets the command-line used to execute the {@link Process}.
   *
   * @param commandLine array of elements constituting the command used to executed the {@link Process}.
   * @return this {@link ProcessContext}.
   * @see #ranWith(List)
   * @see #getCommandLine()
   */
  @NullSafe
  public ProcessContext ranWith(String... commandLine) {
    return ranWith(asList(nullSafeArray(commandLine, String.class)));
  }

  /**
   * Sets the command-line used to execute the {@link Process}.
   *
   * @param commandLine {@link List} of elements constituting the command used to execute the {@link Process}.
   * @return this {@link ProcessContext}.
   * @see #getCommandLine()
   */
  @NullSafe
  public ProcessContext ranWith(List<String> commandLine) {
    this.commandLine = new ArrayList<>(nullSafeList(commandLine));
    return this;
  }

  /**
   * Sets the destination of the {@link Process Process's} standard error stream.
   *
   * @param error {@link Redirect} representing the destination of the {@link Process Process's} error stream.
   * @return this {@link ProcessContext}.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #getError()
   */
  public ProcessContext redirectError(Redirect error) {
    this.error = error;
    return this;
  }

  /**
   * Sets whether the {@link Process Process's} standard error stream is being redirected (merged) into
   * the {@link Process Process's} standard output stream.
   *
   * @param redirectErrorStream boolean value to indicate whether to redirect the standard error stream into
   * the standard output stream.
   * @return this {@link ProcessContext}.
   * @see #isRedirectingErrorStream()
   */
  public ProcessContext redirectErrorStream(boolean redirectErrorStream) {
    this.redirectErrorStream = redirectErrorStream;
    return this;
  }

  /**
   * Sets the source of the {@link Process Process's} standard input stream.
   *
   * @param input {@link Redirect} representing the source of the {@link Process Process's} input stream.
   * @return this {@link ProcessContext}.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #getInput()
   */
  public ProcessContext redirectInput(Redirect input) {
    this.input = input;
    return this;
  }

  /**
   * Sets the destination of the {@link Process Process's} standard output stream.
   *
   * @param output {@link Redirect} representing the destination of the {@link Process Process's} output stream.
   * @return this {@link ProcessContext}.
   * @see java.lang.ProcessBuilder.Redirect
   * @see #getOutput()
   */
  public ProcessContext redirectOutput(Redirect output) {
    this.output = output;
    return this;
  }

  /**
   * Sets the given {@link Environment} as the system environment variable configuration used when the {@link Process}
   * was launched.
   *
   * @param environment snapshot of the {@link Environment} configuration used when the {@link Process} was launched.
   * @return this {@link ProcessContext}.
   * @see #usingEnvironmentVariables()
   * @see #getEnvironment()
   */
  public ProcessContext using(Environment environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Sets the current system environment variables as the {@link Environment} used when the {@link Process}
   * was launched.
   *
   * @return this {@link ProcessContext}.
   * @see org.cp.elements.util.Environment#fromEnvironmentVariables()
   * @see #using(Environment)
   * @see #getEnvironment()
   */
  public ProcessContext usingEnvironmentVariables() {
    return using(Environment.fromEnvironmentVariables());
  }
}
