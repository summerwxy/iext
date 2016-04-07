/**
 * This class is the main view for the application. It is specified in app.js as the
 * "mainView" property. That setting automatically applies the "viewport"
 * plugin causing this view to become the body element (i.e., the viewport).
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('iext.view.main.Main', {
    extend: 'Ext.container.Container',
    xtype: 'app-main',

    requires: [
        'iext.view.main.MainController',
        'iext.view.main.MainModel'
    ],

    controller: 'main',
    viewModel: 'main',
    uses : [
        'iext.view.ux.Header',
        'iext.view.ux.Footer'
    ],  

    layout: {
        type: 'border'
    },

    items: [{
        xtype: 'mainheader',
        region: 'north'
    }, {
        xtype: 'mainfooter',
        region: 'south'
    }, {
        xtype: 'panel',
        region: 'west',
        title: '功能菜单',
        width: 250,
        split: true
    }, {
        xtype: 'tabpanel',
        region: 'center',
        items: [{
            title: '首页',
            html: '欢迎使用 iwill 爱维尔 后台系统'
        }]
    }]
});
