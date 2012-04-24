$(function() {

	var ingredientLi = "<li>ingrédient <span name='ingredientId'></span><button name='minus'>-</button><input type='text' name='ingredient' /><button name='add'>+</button></li>";
	var stepLi = "<li>etape <span name='stepId'></span><button name='minus'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";
	
	// liste des ingrédients pour l autocompletion
	var ingredientsList = [];

	$.when( 
		$.ajax({
			url : "ingredients",
			type : "GET",
			contentType : "application/json"
			}), 
		$.ajax({
			url : "recipe/5",
			type : "GET",
			contentType : "application/json"
		}) 
	).then(function( ingredients, recipe){
		
		ingredientsList = ingredients[0];
		var r = recipe[0];
		$('#title').val(r.title);
		$('#id').html(r.id);
		//liste les ingrédients
		$.each(r.ingredients, function(index, i){
			$('#ingredients').append($(ingredientLi));
			var li = $('#ingredients li').last();
			li.find('span[name="ingredientId"]').html(i.id);
			li.find('input[name="ingredient"]').val(i.label);
		});
		$.each(r.recipeSteps, function(index, s){
			$('#steps').append($(stepLi));
			var li = $('#steps li').last();
			li.find('span[name="stepId"]').html(s.id);
			li.find('input[name="step"]').val(s.label);
		});
	});
	

	// a l init de la page
	$(document).ready(function() {
		/*
		$('#createRecipeForm > #submitButton').click(modifyRecipe);
		$("#steps").on("click", "button", onStepAddOrDel);
		$("#ingredients").on("click", "button", onIngredientAddOrDel);
		*/
	});
});
