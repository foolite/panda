<#include "common.ftl"/>
<@header/>

<div class="p-section">
	<@sheader steps=[ ui.name, ui.name + "-success" ]/>
	<@swell step="-success"/>

	<#include "bulk-success-toolbar.ftl"/>

	${s}#include "/action-alert.ftl"/>
	<br/>

${s}#if result?has_content>
	${s}#assign _columns_ = [{
			"name": "_rownum_",
			"type": "rownum",
			"header": a.getText("listview-th-rownum", ""),
			"fixed": true
		}] />
<#list ui.displayColumnList as c>
${s}#if a.displayField("${c.name}")>
	${s}#assign _columns_ = _columns_ + [{
			"name": "${c.name}",
			"header": a.getFieldLabel("${c.name}"),
		<#if c.format??>
			"format": {
			<#list c.format.paramList as fp>
				"${fp.name}": ${fp.value},
			</#list>
				"type": "${c.format.type?replace('#', '\\x23')}"
				},
		</#if>
		<#if c.display??>
			"display": ${c.display?string},
		</#if>
		<#if c.hidden??>
			"hidden": ${c.hidden?string},
		</#if>
		<#if c.group??>
			"group": ${c.group?string},
		</#if>
			"sortable": false,
			"tooltip": a.getFieldTooltip("${c.name}")
		}] />
${s}/#if>
</#list>


	${s}@p.listview id="${action.name}_${ui.name}"
		list=result columns=_columns_<#if ui.cssColumn?has_content> cssColumn="${ui.cssColumn}"</#if>
		cssTable="table-hover table-striped"
	<#if ui.params.addon?has_content>
		addon="${ui.params.addon}"
	</#if>
	/>
	
	<br/>
	<div class="p-tcenter">
		<#include "bulk-success-buttons.ftl"/>
	</div>
${s}#else>
	<@sback/>
${s}/#if>
	<@safeinc step="_execute"/>
</div>

<@footer/>