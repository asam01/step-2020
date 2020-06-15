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
      ['I am minoring in Drama.', 'My favorite color is teal.', 
        'I love crime shows.', 'I love superhero movies.',
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

function createMap() {
    
  var googleCoordinates = {lat: 40.741, lng: -74.002};
  var mapProp = {center : {lat: 40.135, lng: -75.244}, zoom: 7};
  const map = new google.maps.Map(document.getElementById('map'), mapProp);

  const googleNYMarker = new google.maps.Marker({
    position: googleCoordinates,
    map: map,
    title: 'Google NYC'
  });
  
  // info box pops up when Google NYC marker is clicked
  var googleInfo = new google.maps.InfoWindow({content: 'Google NYC'});
  googleNYMarker.addListener('click', () => {
    googleInfo.open(map, googleNYMarker);
  });

  const homeMarker = new google.maps.Marker({
    position: {lat: 39.084130, lng: -77.152329},
    map: map,
    title: 'Rockville'
  });

  var homeInfo = new google.maps.InfoWindow({content: 'Rockville'});
  homeMarker.addListener('click', () => {
    homeInfo.open(map, homeMarker);
  });
}