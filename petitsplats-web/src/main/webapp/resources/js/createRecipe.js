function onCreateSuccess(data) {
	$('#infos').html('la sauvegarde est ok pour la recette ' + data);
}

var step = "<li>etape <span name='stepId'></span><button name='minus'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";

function onStepAddOrDel(){
	var button = $(this);
	
	var steps = $('#createRecipeForm li');
	
	if (button.attr('name').indexOf('add') >= 0){
		$(step).insertAfter(button.parent('li'));
	}else {
		button.parent('li').remove();
	}

	steps = $('#createRecipeForm li');
	if (steps.size() === 1){
		steps.first().find('button[name="minus"]').css('visibility', 'hidden');
		steps.first().find('input').attr('name', 'step1');
		steps.first().find('span[name="stepId"]').html('1');
		return false;
	}
	
	steps.each(function(index){
		$(this).find('input').attr('name', 'step' + (index +1));
		$(this).find('span[name="stepId"]').html(index +1);
		$(this).find('button[name="minus"]').css('visibility', 'visible');
	});
	
	
	
	return false;
}


function createRecipe() {
	var recipe = {
		'title' : $('#createRecipeForm > input[name="title"]').val(),
		'imageId' : $('#createRecipeForm > input[name="imgId"]').val(),
		'recipeSteps' : [ step1, step2, step3]
	};

	var json = JSON.stringify( recipe );
	//jQuery.post('recipe', JSON.stringify( recipe ), onCreateSuccess, "json");
	
	 $.ajax(
	            {
	              url:"recipe", 
	              type: "POST", 
	              data: JSON.stringify( recipe ), 
	              success: onCreateSuccess, 
	              dataType: "json",
	              contentType: "application/json"
	            } );  
	
	
	return false;
}

$(document).ready(function() {
	$('#createRecipeForm > #submitButton').click(createRecipe);
	$("#steps").on("click", "button", onStepAddOrDel);
	
});