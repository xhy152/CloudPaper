<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="en-GB" >
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login CloudPaper</title>

	<script src="${pageContext.request.contextPath}/js/login/part1.js"></script>

  <link rel="shortcut icon" href="images/paper.ico">
  <link rel="stylesheet"  type="text/css" href="${pageContext.request.contextPath}/css/login.css">

      <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries (Do not modify the order) -->
      <!--[if lt IE 9]>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="//html5base.googlecode.com/svn-history/r38/trunk/js/selectivizr-1.0.3b.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/es5-shim/4.0.3/es5-shim.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->
      <!-- Optimizely for mendeley.com -->
      <script src="//cdn.optimizely.com/js/238413261.js"></script>
      <!-- GA for mendeley.com -->
      <script>
	      (function(i, s, o, g, r, a, m) {
	    	  i['GoogleAnalyticsObject'] = r;
	    	  i[r] = i[r] ||
	    	  function() { (i[r].q = i[r].q || []).push(arguments)
	    	  },
	    	  i[r].l = 1 * new Date();
	    	  a = s.createElement(o),
	    	  m = s.getElementsByTagName(o)[0];
	    	  a.async = 1;
	    	  a.src = g;
	    	  m.parentNode.insertBefore(a, m)
	    	})(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');
	    	ga('create', 'UA-3874710-1', 'auto');
	    	ga('send', 'pageview');
      </script>
    <!-- SiteCatalyst -->
    <script src="//assets.adobedtm.com/376c5346e33126fdb6b2dbac81e307cbacfd7935/satelliteLib-4a7497b2b1d1900fe42ef2c13e32daeedf9c1642.js"></script>
    <!-- End SiteCatalyst -->
  </head>
  <body>
    <div class="page-onboarding">
  <div class="header">
    <header class="masthead reversed-nav">
      <div class="with-container">

		<a href="${pageContext.request.contextPath}/jsp/login.jsp" title="CloudPaper" class="button-secondary">论文云薄</a>
        <ul class="navigation-list nav-visible">
          <li class="button-group">
            <a href="${pageContext.request.contextPath}/jsp/register.jsp" class="button-secondary">注册新用户</a>
          </li>
        </ul>

      </div>
    </header>
  </div>
      <section class="content with-container">
        <div class="row full center">
<div class="sign-in-page column l-6-12 m-8-12 s-10-12 xs-1-1">
  <div class="card-wrapper js-toggle-wrapper">
    <h1 style="text-align:center">登  录</h1>
    <div class="card">
        <div class="row">
          <div class="column d-1-1">
            <div class="input-wrap js-animated-labels">
              <input type="text" id="username" data-pattern="\S" name="username" value="" autofocus="" onclick="document.getElementById('username_ret').style.display='none'">
              <label for="username" class="focused">账号</label>
            </div>
            <label id="username_ret" style="display:none">1111</label>
            <div data-for="username" class="error-message" data-error-empty="账号未填写" data-error-invalid="Not a valid email. Take a closer look."></div>
          </div>
        </div>

        <div class="row">
          <div class="column d-1-1">
            <div class="input-wrap js-animated-labels ">
              <input type="password" data-pattern=".{5,}" id="password" name="password" onclick="document.getElementById('password_ret').style.display='none'">
              <label for="password" class="focused">密码</label>
            </div>
            <label id="password_ret" style="display:none"></label>
            <div data-for="password" class="error-message" data-error-empty="密码未填写" data-error-invalid="Password must be at least 5 characters.">
            </div>
          </div>
        </div>

		 <script type="text/javascript">
		 	function userSignIn(){
		 	    var username = $("#username").val();
		 	    var password = $("#password").val();
// 		 	    alert(username);
// 		 	    alert(password);
		 	    if(username.length!=0 && password.length>=6){
			 		$.ajax({
			 			url : "user_userSignIn",
			 			data : {
			 			    "username" : username,
			 			    "password" : password
			 			},
			 			dataType : 'json',
			 			type : 'post',
			 			async : false,
			 			success : function(data) {
			 			    if(data=="nousername"){
				 				document.getElementById("username_ret").innerText="用户名不存在";
					 			$("#username_ret").css("display","block");
			 			    }else if(data=="passworderror"){
				 				document.getElementById("password_ret").innerText="密码错误";
					 			$("#password_ret").css("display","block");
			 			    }else{
			 			      
			 			       window.location.href="/CloudPaper/jsp/index.jsp";
			 			    }
			 			},
			 			error : function() {
			 			    alert("login error");
			 			}
			 		});
		 	    }else{
			 		if(username.length==0){
			 			document.getElementById("username_ret").innerText="用户名不能为空";
			 			$("#username_ret").css("display","block");
			 	    }
			 	    if(password.length==0){
				 		document.getElementById("password_ret").innerText="密码不能为空";
			 			$("#password_ret").css("display","block");
			 	    }else if(password.length<6){
			 			document.getElementById("password_ret").innerText="密码至少为6位";
		 				$("#password_ret").css("display","block");
			 	    }
		 	    }
		 	}
    	 </script>

        <div class="row remember-me-row">
          <div class="column d-1-1">
<!--             <div class="checkbox-wrapper"> -->
<!--               <input type="checkbox" id="remember_me" name="remember_me" checked/> -->
<!--               <label for="remember_me" id="remember_me_label">记住账号</label> -->
<!--             </div> -->
            <button class="button-primary with-icon-after icon-navigateright float-right submit" id="signin-form-submit" onclick="userSignIn()">登 录</button>
          </div>
        </div>
      
    </div>
    <p class="text-center footnote"> 没有账号？ <a id="create-free-account" href="${pageContext.request.contextPath}/jsp/register.jsp">注册新用户</a></p>
  </div>
</div>
        </div>
      </section>

        <script src="//static.mendeley.com/lib-js-analytics/live/common-analytics.js" async></script>
    </div>

  <!--[if (gte IE 9) | (!IE)]><!-->
    <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
  <!--<![endif]-->
  <script src="//static.mendeley.com/weblet-account/build/420/js/sign-in.js"></script>
    <!-- SiteCatalyst -->
    <script type="text/javascript">
      var pageData = {
        page: {
          businessUnit: 'els:rp:st',
          language: 'en',
          loadTimeStamp: '1509801315857',
          name: 'sign-in',
          productName: 'md',
          environment: 'prod',
          noTracking: 'false',
          type: 'AP-LP'
        },
        visitor: {
          userId: '',
          accountId: '',
          accessType: 'md:guest',
          accountName: '',
          ipAddress: '54.154.141.35'
        },
        form : {
            productName : 'mendeley',
            step : 'start',
            type : 'login'
        }
      };

      if (pageDataTracker && pageDataTracker.trackPageLoad) {
        pageDataTracker.trackPageLoad();
      }
    </script>
    <!-- End SiteCatalyst -->
  </body>
</html>