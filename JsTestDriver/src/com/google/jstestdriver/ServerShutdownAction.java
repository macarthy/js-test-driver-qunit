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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ServerShutdownAction implements Action {

  private final ServerStartupAction serverStartupAction;
  private final RunTestsAction runTestsAction;

  public ServerShutdownAction(ServerStartupAction serverStartupAction,
      RunTestsAction runTestsAction) {
    this.serverStartupAction = serverStartupAction;
    this.runTestsAction = runTestsAction;
  }

  public void run() {
    serverStartupAction.getServer().stop();
    System.exit(runTestsAction != null && runTestsAction.failures() ? 1 : 0);
  }
}
