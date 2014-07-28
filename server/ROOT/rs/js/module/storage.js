/**
 * Created by pw on 14-7-20.
 */

function currentTime() {
    var date = new Date();
    return date.getFullYear() + "-" + add0(date.getMonth() + 1) + "-" + add0(date.getDate()) +
        "_" + add0(date.getHours()) + ":" + add0(date.getMinutes()) + ":" + add0(date.getSeconds()) + "." + date.getMilliseconds();
}

function add0(num) {
    if (num < 10) {
        return "0" + num;
    } else {
        return "" + num;
    }
}

routeApp.controller('MaterialCtl', function ($scope) {

    var module = $('.module-content');
    var gridJq = module.find('.material-table');

    gridJq.zgrid({
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
                url: "/ieslab/storage/material/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": 'mcode',
                    "columnName": '物料编码',
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": 'name',
                    "columnName": '物料名称',
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": 'model',
                    "columnName": '规格',
                    "show": true,
                    "width": 250
                },
                {
                    "fieldName": 'unit',
                    "columnName": '单位',
                    "show": true,
                    "width": 50
                },
                {
                    "fieldName": 'cateL1',
                    "columnName": '大类',
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": 'cateL2',
                    "columnName": '中类',
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": 'cateL3',
                    "columnName": '小类',
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": 'smanager',
                    "columnName": '库管员',
                    "show": true,
                    "width": 100
                }
            ]
        }
    });

});


routeApp.controller('StorageCtl', function ($scope) {
    var module = $('.module-content');
    var gridJq = module.find('.storage-table');

    var yearHtml = '';
    yearHtml += '<select class="query-year">';
    yearHtml += '   <option value="2014" selected = "selected" >2014</option>';
    yearHtml += '   <option value="2013">2013</option>';
    yearHtml += '   <option value="2012">2012</option>';
    yearHtml += '</select>';

    var monthHtml = '';
    monthHtml += '<select class="query-month">';
    var curMonth = new Date().getMonth() + 1;
    for (var i = 1; i < 13; i++) {
        monthHtml += '  <option value="' + i + '" ' + (curMonth == i ? 'selected = "selected"' : '') + '>' + i + '月份</option>';
    }
    monthHtml += '</select>';

    gridJq.zgrid({
        header: {
            queryArea: [
                {
                    html: yearHtml,
                    clz: 'query-year',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                },
                {
                    html: monthHtml,
                    clz: 'query-month',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                }
            ],
            queryParam: function (params) {
                return {
                    "startDate": params[0] + "." + add0(params[1]) + ".01",
                    "endDate": params[0] + "." + add0(params[1]) + ".31"
                };
            }
        },
        table: {
            pager: {
                pgsz: 20,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: 'impDate',
                asc: false
            },
            query: {
                url: "/ieslab/storage/storage/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "impDate",
                    "columnName": "导入日期",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mcode",
                    "columnName": "物料编码",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mname",
                    "columnName": "物料名称",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "total",
                    "columnName": "库存总量",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "material",
                    "columnName": "材料库位",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "maintenance",
                    "columnName": "维护库位",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "repair",
                    "columnName": "返修库位",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "inferior",
                    "columnName": "次品库位",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "i6",
                    "columnName": "i6受控库位",
                    "show": true,
                    "width": 80
                }
            ]
        }
    });
});

routeApp.controller('StorageInOutCtl', function ($scope) {
    var module = $('.module-content');
    var gridJq = module.find('.storageInOut-table');

    var yearHtml = '';
    yearHtml += '<select class="query-year">';
    yearHtml += '   <option value="2014" selected = "selected" >2014</option>';
    yearHtml += '   <option value="2013">2013</option>';
    yearHtml += '   <option value="2012">2012</option>';
    yearHtml += '</select>';

    var monthHtml = '';
    monthHtml += '<select class="query-month">';
    var curMonth = new Date().getMonth() + 1;
    for (var i = 1; i < 13; i++) {
        monthHtml += '  <option value="' + i + '" ' + (curMonth == i ? 'selected = "selected"' : '') + '>' + i + '月份</option>';
    }
    monthHtml += '</select>';

    gridJq.zgrid({
        header: {
            queryArea: [
                {
                    html: yearHtml,
                    clz: 'query-year',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                },
                {
                    html: monthHtml,
                    clz: 'query-month',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                }
            ],
            queryParam: function (params) {
                return {
                    "startDate": params[0] + "." + add0(params[1]) + ".01",
                    "endDate": params[0] + "." + add0(params[1]) + ".31"
                };
            }
        },
        table: {
            pager: {
                pgsz: 20,
                pgnm: 1,
                pgcount: 0,
                total: 0
            },
            order: {
                by: 'impDate',
                asc: false
            },
            query: {
                url: "/ieslab/storage/storageInOut/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "impDate",
                    "columnName": "导入日期",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mcode",
                    "columnName": "物料编码",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mname",
                    "columnName": "物料名称",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "docNo",
                    "columnName": "单据号",
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": "batchNo",
                    "columnName": "批号",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "itemNo",
                    "columnName": "货号",
                    "show": true,
                    "width": 120
                },
                {
                    "fieldName": "businessType",
                    "columnName": "业务类型",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "inCount",
                    "columnName": "收数量",
                    "show": true,
                    "width": 80
                },
                {
                    "fieldName": "outCount",
                    "columnName": "发数量",
                    "show": true,
                    "width": 80
                }
            ]
        }
    });
});

