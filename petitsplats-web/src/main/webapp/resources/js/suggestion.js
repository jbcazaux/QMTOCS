
function goToRecipe(id, reverse){
	if ($('#suggestion_' + (id)).length > 0){
		console.log('change page to : suggestion_' + (id));
		$.mobile.changePage($('#suggestion_' + (id)), {reverse: reverse});	
	}
}

$( ".prev" ).live('click', function(e){
	
	var page = $(e.target).parents(":jqmData(role='page')");
	var id = parseInt(page.attr('id').split("_").pop());
	
	goToRecipe(id + 1, true);
	
	return false;
	
});

$( ".next" ).live('click', function(e){
	
	var page = $(e.target).parents(":jqmData(role='page')");
	var id = parseInt(page.attr('id').split("_").pop());
	
	goToRecipe(id - 1, false);
	
	
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
	
	
	if('ontouchstart' in document.documentElement){
		$page.bind("swipeleft", function(){
			goToRecipe(id - 1, false);
		});
		$page.bind("swiperight", function(){
			goToRecipe(id + 1, true);
		});
//		$page.find( ".scrollable" ).first().bind("swipeleft", function(){
//			console.log('swipe left');
//			goToRecipe(id - 1, false);
//		});
//		$page.find( ".scrollable" ).first().bind("swiperight", function(){
//			console.log('swipe right');
//			goToRecipe(id + 1, true);
//		});
	}
});