/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('iext.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',


    requires: [
        'iext.view.ux.*'
    ],

    openFuncTab: function(btn, evt) {
        var tabs = Ext.getCmp('tabs');
        var id = btn.widgetName;
        var name = Ext.util.Format.trim(btn.text);
        var tabIndex;
        // 找看看有沒有已經開過了
        for (var i = 0; i < tabs.items.length; i++) { 
            if (tabs.items.get(i).id == 'tab-' + id) {
                tabIndex = i;
            }
        }
        // 沒開過才新建
        if (Ext.isEmpty(tabIndex)) {
            tabs.add({
                title: name,
                id: 'tab-' + id,
                iconCls: btn.iconCls,
                closable: true,
                xtype: btn.widgetName
            });
            tabIndex = tabs.items.length - 1;
        }
        // 設定 active
        tabs.setActiveTab(tabIndex);
    },

    onItemSelected: function (sender, record) {
        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
    },

    onConfirm: function (choice) {
        if (choice === 'yes') {
            //
        }
    }
});
