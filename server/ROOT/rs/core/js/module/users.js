/**
 * Created by pw on 14-7-20.
 */
mainApp.controller('UsersCtl', function ($scope) {

    var module = $('.module-users');

    var ugridJq = $('.user-table');

    ugridJq.zgrid({
        header: {
        },
        table: {
            pager: {
                pgsz: 20,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: '',
                asc: true
            },
            query: {
                url: "/user/query",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "name",
                    "columnName": "用户名",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "alias",
                    "columnName": "昵称",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "phone",
                    "columnName": "电话",
                    "show": true,
                    "width": 150
                }, {
                    "fieldName": "email",
                    "columnName": "邮箱",
                    "show": true,
                    "width": 150
                },{
                    "fieldName": "lastLogin",
                    "columnName": "最后登录时间",
                    "show": true,
                    "width": 200
                },{
                    "fieldName": "lastIP",
                    "columnName": "最后登录ip",
                    "show": true,
                    "width": 200
                }
            ]
        }
    });
});
