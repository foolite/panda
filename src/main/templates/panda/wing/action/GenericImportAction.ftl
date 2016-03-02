<html>
<head>
	<title><@p.text name="title-import"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>

<body>

<div class="p-section" id="a_geimp">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li class="active"><@p.text name="step-import"/></li>
		</ol>
	</div>

<#if text.getText("well-import", "")?has_content>
	<div class="p-well"><@p.text name="well-import"/></div>
</#if>

	<#include "/action-alert.ftl"/>

	<@p.form id="geimp" method="post" enctype="multipart/form-data" theme="bs3h">
		<@p.file
			key="file"
			value=""
			size="50"
		/>
		<@p.checkbox name="loose" fieldLabel="#(p.loose)" fieldValue="true"/>
		<@p.div cssClass="p-buttons">
			<@p.submit icon="import"><@p.text name="button-import"/></@p.submit>
		</@p.div>
	</@p.form>

<#if result??>
<#macro rview name head data err>
<#if data?has_content>
	<hr/>
	<div class="panel panel-${name}">
		<div class="panel-heading"><@p.text name="result-${name}"><@p.param name="count" value=(data?size)/></@p.text></div>
		<div class="table-responsive">
		<table class="table table-striped table-bordered p-fz80p p-th-nowrap p-td-nowrap">
		<thead>
			<tr><th>##</th>
<#if err>
				<th><@p.text name="head-error"/></th>
</#if>
<#list head as c>
				<th>${assist.escapePhtml(c!"")}</th>
</#list>
			</tr>
		</thead>
		<tbody>
<#list data as row>
			<tr><td>${(row_index + 1)?c}</td>
	<#list row as c>
				<td>${assist.escapePhtml(c!"")}</td>
	</#list>
			</tr>
</#list>
		</tbody>
		</table>
		</div>
	</div>
</#if>
</#macro>

	<@rview name="warning" head=result.headers data=(result.warning)![] err=true/>

	<@rview name="success" head=result.headers data=(result.success)![] err=false/>
</#if>

</div>

</body>
</html>
