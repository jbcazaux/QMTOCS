function onCreateSuccess(data) {
	$('#infos').html('la sauvegarde est ok pour la recette ' + data);
}


var stepLi = "<li>etape <span name='stepId'></span><button name='minus'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";
var ingredientLi = "<li>ingrédient <span name='ingredientId'></span><button name='minus'>-</button><input type='text' name='ingredient' /><button name='add'>+</button></li>";

function onStepAddOrDel(){
	var button = $(this);
	var steps = $('#createRecipeForm #steps li');
	
	if (button.attr('name').indexOf('add') >= 0){
		$(stepLi).insertAfter(button.parent('li'));
	}else {
		button.parent('li').remove();
	}

	steps = $('#createRecipeForm #steps li');
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

function onIngredientAddOrDel(){
	var button = $(this);
	var ingredients = $('#createRecipeForm #ingredients li');
	
	if (button.attr('name').indexOf('add') >= 0){
		$(ingredientLi).insertAfter(button.parent('li'));
	}else {
		button.parent('li').remove();
	}

	ingredients = $('#createRecipeForm #ingredients li');
	if (ingredients.size() === 1){
		ingredients.first().find('button[name="minus"]').css('visibility', 'hidden');
		ingredients.first().find('input').attr('name', 'ingredient1');
		ingredients.first().find('span[name="ingredientId"]').html('1');
		return false;
	}
	
	ingredients.each(function(index){
		$(this).find('input').attr('name', 'ingredient' + (index +1));
		$(this).find('span[name="ingredientId"]').html(index +1);
		$(this).find('button[name="minus"]').css('visibility', 'visible');
	});
	
	
	
	return false;
}



function createRecipe() {
	
	var steps = new Array();
	$('input[name^="step"]').each(function(index){
		var step = {label: $(this).val()};
		if (step.label){
			steps.push(step);
		};
	});
	
	var ingredients = new Array();
	$('input[name^="ingredient"]').each(function(index){
		var ingredient = {name: $(this).val()};
		if (ingredient.name){
			ingredients.push(ingredient);
		};
	});
	
	var recipe = {
		'title' : $('#createRecipeForm > input[name="title"]').val(),
		'imageId' : $('#createRecipeForm > input[name="imgId"]').val(),
		'recipeSteps' : steps,
		'ingredients' : ingredients
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
	$("#ingredients").on("click", "button", onIngredientAddOrDel);
	
});