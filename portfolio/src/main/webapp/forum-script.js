function initComments() {
  getLoginStatus();
  userLogin();
  getComments();
}

async function getLoginStatus() {

  const response = await fetch('/login-status');
  const status = await response.json();
 
  // user is logged in, allow them to view comments
  if(status === "true") {
    document.getElementById('comment-container').style.visibility = 'visible';
  }
  else {
    document.getElementById('comment-container').style.visibility = 'hidden';
  }
}

// gets login prompt
async function userLogin() {
  const response = await fetch('login');
  const loginMessage = await response.text();

  document.getElementById('login').innerHTML = loginMessage;
}

async function getComments() {

  const response = await fetch('/data');
  const comments = await response.json();

  // get each comment as its own list element
  const commentList = document.getElementById('forum-container');
  comments.forEach((comment) => {
    commentList.appendChild(createCommentElement(comment))
  });
}

/* creates an element that represents a task */
function createCommentElement(comment) {

  const commentListElement = document.createElement('li');
  commentListElement.className = 'comment';
  
  const nameElement = document.createElement('span');
  nameElement.innerText = comment.name;

  const commentElement = document.createElement('span');
  commentElement.innerText = comment.comment;

  /* delete comment from forum */
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment);
    commentListElement.remove();
  });

  commentListElement.appendChild(nameElement);
  commentListElement.appendChild(commentElement);
  commentListElement.appendChild(deleteButtonElement);

  return commentListElement;
}

function deleteComment(comment) {

  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

/* deletes data from server, then resets the page */
function deleteAllData() {

  fetch('/delete-data', {method: 'POST'}).then(() => {
    getComments();
  });
  var commentList = document.getElementById('forum-container');
  commentList.innerHTML = "";
}

/* make sure all elements of form are filled */
function validate() {

  var name = document.getElementById('usr-name');
  var comment = document.getElementById('usr-comment');

  if (!name.value || !comment.value) {
    window.alert("One or more fields left blank.");
    return false;
  }
  return true;
}