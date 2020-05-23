var user_name;

var connection = new WebSocket('ws://192.168.0.122:8189');
connection.onopen = function () {
	console.log('Connected!');
	connection.send("set_room_name:lobby");
};

connection.onload = function () {
	connection.send('get');
	connection.send("set_room_name:lobby");
}

connection.onmessage = function (event) {
	console.log('received message:');
	console.log(event.data);
	
	if (event.data === 'REGISTER') {
		openNameInputWindow();
	} else if (event.data.includes('users_list:')) {
		updateUserList(event.data.replace("users_list:", "").split(","));
	} else if (event.data.includes('rooms_list:')) {
		updateRoomList(event.data.replace("rooms_list:", "").split(","));
	}

}

function newRoom() {
	room_name = document.getElementById('name_input2').value;
	connection.send('room:' + room_name);
	//window.location.href = "drawing.html?room="+room_name;
}

function newUser() {
	user_name = document.getElementById('name_input').value;
	connection.send('user:' + user_name);
}

function updateUserList(users) {
	var user_list_html = '';
	
	users.forEach (function(element) {
		user_list_html = '<div id="message">' + element + '</div>' + user_list_html;

	});

	document.getElementById('messages_container').innerHTML = user_list_html; 
}

function updateRoomList(rooms) {
	var room_list_html = '';
	rooms.forEach (function(element) {
		if (element == 'lobby') return;
		room_list_html = '<a href="drawing.html?room=' + element + '" onclick="enterRoom(\'' + element + '\')"><div style=\'width:100px;height:100px;background-color:yellow;border:1px solid black;\'>' 
												+ element + '</div></a>' + room_list_html;
	});

	document.getElementById('rooms_list').innerHTML = room_list_html; 	
}

function enterRoom(element) {
	alert(element);
	connection.send('user_connect:' + element);
}

function sendMessage(element){
	
	if (event.key === 'Enter') {
		connection.send(element.value);
		element.value = '';
	}
}