routeApp.controller('StorageTotalCtl', function ($scope) {
    var module = $('.module-storage-storageTotal');
    var gridJq = module.find('.storageTotal-table');

    var yearHtml = '';
    yearHtml += '<select class="query-year">';
    yearHtml += '   <option value="2014" selected = "selected" >2014</option>';
    yearHtml += '   <option value="2013">2013</option>';
    yearHtml += '   <option value="2012">2012</option>';
    yearHtml += '</select>';

    var monthHtml = '';
    monthHtml += '<select class="query-month">';
    var curMonth = new Date().getMonth() + 1;
    for (var i = 1; i < 13; i++) {
        monthHtml += '  <option value="' + i + '" ' + (curMonth == i ? 'selected = "selected"' : '') + '>' + i + '月份</option>';
    }
    monthHtml += '</select>';

    gridJq.zgrid({
        header: {
            queryArea: [
                {
                    html: yearHtml,
                    clz: 'query-year',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                },
                {
                    html: monthHtml,
                    clz: 'query-month',
                    eventType: 'change',
                    val: function (jq) {
                        return jq.val()
                    }
                }
            ],
            queryParam: function (params) {
                return {
                    "month": params[0] + "." + add0(params[1])
                };
            }
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
                url: "/ieslab/storage/storageTotal/list",
                kwd: ""
            },
            columns: [
                {
                    "fieldName": "impMonth",
                    "columnName": "导入月份",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mcode",
                    "columnName": "物料编码",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "mname",
                    "columnName": "物料名称",
                    "show": true,
                    "width": 200
                },
                {
                    "fieldName": "totalIn",
                    "columnName": "总入库数量",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "totalInNum",
                    "columnName": "总入库次数",
                    "show": true,
                    "width": 100
                },
                {
                    "fieldName": "totalOut",
                    "columnName": "总出库数量",
                    "show": true,
                    "width": 150
                },
                {
                    "fieldName": "totalOutNum",
                    "columnName": "总出库次数",
                    "show": true,
                    "width": 100
                }
            ]
        }
    });


    var monthJq = module.find('.storageTotal-month');

    function refreshMonth() {
        monthJq.empty();
        var cyear = new Date().getFullYear();
        for (var i = 1; i < 13; i++) {
            var mstr = cyear + "." + add0(i);
            monthJq.append('<div class="month-chk m-' + mstr.replace('.', '') + '" month="' + mstr + '">' + mstr + "<i class='fa fa-spinner fa-spin-1s'></i></div>");
        }
        $z.http.post('/ieslab/storage/storageTotal/month', {
            'year': cyear
        }, function (re) {
            var mmap = re.data;
            for (var m in mmap) {
                monthJq.find('.m-' + m.replace('.', '')).addClass('chked');
            }
        });
    }

    var isChecking = false;
    monthJq.delegate('.month-chk', 'click', function () {
        var mbtn = $(this);
        if (!isChecking) {
            isChecking = true;
            mbtn.removeClass('chked');
            mbtn.addClass('chking');
            var chkmonth = mbtn.attr('month');
            $z.http.post('/ieslab/storage/storageTotal/anlysis', {
                'month': chkmonth
            }, function (re) {
                isChecking = false;
                mbtn.removeClass('chking');
                refreshMonth();
            });
        }
    });
    refreshMonth();
});
routeApp.controller('ImportCtl', function ($scope) {
    var module = $('.module-storage-import')
    var log = module.find('.import-log');
    var logNum = 0;

    module.delegate('.btn-import', 'click', function () {
        var btn = $(this);
        var fpath = btn.prev().val();
        if (fpath == "") {
            alert($z.msg('import.input.file'));
            return;
        }
        if (btn.hasClass("ing")) {
            return;
        }
        btn.addClass('ing');
        btn.html($z.msg('import.input.submit.ing'));

        log.prepend('<div>' + currentTime() + "  " + $z.msg('import.input.submit.ready') + '</div>');

        $z.http.cometES({
            url: "/ieslab/import/" + btn.attr('type'),
            onChange: function (respTxt, opt) {
                if (logNum > 1000) {
                    log.empty();
                    logNum = 0;
                }
                log.prepend('<div>' + currentTime() + " " + respTxt + '</div>');
                logNum++;
            },
            onFinish: function () {
                // log.append('<div>------------------------Import Done------------------------</div>');
                log.prepend('<div>------------------------Import Done------------------------</div>');
                logNum++;
            },
            data: {
                'path': fpath
            }
        });
    });

});