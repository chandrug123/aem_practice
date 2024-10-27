loadImage();

function loadImage() {
    const test = document.getElementById("testqq").innerHTML;
    console.log(test);
    $.ajax({
        url: "/bin/pageviews", // Path to the AEM Servlet
        type: "GET",
        data: {
            path: test // Example path to be sent as a parameter
        },
        success: function(response) {
            console.log(response); // This will log the response from the servlet
            // Do something with the response, e.g., display it in the UI
            $('#responseDiv').html(response.message);
            const test = document.getElementById("responseDiv").innerHTML = response;
        },
        error: function(xhr, status, error) {
            console.log("Error: " + error);
        }
    });

}