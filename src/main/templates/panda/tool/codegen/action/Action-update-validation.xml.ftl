<#include "common.ftl"/>
<!DOCTYPE validators PUBLIC
		"-//Apache Struts//XWork Validator 1.0.3//EN"
		"http://struts.apache.org/dtds/xwork-validator-1.0.3.dtd">

<validators>
	<#assign requiredPK = true/>
	<#include "field-validation.xml.ftl"/>

	<#include "data-validation.xml.ftl"/>
</validators>