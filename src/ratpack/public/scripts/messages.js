(function () {
    console.log('loading...');
    var url = location.protocol + '//' + location.host + '/api';

    console.log(url);

    $.when($.getJSON(url)).then(function (data) {
        console.log(JSON.stringify(data));
        var messages = data || [];
        bindToMessageBody(messages);
    });
})();

function bindToMessageBody(messages) {
    console.log("messages: " + JSON.stringify(messages));
    var messagesBody = $('#messagesBody');

    $.each(messages, function (index, message) {
        console.log("index: " + index + ", " + "message: " + JSON.stringify(message));
        var tr = $('<tr></tr>');

        var indexTd = $('<td></td>').html(index);
        indexTd.appendTo(tr);

        var createAt = '';
        if (message.createAt) {
            var c = moment(parseInt(message.createAt, 10));
            createAt = c.format('YYYY年MM月DD日 HH:mm:ss');
        }
        var createAtTd = $('<td></td>').html(createAt);
        createAtTd.appendTo(tr);

        var contents = message.contents || '';
        var contentsTd = $('<td></td>').text(contents);
        contentsTd.appendTo(tr);

        var buttonTd = $('<td></td>');

        var id = message.id;

        var deleteButton = $('<button></button>', {
            "type": "button",
            "class": "btn btn-danger",
            "id": id
        }).text('削除');

        deleteButton.on('click', function (event) {
            var buttonId = event.target.id;
            $.ajax({
                url: location.protocol + '//' + location.host + '/messages' + '/' + buttonId,
                "type": "DELETE",
                "dataType": "json",
                "data": {}
            }).then(function (data) {
                console.log("result: " + JSON.stringify(data));
                // success
                location.href = location.protocol + '//' + location.host;
            }, function (data) {
                console.error("fail: " + JSON.stringify(data));
            });
        });
        deleteButton.appendTo(buttonTd);

        buttonTd.appendTo(tr);

        tr.appendTo(messagesBody);
    });

}
