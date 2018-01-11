let targetSelect

$(document).ready(function() {

    $('#compile').click(function(event) {
       event.preventDefault();

     var data = {
     'content':sourceEditor.getSession().getValue(),
     'target':targetSelect.val()
     }


       $.ajax({
        url: '/api/convert/request',
        type: 'POST',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType:'application/json',
        success: function(data) {
            console.log("received")
            console.log(data.content)

            resultEditor.getSession().setValue(data.content);
        },
        error: function() {
            $('#notification-bar').text('An error occurred');
        }
       });
    });

})

$(document).ready(function(){

    $.ajax({
        url: '/api/convert/targets',
        type: 'GET',
        dataType:'json',
        contentType:'application/json',
        success: function(data){
            console.log("targets received")
            console.log(data)

            targetSelect = $("#target-type");
            targetSelect.empty(); // remove old options
            $.each(data, function(key,value) {
              targetSelect.append($("<option></option>")
                 .attr("value", value.key).text(value.name));
            });

        },
        error: function(){
                $('#notification-bar').text('An error occurred');
        }
    })


})