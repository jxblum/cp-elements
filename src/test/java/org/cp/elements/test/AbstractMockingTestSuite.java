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

package org.cp.elements.test;

import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;

/**
 * The AbstractMockingTestSuite class is an abstract base class encapsulating mocking functionality common to all
 * unit tests.
 * <p/>
 * @author John J. Blum
 * @see org.cp.elements.test.CommonBaseTestSuite
 * @see org.jmock.Mockery
 * @see org.jmock.lib.concurrent.Synchroniser
 * @see org.jmock.lib.legacy.ClassImposteriser
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractMockingTestSuite extends CommonBaseTestSuite {

  protected Mockery mockContext;

  @Before
  public void preTestCaseSetup() {
    mockContext = new Mockery();
    mockContext.setImposteriser(ClassImposteriser.INSTANCE);
    mockContext.setThreadingPolicy(new Synchroniser());
  }

  @After
  public void postTestCaseTearDown() {
    mockContext.assertIsSatisfied();
    mockContext = null;
  }

}
