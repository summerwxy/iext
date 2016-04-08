
Ext.define('iext.view.main.AccordionMenu', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accordionmenu',

    layout: {
        type: 'accordion',
        animate: true
    },

    viewModel: 'main',

    title: '功能菜单',
    
    initComponent: function() {
        this.items = [];
        var menus = this.getViewModel().get('menu');
        for (var i in menus) {  
            var menugroup = menus[i];  
            var accpanel = {  
                menuAccordion: true,  
                xtype: 'panel',  
                title: menugroup.text,  
                layout: 'fit',  
                dockedItems: [{  
                    dock: 'left',  
                    xtype: 'toolbar',  
                    items: []  
                }] 
            };  
            for (var j in menugroup.items) {  
                var menumodule = menugroup.items[j];  
                accpanel.dockedItems[0].items.push({  
                    xtype: 'button',  
                    text: this.addSpace(menumodule.text, 12),  
                    iconCls: menumodule.iconCls,
                    widgetName: menumodule.widgetName, // widgetName 是自己随便取的, 可以在 DOM 里面取得
                    handler : 'openFuncTab'  
                });  
            }  
            this.items.push(accpanel);  
        }  
        this.callParent(arguments);
    },

    addSpace: function(text, len) {  
        var result = text;  
        for (var i = text.length; i < len; i++) {  
            result += '　';  
        }  
        return result;  
    }   

});
