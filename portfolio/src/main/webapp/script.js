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

function getRandomFact() {
  const facts =
      ['I am minoring in Drama.', 'My favorite color is teal.', 'I love crime shows.', 'I love superhero movies.',
      'I am on the bhangra team at Carnegie Mellon University.'];

  // Pick a random greeting.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

// dealing with image gallery
function fn(imgs) {
  var expandImg = document.getElementById("expanded-img");
  var imgText = document.getElementById("img-text");

  expandImg.src = imgs.src;
  imgText.innerHTML = imgs.alt; // backup text

  expandImg.parentElement.style.display = "block";
}

async function getTasks() {
  const response = await fetch('/data');
  const tasks = await response.json();

  // get each comment as its own list element
  const taskList = document.getElementById('forum-container');
  tasks.forEach((task) => {
    taskList.appendChild(createTaskElement(task))
  });
}

/* creates an element that represents a task */
function createTaskElement(task) {

  const taskElement = document.createElement('li');
  taskElement.className = 'task';

  const nameElement = document.createElement('span'); //check this
  nameElement.innerText = task.name;

  const commentElement = document.createElement('span');
  commentElement.innerText = task.comment;

  /* delete comment from forum */
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteTask(task);
    taskElement.remove();
  });

  taskElement.appendChild(nameElement);
  taskElement.appendChild(commentElement);
  taskElement.appendChild(deleteButtonElement);

  return taskElement;
}

function deleteTask(task) {
  const params = new URLSearchParams();
  params.append('id', task.id);
  fetch('/delete-task', {method: 'POST', body: params});
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