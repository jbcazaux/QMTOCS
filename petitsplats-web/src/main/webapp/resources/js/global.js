function adjustHeight(scrollableView) {
	if (scrollableView.offset()){
		scrollableView.height( 'auto' ).height( window.innerHeight - scrollableView.offset().top );
		scrollableView.scrollview( 'scrollTo', 0, 0, 300 );
	}
};

function removeUrlBar(){
	window.scrollTo( 0, 0);
	var nPageH = $(document).height();
	var nViewH = window.outerHeight;
	if (nViewH > nPageH) {
	  nViewH -= 250;
	  $('BODY').css('height',nViewH + 'px');
	}
	window.scrollTo(0,1);
}

$( ":jqmData(role='page')" ).live( "pageshow", function(event) {
	var $page = $( this );

	$page.find( ":jqmData(scroll):not(.ui-scrollview-clip)" ).each(function () {
		var $this = $( this );
		if ( $this.hasClass( "ui-scrolllistview" ) ) {
			$this.scrolllistview();
		} else {
			var st = $this.jqmData( "scroll" ) + "",
				paging = st && st.search(/^[xy]p$/) != -1,
				dir = st && st.search(/^[xy]/) != -1 ? st.charAt(0) : null,

				opts = {
					direction: dir || undefined,
					paging: paging || undefined,
					scrollMethod: $this.jqmData("scroll-method") || undefined
				};

			$this.scrollview( opts );
		}
	});

	removeUrlBar();
	adjustHeight($('.ui-scrollview-clip', this));
});

$( window ).bind( "orientationchange", function( event ) {

	removeUrlBar();
	
	setTimeout( function() {
		adjustHeight($('.ui-scrollview-clip', $( ".ui-page-active" )));
		}, 400 );
});

function createPage(data){
	$.get('suggestion.html', function(suggestionTemplate) {
	    var v = Mustache.render(suggestionTemplate, data);
	    $('body').append(v);
	});
};

$(document).ready(function(){
	
	removeUrlBar();

	console.log('fetching: remote/' + 1 + '.json');
	$.ajax({
		  url: 'recipe/' + 1,
		  success: function(data) {
			  
			  createPage(data);
			  
			  $('.home_suggestion').on('click', function (){
				  $.mobile.changePage($('#suggestion_1'));
			  });
			},
		  error: function (jqXHR, textStatus, errorThrown){
			  console.log('erreur... ' + textStatus);
		  	},	
		  dataType: 'json',
		  cache:false
		}); 
	 
});