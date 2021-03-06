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
public class FileSource {

  private String fileSrc;
  private String basePath;
  private long timestamp;

  public FileSource() {
  }

  public FileSource(String fileSrc, long timestamp) {
    this.fileSrc = fileSrc;
    this.timestamp = timestamp;
  }

  public String getFileSrc() {
    return fileSrc;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getBasePath() {
    if (basePath == null) {
      if (fileSrc.startsWith("/test/")) {
        basePath = fileSrc.substring(6);
      } else {
        basePath = fileSrc;
      }
    }
    return basePath;
  }

  public void setFileSource(String fileSrc) {
    this.fileSrc = fileSrc;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
  
  @Override
  public String toString() {
    return String.format("%s(%s, %s, %s)", getClass().getSimpleName(), fileSrc, basePath, timestamp);
  }
}
