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

import com.google.sps.data.Pair;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/** handles comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  // stores both the name and the comment
  private List<Pair<String, String>> commentData; 

  @Override
  public void init() {
    commentData = new ArrayList<Pair<String, String>>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setContentType("application/json");

    List<String> allData = new ArrayList<String>();

    for (Pair p : commentData) {
      allData.add(p.toString());
    }

    String json = new Gson().toJson(allData);
    response.getWriter().println(json); 
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // get user comment
    String comment = request.getParameter("text-input");
    String name = request.getParameter("name");

    commentData.add(new Pair<String, String>(name, comment));
    response.sendRedirect("/forum.html"); // send back to forum page
  }
}
