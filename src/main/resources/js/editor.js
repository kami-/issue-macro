AJS.bind("init.rte", function() {
    AJS.MacroBrowser.setMacroJsOverride('my-macro', {
        opener: function(macro) {
            tinymce.confluence.macrobrowser.macroBrowserComplete({name: "my-macro", "bodyHtml": undefined, "params": {}});
        }
    });
});