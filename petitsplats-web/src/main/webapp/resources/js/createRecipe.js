$(function() {

	// liste des ingrédients pour l autocompletion
	var ingredientsList = [];

	// fetch la liste
	$.ajax({
		url : "ingredients",
		type : "GET",
		success : initAutocomplete,
		contentType : "application/json"
	});

	// callback lors du fetch de la liste
	function initAutocomplete(data) {
		ingredientsList = data;
		$('input[name^="ingredient"]').autocomplete({
			source : ingredientsList,
			minLength : 2,
			change : function(event, ui) {
				var id = (ui.item && ui.item.id) || '';
				$(this).attr('data-id', id);
			}
		});
	};

	// call back lors de la création de la recette en base
	function onCreateSuccess(data) {
		$('#infos').html('la sauvegarde est ok pour la recette ' + data);
	}

	// les templates des li qui sont ajoutées
	var stepLi = "<li>etape <span name='stepId'></span><button name='minus'>-</button><input type='text' name='step' /><button name='add'>+</button></li>";
	var ingredientLi = "<li>ingrédient <span name='ingredientId'></span><button name='minus'>-</button><input type='text' name='ingredient' /><button name='add'>+</button></li>";

	// ajout ou suppression d une etape
	function onStepAddOrDel() {
		var button = $(this);
		var steps = $('#createRecipeForm #steps li');

		if (button.attr('name').indexOf('add') >= 0) {
			$(stepLi).insertAfter(button.parent('li'));
		} else {
			button.parent('li').remove();
		}

		steps = $('#createRecipeForm #steps li');
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

	// ajout ou suppression d un agrement
	function onIngredientAddOrDel() {
		var button = $(this);
		var ingredients = $('#createRecipeForm #ingredients li');

		if (button.attr('name').indexOf('add') >= 0) {
			$(ingredientLi).insertAfter(button.parent('li'));
		} else {
			button.parent('li').remove();
		}

		ingredients = $('#createRecipeForm #ingredients li');
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
	}
	;

	// envoie de la requete pour la creation de la recette
	function createRecipe() {

		var steps = new Array();
		$('input[name^="step"]').each(function(index) {
			var step = {
				label : $(this).val(),
				order : (index + 1)
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
			'title' : $('#createRecipeForm > input[name="title"]').val(),
			'recipeSteps' : steps,
			'ingredients' : ingredients
		};

		$.ajax({
			url : "recipe",
			type : "POST",
			data : JSON.stringify(recipe),
			success : onCreateSuccess,
			dataType : "json",
			contentType : "application/json"
		});

		return false;
	};

	function onLoadRecipe(data){
		
		
	};
	
	// a l init de la page
	$(document).ready(function() {
		$('#createRecipeForm > #submitButton').click(createRecipe);
		$("#steps").on("click", "button", onStepAddOrDel);
		$("#ingredients").on("click", "button", onIngredientAddOrDel);
		
		$("#loadButton").on("click", function(){
			var id = $("#imgid").val();
			$('#recipeImg').attr('src', 'recipe/' +id+'.jpg');
		});

	});
});
