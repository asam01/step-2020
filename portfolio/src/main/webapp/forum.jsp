<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
   String uploadUrl = blobstoreService.createUploadUrl("/form-handler"); %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Forum</title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="forum-style.css">
    <script src="script.js"></script>
  </head>

  <body onload="getComments()">

    <div class="nav-links">
      <a href="/index.html"><b>Home</b></a>
      <a href="/about.html"><b>About</b></a>
      <a href="/contact.html"><b>Contact</b></a>
      <a href="/gallery.html"><b>Gallery</b></a>
      <a href="/forum.html"><b>Forum</b></a>
    </div>

    <div id="content">
      <h1>Forum</h1>

      <form method="POST" onsubmit="return validate()"
        enctype="multipart/form-data" action="<%= uploadUrl %>">

        <!-- name collection -->
        <label class="center">Name:</label><br>
        <input type="text" name="name" id="usr-name" placeholder="My name." class="center">
        <br><br>

        <label class="center">Enter a comment:</label><br>
        <textarea name="text-input" id="usr-comment" placeholder="My comment." class="center"></textarea>
        <br><br>

        <label class="center">Number of comments displayed:</label><br>
        <input type="number" class="center" id="max-comments" name="num-comments" 
          min="0" placeholder="10"><br>

        <label class="center">Upload an Image:</label><br>
        <input type="file" class="center" id="img-upload" name="image">
        <br><br>

        <input type="submit" class="center" /><br><br>
      </form>

      <button onclick="deleteAllData()" id="delete-all" class="center">Delete All Comments</button>

      <!-- print out comments in list format -->
      <ul class="center" id="forum-container"></ul>
    </div>

  </body>
</html>