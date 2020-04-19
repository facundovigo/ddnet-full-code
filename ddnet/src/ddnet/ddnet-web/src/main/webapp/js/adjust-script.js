

var ajustes = {
	ddnet_principal: function(){
		$('div#tabPrincipal').css({height:(window.innerHeight-40)+'px'});
		$('div#tabPrincipal').find('.ui-widget-content').css({height:(window.innerHeight-105)+'px'});
	},
	study_manager: function(){
		$('div.study-actions-buttons').css({
			width:'97.5%',
			'top':$('div#tabPrincipal .ui-widget-content').height()+10
		});
		$('.chrome div.dataTables_scrollBody').height(
			$('div.form-holder').is(':visible') ? 
				$('div#tabPrincipal .ui-widget-content').height()-370 :
				$('div#tabPrincipal .ui-widget-content').height()-200
		);
		$('.gecko div.study-list-table').css({width:'100%'});
		$('.gecko div.dataTables_scrollBody').height(
			$('div.form-holder').is(':visible') ? 
				$('div#tabPrincipal .ui-widget-content').height()-380 :
				$('div#tabPrincipal .ui-widget-content').height()-205
		);
		var anchoVentana=document.body.clientWidth;
		if(anchoVentana<1200){
			$('div.form-holder').css('font-size','0.8em');
			$('form#studySearchFilter').css('width','100%');
			$('div.study-actions-buttons').css('font-size','0.75em');
			$('div.study-actions-buttons').find('button').height(26);
			$('.gecko td.filter-align-right').css('width','260px');
		} else {
			$('.win div.form-holder').css('font-size','9pt');
			$('.linux div.form-holder').css('font-size','10pt');
			$('form#studySearchFilter').css('width','90%');
			$('select#formModalities').css('font-size','0.9em');
			$('div.study-actions-buttons').css('font-size','10pt');
			$('div.study-actions-buttons').find('ui.button,select').css('font-size','0.9em');
			$('.linux.gecko td.filter-align-right').css('width','280px');
		}
	},
	abm: function(){
		$('div#abmTabs').css({height:(window.innerHeight-120)+'px'});
		$('div#abmTabs').find('.ui-tabs-nav').css({height:(window.innerHeight-130)+'px'});
		$('div#abmTabs').find('.ui-widget-content').css({height:(window.innerHeight-154)+'px'});
		$('div#abmTabs').css('width','100%');
		var width= $('div#abmTabs').width();
		var percent= (182*100)/width;
		$('div#abmTabs').find('.ui-widget-content').css('width',(100-percent)+'%');
		var ptje= (170*100)/width;
		$('.mac div#abmTabs').find('.ui-widget-content').css('width',(100-ptje)+'%');
	},
	abmUsuarios: function(){
		var height= $('div#abmTabs').find('.ui-tabs-nav').height();
		$('.linux div.user-list').css('height',(height-168)+'px');
		$('.win div.user-list').css('height',(height-175)+'px');
	},
	abmEstudios: function(){
		var height= $('div#abmTabs').find('.ui-tabs-nav').height();
		$('div.study-list').css('height',(height-165)+'px');
	},
	studyWindow: function(){
		$('div#tabs').css({height:(window.innerHeight-136)+'px'});
		$('div#tabs').find('.ui-widget-content').css({height:(window.innerHeight-207)+'px'});
		var height= $('div#tabs').find('.ui-widget-content').height()-80;
		$('textarea#studyReportBody').height(height);
		$('div.report-left').height(height+45);
		$('div.report-right').height(height+45);
		$('div.study-messages').height(height+45);
		$('div.study-assignments').height(height+45);
		$('div.study-clinical-data').height(height+45);
		$('div.med-order-list').height(height+45);
		$('div.files-list').height(height+45);
		$('div.mp3-list').height(height+45);
		$('div#tabs').find('.ui-').css('width','95%');
		var selectedTab= $('div#tabs').find('div.ui-tabs-panel[aria-hidden="false"]');
		var width= ($(selectedTab).width()-530)/2;
		$('div.report-left').width(width);
		$('div.report-right').width(width);
		$('.win div.report-left').width(width-26);
		$('.win div.report-right').width(width-26);
		width= ($(selectedTab).width()-40)/3;
		$('div.study-messages').width(width);
		$('div.study-assignments').width(width);
		$('div.study-clinical-data').width(width);
		$('div.med-order-list').width(width);
		$('div.files-list').width(width);
		$('div.mp3-list').width(width);
	}
};
	