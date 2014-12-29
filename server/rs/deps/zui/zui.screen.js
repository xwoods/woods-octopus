/**
 * 控件介绍
 *
 * 替换zscreen         ->      控件名称
 *
 */
(function ($) {
    var OPT_NAME = "zscreen_option";
    var SEL_CLASS = ".zscreen";
    var SEL_CLASS_NM = "zscreen";
    var LY_INDEX = 0;

    function getIndex() {
        return LY_INDEX++;
    }

    // _________________________________
    var util = {
        opt: function (selection) {
            return $.data(selection[0], OPT_NAME);
        },
        setOpt: function (selection, opt) {
            $.data(selection[0], OPT_NAME, opt);
        },
        removeOpt: function (selection) {
            $.removeData(selection[0], OPT_NAME);
        },
        checkopt: function (opt) {
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
        mxLayout: function (selection) {
            var sizeX = parseInt(selection.find('.screen-mx-size-x').val());
            var sizeY = parseInt(selection.find('.screen-mx-size-y').val());
            var resolution = selection.find('.screen-mx-rev').val().split('x');
            var revWidth = parseInt(resolution[0]);
            var revHeight = parseInt(resolution[1]);
            return {
                'sizeX': sizeX,
                'sizeY': sizeY,
                'width': revWidth,
                'height': revHeight
            };
        }
    };
    // _________________________________
    var dom = {
        init: function (selection) {
            var opt = util.opt(selection);
            var doc = opt.doc;
            var html = '';
            html += '<div class="zscreen">'
            html += '<div class="edit-title-bar">';
            html += '   <div class="edit-doc-name">' + doc.name + "." + doc.type + '</div>';
            html += '   <div class="edit-btn screen-play">播放</div>';
            html += '   <div class="edit-btn screen-sourcecode">查看源码</div>';
            html += '   <div class="edit-btn screen-save">保存</div>';
            html += '</div>';
            html += '<div class="edit-body">';
            html += '   <div class="edit-container">'
            html += '       <div class="screen-layout">';
            html += '           <div class="screen-main">';
            html += '               <div class="screen-dashboard-menu">';
            html += '               <div class="screen-dashboard-menu-inner">';
            html += '                   <span class="screen-mx-label">矩阵大小</span>';
            html += '                   <span class="screen-mx-attr">';
            html += '                       <input class="screen-mx-size-x" value="3"> x <input class="screen-mx-size-y" value="3">';
            html += '                   </span>';
            html += '                   <b class="vsep"></b>';
            html += '                   <span class="screen-mx-label">矩阵分辨率</span>';
            html += '                   <span class="screen-mx-attr">';
            html += '                       <select class="screen-mx-rev">';
            html += '                           <option value="1920x1080">1920 x 1080</option>';
            html += '                           <option value="1366x768">1366 x 768</option>';
            html += '                           <option value="1280x720">1280 x 720</option>';
            html += '                           <option value="1024x768">1024 x 768</option>';
            html += '                           <option value="1080x1920">1080 x 1920</option>';
            html += '                           <option value="768x1366">768 x 1366</option>';
            html += '                           <option value="720x1280">720 x 1280</option>';
            html += '                           <option value="768x1024">768 x 1024</option>';
            html += '                       </select>';
            html += '                   </span>';
            html += '                   <b class="vsep"></b>';
            html += '                   <span class="screen-mx-label">辅助线</span>';
            html += '                   <span class="screen-mx-attr">';
            html += '                       <input class="screen-mx-layout-assist" type="checkbox" checked="checked">';
            html += '                   </span>';
            html += '               </div>';
            html += '               </div>';
            html += '               <div class="screen-dashboard">';
            html += '                   <div class="screen-canvas">';
            html += '                       <div class="screen-layout-container">';
            html += '                           <table class="screen-layout-staff active"></table>';
            html += '                       </div>';
            html += '                       <div class="screen-layout-stack">';
            html += '                       </div>';
            html += '                   </div>';
            html += '               </div>';
            html += '               <div class="screen-timeline">';
            html += '                   <div class="screen-timeline-bar">';
            html += '                       <div class="screen-timeline-stack-info">';
            html += '                           <span>总时长</span>';
            html += '                           <b>0</b> ';
            html += '                           <span>秒</span>';
            html += '                       </div>';
            html += '                       <div class="screen-timeline-item-info">';
            html += '                           <span>宽度</span>';
            html += '                           <input class="screen-mx-item-width" type="width" value="0" oldval="0">';
            html += '                           <span>高度</span>';
            html += '                           <input class="screen-mx-item-height" type="height" value="0" oldval="0">';
            html += '                           <span>X</span>';
            html += '                           <input class="screen-mx-item-left" type="left" value="0" oldval="0">';
            html += '                           <span>Y</span>';
            html += '                           <input class="screen-mx-item-top" type="top" value="0" oldval="0">';
            html += '                       </div>';
            html += '                   </div>';
            html += '                   <div class="screen-timeline-stack">';
            html += '                   </div>';
            html += '               </div>';
            html += '           </div>';
            html += '           <div class="screen-lys">';
            html += '               <div class="screen-lys-btn-add">';
            html += '                   <i class="fa fa-plus-circle fa-1x"></i> 新建层';
            html += '               </div>';
            html += '               <ul class="screen-lys-list">';
            html += '               </ul>';
            html += '           </div>';
            html += '           <div class="screen-material">';
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="source-layout">';
            html += '            <textarea ></textarea>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
            html += '</div>';
            selection.append(html);

            var material = selection.find('.screen-material');
            material.netdisk({
                root: {
                    module: 'matrix',
                    moduleKey: 'material'
                },
                isPrivate: false,
                multisel: true,
                switchs: {
                    'trans': false,
                    'createFile': false,
                    'upload': false,
                    'createFolder': false,
                    'download': false,
                    'delete': false,
                    'rename': false,
                    'add2Screen': true
                },
                createType: [],
                uploadType: [],
                events: {
                    add2Screen: function (doclist) {
                        events.addPobj2TimelineAndLayout(selection, doclist);
                    }
                }
            });
            material.netdisk('listHeight');
            selection.find('.screen-lys-list').sortable({});
            events.mxStaffChange(selection);
        }
    };
    // _________________________________
    var data = {
        init: function (selection) {
            var opt = util.opt(selection);
            $z.http.getText('/doc/txt/read', {
                'docId': opt.doc.id
            }, function (text) {
                if ($z.util.isBlank(text)) {
                    text = "{}";
                }
                var screen = eval("(" + text + ")");
                if ($.isEmptyObject(screen)) {
                    screen = {
                        "width": 1920,
                        "height": 1080,
                        "sizeX": 3,
                        "sizeY": 3,
                        "layers": []
                    };
                }
                // 按照screen进行加载
                data.load(selection, screen);
            });
        },
        load: function (selection, screen) {
            selection.find('.screen-mx-size-x').val(screen.sizeX);
            selection.find('.screen-mx-size-y').val(screen.sizeY);
            selection.find('.screen-mx-rev').val(screen.width + "x" + screen.height);

            var lymap = {}
            for (var i = screen.layers.length - 1; i >= 0; i--) {
                var layer = screen.layers[i];
                var ci = events.addLayerByName(selection, layer.name);
                lymap[ci] = layer;
            }

            for (var ci in lymap) {
                var layer = lymap[ci];
                var pobjs = layer.pobjs;
                if (pobjs && pobjs.length > 0) {
                    var doclist = [];
                    for (var i = 0; i < pobjs.length; i++) {
                        var pobj = pobjs[i];
                        var docId = pobj.deps[0].id;
                        var doc = getDoc(docId);
                        var meta = $z.util.str2json(doc.meta);
                        meta.width = pobj.width;
                        meta.height = pobj.height;
                        meta.top = pobj.top;
                        meta.left = pobj.left;
                        meta.duration = pobj.duration;
                        doc.meta = $z.util.json2str(meta);
                        doclist.push(doc);
                    }
                    events.addPobj2TimelineAndLayout(selection, doclist, ci);
                }
            }


            events.mxStaffChange(selection);
            layout.resize(selection);

        },
        get: function (selection) {
            var mxlayout = util.mxLayout(selection);
            var screen = {
                width: mxlayout.width,
                height: mxlayout.height,
                sizeX: mxlayout.sizeX,
                sizeY: mxlayout.sizeY,
                layers: []
            };

            var lis = selection.find('.screen-lys-list li');
            lis.each(function (i, ele) {
                var li = $(ele);
                var cindex = li.attr('cindex');
                var layer = {
                    name: li.find('.ly-name').html(),
                    zIndex: i + 1,
                    pobjs: []
                };
                screen.layers.push(layer);
                var tlpobjs = selection.find('.screen-timeline-stack-item.screen-mx-ly-' + cindex + " .screen-mx-pobj");
                tlpobjs.each(function (i, ele) {
                    var mobj = $(ele);
                    var pobj = mobj.data('POBJ');
                    var rpobj = {
                        'width': pobj.mymeta.width,
                        'height': pobj.mymeta.height,
                        'top': pobj.mymeta.top,
                        'left': pobj.mymeta.left,
                        'duration': parseInt(mobj.find('.mx-pobj-duration-val').val()),
                        'libName': pobj.cate,
                        'params': {},
                        'deps': []
                    };
                    rpobj.deps.push({
                        id: pobj.id,
                        type: pobj.type
                    });
                    layer.pobjs.push(rpobj);
                });
            });

            return screen;
        }
    };
// _________________________________
    var events = {
        unbind: function (selection) {
            selection.undelegate();
        },
        bind: function (selection) {
            selection.delegate('.screen-mx-size-x', 'change', function () {
                var sel = util.selection($(this));
                events.mxStaffChange(sel);
                events.mxLayoutChange(sel);
            });
            selection.delegate('.screen-mx-size-y', 'change', function () {
                var sel = util.selection($(this));
                events.mxStaffChange(sel);
                events.mxLayoutChange(sel);
            });
            selection.delegate('.screen-mx-rev', 'change', function () {
                var sel = util.selection($(this));
                events.mxLayoutChange(sel);
            });
            selection.delegate('.screen-mx-layout-assist', 'click', function () {
                var selection = util.selection(this);
                var st = selection.find('.screen-layout-staff');
                $(this).prop('checked') ? st.addClass('active') : st.removeClass('active');
            });

            // 添加新播层
            selection.delegate('.screen-lys-btn-add', 'click', events.addLayer);
            // 删除播放层
            selection.delegate('.screen-lys-list li .ly-del', 'click', events.delLayer);
            // 切换播放层
            selection.delegate('.screen-lys-list li', 'click', events.switchLayer);
            // 修改播放层名称
            selection.delegate('.screen-lys-list li .ly-name', 'click', events.changeLayerName);


            // 激活pobj
            selection.delegate('.screen-mx-pobj', 'click', events.selPobj);
            // 删除pobj
            selection.delegate('.screen-mx-pobj .mx-pobj-del', 'click', events.deletePobj);
            // 修改duration
            selection.delegate('.screen-mx-pobj .mx-pobj-duration-val', 'change', events.changePobjDuration);
            // 调整pobj
            selection.delegate('.screen-timeline-item-info input', 'change', events.changePobjInfo);

            // 查看文件
            selection.delegate('.mx-pobj-name', 'click', function () {
                var mpobj = $(this).parent();
                var pobj = mpobj.data("POBJ");
                $.zpreview({
                    doc: pobj
                })
            });


            // 保存
            selection.delegate('.screen-save', 'click', function () {
                var sbtn = $(this);
                var selection = util.selection(sbtn);
                var opt = util.opt(selection);
                var screenText = $z.util.json2strF(data.get(selection), true);
                $z.http.post('/doc/txt/write', {
                    'docId': opt.doc.id,
                    'content': screenText
                }, function (re) {
                    alert('保存成功');
                });
            });

            // 查看源码
            selection.delegate('.screen-sourcecode', 'click', function () {
                var sbtn = $(this);
                var selection = util.selection(sbtn);
                if (sbtn.hasClass('checksource')) {
                    sbtn.removeClass('checksource');
                    sbtn.html('查看源码');
                    selection.find('.edit-container').removeClass('source');
                } else {
                    sbtn.addClass('checksource');
                    sbtn.html('返回编辑');
                    selection.find('.edit-container').addClass('source');
                    selection.find('.source-layout textarea').val($z.util.json2strF(data.get(selection), true));
                }
            });

            // 播放
            selection.delegate('.screen-play', 'click', function () {
                var opt = util.opt(util.selection(this));
                var yes = confirm("将当前屏幕播放在 '默认' 矩阵中播放吗?");
                if (yes) {
                    $z.http.post("/matrix/play/start", {
                        'mc': 'default',
                        'scrn': opt.doc.id
                    }, function (re) {
                        alert('播放设置成功');
                    });
                }
            });

        },
        addPobj2TimelineAndLayout: function (selection, doclist, cindex) {
            // 添加到当前的时间线中
            var mxLayout = util.mxLayout(selection);
            var opt = util.opt(selection);
            var timeline = selection.find(cindex ? ('.screen-timeline-stack-item.screen-mx-ly-' + cindex) : '.screen-timeline-stack-item.active');
            var layout = selection.find(cindex ? ('.screen-layout-stack-item.screen-mx-ly-' + cindex) : '.screen-layout-stack-item.active');
            var ci = timeline.children().length;
            var lyinfoX = selection.find('.screen-mx-item-left');
            var lyinfoY = selection.find('.screen-mx-item-top');
            var lyinfoW = selection.find('.screen-mx-item-width');
            var lyinfoH = selection.find('.screen-mx-item-height');
            for (var i = 0; i < doclist.length; i++) {
                var doc = doclist[i];
                var mi = $z.util.str2json(doc.meta);
                mi = $.extend({}, {
                    'width': mxLayout.width,
                    'height': mxLayout.height,
                    'duration': 10,
                    'top': 0,
                    'left': 0
                }, mi); // TODO 根据前一个修改
                doc.mymeta = mi;
                var html = '';
                html += '                           <div class="screen-mx-pobj pobj-' + doc.id + '" docId="' + doc.id + '">';
                html += '                               <div class="mx-pobj-index">' + (ci + i + 1) + '</div>';
                html += '                               <div class="mx-pobj-del"><i class="fa fa-remove fa-1x"></i></div>';
                html += '                               <div class="mx-pobj-duration">';
                html += '                                   <input class="mx-pobj-duration-val" value="' + mi.duration + '" oldval="' + mi.duration + '">';
                html += '                               </div>';
                html += '                               <div class="mx-pobj-img">';
                if (doc.hasPreview) {
                    html += '         <img class="file-type" src="/doc/preview/' + doc.id + '?' + new Date().getTime() + '">';
                    if (doc.hasTrans && 'video' == doc.cate) {
                        if (mi.transCutX > 1 && mi.transCutY > 1) {
                            html += '   <div class="file-tip video-cutAs">' + mi.transCutX + "x" + mi.transCutY + '</div>';
                        }
                        html += '</img>';
                    }
                } else {
                    html += '         <div class="file-type zui-icon-64 ' + doc.type + '"></div>';
                }
                html += '                               </div>';
                html += '                               <div class="mx-pobj-name">';
                html += '                                   <span>' + doc.name + '.' + doc.type + '</span>';
                html += '                               </div>';
                html += '                           </div>';

                $(html).data("POBJ", doc).appendTo(timeline);


                // 添加到当前的layout
                var lhtml = '';
                lhtml += '<div class="screen-mx-lypobj pobj-' + doc.id + '"  docId="' + doc.id + '">';
                lhtml += '  <div class="screen-mx-lypobj-inner ' + doc.cate + '" ';
                if (doc.hasPreview) {
                    if (doc.cate == 'video') {
                        lhtml += 'style="background-image: url(/doc/preview/' + doc.id + '?' + new Date().getTime() + ');" >';
                    } else if (doc.cate == "image") {
                        lhtml += 'style="background-image: url(/doc/bin/read?docId=' + doc.id + ');" >';
                    }
                } else {
                    lhtml += 'style="background-image: url(/doc/type/read?type=' + doc.type + ');" >';
                }
                lhtml += '  <div class="mx-lypobj-name" >' + doc.name + "</div>";
                lhtml += '  </div>';
                lhtml += '</div>';

                $(lhtml).data("POBJ", doc).css({
                    'width': opt.layout.scaleX * mi.width,
                    'height': opt.layout.scaleY * mi.height,
                    'top': opt.layout.scaleX * mi.top,
                    'left': opt.layout.scaleY * mi.left
                }).attr('width', mi.width).attr('height', mi.height).appendTo(layout).resizable({
                    resize: function (event, ui) {
                        var width = Math.floor(ui.size.width / opt.layout.scaleX);
                        var height = Math.floor(ui.size.height / opt.layout.scaleY);
                        lyinfoW.val(width);
                        lyinfoH.val(height);
                    },
                    stop: function (event, ui) {
                        var pobj = ui.helper.data('POBJ');
                        var width = Math.floor(ui.size.width / opt.layout.scaleX);
                        var height = Math.floor(ui.size.height / opt.layout.scaleY);
                        lyinfoW.val(width);
                        lyinfoH.val(height);
                        pobj.mymeta.width = width;
                        pobj.mymeta.height = height;
                    }
                }).draggable({
                    start: function (event, ui) {
                        ui.helper.addClass('moving');
                    },
                    stop: function (event, ui) {
                        ui.helper.removeClass('moving');
                        var left = Math.floor(ui.position.left / opt.layout.scaleX);
                        var top = Math.floor(ui.position.top / opt.layout.scaleY);
                        lyinfoX.val(left);
                        lyinfoY.val(top);
                        var pobj = ui.helper.data('POBJ');
                        pobj.mymeta.top = top;
                        pobj.mymeta.left = left;
                    },
                    drag: function (event, ui) {
                        lyinfoX.val(Math.floor(ui.position.left / opt.layout.scaleX));
                        lyinfoY.val(Math.floor(ui.position.top / opt.layout.scaleY));
                    }
                });

            }

            timeline.sortable({
                'stop': function () {
                    events.afterPobjMove(selection);
                    events.refreshTotalDuration(selection);
                }
            }).disableSelection();

            events.refreshTotalDuration(selection);
            var cmpobj = cindex ? timeline.find('.screen-mx-pobj').first() : timeline.find('.screen-mx-pobj').last();
            cmpobj.click();
        },
        mxStaffChange: function (selection) {
            var st = selection.find('.screen-layout-staff');
            var sthtml = '';
            var mxLayout = util.mxLayout(selection);
            for (var i = 0; i < mxLayout.sizeY; i++) {
                sthtml += '<tr>';
                for (var j = 0; j < mxLayout.sizeX; j++) {
                    sthtml += '<td></td>';
                }
                sthtml += '</tr>';
            }
            st.empty().append(sthtml);
        },
        mxLayoutChange: function (selection) {
            layout.resizeDashboard(selection)
        },
        addLayer: function () {
            var lyName = prompt('请输入播放层名称', '');
            if (lyName == '' || lyName == undefined || lyName == null) {
                return;
            }
            var abtn = $(this);
            var selection = util.selection(abtn);
            events.addLayerByName(selection, lyName);
        },
        addLayerByName: function (selection, lyName) {
            var cindex = getIndex();
            var lys = selection.find('.screen-lys-list');
            var lyhtml = '';
            lyhtml += '                   <li class="screen-mx-ly-' + cindex + '" cindex="' + cindex + '">';
            lyhtml += '                       <div class="ly-del"><i class="fa fa-1x fa-remove"></i></div>';
            lyhtml += '                       <div class="ly-name">' + lyName + '</div>';
            lyhtml += '                   </li>';
            $(lyhtml).prependTo(lys);

            // timeline
            var tl = '';
            tl += '<div class="screen-timeline-stack-item screen-mx-ly-' + cindex + '" ></div>';
            selection.find('.screen-timeline-stack').append(tl);
            // layout
            var layout = '';
            layout += '<div class="screen-layout-stack-item screen-mx-ly-' + cindex + '" ></div>';
            selection.find('.screen-layout-stack').append(layout);

            // 激活
            selection.find('li.screen-mx-ly-' + cindex).click();

            return cindex;
        },
        delLayer: function () {
            var li = $(this).parent();
            var selection = util.selection(li);
            var nli = null;
            if (li.hasClass('active')) {
                nli = li.next();
                if (nli.length == 0) {
                    nli = li.prev();
                }
                if (nli.length == 0) {
                    nli = null;
                }
            }
            var cindex = li.attr('cindex');
            li.remove();
            var tljq = selection.find('.screen-timeline-stack-item.screen-mx-ly-' + cindex);
            var lyjq = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex);
            tljq.remove();
            lyjq.remove();

            if (nli != null) {
                nli.click();
            }
        },
        switchLayer: function () {
            var li = $(this);
            var selection = util.selection(li);
            if (li.hasClass('active')) {
                return;
            }
            var cindex = li.attr('cindex');
            var tljq = selection.find('.screen-timeline-stack-item.screen-mx-ly-' + cindex);
            var lyjq = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex);
            li.siblings().removeClass('active');
            li.addClass('active');
            tljq.siblings().removeClass('active');
            tljq.addClass('active');
            lyjq.siblings().removeClass('active');
            lyjq.addClass('active');

            events.refreshTotalDuration(selection);
            events.refreshPobjInfo(selection);
        },
        changeLayerName: function () {
            var ln = $(this);
            var lyName = prompt('请修改名称', ln.html());
            if (lyName == '' || lyName == undefined || lyName == null) {
                return;
            } else {
                ln.html(lyName);
            }
        },
        selPobj: function () {
            var mpobj = $(this);
            var selection = util.selection(mpobj);
            if (mpobj.hasClass('active')) {
                return;
            }
            mpobj.siblings().removeClass('active');
            mpobj.addClass('active');

            var docId = mpobj.attr('docId');
            var lypobj = selection.find('.screen-mx-lypobj.pobj-' + docId);
            lypobj.siblings().removeClass('active');
            lypobj.addClass('active');

            // 参数
            var pobj = mpobj.data('POBJ');
            selection.find('.screen-timeline-item-info input[type=width]').val(pobj.mymeta.width);
            selection.find('.screen-timeline-item-info input[type=height]').val(pobj.mymeta.height);
            selection.find('.screen-timeline-item-info input[type=top]').val(pobj.mymeta.top);
            selection.find('.screen-timeline-item-info input[type=left]').val(pobj.mymeta.left);
        },
        afterPobjMove: function (selection) {
            var pobjs = selection.find('.screen-timeline-stack-item.active .screen-mx-pobj');
            pobjs.each(function (i, ele) {
                $(ele).find('.mx-pobj-index').html(i + 1);
            });
        },
        deletePobj: function (e) {
            e.stopPropagation();
            var dpobj = $(this).parent();
            var docId = dpobj.attr('docId');
            var selection = util.selection(dpobj);
            var npobj = null;
            if (dpobj.hasClass('active')) {
                npobj = dpobj.next();
                if (npobj.length == 0) {
                    npobj = dpobj.prev();
                }
                if (npobj.length == 0) {
                    npobj = null;
                }
            }
            dpobj.remove();
            var lypobj = selection.find('.screen-mx-lypobj.pobj-' + docId);
            lypobj.remove();
            if (npobj != null) {
                npobj.click();
                events.afterPobjMove(selection);
                events.refreshTotalDuration(selection);
            } else {
                // 没有其他pobj了?
                selection.find('.screen-timeline-item-info input').val(0);
                selection.find('.screen-timeline-stack-info b').val(0);
            }
        },
        changePobjDuration: function (e) {
            e.stopPropagation();
            var dujq = $(this);
            var selection = util.selection(dujq);
            var currentDu = parseInt(dujq.val());
            if (isNaN(currentDu) || currentDu <= 0) {
                dujq.val(dujq.attr('oldval'));
            } else {
                dujq.attr('oldval', currentDu);
                events.refreshTotalDuration(selection);
                if (currentDu != dujq.val()) {
                    dujq.val(currentDu);
                }
            }
        },
        refreshTotalDuration: function (selection) {
            var tdu = 0;
            var pobjs = selection.find('.screen-timeline-stack-item.active .screen-mx-pobj');
            pobjs.each(function (i, ele) {
                tdu += parseInt($(ele).find('.mx-pobj-duration-val').val());
            });
            selection.find('.screen-timeline-stack-info b').html(tdu);
        },
        refreshPobjInfo: function (selection) {
            var mpobj = selection.find('.screen-timeline-stack-item.active .screen-mx-pobj.active');
            if (mpobj.length > 0) {
                var pobj = mpobj.data('POBJ');
                selection.find('.screen-timeline-item-info input[type=width]').val(pobj.mymeta.width);
                selection.find('.screen-timeline-item-info input[type=height]').val(pobj.mymeta.height);
                selection.find('.screen-timeline-item-info input[type=top]').val(pobj.mymeta.top);
                selection.find('.screen-timeline-item-info input[type=left]').val(pobj.mymeta.left);
            } else {
                selection.find('.screen-timeline-item-info input').val(0);
            }
        },
        changePobjInfo: function () {
            var injq = $(this);
            var selection = util.selection(injq);
            var tp = injq.attr('type');
            var val = parseInt(injq.val());

            var apobj = selection.find('.screen-timeline-stack-item.active .screen-mx-pobj.active');
            apobj.data('POBJ').mymeta[tp] = val;

            layout.resetCurrentLypobj(selection);
        }
    };
// _________________________________
    var layout = {
        resize: function (selection) {
            var scrnMain = selection.find('.screen-main');
            var scrnDash = selection.find('.screen-dashboard');
            var scrnTimeline = selection.find('.screen-timeline');
            var scrnDashMenu = selection.find('.screen-dashboard-menu');
            scrnDash.css({
                'height': (scrnMain.height() - scrnDashMenu.height() - scrnTimeline.height())
            });

            layout.resizeDashboard(selection)
        },
        resizeDashboard: function (selection) {
            var opt = util.opt(selection);
            var scrnDash = selection.find('.screen-dashboard');
            var daWidth = scrnDash.width();
            var daHeight = scrnDash.height();
            var mxLayout = util.mxLayout(selection);
            var lyPos = $z.util.innerPostion(daWidth, daHeight, mxLayout.sizeX * mxLayout.width, mxLayout.sizeY * mxLayout.height);

            opt.layout = {
                scaleX: lyPos.width / (mxLayout.sizeX * mxLayout.width),
                scaleY: lyPos.height / (mxLayout.sizeY * mxLayout.height)
            };
            // 背景
            selection.find('.screen-layout-container').css(lyPos);
            selection.find('.screen-layout-staff td').css({
                'width': lyPos.width / mxLayout.sizeX,
                'height': lyPos.height / mxLayout.sizeY
            });
            // stack
            var scale = lyPos.width / (mxLayout.sizeX * mxLayout.width);
            selection.find('.screen-layout-stack').css(lyPos);

            // stack-item
            selection.find('.screen-mx-lypobj').each(function (i, ele) {
                var lypobj = $(ele);
                var mi = lypobj.data("POBJ").mymeta;
                lypobj.css({
                    'width': opt.layout.scaleX * mi.width,
                    'height': opt.layout.scaleY * mi.height,
                    'top': opt.layout.scaleX * mi.top,
                    'left': opt.layout.scaleY * mi.left
                });
            });
        },
        resetLypobj: function (selection, docId) {
            var lypobj = selection.find('.screen-mx-lypobj.pobj-' + docId);
            var opt = util.opt(selection);
            var mi = lypobj.data("POBJ").mymeta;
            lypobj.css({
                'width': opt.layout.scaleX * mi.width,
                'height': opt.layout.scaleY * mi.height,
                'top': opt.layout.scaleX * mi.top,
                'left': opt.layout.scaleY * mi.left
            });
        },
        resetCurrentLypobj: function (selection) {
            var lypobj = selection.find('.screen-mx-lypobj.active');
            var opt = util.opt(selection);
            var mi = lypobj.data("POBJ").mymeta;
            lypobj.css({
                'width': opt.layout.scaleX * mi.width,
                'height': opt.layout.scaleY * mi.height,
                'top': opt.layout.scaleX * mi.top,
                'left': opt.layout.scaleY * mi.left
            });
        }
    };
// _________________________________
    var commands = {
        depose: function () {
        }
    };
// _________________________________
    $.fn.extend({
        zscreen: function (opt, arg0, arg1, arg2, arg3, arg4) {
            var selection = this;
            // 检查有效选区
            if (selection.size() == 0)
                return selection;
            // 命令模式
            if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.zscreen: don't support command '" + opt + "'";
                var re = commands[opt].call(selection, arg0, arg1, arg2, arg3, arg4);
                return typeof re == "undefined" ? selection : re;
            }
            // 先销毁再初始化
            commands.depose.call(selection);
            // 记录检查配置
            opt = util.checkopt(opt);
            util.setOpt(selection, opt);
            // dom初始化
            dom.init(selection);
            // 绑定事件
            events.bind(selection);
            // 调整布局
            layout.resize(selection);
            // 数据初始化
            data.init(selection);
            // 返回支持链式赋值
            return selection;
        }
    });
})
(window.jQuery);
