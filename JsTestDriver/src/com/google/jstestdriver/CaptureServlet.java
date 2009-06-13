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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServlet extends HttpServlet {

  private final BrowserHunter browserHunter;

  public CaptureServlet(BrowserHunter browserHunter) {
    this.browserHunter = browserHunter;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.sendRedirect(service(req.getHeader("User-Agent")));
  }

  public String service(String userAgent) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    SlaveBrowser slaveBrowser =
      browserHunter.captureBrowser(parser.getName(), parser.getVersion(), parser.getOs());

    return browserHunter.getCaptureUrl(slaveBrowser.getId());
  }
}
