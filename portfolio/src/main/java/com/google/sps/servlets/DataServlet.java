// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import com.google.gson.Gson;

/** handles comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  private List<String> comments;
  private List<String> names;

  @Override
  public void init() {
    comments = new ArrayList<String>();
    names = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    List<String> namesAndComments = new ArrayList<String>();

    assert names.size() == comments.size() : "error";
    for(int index=0; index<names.size(); index++) {
      String name = names.get(index);
      String comment = comments.get(index);

      namesAndComments.add(name + ": " + comment);
    }

    // convert arraylist into json
    //String json = new Gson().toJson(comments);

    String json = new Gson().toJson(namesAndComments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // get user comment
    String comment = request.getParameter("text-input");
    String name = getNameParameter(request, "name"); // blank name

    comments.add(comment);
    names.add(name);
    response.sendRedirect("/forum.html"); // send back to forum page
  }

  /* fill in No-Name if comment is entered without name  */
  public String getNameParameter(HttpServletRequest request, String name) {
    String val = request.getParameter(name);
    if (val == null)
      return "No-Name";
    return val;
  }
}
