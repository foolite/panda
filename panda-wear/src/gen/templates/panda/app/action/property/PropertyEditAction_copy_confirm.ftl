<html>
<head>
	<title><@p.text name="title-copy"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li><@p.text name="step-copy"/></li>
			<li class="active"><@p.text name="step-copy-confirm"/></li>
		</ol>
	</div>
<#assign _well = a.getText("well-copy-confirm", "")/>
<#if _well?has_content>
	<div class="p-well">${_well}</div>
</#if>


	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if a.canAccess("./list")><li><@p.a action="./list" icon="icon-list" label="#(btn-list)"/>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="p-cform" id="property" focusme="true" method="post" theme="bs3h">
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
				cssClass="p-code"
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
	<#if a.displayField("updatedAt")>
			<@p.viewfield
				key="updatedAt"
				value="%{r.updatedAt}"
				format="datetime"
			/>
	</#if>
	<#if a.displayField("updatedBy")>
			<@p.hidden
				name="updatedBy"
				value="%{r.updatedBy}"
			/>
	</#if>
	<#if a.displayField("updatedByUser")>
			<@p.viewfield
				key="updatedByUser"
				value="%{r.updatedByUser}"
			/>
	</#if>
		<@p.div cssClass="p-buttons">
			<@p.submit action="./copy.execute" icon="icon-copy-execute" label="#(btn-copy-execute)"/>
			<@p.submit action="./copy.input" icon="icon-back" label="#(btn-back)"/>
		</@p.div>
	</@p.form>
<#else>
	<div class="p-tcenter">
		<@p.a href="#" onclick="window.history.back();return false;" btn="default" icon="back" label="#(btn-back)"/>
	</div>
</#if>
</div>

</body>
</html>