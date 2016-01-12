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

package org.cp.elements.io;

import java.io.File;
import java.io.FileFilter;

import org.cp.elements.lang.Filter;
import org.cp.elements.lang.concurrent.ThreadSafe;

/**
 * The RejectingAllFilesFilter class is a {@link FileFilter} and {@link Filter} implementation that rejects all files.
 *
 * @author John J. Blum
 * @see java.io.File
 * @see java.io.FileFilter
 * @see org.cp.elements.lang.Filter
 * @since 1.0.0
 */
@ThreadSafe
@SuppressWarnings("unused")
public class RejectingAllFilesFilter implements FileFilter, Filter<File> {

  public static final RejectingAllFilesFilter INSTANCE = new RejectingAllFilesFilter();

  /**
   * Rejects all files regardless of their stature.
   *
   * @param pathname the {@link File} to evaluate.
   * @return false rejecting all files.
   * @see org.cp.elements.lang.Filter#accept(Object)
   * @see java.io.FileFilter#accept(File)
   * @see java.io.File
   */
  @Override
  public boolean accept(final File pathname) {
    return false;
  }

}
