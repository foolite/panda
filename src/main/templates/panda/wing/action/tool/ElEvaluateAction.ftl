<html>
<head>
	<title><@p.text name="title"/></title>
</head>

<body>

<div class="p-section" id="a_eleval">
	<div class="p-header">
		<h3><i class="fa fa-keyboard-o"></i> <@p.text name="title"/></h3>
	</div>
	<#include "/panda/mvc/view/action-alert.ftl"/>

	<@p.form id="eleval" method="get" theme="simple" action="+/xml" onsubmit="return elEvaluate();">
	<table width="100%">
		<tr><td>Expression: (Ctrl+Enter to evaluate)</td></tr>
		<tr><td><@p.textarea name="expr" rows="8" cssStyle="width:100%"/></td></tr>
		<tr><td align="center"><@p.submit id="eleval_submit" icon="flash">Evaluate</@p.submit></td></tr>
	</table>
	</@p.form>

	<div id="eleval_result"></div>

	<script type="text/javascript">
		function onPageLoad() {
			$('#eleval_expr').focus().keyup(function(evt) {
				if (evt.ctrlKey && evt.which == 13) {
					$('#eleval_submit').click();
				}
			});
		}
		
		function elSetResult(s) {
			var $or = $('#eleval_result');
			if (s) {
				$or.html("<hr/><pre>" + s.escapeHtml() + "</pre>");
			}
			else {
				$or.empty();
			}
		}

		function elEvaluate() {
			try {
				elSetResult();
				if ($('#eleval_expr').val().strip().isEmpty()) {
					return false;
				}
				
				var $o = $('#eleval').loadmask("Evaluating...");
				$.ajax({
					url: $o.attr('action'),
					data: $o.serializeArray(),
					dataType: 'text',
					success: function(data, ts, xhr) {
						elSetResult(data.prettifyXml());
					},
					error: function(xhr, ts, err) {
						elSetResult(err + '\r\n' + xhr.responseText);
					},
					complete: function(xhr, ts) {
						$o.unloadmask();
					}
				});
			}
			catch (ex) {
				elSetResult("ERROR:\r\n" + ex);
			}
			return false;
		}
	</script>
</div>

</body>
</html>
