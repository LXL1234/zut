<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta http-equiv="Cache-Control" content="no-siteapp" />
<!--[if lt IE 9]>
<script type="text/javascript" src="lib/html5shiv.js"></script>
<script type="text/javascript" src="lib/respond.min.js"></script>
<![endif]-->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/h-ui/css/H-ui.min.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/h-ui.admin/css/H-ui.admin.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/lib/Hui-iconfont/1.0.8/iconfont.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/h-ui.admin/skin/default/skin.css"
	id="skin" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/h-ui.admin/css/style.css" />
<!--[if IE 6]>
<script type="text/javascript" src="lib/DD_belatedPNG_0.0.8a-min.js" ></script>
<script>DD_belatedPNG.fix('*');</script>
<![endif]-->
<title>角色管理</title>
</head>
<body>
	<nav class="breadcrumb">
		<i class="Hui-iconfont">&#xe67f;</i> 首页 <span class="c-gray en">&gt;</span>
		管理员管理 <span class="c-gray en">&gt;</span> 角色管理 <a
			class="btn btn-success radius r"
			style="line-height: 1.6em; margin-top: 3px"
			href="javascript:location.replace(location.href);" title="刷新"><i
			class="Hui-iconfont">&#xe68f;</i></a>
	</nav>
	<div class="page-container">
		<div class="pd-20">
			<div class="text-c">
				日期范围： <input type="text"
					onfocus="WdatePicker({maxDate:'#F{$dp.$D(\'datemax\')||\'%y-%M-%d\'}'})"
					id="datemin" class="input-text Wdate" style="width: 120px;">
				- <input type="text"
					onfocus="WdatePicker({minDate:'#F{$dp.$D(\'datemin\')}',maxDate:'%y-%M-%d'})"
					id="datemax" class="input-text Wdate" style="width: 120px;">
				<input type="text" class="input-text" style="width: 250px"
					placeholder="输入会员名称、电话、邮箱" id="" name="">
				<button type="submit" class="btn btn-success" id="" name="">
					<i class="icon-search"></i> 搜用户
				</button>

			</div>

			<div class="cl pd-5 bg-1 bk-gray">
				<span class="l"> <a href="javascript:;" onclick="datadel()"
					class="btn btn-danger radius"><i class="Hui-iconfont">&#xe6e2;</i>
						删除记录</a> <a class="btn btn-primary radius" href="javascript:;"
					onclick="admin_role_add('添加角色','admin-role-add.html','800')"><i
						class="Hui-iconfont">&#xe600;</i> 添加记录</a>
				</span> <span class="r">共有数据：<strong>54</strong> 条
				</span>
			</div>
			<table class="table table-border table-bordered table-hover table-bg">
				<thead>
					<tr>
						<th scope="col" colspan="6">论文管理</th>
					</tr>
					<tr class="text-c">
						<th width="25"><input type="checkbox" value="" name=""></th>
						<th width="40">ID</th>
						<th width="200">角色名</th>
						<th>用户列表</th>
						<th width="300">描述</th>
						<th width="70">操作</th>
					</tr>
				</thead>
				<tbody>


					<c:forEach items="${paperList}" var="paper">

						<tr>
							<td><input name="ids" type="checkbox"></td>
							<td>${paper.paperinfo_Id }</td>
							<td>${paper.paperinfo_Author }</td>
							<td>${paper.paperinfo_Name }</td>
							<td>${paper.paperinfo_CN }</td>
							<td>${paper.paperinfo_ISSN }</td>
							<td class="text-center">${paper.paperinfo_Time }</td>
							<td>${paper.paperinfo_authorlev }</td>
							<td class="text-center">${paper.paperinfo_orglev }</td>
							<td class="text-center">
								<button type="button" class="btn bg-olive btn-xs">订单</button>
								<button type="button" class="btn bg-olive btn-xs">详情</button>
								<button type="button" class="btn bg-olive btn-xs">编辑</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!--请在下方写此页面业务相关的脚本-->
		<script type="text/javascript"
			src="lib/datatables/1.10.0/jquery.dataTables.min.js"></script>
		<script type="text/javascript">
			/*管理员-角色-添加*/
			function admin_role_add(title, url, w, h) {
				layer_show(title, url, w, h);
			}
			/*管理员-角色-编辑*/
			function admin_role_edit(title, url, id, w, h) {
				layer_show(title, url, w, h);
			}
			/*管理员-角色-删除*/
			function admin_role_del(obj, id) {
				layer.confirm('角色删除须谨慎，确认要删除吗？', function(index) {
					$.ajax({
						type : 'POST',
						url : '',
						dataType : 'json',
						success : function(data) {
							$(obj).parents("tr").remove();
							layer.msg('已删除!', {
								icon : 1,
								time : 1000
							});
						},
						error : function(data) {
							console.log(data.msg);
						},
					});
				});
			}
		</script>
</body>
</html>