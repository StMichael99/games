// Get the modal
var modal2 = document.getElementById('myModal2');

var container2 = document.getElementById('myContainer');

// Get the close button
var btnClose2 = document.getElementById('closeModal2');

// Open the modal
openNameInputWindow2 = function() {
    modal2.className = 'Modal is-visuallyHidden';
    setTimeout(function() {
      container2.className = "MainContainer is-blurred";
      modal2.className = "Modal";
    }, 100);
}

// Close the modal
btnClose2.onclick = function() {
    modal2.className = "Modal is-hidden is-visuallyHidden";
    container2.className = "MainContainer";
    newRoom();
}