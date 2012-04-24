
$.urlParam = function(name){
	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
	if (!results) { return 0; }
	return results[1] || 0;
};

$(function() {
	var ingredientLi = "<li>ingrédient <span name='ingredientId'></span><button name='minus'>-</button><input type='text' name='ingredient' /><button name='add'>+</button></li>";
	var stepLi = "<li>etape <span name='stepId'></span><button name='minus'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";
	
	// liste des ingrédients pour l autocompletion
	var ingredientsList = [];
	var recipeId = $.urlParam('r');

	$.when( 
		$.ajax({
			url : "ingredients",
			type : "GET",
			contentType : "application/json"
			}), 
		$.ajax({
			url : "recipe/" + recipeId,
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
			li.find('span[name="ingredientId"]').html(index + 1);
			var input = li.find('input[name="ingredient"]');
			input.val(i.label);
			input.attr('data-id', i.id);
			input.autocomplete({
				source : ingredientsList,
				minLength : 2,
				change : function(event, ui) {
					var id = (ui.item && ui.item.id) || '';
					$(this).attr('data-id', id);
				}
			});
		});
		//liste des etapes
		$.each(r.recipeSteps, function(index, s){
			$('#steps').append($(stepLi));
			var li = $('#steps li').last();
			li.find('span[name="stepId"]').html(s.order);
			li.find('input[name="step"]').val(s.label);
			li.find('input[name="step"]').attr('data-id', s.id);
		});
	});
	
	// envoie de la requete pour la modification de la recette
	function modifyRecipe() {

		var steps = new Array();
		$('input[name^="step"]').each(function(index) {
			var step = {
				label : $(this).val(),
				order : (index + 1),
				id: $(this).attr('data-id') || ''
			};
			if (step.label) {
				steps.push(step);
			};
		});

		var ingredients = new Array();
		$('input[name^="ingredient"]').each(function(index) {
			var ingredient = {
				label : $(this).val(),
				id : ($(this).attr('data-id') ? $(this).attr('data-id') : '')
			};
			if (ingredient.label) {
				ingredients.push(ingredient);
			}
			;
		});

		var recipe = {
			'title' : $('#modifyRecipeForm > #title').val(),
			'recipeSteps' : steps,
			'ingredients' : ingredients,
			'id' : $('#id').html()
		};

		$.ajax({
			url : "recipe/" + recipe.id,
			type : "PUT",
			data : JSON.stringify(recipe),
			//success : onCreateSuccess,
			dataType : "json",
			contentType : "application/json"
		}).done(function(data){
			$('#infos').html('la modification est OK - ' + data);
			return false;
		});

		return false;
	};
	
	// a l init de la page
	$(document).ready(function() {
		
		$('#modifyRecipeForm > #submitButton').click(modifyRecipe);
		/*$("#steps").on("click", "button", onStepAddOrDel);
		$("#ingredients").on("click", "button", onIngredientAddOrDel);
		*/
	});
});
