YX = (function() {
	return {
		initForm : function(form, params) {
			for(var n in params){
				$("#" + form + " *[name='" + n + "']").val(params[n]);
				var img = $("#" + n);
				if (img != null)
					img.attr("src", params[n]);
			}
		},
		
		globalInit :function() {
			// 设置输入框样式
			$("input[type=radio]").addClass("radio");
			$("input[type=checkbox]").addClass("checkbox");
			
			
			$("textarea").addClass("text_area");
			// $("input[type=checkbox]").addClass("checkbox");
			$("input[type=button]" ).button();
			$("input[type=submit]" ).button();	
			$("input[type=reset]" ).button();
			// 数据行背景
			$(".table_content_line").hover(function() {
	   			$(this).css("background-color", "#EEE");
			}, function(){
				$(this).css("background-color", "#FFF");
			});	
			
			$("input[type=text]").each(function(){
		        $(this).focus(function(){
		        	var tips = $(this).attr("tip");
		        	if(tips!=null){
		        		var text = $(this).val();
				     	if(text==tips){  
				     	    $(this).val(''); 
				     	  }
		        	}
		        	$(this).removeClass("blur-content");
		     	    $(this).css("color","#000000"); 
				}).blur(function(){
					var tips = $(this).attr("tip");
					if(tips!=null){
						var text=$(this).val(); 
					    if(text==""){
					       $(this).val(tips);
						   $(this).css("color","#CDC5BF");
					    }else{
					       $(this).css("color","#000000");
					    }		    	
					}
			   });
		        $(this).addClass("text_input");		     
		    });
			
			$("input[type=password]").each(function(){
		        $(this).focus(function(){
		        	var tips = $(this).attr("tip");
		        	if(tips!=null){
		        		var text = $(this).val();
				     	if(text==tips){  
				     	    $(this).val(''); 
				     	  }
		        	}
		        	$(this).removeClass("blur-content");
		     	    $(this).css("color","#000000"); 
				}).blur(function(){
					var tips = $(this).attr("tip");
					if(tips!=null){
						var text=$(this).val(); 
					    if(text==""){
					       $(this).val(tips);
						   $(this).css("color","#CDC5BF");
					    }else{
					       $(this).css("color","#000000");
					    }		    	
					}
			   });
		        $(this).addClass("text_input");		     
		    });
			
			// 每页显示多少条
			$("#page_rownum").button({
				icons : {
					secondary : "ui-icon-triangle-1-s"
				}
			}).click(function() {
				var menu = $(this).parent().next().show().position({
					my : "right top",
					at : "right bottom",
					of : this
				});
				$(document).one("click", function() {
					menu.hide();
				});
				return false;
			}).parent().buttonset().next().hide().menu();
		},
		
		selAll : function(checkObjSelector, selAllBoxSelector) {
			$(selAllBoxSelector).click(function(event) {
				var obj = $(checkObjSelector); 
				if (obj != null) {
					if (obj.length != null) {
						for (var i=0; i<obj.length; i++) {
							obj[i].checked = this.checked;
						}					
					} else {
						obj.checked = this.checked;
					}
				}
			});
		},
		
		clearTipValue : function() {
			$("input[type=text]").each(function(){
	        	var tips = $(this).attr("tip");
	        	if(tips!=null){
	        		var text = $(this).val();
			     	if(text==tips)
			     	    $(this).val('');  
	        	}	
			});
		}
	} 
}());

//去掉空格
function Taketrim(str) {
	var reg = /(^\s+)|(\s+$)/g;
	return str.replace(reg, "");
}

//密码验证
function pwdReg(str){				
    var reg = /^[(0-9)|(a-z)|(A-Z)]{6,16}$/ ;
	if(reg.test(str)){
	  return true ;//密码有数字或者字母组成 
	}	
	return false ;		
}	
function isSerAndRep(str){
	var repeat = true ;
	var series_asc = true ;//顺序连续
	var series_desc = true ;//逆序连续 
	var len = str.length;
	for(var i=0;i<len;i++){
		 repeat = repeat && (str.charCodeAt(i) == str.charCodeAt(0));//是否重复 
	}
	for(var i=0;i<len;i++){
		series_asc = series_asc && (str.charCodeAt(i) == Number(str.charCodeAt(i+1)-1));	//正序连续  
		 if(i>=Number(len-2)){
			 break ;//控制循环次数 
		 }
	}
	for(var i=0;i<len;i++){
		series_desc = series_desc && (str.charCodeAt(i) == Number(str.charCodeAt(i+1)+1));	//逆序连续  
		 if(i>=Number(len-2)){
			 break ;//控制循环次数 
		 }			
	}
	 return !(repeat||series_asc||series_desc);
}

//验证超管用户密码 6-16位的字母和数字
function pwdCheck(str){
	var flag = false ;
	var reg1 = /^\d{6,16}$/; //16位数字 
	var reg2 = /^[a-zA-Z]{6,16}$/;//16位字母 
	 if(reg1.test(str)){
		flag = true ;
	 }else if(reg2.test(str)){
		flag = true ;
	} 
	return flag;
}

//去掉所有的html标记
function delHtmlTag(str){
	return str.replace(/<[^>]+>/g,"");
}