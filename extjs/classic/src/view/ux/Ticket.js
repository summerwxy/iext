
Ext.define('iext.view.ux.Ticket', {  
    extend: 'Ext.panel.Panel',
    alias : 'widget.ticket',  
    title: '回收券检查',
    iconCls: 'fa-money',
    layout: 'fit',
    items: [{
        xtype: 'component',
        autoEl: {
            tag: 'iframe',
            style: 'border: none',
            src: 'http://192.168.0.45:3000/imis/pos/f1'
        }
    }]
});
