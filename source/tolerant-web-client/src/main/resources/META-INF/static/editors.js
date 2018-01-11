let sourceEditor;
let resultEditor;

$(document).ready(function() {

    sourceEditor = ace.edit("code-editor");
    sourceEditor.setTheme("ace/theme/tomorrow");
    sourceEditor.getSession().setMode("ace/mode/xml");

    resultEditor = ace.edit("result-editor");
    resultEditor.setTheme("ace/theme/tomorrow");
    resultEditor.getSession().setMode("ace/mode/xml");
})

