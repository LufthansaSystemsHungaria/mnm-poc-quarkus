var eventSource = new EventSource("/api/mm/barcode/streaming");

eventSource.onmessage = function (event) {
  var container = document.getElementById("container");
  var paragraph = document.createElement("p");

  var data = JSON.parse(event.data);

  if (data && data.barcodeData) {
    paragraph.innerHTML = "<hr><b>" + data.info + "</b><br>"
            + "<img src='data:" + data.mediaType+ ";base64," + data.barcodeData + "'/>";
    container.insertBefore(paragraph, container.firstChild);
    Toastify({
            text: "Barcode found",
            backgroundColor: "green"
        }).showToast();
  } else {
    paragraph.innerHTML = "<hr><b>" + data.info + "</b><br>";
    container.insertBefore(paragraph, container.firstChild);
    Toastify({
        text: "Barcode not found",
        backgroundColor: "orange"
    }).showToast();
  }
};

function doSubmit() {
  var files = $('#file')[0].files[0];
  if (files) {
      var fd = new FormData();
      fd.append('file',files);

      $.ajax({
        url: '/api/mm/barcode',
        data: fd,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',
        success: function (data) {

        }
      });

      $('#file').val("");
  } else {
    Toastify({
      text: "Please choose an image",
      backgroundColor: "red"
    }).showToast();
  }
}