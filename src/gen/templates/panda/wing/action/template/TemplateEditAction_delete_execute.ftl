<html>
<head>
	<title><@p.text name="title-delete"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li><@p.text name="step-delete"/></li>
			<li class="active"><@p.text name="step-delete-success"/></li>
		</ol>
	</div>

	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if action.hasPermission("~/list")><li><@p.a icon="icon-list" action="~/list"><@p.text name='button-list'/></@p.a>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="p-sform" id="template" initfocus="true" method="post" theme="bs3h">
			<@p.viewfield
				key="id"
				value="%{r.id}"
			/>
			<@p.viewfield
				key="name"
				value="%{r.name}"
			/>
			<@p.viewfield
				key="language"
				value="%{r.language}"
				list="consts.localeLanguageMap"
			/>
			<@p.viewfield
				key="country"
				value="%{r.country}"
				list="consts.localeCountryMap"
			/>
			<@p.viewfield
				key="source"
				value="%{r.source}"
			/>
			<@p.viewfield
				key="status"
				value="%{r.status}"
				list="%{consts.dataStatusMap}"
			/>
			<@p.viewfield
				key="uusid"
				value="%{r.uusid}"
			/>
			<@p.viewfield
				key="uusnm"
				value="%{r.uusnm}"
			/>
			<@p.viewfield
				key="utime"
				value="%{r.utime}"
				format="datetime"
			/>
			<#assign _buttons_ = [] />
			<#if action.hasPermission('~/list')>
				<@p.url var="_u_" action="~/list"/>
				<#assign _buttons_ = _buttons_ + [{
					"icon": "icon-list",
					"onclick": "location.href='${vars._u_?js_string}'; return false;",
					"text": "button-list"
				}]/>
			</#if>
			<#include "/panda/mvc/view/form-buttons.ftl"/>
			<@form_buttons buttons=_buttons_/>
	</@p.form>
</#if>
</div>

</body>
</html>