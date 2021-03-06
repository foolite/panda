<#include "common.ftl"/>
<@header/>
<@headinc step=""/>

<div class="p-section">
	<@sheader steps=[ ui.name ]/>
	<@swell/>

	<#include "edit-toolbar.ftl"/>

	<div id="${formId}_alert">${s}#include "/action-alert.ftl"/></div>

	<#list ui.orderedFieldList as f>
		<#if f.editTag?? && f.editTag.name?ends_with(".file")>
			<#assign _formenctype = "multipart/form-data" />
		</#if>
	</#list>
	${s}@p.form cssClass="p-eform" id="${formId}" method="post"<#if _formenctype?has_content> enctype="${_formenctype}"</#if>${gen.focusme(ui)}>
		<#include "edit-update-fields.ftl" />
		${s}#assign _buttons_ = [] />
<#if "true" == props['ui.input.confirm']!>
	${s}#if a.inputConfirm>
		${s}#assign _buttons_ = _buttons_ + [{
			"icon": "icon-${ui.name}-confirm",
			"action": "./${ui.name}${props['ui.action.seperator']!'.'}confirm",
			"text": "btn-${ui.name}-confirm"
		}]/>
	${s}#else>
</#if>
		${s}#assign _buttons_ = _buttons_ + [{
			"icon": "icon-${ui.name}-execute",
			"action": "./${ui.name}${props['ui.action.seperator']!'.'}execute",
			"text": "btn-${ui.name}-execute"
		}]/>
<#if "true" == props['ui.input.confirm']!>
	${s}/#if>
</#if>
<#if "true" == props['ui.input.reset']!>
		${s}#assign _buttons_ = _buttons_ + [{
			"type": "reset",
			"icon": "icon-reset",
			"text": "btn-reset"
		}]/>
</#if>
		<#include "edit-actions.ftl"/>
	${s}/@p.form>

	<#include "edit-script.ftl" />
</div>

<@footinc step=""/>
<@footer/>
