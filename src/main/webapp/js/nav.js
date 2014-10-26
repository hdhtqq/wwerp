IEPNGFix.blankImg = '/iepngfix/blank.gif';
	$(function() {
		$("#nav_yxon_sys").hover(function() {
			$(this).attr("src", "/img/yxon_sys.png");
		}, function() {
			$(this).attr("src", "/img/yxon_sys_g.png");
		});
		$("#nav_yxon_client").hover(function() {
			$(this).attr("src", "/img/yxon_client.png");
		}, function() {
			$(this).attr("src", "/img/yxon_client_g.png");
		});
		$("#nav_yxon_login").hover(function() {
			$(this).attr("src", "/img/yxon_login.png");
		}, function() {
			$(this).attr("src", "/img/yxon_login_g.png");
		});
		
		$("#weixin_icon").hover(function() {
			var c = $("#weixin_erweima");
			c.stop(false, true);
			c.fadeIn(2000, function() {
				c.css("display", "inline");	
			});
		}, function() {
			var c = $("#weixin_erweima");
			c.stop(false, true);
			c.fadeOut(2000, function() {
				c.css("display", "none");			
			});
		});
	});