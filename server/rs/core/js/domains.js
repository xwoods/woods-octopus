/**
 * Created by pw on 14-7-28.
 */
mainApp.controller('DomainsCtl', function ($scope) {

    var module = $('.module-domains');

    var dgridJq = $('.domain-table');
    var dformJq = $('.domain-form');


    dgridJq.zgrid({
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
                url: "/domain/query",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "name",
                    "columnName": "域名称",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "alias",
                    "columnName": "域别名",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "about",
                    "columnName": "域信息",
                    "show": true,
                    "width": 600
                }
            ]
        }
    });

    var addForm = $('.domain-add');

    addForm.delegate('input', 'change', function () {
        var input = $(this);
        var initem = input.parent();
        var inval = input.val();
        if ($z.util.isBlank(inval)) {
            initem.removeClass('ok').removeClass('ing').removeClass('fail');
        } else {
            // 需要进行检查
            initem.removeClass('ok').removeClass('fail').addClass('ing');
            $z.http.get("/domain/checkExist", {
                'field': input.attr("name"),
                'value': inval
            }, function (re) {
                if (re.data) {
                    initem.removeClass('ing').removeClass('ok').addClass('fail');
                } else {
                    initem.removeClass('ing').removeClass('fail').addClass('ok');
                }
            });
        }
    });

    addForm.delegate('.form-submit.ok', 'click', function () {
        var btn = $(this);
        var inputName = addForm.find('input[name=name]');
        var inputAlias = addForm.find('input[name=alias]');
        var inputAbout = addForm.find('textarea');
        if (inputName.parent().hasClass('ok') && inputAlias.parent().hasClass('ok')) {
            $z.http.post("/domain/register", {
                'name': inputName.val(),
                'alias': inputAlias.val(),
                'about': inputAbout.val()
            }, function (re) {
                dgridJq.zgrid('refresh');
                btn.next().click();
            });
        } else {
            alert('信息不完整或不正确, 请完善后再提交');
        }
    });

    addForm.delegate('.form-submit.cancel', 'click', function () {
        var inputName = addForm.find('input[name=name]');
        var inputAlias = addForm.find('input[name=alias]');
        var inputAbout = addForm.find('textarea');
        inputName.parent().removeClass('ok').removeClass('ing').removeClass('fail');
        inputAlias.parent().removeClass('ok').removeClass('ing').removeClass('fail');
        inputName.val('');
        inputAlias.val('');
        inputAbout.val('');
    });
});
