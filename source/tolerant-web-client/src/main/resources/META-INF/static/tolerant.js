var editors = {
/*
    source : null,
    result : null,
    report : null,
    targets : null,
    sourceTabs : null,
    resultTabs : null, */




    init : function(){
        this.targets = $("#target-type");

        this.sourceTabs = $( "#source-tabs" ).tabs();
        this.resultTabs = $( "#result-tabs" ).tabs();

        this.loadTargets();
        this.initEditors();

        $('#compile').click(function(event){
            event.preventDefault();
            editors.convert();
        })

    },

    initEditors : function() {
        this.source = ace.edit("code-editor");
        this.source.setTheme("ace/theme/tomorrow");
        this.source.getSession().setMode("ace/mode/xml");
        this.source.on("change", editors.convert)

        this.result = ace.edit("result-editor");
        this.result.setTheme("ace/theme/tomorrow");
        this.result.getSession().setMode("ace/mode/xml");

        this.report = ace.edit("report");
        this.report.setTheme("ace/theme/tomorrow");
        this.report.getSession().setMode("ace/mode/text");

    },

    insertTargets : function(data) {
        console.log("targets received")
        console.log(data)
        editors.targets.empty(); // remove old options

        $.each(data, function(key,value) {
          editors.targets.append($("<option></option>")
             .attr("value", value.key).text(value.name));
        });
    },

    loadTargets : function(){
        $.ajax({
            url: '/api/convert/targets',
            type: 'GET',
            dataType:'json',
            contentType:'application/json',
            success: this.insertTargets,
            error: function(){
                    $('#notification-bar').text('An error occurred');
            }
        })
    },

    clearEditors : function(){
        editors.report.getSession().setValue("");
        editors.result.getSession().setValue("");
    },

    insertConvertResult: function(data) {
        editors.clearEditors();

        console.log("received");

        if(data.error){
            editors.resultTabs.tabs("option", "active", 0);
            console.log("Error reported, only displaying error")
            editors.report.getSession().setValue(data.error);
            editors.resultTabs.tabs( "disable", 1 );
        } else {
            editors.resultTabs.tabs( "enable", 1 );
            editors.resultTabs.tabs( "option", "active", 1);

            editors.report.getSession().setValue(data.report);
            editors.result.getSession().setValue(data.content);
        }

    },

    convert: function() {

        if (!editors.isConverting){
            editors.isConverting = true;

            var data = {
                'content':editors.source.getSession().getValue(),
                'target':editors.targets.val()
            }


            $.ajax({
                url: '/api/convert/request',
                type: 'POST',
                data: JSON.stringify(data),
                dataType: 'json',
                contentType:'application/json',
                success: function(data){
                    editors.isConverting = false;
                    editors.insertConvertResult(data)
                    if (editors.isReconvert){
                        console.log("Reconvert")
                        editors.isReconvert = false;
                        editors.convert();
                    }
                },
                error: function() {
                    editors.isConverting = false;
                    if (editors.isReconvert){

                        editors.isReconvert = false;
                        editors.convert();
                    }

                    $('#notification-bar').text('An error occurred');
                }
            });
        } else {
            console.log("Already converting, adding reconvert")
            editors.isReconvert = true;
        }

    }



}

$(document).ready(function() {
    editors.init()
})


