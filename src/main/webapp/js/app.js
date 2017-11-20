$(document).ready(function() {
	
	var url = window.location.href + "get";
	
	var clusterize = new Clusterize({
		scrollId: "scrollArea",
		contentId: "contentArea",
		no_data_text: ""
	});
	
	$("#autocomplete").keyup(function(event) {
		
		if (this.value.length === 0) {
			clusterize.clear();
	        return;
	    }
		
		$.ajax({
            url: url,
            dataType: "json",
            data: {
              term: $(this).val()
            },
            success: function (data) {
        			var results = []
            		for (i=0; i< data.length; i++) {
            			results[i] = "<li>" + data[i].label + "</li>";
            		}
            		clusterize.update(results);
            },
            error: function (xhr, textStatus, errorThrown) {
                console.log(xhr.status);
                console.log(errorThrown);
            }
        });
	});
	
});
