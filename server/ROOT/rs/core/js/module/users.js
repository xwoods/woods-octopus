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
                    "width": 120
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
                    "width": 120
                },
                {
                    "fieldName": "email",
                    "columnName": "邮箱",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "lastDomain",
                    "columnName": "最后登录域",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "lastLogin",
                    "columnName": "最后登录时间",
                    "show": true,
                    "width": 180
                },
                {
                    "fieldName": "lastIP",
                    "columnName": "最后登录ip",
                    "show": true,
                    "width": 180
                }
            ]
        }
    });

    var domainSel = $('.doman-sel');
    var userSel = $('.user-in-domain');

    function refreshUserInDomain(dmn) {
        $z.http.get("/domain/user/list", {
            'domain': dmn
        }, function (re) {
            var ulist = re.data;
            var uhtml = '';
            for (var i = 0; i < ulist.length; i++) {
                uhtml += '<li unm="' + ulist[i].name + '">';
                uhtml += '  <span>' + ulist[i].name + '</span>';
                uhtml += '  <span>' + $z.msg(ulist[i].alias) + '</span>';
                uhtml += '</li>';
            }
            userSel.empty().append(uhtml);
        });
    }

    domainSel.on('change', function () {
        var dmn = $(this).val();
        refreshUserInDomain(dmn);
    });


    $z.http.get("/domain/query", function (re) {
        var dlist = re.data.list;
        var shtml = '';
        for (var i = 0; i < dlist.length; i++) {
            shtml += '<option value="' + dlist[i].name + '" >';
            shtml += $z.msg(dlist[i].alias);
            shtml += '</option>';
        }
        domainSel.empty().append(shtml);

        refreshUserInDomain(dlist[0].name);
    });

    var u2dinput = $('.user-to-domain');

    $('.btn-u2d').on('click', function () {
        var unms = u2dinput.val();
        if (!$z.util.isBlank(unms)) {
            var dmn = domainSel.val();
            $z.http.post("/domain/user/add", {
                'users': unms,
                'domain': dmn
            }, function (re) {
                refreshUserInDomain(dmn);
                u2dinput.val('');
            })
        }
    });

});
