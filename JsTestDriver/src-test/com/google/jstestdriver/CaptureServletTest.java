/*
 * Copyright 2008 Google Inc.
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

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServletTest extends TestCase {

  public void testRedirectQuirksUrl() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureServlet servlet = new CaptureServlet(new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT));

    assertEquals("/slave/1/RemoteConsoleRunnerquirks.html", servlet.service("Chrome/2.0",
        CaptureServlet.QUIRKS, null));
  }

  public void testRedirectStrictUrl() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureServlet servlet = new CaptureServlet(new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT));

    assertEquals("/slave/1/RemoteConsoleRunnerstrict.html", servlet.service("Chrome/2.0",
        CaptureServlet.STRICT, null));
  }
  
  public void testRedirectStrictUrlWithId() throws Exception {
    String id = "5";
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureServlet servlet = new CaptureServlet(new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT));
    
    assertEquals("/slave/" + id + "/RemoteConsoleRunnerstrict.html", servlet.service("Chrome/2.0",
        CaptureServlet.STRICT, id));
  }
}
