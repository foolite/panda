<html>
<head>
	<title><@p.text name="title-print"><@p.param name="title" value="#(title)"/></@p.text></title>
</head>
<body>

<div class="p-section">
	<div class="p-header">
		<ol class="breadcrumb">
			<li><@p.i icon="icon"/> <@p.text name="title"/></li>
			<li class="active"><@p.text name="step-print"/></li>
		</ol>
	</div>
<#assign _well = a.getText("well-print", "")/>
<#if _well?has_content>
	<div class="p-well">${_well}</div>
</#if>

	<#include "/action-alert.ftl"/>

<#if r??>
	<@p.form cssClass="p-vform" id="media" theme="bs3h">
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
				defaultAction="mediaview"
				defaultParams="!{'id': '%{r.id}'}"
				defaultEnable="%{r.id != null && r.size > 0}"
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
	</@p.form>
</#if>
</div>

</body>
</html>
