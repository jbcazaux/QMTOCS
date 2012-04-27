
$.urlParam = function(name){
	var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
	if (!results) { return 0; }
	return results[1] || 0;
};

$(function() {
	var ingredientLi = "<li>ingrédient <span name='number'></span><button name='minus' style='visibility: hidden'>-</button><input type='text' /><button name='add'>+</button></li>";
	var stepLi = "<li>etape <span name='number'></span><button name='minus' style='visibility: hidden'>-</button><input type='text' /><button name='add'>+</button></li>";
	
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
			li.find('span[name="number').html(index + 1);
			var input = li.find('input');
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
			li.find('span[name="number"]').html(s.order);
			li.find('input').val(s.label);
			li.find('input').attr('data-id', s.id);
			if (r.recipeSteps.length > 1){
				li.find('button[name="minus"]').css('visibility', 'visible');
			}
		});
	});
	
	// envoie de la requete pour la modification de la recette
	function modifyRecipe() {

		var steps = new Array();
		$('#steps input').each(function(index) {
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
		$('#ingredients input').each(function(index) {
			var ingredient = {
				label : $(this).val(),
				id : ($(this).attr('data-id') ? $(this).attr('data-id') : '')
			};
			if (ingredient.label) {
				ingredients.push(ingredient);
			};
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
			dataType : "json",
			contentType : "application/json"
		}).done(function(data){
			$('#infos').html('la modification est OK - ');
			return false;
		});

		return false;
	};
	
	// ajout ou suppression d un li (pour step ou ingredient)
	function addOrDelLi(type, button, li) {

		if (button.attr('name').indexOf('add') >= 0) {
			li.insertAfter(button.parent('li'));
		} else {
			button.parent('li').remove();
		}

		var lis = $('#modifyRecipeForm #'+ type + ' li');
		if (lis.size() === 1) {
			lis.first().find('button[name="minus"]').css('visibility',
					'hidden');
			lis.first().find('span[name="number"]').html('1');
			return false;
		}

		lis.each(function(index) {
			$(this).find('span[name="number"]').html(index + 1);
			$(this).find('button[name="minus"]').css('visibility', 'visible');
		});

		return false;
	}
	
	//ajout ou suppression d une etape
	function onStepAddOrDel(){
		return addOrDelLi('steps', $(this), $(stepLi));
	};
	
	function onIngredientAddOrDel(){
		addOrDelLi('ingredients', $(this), $(ingredientLi));
		var ingredients = $('#modifyRecipeForm #ingredients li');
		ingredients.each(function(index) {
			var input = $(this).find('input');
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
	
	// a l init de la page
	$(document).ready(function() {
		
		$('#modifyRecipeForm > #submitButton').click(modifyRecipe);
		$("#ingredients").on("click", "button", onIngredientAddOrDel);
		$("#steps").on("click", "button", onStepAddOrDel);
	});
	
	
});
