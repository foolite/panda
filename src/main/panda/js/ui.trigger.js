(function() {
	$.fn.ptrigger = function(option) {
		option = $.extend({ 'icon' : 'fa fa-remove' }, option);
		return this.each(function() {
			var $t = $(this);
			var f = option.onclick || $t.data('ptrigger');
			if (!f || f == 'false') {
				return;
			}

			var i = option.icon || $t.data('ptrigger-icon');
			var $i = $('<i class="p-trigger ' + i + '"></i>');
			$t.addClass('p-has-trigger');
			$i.insertAfter($t)
			  .click(function() {
					if (f && f != "true" && f != true) {
						panda_call(f, $t.get(0));
					}
					else {
						$t.val('');
					}
			  });
		});
	};
	
	// DATA-API
	// ==================
	$(window).on('load', function () {
		$('[data-ptrigger]').ptrigger();
	});
})();
