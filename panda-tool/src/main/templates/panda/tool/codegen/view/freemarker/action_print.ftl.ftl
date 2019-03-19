<#include "common.ftl"/>
<@header/>
<@headinc step=""/>

<div class="p-section">
	<@sheader steps=[ ui.name ]/>
	<@swell/>

	${s}#include "/action-alert.ftl"/>

${s}#if r??>
	${s}@p.form cssClass="p-vform" id="<#if ui.formId?has_content>${ui.formId}<#else>${action.name}</#if>"${gen.focusme(ui)}>
		<#include "edit-view-fields.ftl"/>
	${s}/@p.form>
${s}/#if>
</div>

<@footinc step=""/>
<@footer/>
