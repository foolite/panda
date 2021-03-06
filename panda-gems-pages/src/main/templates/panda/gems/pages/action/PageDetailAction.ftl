<html>
<head>
	<title>${r.title?html}</title>
</head>

<body>

<style>
.page-icatch img {
	width: 100%;
}
.page-title {
	text-align: center;
}
.page-meta {
	text-align: right;
}
.page-content {
	padding: 10px;
}
</style>

<div class="p-section" id="a_page">
<#if r.thumbnail?has_content>
	<@p.url var="mp" action="%{!!(b.media_path)|||'/media'}"/>
	<div class="page-icatch"><img src="${vars.mp}/media/${r.thumbnail}"></div>
</#if>

	<h1 class="page-title">${r.title?html}</h1>
	<div class="page-meta">
		<span class="page-date"><@p.property value=r.publishDate format="date"/></span>
	<#assign ts = (a.getTags(r))![] />
	<#if ts?has_content>
		<@p.url var="pp" action="%{!!(b.pages_path)|||'/pages'}"/>
		<span class="page-tags">
		<#list ts as t>
			<@p.a cssClass="btn btn-xs btn-info" action="${vars.pp}/tag"><@p.param name="tag" value=t/>#${t?html}</@p.a>
		</#list>
		<span>
	</#if>
	</div>
	<div class="page-content">${r.content}</div>
</div>

</body>
</html>
