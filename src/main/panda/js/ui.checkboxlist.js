(function($) {
	$(window).on('load', function() {
		$(".p-checkboxlist.order :checkbox").click(function() {
			var $t = $(this);
			var $l = $t.closest('label');
			var $h = $t.closest('.p-checkboxlist').find('hr');
			$l.fadeOut(function() {
				if ($t.is(':checked')) {
					$l.insertBefore($h);
				}
				else {
					$l.insertAfter($h);
				}
				$l.fadeIn();
			});
		});
	});
})(jQuery);
