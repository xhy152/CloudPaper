<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Menu-Tree</title>
<!-- <script  type="text/javascript" src="codebase/jquery-1.3.2.min.js"></script> -->
<script src="http://libs.baidu.com/jquery/1.5.2/jquery.min.js"></script>
<script type = "text/javascript" src=js/jquery.treeview.js></script>
<script type = "text/javascript" src=js/jquery.contextmenu.r2.js></script>
<script type = "text/javascript" src=js/menu_tree.js></script>
<script type = "text/javascript" src=js/jquery.cookie.js></script>
<link rel="stylesheet" type="text/css" href="css/menu_tree.css"/>
<link rel="stylesheet" type="text/css" href="codebase/GooUploader.css"/>
<script  type="text/javascript" src="codebase/GooUploader.js"></script>
<script type="text/javascript" src="codebase/swfupload/swfupload.js"></script>
</head> 
<body> 
<div class="menu">
<ul id="tree" class="filetree treeview-famfamfam">
</ul>
</div>
<div class="contextMenu" id="folderMenu">
   <ul>
     <li id="addFolder">添加文件夹</li>
     <li id="deleteFolder">删除文件夹</li>
     <li id="renameFolder">重命名文件夹</li>
     <li id="uploadFile">上传文件</li>
   </ul>
</div>

<div class="contextMenu" id="fileMenu">
   <ul>
     <li id="deleteFile">删除文件</li>
     <li id="renameFile">重命名文件</li>
<!--      <li id="exportFile">导出文件</li> -->
   </ul>
</div>

<div id="overlay" class="black_overlay">
</div>

</body> 
</html>