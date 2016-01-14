<html>
<head>
	<title><@p.text name="title-update"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li class="active"><@p.text name="step-update"/></li>
		</ol>
	</div>

	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if action.hasPermission("~/list")><li><@p.a icon="icon-list" action="~/list"><@p.text name='button-list'/></@p.a>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="n-eform" id="property" initfocus="true" method="post" theme="bs3h">
			<@p.viewfield
				key="id"
				value="%{r.id}"
				required="true"
			/>
			<@p.textfield
				key="clazz"
				value="%{r.clazz}"
				required="true"
				maxlength="100"
				size="60"
			/>
			<@p.select
				key="language"
				value="%{r.language}"
				required="true"
				emptyOption="false"
				list="consts.localeLanguageMap"
			/>
			<@p.select
				key="country"
				value="%{r.country}"
				required="true"
				emptyOption="false"
				list="consts.localeCountryMap"
			/>
			<@p.textfield
				key="name"
				value="%{r.name}"
				required="true"
				maxlength="50"
				size="60"
			/>
			<@p.textarea
				key="value"
				value="%{r.value}"
				maxlength="5000"
				cols="60"
				rows="8"
			/>
			<@p.textarea
				key="memo"
				value="%{r.memo}"
				maxlength="1000"
				cols="60"
				rows="5"
			/>
			<@p.radio
				key="status"
				value="%{r.status}"
				emptyOption="true"
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
	<#if action.inputConfirm>
		<#assign _buttons_ = _buttons_ + [{
			"icon": "icon-update-confirm",
			"action": "~/update_confirm",
			"text": "button-update-confirm"
		}]/>
	<#else>
		<#assign _buttons_ = _buttons_ + [{
			"icon": "icon-update-execute",
			"action": "~/update_execute",
			"text": "button-update-execute"
		}]/>
	</#if>
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

		<script type="text/javascript"><!--
				
			function onPageLoad() {
			}
		--></script>
<#else>
	<div class="p-tcenter">
		<@p.a btype="default" icon="back" href="#" onclick="window.history.back();return false;"><@p.text name="button-back"/></@p.a>
	</div>
</#if>
</div>

</body>
</html>