
$.urlParam = function(name){
	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
	if (!results) { return 0; }
	return results[1] || 0;
};

$(function() {
	var ingredientLi = "<li>ingrédient <span name='ingredientId'></span><button name='minus' style='visibility: hidden'>-</button><input type='text' name='ingredient' /><button name='add'>+</button></li>";
	var stepLi = "<li>etape <span name='stepId'></span><button name='minus' style='visibility: hidden'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";
	
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
			if (r.ingredients.length > 1){
				li.find('button[name="minus"]').css('visibility', 'visible');
			}
		});
		//liste des etapes
		$.each(r.recipeSteps, function(index, s){
			$('#steps').append($(stepLi));
			var li = $('#steps li').last();
			li.find('span[name="stepId"]').html(s.order);
			li.find('input[name="step"]').val(s.label);
			li.find('input[name="step"]').attr('data-id', s.id);
			if (r.recipeSteps.length > 1){
				li.find('button[name="minus"]').css('visibility', 'visible');
			}
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
			$('#infos').html('la modification est OK - ');
			return false;
		});

		return false;
	};
	
	// ajout ou suppression d une etape
	function onStepAddOrDel() {
		var button = $(this);
		var steps = $('#modifyRecipeForm #steps li');

		if (button.attr('name').indexOf('add') >= 0) {
			$(stepLi).insertAfter(button.parent('li'));
		} else {
			button.parent('li').remove();
		}

		steps = $('#modifyRecipeForm #steps li');
		if (steps.size() === 1) {
			steps.first().find('button[name="minus"]').css('visibility',
					'hidden');
			steps.first().find('input').attr('name', 'step1');
			steps.first().find('span[name="stepId"]').html('1');
			return false;
		}

		steps.each(function(index) {
			$(this).find('input').attr('name', 'step' + (index + 1));
			$(this).find('span[name="stepId"]').html(index + 1);
			$(this).find('button[name="minus"]').css('visibility', 'visible');
		});

		return false;
	}
	
	// ajout ou suppression d un ingredient
	function onIngredientAddOrDel() {
		var button = $(this);
		var ingredients = $('#modifyRecipeForm #ingredients li');

		if (button.attr('name').indexOf('add') >= 0) {
			$(ingredientLi).insertAfter(button.parent('li'));
		} else {
			button.parent('li').remove();
		}

		ingredients = $('#modifyRecipeForm #ingredients li');
		if (ingredients.size() === 1) {
			ingredients.first().find('button[name="minus"]').css('visibility',
					'hidden');
			ingredients.first().find('input').attr('name', 'ingredient1');
			ingredients.first().find('span[name="ingredientId"]').html('1');
			return false;
		}

		ingredients.each(function(index) {
			var input = $(this).find('input');
			input.attr('name', 'ingredient' + (index + 1));
			$(this).find('span[name="ingredientId"]').html(index + 1);
			$(this).find('button[name="minus"]').css('visibility', 'visible');

			if (!input.hasClass('ui-autocomplete-input')) {
				input.autocomplete({
					source : ingredientsList,
					minLength : 2,
					change : function(event, ui) {
						var id = (ui.item && ui.item.id) || '';
						$(this).attr('data-id', id);
					}
				});
			}
		});

		return false;
	};
	
	// a l init de la page
	$(document).ready(function() {
		
		$('#modifyRecipeForm > #submitButton').click(modifyRecipe);
		$("#ingredients").on("click", "button", onIngredientAddOrDel);
		$("#steps").on("click", "button", onStepAddOrDel);
	});
});
