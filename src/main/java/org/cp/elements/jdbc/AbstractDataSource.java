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
package org.cp.elements.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.cp.elements.lang.Constants;

/**
 * {@link AbstractDataSource} is an abstract based class and implementation of the {@link DataSource} interface
 * enabling easy extension and support for {@link DataSource} operations.
 *
 * @author John J. Blum
 * @see java.sql.Connection
 * @see javax.sql.DataSource
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractDataSource implements DataSource {

  /**
   * @inheritDoc
   */
  @Override
  public Connection getConnection() {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public Connection getConnection(String username, String password) {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public PrintWriter getLogWriter() {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public void setLogWriter(PrintWriter out) {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public int getLoginTimeout() {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public void setLoginTimeout(int seconds) {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  //@Override
  public Logger getParentLogger() {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public boolean isWrapperFor(Class<?> interfaceType) {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }

  /**
   * @inheritDoc
   */
  @Override
  public <T> T unwrap(Class<T> interfaceType) {
    throw new UnsupportedOperationException(Constants.OPERATION_NOT_SUPPORTED);
  }
}
