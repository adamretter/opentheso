/*
 <script type="text/javascript">
 //<![CDATA[
 function srollToSelected() {
 console.log("\n\n DEBUG srollToSelected -----");
 var treeWidgetVar = PrimeFaces.widgets["treeWidgetVar"];
 console.log("treeWidgetVar : ",treeWidgetVar);
 var selectedElement = treeWidgetVar.jq.find('span .ui-state-highlight');
 if (selectedElement != null && selectedElement != undefined && selectedElement.position() != undefined) {
 var scrollPanel = document.getElementById("divArbreTheso");
 //var height = treeWidgetVar.jq.parent().height();
 console.log("selectedElement : ",selectedElement);
 console.log("scrollPanel : ",scrollPanel);
 scrollPanel.scrollTop =selectedElement.position().top;//test
 console.log("go to : ", selectedElement.position().top);
 //console.log("scrollPanel.height : ",height);
 
 }
 
 }
 //]]>
 </script> 
 */

function srollToSelected() {
    var treeWidgetVar = PrimeFaces.widgets["treeWidgetVar"];
    var selectedElement = treeWidgetVar.jq.find('span .ui-state-highlight');
    if (selectedElement != null && selectedElement != undefined && selectedElement.position() != undefined) {
        var scrollPanel = document.getElementById("divArbreTheso");
        scrollPanel.scrollTop = selectedElement.position().top;

    }

}