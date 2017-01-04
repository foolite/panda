<html>
<head>
	<title><@p.text name="title-bdelete"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li><@p.text name="step-bdelete"/></li>
			<li class="active"><@p.text name="step-bdelete-confirm"/></li>
		</ol>
	</div>
<#assign _well = a.getText("well-bdelete", "")/>
<#if _well?has_content>
	<div class="p-well">${_well}</div>
</#if>


	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if a.canAccess("~/list")><li><@p.a icon="icon-list" action="~/list" label="#(button-list)"/>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>
	<br/>

<#if result?has_content>
	<#assign _columns_ = [{
			"name": "_rownum_",
			"type": "rownum",
			"header": a.getText("listview-th-rownum", ""),
			"fixed": true
		}, {
			"name": "_check_",
			"type": "check",
			"fixed": true
		}] />
<#if a.displayField("id")>
	<#assign _columns_ = _columns_ + [{
			"name": "id",
			"pkey" : true,
			"value": true,
			"header": a.getFieldLabel("id"),
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("id")
		}] />
</#if>
<#if a.displayField("clazz")>
	<#assign _columns_ = _columns_ + [{
			"name": "clazz",
			"header": a.getFieldLabel("clazz"),
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("clazz")
		}] />
</#if>
<#if a.displayField("language")>
	<#assign _columns_ = _columns_ + [{
			"name": "language",
			"header": a.getFieldLabel("language"),
			"format": {
				"codemap": consts.localeLanguageMap,
				"type": "code"
				},
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("language")
		}] />
</#if>
<#if a.displayField("country")>
	<#assign _columns_ = _columns_ + [{
			"name": "country",
			"header": a.getFieldLabel("country"),
			"format": {
				"codemap": consts.localeCountryMap,
				"type": "code"
				},
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("country")
		}] />
</#if>
<#if a.displayField("source")>
	<#assign _columns_ = _columns_ + [{
			"name": "source",
			"header": a.getFieldLabel("source"),
			"hidden": true,
			"sortable": false,
			"tooltip": a.getFieldTooltip("source")
		}] />
</#if>
<#if a.displayField("status")>
	<#assign _columns_ = _columns_ + [{
			"name": "status",
			"header": a.getFieldLabel("status"),
			"format": {
				"codemap": consts.dataStatusMap,
				"type": "code"
				},
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("status")
		}] />
</#if>
<#if a.displayField("uusid")>
	<#assign _columns_ = _columns_ + [{
			"name": "uusid",
			"header": a.getFieldLabel("uusid"),
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("uusid")
		}] />
</#if>
<#if a.displayField("utime")>
	<#assign _columns_ = _columns_ + [{
			"name": "utime",
			"header": a.getFieldLabel("utime"),
			"format": {
				"type": "datetime"
				},
			"hidden": false,
			"sortable": false,
			"tooltip": a.getFieldTooltip("utime")
		}] />
</#if>

	<@p.listview id="resource_bdelete"
		action="~/bdelete_execute" method="post"
		list=result columns=_columns_ cssColumn="status"
		cssTable="table-hover table-striped"
	/>
	
	<br/>
	<div class="p-tcenter">
		<@p.submit icon="icon-bdelete-execute" onclick="return resource_bdelete_submit();" label="#(button-bdelete-execute)"/>

		<@p.a btn="default" icon="icon-back" href="javascript:window.history.back()" label="#(button-back)"/>

		<script type="text/javascript"><!--
			function resource_bdelete_submit() {
				return plv_submitCheckedKeys('resource_bdelete');
			}
			
			function onPageLoad() {
				plv_checkAll('resource_bdelete');
			}
		--></script>
	</div>
<#else>
	<div class="p-tcenter">
		<@p.a btn="default" icon="back" href="#" onclick="window.history.back();return false;" label="#(button-back)"/>
	</div>
</#if>
</div>

</body>
</html>