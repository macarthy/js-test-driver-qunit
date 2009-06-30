/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver;

import com.google.jstestdriver.DryRunAction.DryRunActionCallback;
import com.google.jstestdriver.EvalAction.EvalActionCallback;
import com.google.jstestdriver.ResetAction.ResetActionCallback;

import java.util.concurrent.CountDownLatch;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ResponseStreamFactoryImpl implements ResponseStreamFactory {

  private final CountDownLatch latch;
  private final TestResultPrinter printer;

  public ResponseStreamFactoryImpl(CountDownLatch latch, TestResultPrinter printer) {
    this.latch = latch;
    this.printer = printer;
  }

  public ResponseStreamFactoryImpl(CountDownLatch latch) {
    this(latch, null);
  }

  public ResponseStreamFactoryImpl(TestResultPrinter printer) {
    this(null, printer);
  }

  public ResponseStream getEvalActionResponseStream() {
    return new SynchronousResponseStream(new EvalActionCallback(), latch);
  }

  public ResponseStream getResetActionResponseStream() {
    return new SynchronousResponseStream(new ResetActionCallback(), latch);
  }

  public ResponseStream getRunTestsActionResponseStream() {
    return new RunTestsActionResponseStream(printer);
  }

  public ResponseStream getDryRunActionResponseStream() {
    return new SynchronousResponseStream(new DryRunActionCallback(), latch);
  }
}
