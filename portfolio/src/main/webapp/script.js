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

async function getComments() {
  const response = await fetch('/data');
  const comments = await response.json();

  console.log(comments);

  // get each comment as its own list element
  const allComments = document.getElementById('forum-container');
  comments.forEach((comment) => {
    allComments.appendChild(createListElement(comment))
  });
}

/* separates each term in arraylist onto separate line */
function createListElement(text) {
  const liElem = document.createElement('li');
  liElem.innerText = text;
  return liElem;
}