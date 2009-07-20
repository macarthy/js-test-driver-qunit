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

import java.util.Timer;



/**
 * A Simple Manager for the {@link HeartBeat}.
 * @author corysmith@google.com (Cory Smith)
 */
public class SimpleHeartBeatManager implements HeartBeatManager {
  private Timer timer;

  public void startTimer() {
    timer = new Timer(true);
  }

  public void cancelTimer() {
    timer.cancel();
  }

  public void startHeartBeat(String baseUrl, String browserId, String sessionId) {
    timer.schedule(new HeartBeat(
        (baseUrl + "/fileSet?id=" + browserId + "&sessionId=" + sessionId)), 0, 500);
  }
}
