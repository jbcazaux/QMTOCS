function onCreateSuccess(data){
	$('#infos').html('la sauvegarde est ok pour la recette ' + data);
	
}

function createRecipe(){
	
	var recipe = {title: $('#createRecipeForm > input[name="title"]').val(),
			imageId: $('#createRecipeForm > input[name="imgId"]').val()};
	
	jQuery.post( 'recipe', recipe , onCreateSuccess, "json");
	return false;
}


$(document).ready(function(){
	$('#createRecipeForm > #submitButton').click(createRecipe);
});