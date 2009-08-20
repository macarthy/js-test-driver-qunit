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
package com.google.jstestdriver.coverage;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represent a the coverage of a file.
 * @author corysmith@google.com (Cory Smith)
 */
public class FileCoverage implements Comparable<FileCoverage>{

  private String qualifiedFile;
  private List<CoveredLine> lines = Lists.newLinkedList();

  public FileCoverage() {
  }

  public FileCoverage(String qualifiedFile, List<CoveredLine> lines) {
    this.qualifiedFile = qualifiedFile;
    this.lines = lines;
  }
  
  public FileCoverage aggegrate(FileCoverage other) {
    if (qualifiedFile.equals(other.qualifiedFile)) {
      List<CoveredLine> rawLines = Lists.newLinkedList();
      rawLines.addAll(lines);
      rawLines.addAll(other.lines);
      Collections.sort(rawLines);
      List<CoveredLine> newLines = Lists.newLinkedList();
      if (!rawLines.isEmpty()) {
        CoveredLine current = rawLines.remove(0);
        for (CoveredLine line : rawLines) {
          CoveredLine aggregate = current.aggegrate(line);
          if (aggregate == null) {
            newLines.add(current);
            current = line;
          } else {
            current = aggregate;
          }
        }
        newLines.add(current);
      }
      return new FileCoverage(qualifiedFile, newLines); 
    }
    return null;
  }

  public void write(CoverageWriter coverageWriter) {
    coverageWriter.writeRecordStart(qualifiedFile);
    for (CoveredLine line : lines) {
      line.write(coverageWriter);
    }
    coverageWriter.writeRecordEnd();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lines == null) ? 0 : lines.hashCode());
    result = prime * result
      + ((qualifiedFile == null) ? 0 : qualifiedFile.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FileCoverage other = (FileCoverage) obj;
    if (lines == null) {
      if (other.lines != null)
        return false;
    } else if (!lines.equals(other.lines))
      return false;
    if (qualifiedFile == null) {
      if (other.qualifiedFile != null)
        return false;
    } else if (!qualifiedFile.equals(other.qualifiedFile))
      return false;
    return true;
  }

  public int compareTo(FileCoverage o) {
    return qualifiedFile.compareTo(o.qualifiedFile);
  }
  
  @Override
  public String toString() {
    return String.format("%s(%s, %s)", getClass().getSimpleName(), qualifiedFile, lines);
  }
}
