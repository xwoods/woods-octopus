/**
 * Created by pw on 14-7-20.
 */
(function ($) {
    var OPT_NAME = "zgrid_option";
    var SEL_CLASS = ".zgrid";
    var SEL_CLASS_NM = "zgrid";
    // _________________________________
    var util = {
        opt: function (selection) {
            return $.data(selection[0], OPT_NAME);
        },
        setOpt: function (selection, opt) {
            opt = util.checkOpt(opt);
            $.data(selection[0], OPT_NAME, opt);
        },
        removeOpt: function (selection) {
            $.removeData(selection[0], OPT_NAME);
        },
        checkOpt: function (opt) {
            // TODO
            opt.status = opt.status || {
                loading: false
            };
            opt.rows = [];
            opt.columnsRender = opt.columnsRender || {};
            return opt;
        },
        selection: function (ele) {
            var me;
            if (ele instanceof jQuery) {
                me = ele;
            } else {
                me = $(ele);
            }
            if (me.hasClass(SEL_CLASS_NM))
                return me.parent();
            if (me.children(SEL_CLASS).size() > 0)
                return me;
            return me.parents(SEL_CLASS).parent();
        },
        tableWidth: function (opt) {
            var cols = opt.table.columns;
            opt.table._showColNum = 0;
            var rwidth = 0;
            for (var i = 0; i < cols.length; i++) {
                if (cols[i].show) {
                    rwidth += parseInt(cols[i].width);
                    opt.table._showColNum++;
                }
            }
            return rwidth;
        }
    };
    // _________________________________
    var dom = {
        init: function (selection) {
            var opt = util.opt(selection);
            var html = '';
            html += '<div class="zgrid">';
            if (opt.header) {
                html += '   <div class="zgrid-header zgrid-menu-bar">';
                html += '       <ol class="query-area">'
                html += '           <li>';
                html += '               <input type="text" placeholder="请输入过滤条件.." class="query-kwd" value="' + opt.table.query.kwd + '"/>';
                html += '           </li>';
                if (opt.header.queryArea) {
                    var cusQA = opt.header.queryArea;
                    for (var i = 0; i < cusQA.length; i++) {
                        html += '<li>';
                        html += cusQA[i].html;
                        html += '</li>';
                    }
                }
                html += '       </ol>'
                html += '       <ol class="pager-area">'
                html += '           <li>'
                html += '               <select class="pager-size">';
                html += '                   <option value="10">10</option>';
                html += '                   <option value="20">20</option>';
                html += '                   <option value="50">50</option>';
                html += '                   <option value="100">100</option>';
                html += '                   <option value="300">300</option>';
                html += '               </select>';
                html += '           </li>'
                html += '           <li class="pager-btn pager-prev"><i class="fa fa-chevron-circle-left fa-lg"></i></li>'
                html += '           <li>'
                html += '               <b class="pg-current">0</b> <b>/</b>';
                html += '               <b class="pg-count">0</b>';
                html += '               <b class="pg-total">( 0 )</b>';
                html += '           </li>'
                html += '           <li class="pager-btn pager-next"><i class="fa fa-chevron-circle-right fa-lg"></i></li>'
                html += '       </ol>'
                html += '   </div>';
            }
            html += '   <div class="zgrid-table">';
            html += '       <div class="left-panel">';
            html += '           <div class="zgrid-table-head">';
            html += '               <i class="fa fa-gear fa-lg column-setting-toggle"></i>';
            html += '               <ul class="zgrid-column-setting">';
            html += '               </ul>';
            html += '           </div>';
            html += '           <div class="zgrid-table-body">';
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="right-panel">';
            html += '           <div class="zgrid-table-head">';
            html += '           </div>';
            html += '           <div class="zgrid-table-body">';
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="loading-panel">';
            html += '           <div class="loading-tip">';
            html += '               <i class="fa fa-refresh fa-spin-1s fa-5x"></i>';
            html += '           </div>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
            selection.empty()[0].innerHTML = html;

            dom.initComponet(selection, opt)
        },
        initComponet: function (selection, opt) {
            var colset = selection.find('.zgrid-column-setting');
            var lbody = selection.find('.left-panel .zgrid-table-body');
            var rhead = selection.find('.right-panel .zgrid-table-head');
            var rbody = selection.find('.right-panel .zgrid-table-body');

            // columns
            var cols = opt.table.columns;
            colset[0].innerHTML = dom.setHtml(cols);
            rhead[0].innerHTML = dom.headHtml(cols);
            layout.tableResize(selection);

            selection.find('.pager-size').val(opt.table.pager.pgsz);
        },
        setHtml: function (cols) {
            var html = '';
            html += '{{#each cols}}';
            html += '<li>';
            html += '    <span>{{columnName}}</span>';
            html += '    <em></em>';
            html += '    <b>显示</b>';
            html += '    {{#if show}}';
            html += '    <input type="checkbox" field="{{fieldName}}" index="{{@index}}" checked>';
            html += '    {{else}}';
            html += '    <input type="checkbox" field="{{fieldName}}" index="{{@index}}">';
            html += '    {{/if}}';
            html += '    <em></em>';
            html += '    <b>宽度</b>';
            html += '    <input type="number" min="50" max="800" old="{{width}}" value="{{width}}" field="{{fieldName}}" index="{{@index}}">';
            html += '</li>';
            html += '{{/each}}';

            var template = Handlebars.compile(html);
            return template({'cols': cols});
        },
        headHtml: function (cols) {
            var html = '';
            html += '{{#each cols}}';
            html += '<div class="zgrid-th" field="{{fieldName}}" colwidth="{{width}}"';
            html += '   style="width: {{width}}px;';
            html += '    {{#if show}}';
            html += '   display:inline-block';
            html += '    {{else}}'
            html += '   display: none;'
            html += '    {{/if}}';
            html += '">';
            html += '   <i class="fa fa-sort-asc"></i>'
            html += '   <i class="fa fa-sort-desc"></i>'
            html += '   <span>{{columnName}}</span>'
            html += '</div>';
            html += '{{/each}}';
            var template = Handlebars.compile(html);
            return template({'cols': cols});
        },
        leftBodyHtml: function (cols, dlist, row) {
            var html = '';
            for (var i = 0; i < dlist.length; i++) {
                html += '<div class="zgrid-td" no="' + i + '">';
                html += '<div class="row-front">' + (i + 1) + '</div>';
                if (row) {
                    html += '<div class="row-hover">' + row.hover.render(dlist[i]) + '</div>';
                }
                html += '</div>';
            }
            return html;
        },
        rightBodyHtml: function (cols, dlist, columnsRender) {
            var html = '';
            for (var i = 0; i < dlist.length; i++) {
                var da = dlist[i];
                html += '<div class="zgrid-tr">';
                for (var j = 0; j < cols.length; j++) {
                    var col = cols[j];
                    var style = 'style="width:' + col.width + 'px;display:' + (col.show ? "inline-block" : "none" ) + '"';
                    html += '<div class="zgrid-td" field="' + col.fieldName + '" ' + style + ' colwidth="' + col.width + '" >';
                    var render = columnsRender[col.fieldName];
                    var tdhtml = render ? render(da) : da[col.fieldName];
                    if (tdhtml == undefined || tdhtml == null) {
                        tdhtml = "";
                    }
                    html += tdhtml;
                    html += '</div>';
                }
                html += '</div>';
            }
            return html;
        }
    };
    // _________________________________
    var data = {
        init: function (selection) {
            data.refresh(selection)
        },
        queryParams: function (selection) {
            var opt = util.opt(selection);
            var query = opt.table.query;
            var order = opt.table.order;
            var queryParams = {
                'kwd': query.kwd,
                'orderby': order.by,
                'asc': order.asc
            };
            var cusQA = opt.header.queryArea;
            var cusParams = {};
            if (cusQA) {
                var pjqs = [];
                for (var i = 0; i < cusQA.length; i++) {
                    pjqs.push(cusQA[i].val(selection.find("." + cusQA[i].clz)));
                }
                cusParams = opt.header.queryParam(pjqs);
            }
            return $.extend(queryParams, cusParams);
        },
        refresh: function (selection, reset) {
            var opt = util.opt(selection);
            if (opt.status.loading) {
                return;
            }
            var cols = opt.table.columns;
            var query = opt.table.query;
            var pager = opt.table.pager;
            var order = opt.table.order;
            var lbody = selection.find('.left-panel .zgrid-table-body');
            var rbody = selection.find('.right-panel .zgrid-table-body');
            var pageArea = selection.find('.pager-area');
            var loadingTip = selection.find('.loading-panel');
            if (reset) {
                pager.pgnm = 1;
            }
            var queryParams = {
                'kwd': query.kwd,
                'pgsz': pager.pgsz,
                'pgnm': pager.pgnm,
                'orderby': order.by,
                'asc': order.asc
            };
            var cusQA = opt.header.queryArea;
            var cusParams = {};
            if (cusQA) {
                var pjqs = [];
                for (var i = 0; i < cusQA.length; i++) {
                    pjqs.push(cusQA[i].val(selection.find("." + cusQA[i].clz)));
                }
                cusParams = opt.header.queryParam(pjqs);
            }

            opt.status.loading = true;
            loadingTip.show();
            var startTime = new Date().getTime();
            var realQueryParams = $.extend(queryParams, cusParams);

            $z.http.post(query.url, realQueryParams, function (re) {
                var qr = re.data;
                var pg = qr.pager || {
                    recordCount: 0,
                    pageCount: 0,
                    pageNumber: 0
                };
                var dlist = qr.list || [];

                // 更新pager
                pager.total = pg.recordCount;
                pager.pgcount = pg.pageCount;
                pager.pgnm = pg.pageNumber;
                pageArea.find('.pg-current').html(pager.pgnm);
                pageArea.find('.pg-count').html(pager.pgcount);
                pageArea.find('.pg-total').html("( " + pager.total + " )");
                if (pager.pgnm <= 1) {
                    pageArea.find('.pager-prev').addClass('disable');
                }
                if (pager.pgnm == pager.pgcount) {
                    pageArea.find('.pager-next').addClass('disable');
                } else {
                    if (pager.pgcount > 1) {
                        pageArea.find('.pager-next').removeClass('disable');
                    }
                }

                // 更新table
                lbody[0].innerHTML = dom.leftBodyHtml(cols, dlist, opt.table.row);
                rbody[0].innerHTML = dom.rightBodyHtml(cols, dlist, opt.table.columnsRender);

                opt.rows = dlist;

                var stopTime = new Date().getTime();
                var useTime = stopTime - startTime;
                console.log("use " + useTime + "ms to load data");
                setTimeout(function () {
                    loadingTip.hide();
                    opt.status.loading = false;
                }, useTime > 200 ? 0 : 200);
            });
        }
    };
    // _________________________________
    var events = {
        unbind: function (selection) {
            selection.undelegate();
        },
        bind: function (selection) {
            selection.delegate(".column-setting-toggle", 'click', events.showColSetting);
            selection.delegate(".zgrid-column-setting input[type=checkbox]", 'click', events.resizeTable);
            selection.delegate(".zgrid-column-setting input[type=number]", 'change', events.resizeTableByWidth);
            selection.delegate(".zgrid-column-setting li", 'click', events.stopPropagation);

            selection.delegate(".right-panel .zgrid-th", 'click', events.sortBy);
            selection.delegate(".pager-area .pager-size", 'change', events.pageSize);
            selection.delegate(".pager-area .pager-btn", 'click', events.nextPage);
            selection.delegate(".query-area .query-kwd", 'change', events.queryByKwd);

            var opt = util.opt(selection);
            var cusQA = opt.header.queryArea;
            if (cusQA) {
                for (var i = 0; i < cusQA.length; i++) {
                    selection.delegate("." + cusQA[i].clz, cusQA[i].eventType, events.dataRefresh);
                }
            }

            if (opt.table.row && opt.table.row.hover) {
                selection.delegate('.left-panel .zgrid-td .row-hover', 'click', function (e) {
                    e.stopPropagation();
                    var no = parseInt($(this).parent().attr('no'));
                    var rowData = opt.rows[no];
                    opt.table.row.hover.click(rowData);
                });
            }
        },
        dataRefresh: function () {
            var sel = util.selection(this);
            data.refresh(sel, true);
        },
        queryByKwd: function () {
            var qinput = $(this);
            var sel = util.selection(this);
            var opt = util.opt(sel);
            var kwd = $.trim(qinput.val());
            opt.table.query.kwd = kwd;
            if (opt.table.afterChangeKwd) {
                opt.table.afterChangeKwd();
            }
            data.refresh(sel, true);
        },
        nextPage: function () {
            var btn = $(this);
            var sel = util.selection(this);
            var opt = util.opt(sel);
            if (opt.status.loading) {
                return;
            }
            if (btn.hasClass('disable')) {
                return;
            }
            var isNext = btn.hasClass('pager-next');
            opt.table.pager.pgnm = opt.table.pager.pgnm + (isNext ? 1 : -1);
            if (opt.table.pager.pgnm == 2 || opt.table.pager.pgnm == opt.table.pager.pgcount - 1) {
                btn.siblings('.pager-btn').removeClass('disable');
            }
            data.refresh(sel);
        },
        pageSize: function () {
            var sel = util.selection(this);
            var opt = util.opt(sel);
            opt.table.pager.pgsz = $(this).val();
            if (opt.table.afterChangePager) {
                opt.table.afterChangePager();
            }
            data.refresh(sel, true);
        },
        sortBy: function () {
            var sel = util.selection(this);
            var opt = util.opt(sel);
            if (opt.status.loading) {
                return;
            }
            var th = $(this);
            var newby = th.attr('field');
            var oldby = opt.table.order.by;
            if (newby == oldby) {
                if (th.hasClass('asc')) {
                    th.removeClass('asc');
                    th.addClass('desc');
                    opt.table.order.asc = false;
                } else {
                    th.removeClass('desc');
                    th.addClass('asc');
                    opt.table.order.asc = true;
                }
            } else {
                opt.table.order.by = newby;
                if (oldby == "") {
                    // 第一次排序
                    th.addClass('sort').addClass('asc');
                } else {
                    // 其他的去掉
                    th.siblings().removeClass('sort').removeClass('asc').removeClass('desc');
                    th.addClass('sort').addClass('asc');
                    opt.table.order.asc = true;
                }
            }
            data.refresh(sel);
        },
        stopPropagation: function (e) {
            e.stopPropagation();
        },
        showColSetting: function (e) {
            e.stopPropagation();
            var sel = util.selection(this);
            var colsetDialog = sel.find('.zgrid-column-setting');
            colsetDialog.toggle();
            $(document.body).one('click', function () {
                colsetDialog.hide();
            });
        },
        resizeTable: function () {
            var ck = $(this);
            var sel = util.selection(this);
            var opt = util.opt(sel);
            // FIXME 这里还需要再判断一下
            if (opt.table._showColNum < 1) {
                ck.prop('checked', 'checked');
                return;
            }
            var thead = sel.find('.right-panel .zgrid-table-head');
            var tbody = sel.find('.right-panel .zgrid-table-body');
            var field = ck.attr('field');
            var index = parseInt(ck.attr('index'));
            if (ck.prop('checked')) {
                thead.find('.zgrid-th[field=' + field + ']').show();
                tbody.find('.zgrid-td[field=' + field + ']').show();
                thead[0].style.width = parseInt(thead[0].style.width) + opt.table.columns[index].width + "px";
                tbody[0].style.width = thead[0].style.width;
                opt.table._showColNum++;
                opt.table.columns[index].show = true;
            } else {
                thead.find('.zgrid-th[field=' + field + ']').hide();
                tbody.find('.zgrid-td[field=' + field + ']').hide();
                thead[0].style.width = parseInt(thead[0].style.width) - opt.table.columns[index].width + "px";
                tbody[0].style.width = thead[0].style.width;
                opt.table._showColNum--;
                opt.table.columns[index].show = false;
            }
            if (opt.table.afterChangeColumns) {
                opt.table.afterChangeColumns();
            }
        },
        resizeTableByWidth: function () {
            var winput = $(this);
            var sel = util.selection(this);
            var opt = util.opt(sel);
            var thead = sel.find('.right-panel .zgrid-table-head');
            var tbody = sel.find('.right-panel .zgrid-table-body');
            var field = winput.attr('field');
            var index = parseInt(winput.attr('index'));
            var nwidth = winput.val();
            var owidth = parseInt(winput.attr('old'));
            var colhead = thead.find('.zgrid-th[field=' + field + ']');
            var colbody = tbody.find('.zgrid-td[field=' + field + ']');
            var wdif = nwidth - owidth;
            colhead.width(nwidth);
            colbody.width(nwidth);
            opt.table.columns[index].width = parseInt(nwidth);
            // 判断是不是显示的列
            if (opt.table.columns[index].show) {
                thead[0].style.width = parseInt(thead[0].style.width) + wdif + "px";
                tbody[0].style.width = thead[0].style.width;
            }
            winput.attr('old', nwidth);
            if (opt.table.afterChangeColumns) {
                opt.table.afterChangeColumns();
            }
        }
    };
    // _________________________________
    var layout = {
        resize: function (selection) {

        },
        tableResize: function (selection) {
            var opt = util.opt(selection);
            var rhead = selection.find('.right-panel .zgrid-table-head');
            var rbody = selection.find('.right-panel .zgrid-table-body');
            rhead[0].style.width = util.tableWidth(opt) + "px";
            rbody[0].style.width = rhead[0].style.width;
        }
    };
    // _________________________________
    var commands = {
        resize: function () {
            var selection = this;
            layout.resize(selection);
        },
        depose: function () {
            var selection = this;
            events.unbind(selection);
            util.removeOpt(selection);
            selection.empty();
        },
        refresh: function () {
            data.refresh(this);
        },
        queryParams: function () {
            return data.queryParams(this);
        }
    };
    // _________________________________
    $.fn.extend({
        zgrid: function (opt, cmdOpt) {
            var selection = this;
            // 检查有效选区
            if (selection.size() == 0)
                return selection;
            // 命令模式
            if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.zgrid: don't support command '" + opt + "'";
                var re = commands[opt].call(selection, cmdOpt);
                return typeof re == "undefined" ? selection : re;
            }
            commands.depose.call(selection);
            util.setOpt(selection, opt);
            dom.init(selection);
            events.bind(selection);
            layout.resize(selection);
            data.init(selection);
            return selection;
        }
    });
})(jQuery);