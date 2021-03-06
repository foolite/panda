<#list ui.orderedFieldList as f>
	${s}#if a.displayField("${f.name}")>
	<#if f.before?has_content>
			${f.before}
	</#if>
	<#assign tag = ""/>
	<#if f.viewTag??>
		<#assign tag = f.viewTag/>
	<#elseif f.editTag??>
		<#assign tag = f.editTag/>
	</#if>
	<#if f.content?has_content>
			${f.content}
	<#elseif tag?has_content>
		<#if tag.name?ends_with(".hidden")>
			${s}@${tag.name}
				name="${f.name}"
				value="%{r.${f.name}}"
			<#list tag.paramList as tp><#if gen.startsWithLetter(tp.name) && !(tp.name != "readonly")>
				${tp.name}="${tp.value}"
			<#elseif tp.name?starts_with('*')>
				${tp.name?substring(1)}="${tp.value}"
			</#if></#list>
			/>
		<#elseif tag.name?ends_with(".uploader")>
			${s}@${tag.name}
				key="${f.name}"
				value="%{r.${f.name}}"
				readonly="true"
			<#list tag.paramList as tp><#if gen.startsWithLetter(tp.name) && (tp.name != "readonly")>
				${tp.name}="${tp.value}"
			<#elseif tp.name?starts_with('*')>
				${tp.name?substring(1)}="${tp.value}"
			</#if></#list>
			<#if f.editTag.hasParamStartsWithAny("%+")>
			>
				<#list f.editTag.paramList as tp>
					<#if (ui.templates?seq_contains("insert") || ui.templates?seq_contains("copy")) 
						&& (tp.name == '+defaultLink' || tp.name == '_defaultText')>
					<#elseif tp.name?starts_with('%')>
				${s}@p.param name="${tp.name?substring(1)}"><@aurl au=tp.values/>${s}/@p.param>
					<#elseif tp.name?starts_with('_')>
				${s}@p.param name="${tp.name?substring(1)}">${tp.value}${s}/@p.param>
					</#if>
				</#list>
			${s}/@${f.editTag.name}>
			<#else>
			/>
			</#if>
		<#elseif f.viewTag?? || tag.name?ends_with(".viewfield")>
			${s}@${tag.name}
				key="${f.name}"
				value="%{r.${f.name}}"
			<#list tag.paramList as tp><#if gen.startsWithLetter(tp.name)>
				${tp.name}="${tp.value}"
			</#if></#list>
			<#if tag.hasParamStartsWithAny("_*")>
				<#list tag.paramList as tp>
					<#if tp.name?starts_with('_') || tp.name?starts_with('*')>
				${tp.name?substring(1)}="${tp.value}"
					</#if>
				</#list>
			</#if>
			/>
		<#else>
			<#assign _format = ""/>
			<#list tag.paramList as tp>
				<#if tp.name == "format">
					<#assign _format = tp.value/>
				</#if>
			</#list>
			${s}@p.viewfield
				key="${f.name}"
				value="%{r.${f.name}}"
			<#if _format?has_content>
				format="${_format}"
			<#elseif tag.name?ends_with(".checkbox")>
				format="check"
			<#elseif tag.name?ends_with(".datepicker")>
				format="date"
			<#elseif tag.name?ends_with(".timepicker")>
				format="time"
			<#elseif tag.name?ends_with(".datetimepicker")>
				format="datetime"
			<#elseif tag.name?ends_with(".htmleditor")>
				format="html"
			</#if>
			<#list tag.paramList as tp>
				<#if [ "fieldValue", "list", "listKey", "listValue", "listBreak", "listOrder" ]?seq_contains(tp.name)>
				${tp.name}="${tp.value}"
				</#if>
			</#list>
			<#if tag.hasParamStartsWithAny("_*")>
				<#list tag.paramList as tp>
					<#if tp.name?starts_with('_') || tp.name?starts_with('*')>
				${tp.name?substring(1)}="${tp.value}"
					</#if>
				</#list>
			</#if>
			/>
		</#if>
	</#if>
	<#if f.after?has_content>
			${f.after}
	</#if>
	${s}/#if>
</#list>
