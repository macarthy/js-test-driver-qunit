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

import com.google.inject.ImplementedBy;

import java.util.Collection;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
@ImplementedBy(DefaultFileFilter.class)
public interface JsTestDriverFileFilter {

  public String filterFile(String content, boolean reload);

  public Collection<String> resolveFilesDeps(String file);
}
