
Ext.define('iext.view.ux.Header', {  
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.mainheader',  
    items: [{
        xtype: 'label',
        text: 'iwill 爱维尔',
        style: 'font-size: 20px; color: blue;'
    }, '->', {
        text: '主页'
    }]
});
