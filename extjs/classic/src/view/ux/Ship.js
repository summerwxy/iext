
Ext.define('iext.view.ux.Ship', {  
    extend: 'Ext.panel.Panel',
    alias : 'widget.ship',  
    title: '出货单管理',
    iconCls: 'fa-truck',
    layout: 'fit',
    items: [{
        xtype: 'component',
        autoEl: {
            tag: 'iframe',
            style: 'border: none',
            src: 'http://192.168.0.45:3000/imis/xout/page1'
        }
    }]
});
