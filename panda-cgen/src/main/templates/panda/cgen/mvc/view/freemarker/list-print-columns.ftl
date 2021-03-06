	${s}#assign _columns_ = [{
			"name": "_number_",
			"type": "number",
			"fixed": true,
			"header": a.getText("listview-th-number", "")
		}, <#list ui.orderedColumnList as c>{
			"name": "${c.name}",
			"value": false,
			"header": a.getFieldLabel("${c.name}"),
			"display": a.displayField("${c.name}"),
		<#if c.format??>
			"format": {
			<#list c.format.paramList as fp>
				"${fp.name}": ${fp.value},
			</#list>
			<#if c.format.type?has_content>
				"type": "${c.format.type?replace('#', '\\x23')}"<#if c.format.pattern?has_content>,</#if>
			</#if>
			<#if c.format.pattern?has_content>
				"pattern": "${c.format.pattern?replace('#', '\\x23')}"
			</#if>
			},
		</#if>
		<#if c.hidden??>
			"hidden": ${c.hidden?string},
		</#if>
		<#if c.group??>
			"group": ${c.group?string},
		</#if>
		<#if c.cssClass?has_content>
			"cssClass": "${c.cssClass}",
		</#if>
			"sortable": false,
			"tooltip": a.getFieldTooltip("${c.name}")
		}<#if c_has_next>, </#if></#list>] />
