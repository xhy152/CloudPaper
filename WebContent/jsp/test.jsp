
<html> 


<head></head> 


<body> 


<div id="container"> 


 <div id="dragsource"> 


 <p>拽我!</p> 


 </div> 


 </div><!-- End container --> 


   


 <div id="droppalbe" style="width: 300px;height:300px;background-color:gray"> 


 <p>Drop here</p> 


 </div> 


   


 <script type="text/javascript" src="js/jquery-1.7.min.js"></script> 


 <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script> 


 <script> 


 $(function() { 


 $( "#dragsource" ).draggable(); 


 $( "#droppable" ).droppable(); 


 }) 


 </script> 


 </body> 


 </html> 
