$( ".prev" ).live('click', function(e){
	
	var page = $(e.target).parents(":jqmData(role='page')");
	var id = parseInt(page.attr('id').split("_").pop());
	
	if ($('#suggestion_' + (id+1)).length > 0){
		console.log('change page to : suggestion_' + (id+1));
		$.mobile.changePage($('#suggestion_' + (id+1)), {reverse: true});	
	}
	
	return false;
	
});

$( ".next" ).live('click', function(e){
	
	var page = $(e.target).parents(":jqmData(role='page')");
	var id = parseInt(page.attr('id').split("_").pop());
	
	if ($('#suggestion_' + (id-1)).length > 0){
		console.log('change page to : suggestion_' + (id-1));
		$.mobile.changePage($('#suggestion_' + (id-1)));	
	}
	
	
	return false;
	
});


function retrievePage(id){
	
	if (isNaN(id)) return;
	
	console.log('fetching: remote/' + id + '.json');

	$.when(getRecipeData(id)).then(function(data){
		createPage(data);
	});
};

$( ":jqmData(role='page')" ).live( "pageshow", function(event) {
	var $page = $( this );
	var id = $page.attr('id').split("_").pop();
	id = parseInt(id);
	if ($('body').find('#suggestion_' + (id+1) ).length == 0){
		retrievePage(id + 1);
	}
	if (id > 1 && $('body').find('#suggestion_' + (id-1) ).length == 0){
		retrievePage(id - 1);
	}
});