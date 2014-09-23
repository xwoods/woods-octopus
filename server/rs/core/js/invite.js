mainApp.controller('InviteCtl', function ($scope) {

    $scope.userName = "";
    $scope.isMale = "true";
    $scope.domainList = "";
    $scope.domainNameList = "";

    function addFontColor(str, color) {
        return '<font style="color:' + color + '">' + str + '</font>';
    };

    var module = $('.module-invite');
    var inviteJq = module.find('.invite-table');
    var dmnlistJq = module.find('.domain-list');
    var iurl = module.find('.invite-url');

    $z.http.get("/domain/list", function (re) {
        var dl = re.data;
        var shtml = '';
        for (var i = 0; i < dl.length; i++) {
            shtml += '<option value="' + dl[i].name + "," + dl[i].alias + '">' + dl[i].alias + '</option>';
        }
        dmnlistJq.html(shtml);
    });

    module.delegate('.domain-add', 'click', function () {
        var dsel = dmnlistJq.val();
        var dn = dsel.split(',');
        if ($scope.domainList.indexOf(dn[0]) != -1) {
            return;
        }
        $scope.domainList = $scope.domainList + ($z.util.isBlank($scope.domainList) ? "" : ",") + dn[0];
        $scope.domainNameList = $scope.domainNameList + ($z.util.isBlank($scope.domainNameList) ? "" : ",") + dn[1];
        $scope.$apply();
    });


    module.delegate('.invite-add', 'click', function () {
        var ir = {
            "userName": $scope.userName,
            "isMale": $scope.isMale,
            "domainList": $scope.domainList,
            "domainNameList": $scope.domainNameList,
            "hasReg": false
        };
        $z.http.post('/user/invite/add', ir, function () {
            inviteJq.zgrid('refresh');
            $scope.isMale = "true";
            $scope.userName = "";
            $scope.domainList = "";
            $scope.domainNameList = "";
            $scope.$apply();
        });
    });


    module.delegate('.show-invite-url', 'click', function () {
        var cu = $(this);
        var rid = cu.attr('rid');
        var unm = cu.html();
        iurl.find('b').html(unm);
        iurl.find('span').html(window.location.host + "/login#icode=" + rid);
    });

    inviteJq.zgrid({
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
                url: "/user/invite/list",
                kwd: ""
            },
            columnsRender: {
                'isMale': function (rowData) {
                    return rowData.isMale ? addFontColor("男", "#00f") : addFontColor("女", "#f00");
                },
                'hasReg': function (rowData) {
                    return rowData.hasReg ? addFontColor("已用", "#000") : addFontColor("可用", "#080");
                },
                "userName": function (rowData) {
                    return '<span class="show-invite-url" rid="' + rowData.id + '">' + rowData.userName + "</span>"
                }
            },
            columns: [
                {
                    "fieldName": "hasReg",
                    "columnName": "已使用",
                    "show": true,
                    "width": 50
                },
                {
                    "fieldName": "useTime",
                    "columnName": "使用时间",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "regName",
                    "columnName": "注册名称",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "userName",
                    "columnName": "邀请对象",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "isMale",
                    "columnName": "性别",
                    "show": true,
                    "width": 50
                },
                {
                    "fieldName": "domainList",
                    "columnName": "加入域",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "domainNameList",
                    "columnName": "加入域(显示)",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "createUser",
                    "columnName": "邀请人",
                    "show": true,
                    "width": 150
                }
            ]
        }
    });

});
