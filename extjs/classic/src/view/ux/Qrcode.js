
Ext.define('iext.view.ux.Qrcode', {  
    extend: 'Ext.panel.Panel',
    alias : 'widget.qrcode',  
    title: '二维码快递',
    iconCls: 'fa-qrcode',
    layout: 'fit',
    items: [{
        xtype: 'component',
        autoEl: {
            tag: 'iframe',
            style: 'border: none',
            src: 'http://192.168.0.45:3000/imis/market/list_mooncake_g3'
        }
    }]
});
