<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-cn">
<head>
<title>秒杀详情页</title>
<%@include file="common/head.jsp"%>
</head>
<body>
	<div class="container">
		<div class="panel panel-default text-center">
			<div class="panel-heading">
				<h1>${seckill.name}</h1>
			</div>
			<div class="panel-body">
				<h2 class="text-denger">
					<%--显示time图标--%>
					<span class="glyphicon glyphicon-time"></span>
					<%--展示倒计时--%>
					<span class="glyphicon" id="seckill-box"></span>
				</h2>
			</div>
		</div>
	</div>
	<div id="killPhoneModal" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<span class="glyphicon glyphicon-phone"></span> 秒杀电话:
					</h3>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-xs-8 col-xs-offset-2">
							<input type="text" name="killPhone" id="killPhoneKey" placeholder="填手机号^o^" class="form-control" value="13511111111" />
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<!-- 验证信息 -->
					<span id="killPhoneMessage" class="glyphicon"></span>
					<button type="button" id="killPhoneBtn" class="btn btn-success">
						<span class="glyphicon glyphicon-phone"></span> Submit
					</button>
				</div>
			</div>
		</div>
	</div>

</body>

<%--使用CDN 获取公共JS http://www.bootcdn.cn--%>
<%--jQuery cookie操作插件--%>
<script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<%--jQuery countDown倒计时插件--%>
<script src="//cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<%--开始编写交互逻辑--%>
<script src="/seckill/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function () {
		//使用EL表达式传入参数
	    seckill.detail.init({
	                            seckillId:${seckill.seckillId},
	                            startTime:${seckill.startTime.time},//毫秒
	                            endTime:${seckill.endTime.time}
	                        });
	});
</script>
</html>