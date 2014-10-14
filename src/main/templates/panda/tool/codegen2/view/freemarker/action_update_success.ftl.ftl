<#include "common.ftl"/>
<@header/>

<div class="p-section">
	<div class="p-header">
		<h3>${s}@p.text name="title-${d}{actionResult}">${s}@s.param>${s}@p.text name="title"/>${s}/@s.param>${s}/@p.text></h3>
	</div>
	<#include "success-toolbar.ftl"/>

<#if "true" == props["ui.input.step"]!"">
	${s}#if action.getTextAsBoolean('ui-input-step', false)>
		<ul class="n-step">
			<li class="n-step-first">${s}@p.text name="step-${ui.name}_input"/></li>
			<li class="n-step-arrow">${s}@p.text name="step-arrow-e">&gt;${s}/@p.text></li>
			<li class="n-step-next">${s}@p.text name="step-${ui.name}_confirm"/></li>
			<li class="n-step-arrow">${s}@p.text name="step-arrow-e">&gt;${s}/@p.text></li>
			<li class="n-step-last n-step-self">${s}@p.text name="step-${ui.name}Success"/></li>
		</ul>
		<div class="n-clear"><hr/></div>
	${s}/#if>
</#if>

	${s}#include "/panda/exts/struts2/views/action-alert.ftl"/>

	${s}@p.form cssClass="p-sform" id="<#if ui.formId?has_content>${ui.formId}<#else>${action.name}</#if>" initfocus="${ui.focus?c}" method="post"<#if ui.theme?has_content> theme="${ui.theme}"</#if>>
		<#include "view-fields.ftl"/>
		<#include "input-success-actions.ftl"/>
	${s}/@p.form>
</div>

<@footer/>
