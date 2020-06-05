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
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Task;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.FetchOptions;

/** handles comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
  // stores both the name and the comment
  private List<Pair<String, String>> commentData; 
  private int maxComments;

  @Override
  public void init() {
    commentData = new ArrayList<Pair<String, String>>();
    maxComments = 5; // arbitrary default value
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    /*
    List<Task> tasks = new ArrayList<Task>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name") + ": ";
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");

      Task task = new Task(id, name, comment, timestamp);
      tasks.add(task);
    } */

    List<Entity> resultsList = results.asList(FetchOptions.Builder.withLimit(maxComments));

    List<Task> tasks = new ArrayList<Task>();
    for (Entity entity : resultsList) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name") + ": ";
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");

      Task task = new Task(id, name, comment, timestamp);
      tasks.add(task);
    } 

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(tasks));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // get user comment
    String comment = request.getParameter("text-input");
    String name = request.getParameter("name");
    long timestamp = System.currentTimeMillis();
    
    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("name", name);
    taskEntity.setProperty("comment", comment);
    taskEntity.setProperty("timestamp", timestamp);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);
    
    response.sendRedirect("/forum.html"); // send back to forum page
  }
}
