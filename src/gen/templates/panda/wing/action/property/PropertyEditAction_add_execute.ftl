<html>
<head>
	<title><@p.text name="title-add"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li><@p.text name="step-add"/></li>
			<li class="active"><@p.text name="step-add-success"/></li>
		</ol>
	</div>
<#assign _well = a.getText("well-add-success", "")/>
<#if _well?has_content>
	<div class="p-well">${_well}</div>
</#if>


	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if r?? && a.canAccessData("~/print", r)><li><@p.a icon="icon-print" label="#(button-print)" target="_blank" action="~/print"><@p.param name="id" value="%{r.id}"/></@p.a>
</li></#if><#if a.canAccess("~/add")><li><@p.a icon="icon-new" action="~/add" label="#(button-new)"/>
</li></#if><#if a.canAccess("~/list")><li><@p.a icon="icon-list" action="~/list" label="#(button-list)"/>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="p-sform" id="property" initfocus="true" method="post" theme="bs3h">
	<#if a.displayField("id")>
			<@p.viewfield
				key="id"
				value="%{r.id}"
			/>
	</#if>
	<#if a.displayField("clazz")>
			<@p.viewfield
				key="clazz"
				value="%{r.clazz}"
			/>
	</#if>
	<#if a.displayField("language")>
			<@p.viewfield
				key="language"
				value="%{r.language}"
				list="%{consts.localeLanguageMap}"
			/>
	</#if>
	<#if a.displayField("country")>
			<@p.viewfield
				key="country"
				value="%{r.country}"
				list="%{consts.localeCountryMap}"
			/>
	</#if>
	<#if a.displayField("name")>
			<@p.viewfield
				key="name"
				value="%{r.name}"
			/>
	</#if>
	<#if a.displayField("value")>
			<@p.viewfield
				key="value"
				value="%{r.value}"
				escape="phtml"
			/>
	</#if>
	<#if a.displayField("memo")>
			<@p.viewfield
				key="memo"
				value="%{r.memo}"
			/>
	</#if>
	<#if a.displayField("status")>
			<@p.viewfield
				key="status"
				value="%{r.status}"
				list="%{consts.dataStatusMap}"
			/>
	</#if>
	<#if a.displayField("uusid")>
			<@p.viewfield
				key="uusid"
				value="%{r.uusid}"
			/>
	</#if>
	<#if a.displayField("uusnm")>
			<@p.viewfield
				key="uusnm"
				value="%{r.uusnm}"
			/>
	</#if>
	<#if a.displayField("utime")>
			<@p.viewfield
				key="utime"
				value="%{r.utime}"
				format="datetime"
			/>
	</#if>
			<#assign _buttons_ = [] />
			<#if r?? && a.canAccessData('~/edit', r)>
				<@p.url var="_u_" action="~/edit" escapeAmp="false">
					<@p.param name="id" value="%{r.id}"/>
				</@p.url>
				<#assign _buttons_ = _buttons_ + [{
					"icon": "icon-edit",
					"onclick": "location.href='${vars._u_?js_string}'; return false;",
					"text": "button-edit"
				}]/>
			</#if>
			<#if r?? && a.canAccessData('~/copy', r)>
				<@p.url var="_u_" action="~/copy" escapeAmp="false">
					<@p.param name="id" value="%{r.id}"/>
				</@p.url>
				<#assign _buttons_ = _buttons_ + [{
					"icon": "icon-copy",
					"onclick": "location.href='${vars._u_?js_string}'; return false;",
					"text": "button-copy"
				}]/>
			</#if>
			<#if r?? && a.canAccessData('~/delete', r)>
				<@p.url var="_u_" action="~/delete" escapeAmp="false">
					<@p.param name="id" value="%{r.id}"/>
				</@p.url>
				<#assign _buttons_ = _buttons_ + [{
					"icon": "icon-delete",
					"onclick": "location.href='${vars._u_?js_string}'; return false;",
					"text": "button-delete"
				}]/>
			</#if>
			<#include "/panda/mvc/view/form-buttons.ftl"/>
			<@form_buttons buttons=_buttons_/>
	</@p.form>
</#if>
</div>

</body>
</html>
