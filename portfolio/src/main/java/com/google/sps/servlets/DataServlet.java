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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Comment;
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
    
  private int maxComments;
  private static final int DEFAULT_MAX_COMMENTS = 10;

  @Override
  public void init() {
    maxComments = DEFAULT_MAX_COMMENTS;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
   
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query commentQuery = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery commentResults = datastore.prepare(commentQuery);

    List<Entity> commentResultsList = commentResults.asList(FetchOptions.Builder.withLimit(maxComments));
    List<Comment> comments = new ArrayList<Comment>();

    for (Entity commentEntity : commentResultsList) {

      long id = commentEntity.getKey().getId();
      String name = (String) commentEntity.getProperty("name") + ": ";
      String comment = (String) commentEntity.getProperty("comment");
      long timestamp = (long) commentEntity.getProperty("timestamp");
      String email = "(" + (String) commentEntity.getProperty("email") + ")";

      Comment newComment = new Comment(id, name, comment, timestamp, email);
      comments.add(newComment);
    } 

    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity commentEntity = createCommentEntity(request, email);
    datastore.put(commentEntity);
    
    response.sendRedirect("/forum.html"); // send back to forum page
  }

  private Entity createCommentEntity (HttpServletRequest request, String email) {
    
    String comment = request.getParameter("text-input");
    String name = request.getParameter("name");
    long timestamp = System.currentTimeMillis();
    maxComments = getNumComments(request, "num-comments");
    
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("email", email);
    
    return commentEntity;
  }

  private int getNumComments(HttpServletRequest request, String tag) {
    
    String numCommentsString = request.getParameter(tag);

    // if no entry from user, continue using the previous limit
    if (numCommentsString.equals("")) {
      return maxComments;
    }

    int numComments;
    try{
      numComments = Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Invalid input - could not convert " + numCommentsString + " to int.");
      return -1;
    }

    if (numComments < 0) {
      System.err.println("Invalid input - " + numComments + " is out of range.");
      return -1;
    }

    return numComments;
  }
}