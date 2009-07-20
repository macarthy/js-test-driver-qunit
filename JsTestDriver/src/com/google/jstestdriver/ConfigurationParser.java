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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.oro.io.GlobFilenameFilter;
import org.apache.oro.text.GlobCompiler;
import org.jvyaml.YAML;

/**
 * TODO: needs to give more feedback when something goes wrong...
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ConfigurationParser {

  private final Set<FileInfo> filesList = new LinkedHashSet<FileInfo>();
  private final File basePath;

  private String server = "";
  private List<Plugin> plugins = new LinkedList<Plugin>();

  private final Set<FileInfo> serveFilesList = new LinkedHashSet<FileInfo>();
  private PathResolver pathResolver = new PathResolver();

  public ConfigurationParser(File basePath) {
    this.basePath = basePath;
  }

  @SuppressWarnings("unchecked")
  public void parse(InputStream inputStream) {
    Map<Object, Object> data = (Map<Object, Object>) YAML.load(new BufferedReader(
        new InputStreamReader(inputStream)));
    Set<FileInfo> resolvedFilesLoad = new LinkedHashSet<FileInfo>();
    Set<FileInfo> resolvedFilesExclude = new LinkedHashSet<FileInfo>();
    Set<FileInfo> resolvedFilesServe = new LinkedHashSet<FileInfo>();

    if (data.containsKey("load")) {
      resolvedFilesLoad.addAll(resolveFiles((List<String>) data.get("load"), false));
    }
    if (data.containsKey("exclude")) {
      resolvedFilesExclude.addAll(resolveFiles((List<String>) data.get("exclude"), false));
    }
    if (data.containsKey("server")) {
      this.server = (String) data.get("server");
    }
    if (data.containsKey("plugin")) {
      for (Map<String, String> value: (List<Map<String, String>>) data.get("plugin")) {
        plugins.add(new Plugin(value.get("name"), value.get("jar"), value.get("module")));
      }
    }
    if (data.containsKey("serve")) {
      Set<FileInfo> resolvedServeFiles = resolveFiles((List<String>) data.get("serve"), true);
      resolvedFilesLoad.addAll(resolvedServeFiles);
      resolvedFilesServe.addAll(resolvedServeFiles);
    }

    filesList.addAll(resolvedFilesLoad);
    filesList.removeAll(resolvedFilesExclude);
    serveFilesList.addAll(resolvedFilesServe);
    serveFilesList.removeAll(resolvedFilesExclude);
  }

  private Set<FileInfo> resolveFiles(List<String> files, boolean serveOnly) {
    if (files != null) {
      Set<FileInfo> resolvedFiles = new LinkedHashSet<FileInfo>();

      for (String f : files) {
        boolean isPatch = f.startsWith("patch");

        if (isPatch) {
          String[] tokens = f.split(" ", 2);

          f = tokens[1].trim();
        }
        if (f.startsWith("http://") || f.startsWith("https://")) {
          resolvedFiles.add(new FileInfo(f, -1, false, false));
        } else {
          File file = basePath != null ? new File(basePath, f) : new File(f);
          File testFile = file.getAbsoluteFile();
          File dir = testFile.getParentFile().getAbsoluteFile();
          final String pattern = file.getName();
          String[] filteredFiles = dir.list(new GlobFilenameFilter(pattern,
              GlobCompiler.DEFAULT_MASK | GlobCompiler.CASE_INSENSITIVE_MASK));

          if (filteredFiles == null) {
            System.err.println("No files to load. The patterns/paths used in the configuration"
                + " file didn't match any file, the files patterns/paths need to be relative to"
                + " the configuration file.");
            System.exit(1);
          }
          Arrays.sort(filteredFiles, String.CASE_INSENSITIVE_ORDER);

          for (String filteredFile : filteredFiles) {
            String resolvedFile = pathResolver.resolvePath(dir.getAbsolutePath().replaceAll("\\\\",
                "/")
                + "/" + filteredFile.replaceAll("\\\\", "/"));
            resolvedFiles.add(new FileInfo(resolvedFile, file.lastModified(), isPatch, serveOnly));
          }
        }
      }
      return resolvedFiles;
    }
    return Collections.emptySet();
  }

  public Set<FileInfo> getFilesList() {
    return filesList;
  }

  public String getServer() {
    return server;
  }

  public List<Plugin> getPlugins() {
    return plugins;
  }

  public Set<FileInfo> getServeFilesList() {
    return serveFilesList;
  }
}
