var eventSource = new EventSource("/api/mm/qr/streaming");

eventSource.onmessage = function (event) {
  var container = document.getElementById("container");
  var paragraph = document.createElement("p");

  var data = JSON.parse(event.data);

  paragraph.innerHTML = "<hr><b>" + data.info + "</b><br>"
          + "<img src='data:" + data.mediaType+ ";base64," + data.qrData + "'/>";
  container.appendChild(paragraph);
};

function doSubmit() {
  var fd = new FormData();
  var files = $('#file')[0].files[0];
  fd.append('file',files);
   
  $.ajax({
    url: '/api/mm/qr',
    data: fd,
    cache: false,
    contentType: false,
    processData: false,
    type: 'POST',
    success: function (data) {
      
    }
  });
  
  $('#file').val("");
}