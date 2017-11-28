$(document).ready(function() {
	
	var url = window.location.href + "get";
	
	var clusterize = new Clusterize({
		scrollId: "scrollArea",
		contentId: "contentArea",
		no_data_text: ""
	});
	
	$('#contentArea').on('click', 'li', function() {
		var _term = decodeURI(this.innerHTML);
		$("#autocomplete").val(_term);
		$.ajax({
			url: url,
			dataType: "json",
			data: {
				term: _term.toLowerCase()
			},
			success: function (data) {
				var results = [];
				clusterize.update(results);
				$("#autocomplete").focus();
			},
			error: function (xhr, textStatus, errorThrown) {
				console.log(xhr.status);
				console.log(errorThrown);
			}
		});
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
