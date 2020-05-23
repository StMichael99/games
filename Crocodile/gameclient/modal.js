// Get the modal
var modal1 = document.getElementById('myModal');

var container1 = document.getElementById('myContainer');

// Get the close button
var btnClose1 = document.getElementById('closeModal');

// Open the modal
openNameInputWindow = function() {
    modal1.className = 'Modal is-visuallyHidden';
    setTimeout(function() {
      container1.className = "MainContainer is-blurred";
      modal1.className = "Modal";
    }, 100);
}

// Close the modal
btnClose1.onclick = function() {
    modal1.className = "Modal is-hidden is-visuallyHidden";
    container1.className = "MainContainer";
    newUser();
}