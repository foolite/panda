<html>
<head>
	<title><@p.text name="title"/></title>
</head>

<body>

<div class="p-section" id="a_dataimp">
	<div class="p-header">
		<h3><i class="fa fa-cloud-upload"></i> <@p.text name="title"/></h3>
	</div>
	<#include "/action-alert.ftl"/>

	<@p.form id="dataimp" method="post" enctype="multipart/form-data" theme="bs3h">
		<@p.select key="target" list="%{action.targetSet}"/>
		<@p.file
			key="file"
			value=""
			size="50"
			onchange="onFileChange(this)"
		/>
		<@p.checkbox name="deleteAll" fieldLabel="#(deleteAll)" fieldValue="true"/>
		<@p.div cssClass="p-buttons">
			<@p.submit icon="fa fa-inbox" label="#(btn-import)"/>
		</@p.div>
	</@p.form>
	
	<script type="text/javascript">
		function onFileChange(e) {
			var fn = $(e).val().toUpperCase();
			var st = $('#dataimp_target').get(0);
			for (var i = 0; i < st.options.length; i++) {
				if (fn.indexOf(st.options[i].text) >= 0) {
					st.selectedIndex = i;
					break;
				}
			}
		}
	</script>

<#if result?has_content>
	<hr/>
	<#list result as t>
	<div class="panel panel-success">
		<div class="panel-heading">&lt;&lt;${t[0]}&gt;&gt; (${(t?size - 3)?c})</div>
		<div class="table-responsive">
			<table class="table table-striped table-bordered p-fz80p p-th-nowrap p-td-nowrap">
				<#list t as row>
					<#if row_index == 0>
					<#elseif row_index < 3>
						<#if row_index == 1><thead></#if>
						<tr><th>##</th><#list row as _h><th>${assist.escapePhtml(_h!"")}</th></#list></tr>
						<#if row_index == 2></thead></#if>
					<#else>
						<#if row_index == 3><tbody></#if>
						<tr><th>${(row_index - 2)?c}</th><#list row as _v><td>${assist.escapePhtml(_v!"")}</td></#list></tr>
						<#if !row_has_next></tbody></#if>
					</#if>
				</#list>
			</table>
		</div>
	</div>
	<br/>
	</#list>
</#if>
</div>

</body>
</html>