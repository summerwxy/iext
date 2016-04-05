
Ext.define('iext.view.ux.Room', {  
    extend: 'Ext.panel.Panel',
    alias : 'widget.room',  
    title: '会议室管理',
    iconCls: 'fa-calendar',
    layout: 'fit',
    items: [{
        xtype: 'component',
        autoEl: {
            tag: 'iframe',
            style: 'border: none',
            src: 'http://192.168.0.45:3000/imis/calendar/index_g3'
        }
    }]
});
