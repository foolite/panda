(function($) {
	function setAlertType($p, s, t) {
		$p.removeClass('alert-danger alert-warning alert-info alert-success').addClass(s.types[t]);
	}
	
	function addMsg($p, s, m, t) {
		var ic = s.icons[t];
		var tc = s.texts[t];
		$p.append('<ul class="' +  s.ulCls + '"><li class="' + tc + '"><i class="' + ic + '"></i>' + m + '</li></ul>');
		setAlertType($p, s, t);
	}

	function addMsgs($p, s, m, t) {
		if (m) {
			var ic = s.icons[t];
			var tc = s.texts[t];
			var h = '<ul class="' + s.ulCls + '">';
			if ($.isArray(m)) {
				for (var i = 0; i < m.length; i++) {
					h += '<li class="' + tc + '"><i class="' + ic + '"></i>' + m[i] + '</li>';
				}
			}
			else {
				for (var n in m) {
					var v = m[n];
					for (var i = 0; i < v.length; i++) {
						h += '<li class="' + tc + '"><i class="' + ic + '"></i>' + v[i] + '</li>';
					}
				}
			}
			h += '</ul>';
			$p.append(h);
			setAlertType($p, s, t);
		}
	}
	
	$.palert = {
		ulCls: 'fa-ul',
		icons: {
			'down': 'fa-caret-down',
			'up': 'fa-caret-up',
			'info': 'fa-li fa fa-info-circle',
			'help':'fa-li fa fa-question-circle',
			'warn': 'fa-li fa fa-exclamation-triangle',
			'error': 'fa-li fa fa-exclamation-circle'
		},
		texts: {
			'info': 'text-success',
			'help':'text-warning',
			'warn': 'text-warning',
			'error': 'text-dander'
		},
		types: {
			'info': 'alert-success',
			'help':'alert-info',
			'warn': 'alert-warning',
			'error': 'alert-dander'
		}
	};
	
	var palert = function($c, s) {
		s = $.extend({}, $.palert, s);
		return {
			api: function() {
				return this;
			},
			clear: function() {
				$c.children('.alert').remove();
				return this;
			},
			error: function(m) {
				this.add(m, 'error');
				return this;
			},
			warn: function(m) {
				this.add(m, 'warn');
				return this;
			},
			promt: function(m) {
				this.add(m, 'help');
				return this;
			},
			info: function(m) {
				this.add(m, 'info');
				return this;
			},
			add: function(m, t) {
				t = t || 'info';
				var $p = $c.children('.alert');
				var a = false;
				if ($p.size() < 1) {
					$p = $('<div></div>').addClass('alert alert-dismissable').css('display', 'none');
					$c.prepend($p);
					$p.append("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
					a = true;
				}
				
				if (typeof(m) == 'string') {
					addMsg($p, s, m, t);
				}
				else if ($.isArray(m)) {
					for (var i = 0; i < m.length; i++) {
						if (typeof(m[i]) == 'string') {
							addMsg($p, s, m[i], t);
						}
						else {
							addMsg($p, s, m[i].html, m[i].type);
						}
					}
				}
				else if (m) {
					addMsgs($p, s, m.actionErrors, "error");
					addMsgs($p, s, m.fieldErrors, "error");
					addMsgs($p, s, m.actionWarnings, "warn");
					addMsgs($p, s, m.actionConfirms, "help");
					addMsgs($p, s, m.actionMessages, "info");
				}
				
				if (a) { 
					$p.slideDown();
				}
				return this;
			}
		}
	};
	
	$.fn.palert = function(option, v1, v2) {
		return this.each(function () {
			var ops = typeof option === 'object' && option;
			var pa = palert($(this), ops);
			if (typeof option === 'string') {
				pa[option](v1, v2);
			}
		});
	};
	
	$.palert.toggleFieldErrors = function(el) {
		var $fes = $(el).closest('.p-action-errors').next('.p-field-errors');
		var id = $.palert.icons.down;
		var iu = $.palert.icons.up;
		if ($fes.is(':hidden')) {
			$fes.slideDown();
			$(el).children('i').removeClass(id).addClass(iu);
		}
		else {
			$fes.slideUp();
			$(el).children('i').removeClass(iu).addClass(id);
		}
		return false;
	};
})(jQuery);
