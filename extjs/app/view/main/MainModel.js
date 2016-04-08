/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('iext.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: 'iext',

        loremIpsum: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',

        menu: [{
            text: '常用功能',
            expanded: true,
            items: [{
                text: '会议室管理',
                iconCls: 'fa fa-calendar',
                widgetName: 'room'
            }, {
                text: '出货单管理',
                iconCls: 'fa fa-truck',
                widgetName: 'ship'
            }, {
                text: '二维码管理',
                iconCls: 'fa fa-qrcode',
                widgetName: 'qrcode'
            }, {
                text: '回收券管理',
                iconCls: 'fa fa-money',
                widgetName: 'ticket'
            }]
        }, {
            text: 'POS功能',
            expanded: true,
            items: []
        }]

    },

    //TODO - add data, formulas and/or methods to support your view

});
