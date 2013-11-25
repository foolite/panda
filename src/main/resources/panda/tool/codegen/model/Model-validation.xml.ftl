<#--
/*
 * This file is part of Nuts Framework.
 * Copyright(C) 2009-2012 Nuts Develop Team.
 *
 * Nuts Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License any later version.
 *
 * Nuts Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Nuts Framework. If not, see <http://www.gnu.org/licenses/>.
 */
-->
<#include "common.ftl"/>
<!DOCTYPE validators PUBLIC
		"-//Apache Struts//XWork Validator 1.0.3//EN"
		"http://struts.apache.org/dtds/xwork-validator-1.0.3.dtd">

<!-- This file was auto-generated by ${gen.class.name} <#if date??>(${date?string("yyyy-MM-dd HH:mm:ss")})</#if>. -->
<validators>

<#list model.orgPropertyList as p>
	<field name="${p.name}">
	<#list p.validatorList as v>
		<field-validator type="${v.type}">
		<#if v.paramList?has_content><#list v.paramList as vp>
			<param name="${vp.name}">${vp.value}</param>
		</#list></#if>
			<message key="${v.message}"/>
		</field-validator>
	</#list>
	<#assign type = p.simpleJavaType/>
	<#if type?ends_with('[]')>
		<#assign type = type?substring(0, type?length - 2)/>
	<#elseif type?ends_with('>') && type?index_of('<') gt 0> 
		<#assign ilt = type?index_of('<')/>
		<#assign type = type?substring(ilt + 1, type?length - 1)/>
	</#if>
		<field-validator type="conversion">
			<message key="validation-conversion-${type?html}"/>
		</field-validator>
	</field>
</#list>
</validators>
