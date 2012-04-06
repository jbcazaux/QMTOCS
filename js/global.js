function adjustHeight(scrollableView) {
	if (scrollableView.offset()){
		scrollableView.height( 'auto' ).height( window.innerHeight - scrollableView.offset().top );
		scrollableView.scrollview( 'scrollTo', 0, 0, 300 );
	}
};

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

	adjustHeight($('.ui-scrollview-clip', this));
});

$( window ).bind( "orientationchange", function( event ) {

	window.scrollTo( 0, 0);
	var nPageH = $(document).height();
	 var nViewH = window.outerHeight;
	 if (nViewH > nPageH) {
	   nViewH -= 250;
	   $('BODY').css('height',nViewH + 'px');
	 }
	 window.scrollTo(0,1);

	
	setTimeout( function() {
		
		
		$( ".ui-page-active .ui-title" ).html('size: ' + window.innerHeight);
		adjustHeight($('.ui-scrollview-clip', $( ".ui-page-active" )));
		}, 400 );
	
});

$(document).ready(function(){
	
	window.scrollTo( 0, 0 );
	var nPageH = $(document).height();
	 var nViewH = window.outerHeight;
	 if (nViewH > nPageH) {
	   nViewH -= 250;
	   $('BODY').css('height',nViewH + 'px');
	 }
	 window.scrollTo(0,1);
});