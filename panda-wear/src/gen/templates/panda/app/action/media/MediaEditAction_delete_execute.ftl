<html>
<head>
	<title><@p.text name="title-delete"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li><@p.text name="step-delete"/></li>
			<li class="active"><@p.text name="step-delete-success"/></li>
		</ol>
	</div>
<#assign _well = a.getText("well-delete-success", "")/>
<#if _well?has_content>
	<div class="p-well">${_well}</div>
</#if>


	<div class="p-toolbar-wrap"><ul class="p-toolbar">
<#if a.canAccess("~/list")><li><@p.a action="~/list" icon="icon-list" label="#(btn-list)"/>
</li></#if>	</ul><div class="clearfix"></div></div>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="p-sform" id="media" focusme="true" method="post" theme="bs3h">
	<#if a.displayField("id")>
			<@p.viewfield
				key="id"
				value="%{r.id}"
			/>
	</#if>
	<#if a.displayField("tag")>
			<@p.viewfield
				key="tag"
				value="%{r.tag}"
				list="%{consts.mediaTagMap}"
			/>
	</#if>
	<#if a.displayField("name")>
			<@p.viewfield
				key="name"
				value="%{r.name}"
			/>
	</#if>
	<#if a.displayField("size")>
			<@p.viewfield
				key="size"
				value="%{r.size}"
				format="size"
			/>
	</#if>
	<#if a.displayField("width")>
			<@p.viewfield
				key="width"
				value="%{r.width}"
			/>
	</#if>
	<#if a.displayField("height")>
			<@p.viewfield
				key="height"
				value="%{r.height}"
			/>
	</#if>
	<#if a.displayField("file")>
			<@p.uploader
				key="file"
				value="%{r.file}"
				readonly="true"
				accept="image/*"
				size="30"
				uploadAction="%{b.files_path + '/upload'}"
				uploadName="file"
				dnloadAction="%{b.files_path + '/download'}"
				dnloadName="id"
				defaultAction="media"
				defaultParams="!{'id': '%{r.id}'}"
				defaultEnable="%{r.id != null && r.size > 0}"
			/>
	</#if>
	<#if a.displayField("createdAt")>
			<@p.viewfield
				key="createdAt"
				value="%{r.createdAt}"
				format="datetime"
			/>
	</#if>
	<#if a.displayField("createdBy")>
			<@p.hidden
				name="createdBy"
				value="%{r.createdBy}"
			/>
	</#if>
	<#if a.displayField("createdByUser")>
			<@p.viewfield
				key="createdByUser"
				value="%{r.createdByUser}"
			/>
	</#if>
	<#if a.displayField("updatedAt")>
			<@p.viewfield
				key="updatedAt"
				value="%{r.updatedAt}"
				format="datetime"
			/>
	</#if>
	<#if a.displayField("updatedBy")>
			<@p.hidden
				name="updatedBy"
				value="%{r.updatedBy}"
			/>
	</#if>
	<#if a.displayField("updatedByUser")>
			<@p.viewfield
				key="updatedByUser"
				value="%{r.updatedByUser}"
			/>
	</#if>
			<#assign _buttons_ = [] />
			<#if a.canAccess('~/list')>
				<@p.url var="_u_" action="~/list"/>
				<#assign _buttons_ = _buttons_ + [{
					"icon": "icon-list",
					"text": "btn-list",
					"onclick": "location.href='${vars._u_?js_string}'; return false;"
				}]/>
			</#if>
			<#include "/panda/mvc/view/form-buttons.ftl"/>
			<@form_buttons buttons=_buttons_/>
	</@p.form>
</#if>
</div>

</body>
</html>
