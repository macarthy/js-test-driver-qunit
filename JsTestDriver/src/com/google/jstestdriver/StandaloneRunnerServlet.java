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

import com.google.gson.Gson;
import com.google.jstestdriver.JsonCommand.CommandType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StandaloneRunnerServlet extends HttpServlet  {

  private static final long serialVersionUID = 8525889760512657635L;

  private final Gson gson = new Gson();
  private final BrowserHunter browserHunter;
  private final FilesCache cache;
  private final StandaloneRunnerFilesFilter filter;
  private final SlaveResourceService service;

  public StandaloneRunnerServlet(BrowserHunter browserHunter, FilesCache cache,
      StandaloneRunnerFilesFilter filter, SlaveResourceService service) {
    this.browserHunter = browserHunter;
    this.cache = cache;
    this.filter = filter;
    this.service = service;
  }

  private final static Pattern ID = Pattern.compile("/(\\d+)/.*");

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String path = SlaveResourceServlet.stripId(req.getPathInfo());
    String id = getIdFromUrl(req.getPathInfo());

    if (req.getPathInfo().endsWith("StandaloneRunner.html")) {
      if (browserHunter.isBrowserCaptured(id)) {
        browserHunter.freeBrowser(id);
      }
      service(req.getHeader("User-Agent"), path, id);
    }
    service.serve(path, resp.getOutputStream());
  }

  public static String getIdFromUrl(String pathInfo) {
    Matcher match = ID.matcher(pathInfo);

    if (match.find()) {
      return match.group(1);
    }
    throw new IllegalArgumentException(pathInfo);
  }

  public void service(String userAgent, String path, String id) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    SlaveBrowser slaveBrowser =
        browserHunter.captureBrowser(id, parser.getName(), parser.getVersion(), parser.getOs());
    Set<String> filesToload = filter.filter(path, cache);
    LinkedList<FileSource> filesSources = new LinkedList<FileSource>();

    for (String f : filesToload) {
      filesSources.add(new FileSource("/test/" + f, -1));
    }
    int size = filesSources.size();

    for (int i = 0; i < size; i += CommandTask.CHUNK_SIZE) {
      LinkedList<String> loadFilesParameters = new LinkedList<String>();
      List<FileSource> chunkedFileSources =
          filesSources.subList(i, Math.min(i + CommandTask.CHUNK_SIZE, size));

      loadFilesParameters.add(gson.toJson(chunkedFileSources));
      loadFilesParameters.add("true");
      slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.LOADTEST,
          loadFilesParameters)));
    }
    LinkedList<String> runAllTestsParameters = new LinkedList<String>();

    runAllTestsParameters.add("false");
    runAllTestsParameters.add("true");
    slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.RUNALLTESTS,
        runAllTestsParameters)));
  }
}
