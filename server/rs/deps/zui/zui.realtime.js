/**
 * 控件介绍
 *
 * zrealtime         ->      控件名称
 *
 */
(function ($) {
    var OPT_NAME = "zrealtime_option";
    var SEL_CLASS = ".zrealtime";
    var SEL_CLASS_NM = "zrealtime";
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
            opt.zoom = false;
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
            var mxconf = util.opt(selection).mxconf;
            return {
                'sizeX': mxconf.sizeX,
                'sizeY': mxconf.sizeY,
                'width': mxconf.revWidth,
                'height': mxconf.revHeight
            };
        }
    };
    // _________________________________
    var dom = {
        init: function (selection) {
            var opt = util.opt(selection);
            var mxconf = opt.mxconf;
            var html = '';
            html += '<div class="zrealtime">'
            html += '<div class="edit-title-bar">';
            html += '   <div class="edit-doc-name"></div>';
            html += '   <div class="edit-btn close-masker">关闭</div>';
            html += '</div>';
            html += '<div class="edit-body">';
            html += '   <div class="edit-container">'
            html += '       <div class="screen-layout">';
            html += '           <div class="screen-main">';
            html += '               <div class="screen-dashboard-menu">';
            html += '               <div class="screen-dashboard-menu-inner">';
            html += '                   <span class="screen-mx-label">矩阵大小</span>';
            html += '                   <span class="screen-mx-attr">';
            html += '                       <input class="screen-mx-size-x" value="' + mxconf.sizeX + '" disabled="disabled"> x <input class="screen-mx-size-y" value="' + mxconf.sizeY + '" disabled="disabled">';
            html += '                   </span>';
            html += '                   <b class="vsep"></b>';
            html += '                   <span class="screen-mx-label">矩阵分辨率</span>';
            html += '                   <span class="screen-mx-attr">';
            html += '                       <input class="screen-mx-rev" value="' + mxconf.revWidth + "x" + mxconf.revHeight + '" disabled="disabled"></input>';
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
            // stack
            html += '                       <div class="screen-layout-stack">';
            html += '                       </div>';
            html += '                       <div class="screen-layout-scale-menu-back">';
            html += '                       </div>';
            html += '                       <div class="screen-layout-scale-menu">';
            html += '                           <div class="screen-layout-scale-menu-inner">';
            html += '                               <div class="screen-layout-scale-menu-cube">';
            html += '                                   <div class="screen-layout-scale-menu-cube-inner">';
            html += '                                       <div class="cube-tip">窗口</div>';
            html += '                                   </div>';
            html += '                               </div>';
            html += '                           </div>';
            html += '                           <div class="cube-tip">矩阵</div>';
            html += '                       </div>';
            html += '                       <div class="screen-layout-scale-menu-btns">';
            html += '                           <i class="fa fa-lg fa-search-minus"></i>';
            html += '                           <i class="fa fa-lg fa-search-plus"></i>';
            html += '                           <span class="scale-zoom">ZOOM : <input value="1" disabled="disabled"/></span>';
            html += '                       </div>';
            html += '                       <div class="screen-layout-scale-switch">';
            html += '                           <div class="screen-layout-scale-switch-icon"></div>';
            html += '                       </div>';
            html += '                   </div>';
            html += '               </div>';
            html += '               <div class="screen-timeline">';
            html += '                   <div class="screen-timeline-bar">';
            html += '                       <div class="screen-timeline-item-info">';
            html += '                           <span>锁定</span>';
            html += '                           <div class="screen-mx-item-lock">ON</div>';
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
            html += '               </div>';
            html += '           </div>';
            html += '           <div class="screen-lys">';
            html += '               <div class="screen-lys-btn clear">';
            html += '                   <i class="fa fa-trash fa-1x"></i> 清除所有层';
            html += '               </div>';
            // li-stack
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
            events.mxStaffChange(selection);
        }
    };
    // _________________________________
    var data = {
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

            var opt = util.opt(selection);

            // 辅助线
            selection.delegate('.screen-mx-layout-assist', 'click', function () {
                var selection = util.selection(this);
                var st = selection.find('.screen-layout-staff');
                $(this).prop('checked') ? st.addClass('active') : st.removeClass('active');
            });

            //调整pobj
            selection.delegate('.screen-timeline-item-info input', 'change', events.changePobjInfo);
            // 查看文件
            selection.delegate('.mx-pobj-name', 'click', function () {
                var mpobj = $(this).parent();
                var pobj = mpobj.data("POBJ");
                $.zpreview({
                    doc: pobj
                })
            });
            // 切换播放层
            selection.delegate('.screen-lys-list li', 'click', events.switchLayer);
            // 关闭
            selection.delegate('.close-masker', 'click', function () {
                var opt = util.opt(util.selection(this));
                if (opt.beforeClose) {
                    opt.beforeClose();
                }
                $.masker('close');
            });
            // 显示zoom区域
            selection.delegate('.scale-zoom', 'click', function () {
                if (opt.zoom) {
                    selection.find('.screen-layout-scale-menu').hide();
                    selection.find('.screen-layout-scale-menu-back').hide();
                    selection.find('.screen-layout-scale-menu-btns i').hide();
                } else {
                    selection.find('.screen-layout-scale-menu').show();
                    selection.find('.screen-layout-scale-menu-back').show();
                    selection.find('.screen-layout-scale-menu-btns i').show();
                }
                opt.zoom = !opt.zoom;
            });

            // 隐藏zoom
            selection.delegate('.screen-layout-scale-menu-back', 'click', function () {
                selection.find('.screen-layout-scale-menu').hide();
                selection.find('.screen-layout-scale-menu-back').hide();
                selection.find('.screen-layout-scale-menu-btns i').hide();
                opt.zoom = false;
            });

            // 清除
            selection.delegate('.screen-lys-btn.clear', 'click', events.clearLayer);
            selection.delegate('.screen-mx-pobj .mx-pobj-del', 'click', events.deleteLayer);

            // 加解锁
            selection.delegate('.screen-mx-item-lock', 'click', function () {
                var lockBtn = $(this);
                var selection = util.selection(lockBtn);
                // 看看有没有选中的素材
                var lypobj = selection.find('.screen-layout-stack-item.active .screen-mx-lypobj');
                if (lypobj.length == 0) {
                    return;
                }
                if (lockBtn.hasClass('off')) {
                    // 所上
                    lypobj.removeClass('unlock');
                    lypobj.resizable("disable").draggable('disable');
                    lockBtn.removeClass('off');
                    lockBtn.html('ON')

                    selection.find('.screen-lys').show();
                    selection.find('.screen-material').show();
                } else {
                    // 解锁
                    lypobj.addClass('unlock');
                    lypobj.resizable("enable").draggable('enable');
                    lockBtn.addClass('off');
                    lockBtn.html('OFF');

                    selection.find('.screen-lys').hide();
                    selection.find('.screen-material').hide();
                }
            });
        },
        addLayer: function (selection) {
            var cindex = getIndex();
            var lys = selection.find('.screen-lys-list');
            var lyhtml = '';
            lyhtml += '<li class="screen-mx-ly-' + cindex + '" cindex="' + cindex + '">';
            lyhtml += '     ';
            lyhtml += '</li>';
            $(lyhtml).appendTo(lys);

            // layout
            var layout = '';
            layout += '<div class="screen-layout-stack-item screen-mx-ly-' + cindex + '" ><div class="screen-layout-stack-item-wrap"></div></div>';
            selection.find('.screen-layout-stack').append(layout);

            return cindex;
        },
        addPobj2TimelineAndLayout: function (selection, doclist) {

            // 添加到当前的时间线中
            var mxLayout = util.mxLayout(selection);
            var opt = util.opt(selection);
            var lyinfoX = selection.find('.screen-mx-item-left');
            var lyinfoY = selection.find('.screen-mx-item-top');
            var lyinfoW = selection.find('.screen-mx-item-width');
            var lyinfoH = selection.find('.screen-mx-item-height');
            var cindex = 0;
            for (var i = 0; i < doclist.length; i++) {
                cindex = events.addLayer(selection);
                var pobj = selection.find('li.screen-mx-ly-' + cindex);
                var layout = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex);
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
                html += '                               <div class="mx-pobj-del"><i class="fa fa-remove fa-1x"></i></div>';
                //html += '                               <div class="mx-pobj-duration">';
                //html += '                                   <input class="mx-pobj-duration-val" value="' + mi.duration + '" oldval="' + mi.duration + '" disabled />';
                //html += '                               </div>';
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

                $(html).data("POBJ", doc).appendTo(pobj);


                // 添加到当前的layout
                var lhtml = '';
                lhtml += '<div class="screen-mx-lypobj active pobj-' + doc.id + '"  docId="' + doc.id + '">';
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
                }).attr('width', mi.width)
                    .attr('height', mi.height)
                    .appendTo(layout.find('.screen-layout-stack-item-wrap'))
                    .resizable({
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
                    })
                    .draggable({
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
                    }).resizable('disable').draggable('disable');
            }

            selection.find('li.screen-mx-ly-' + cindex).click();

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
        clearLayer: function () {
            var selection = util.selection(this);
            selection.find('.screen-layout-stack').empty();
            selection.find('.screen-lys-list').empty();
        },
        deleteLayer: function () {
            var mpobj = $(this).parent();
            var li = mpobj.parent();
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
            var lyjq = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex);
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
            var lyjq = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex);
            li.siblings().removeClass('active');
            li.addClass('active');
            lyjq.siblings().removeClass('active');
            lyjq.addClass('active');

            events.refreshPobjInfo(selection);
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
        }
        ,
        refreshPobjInfo: function (selection) {
            var mpobj = selection.find('.screen-layout-stack-item.active .screen-mx-lypobj');
            if (mpobj.length > 0) {
                var pobj = mpobj.data('POBJ');
                selection.find('.screen-timeline-item-info input[type=width]').val(pobj.mymeta.width);
                selection.find('.screen-timeline-item-info input[type=height]').val(pobj.mymeta.height);
                selection.find('.screen-timeline-item-info input[type=top]').val(pobj.mymeta.top);
                selection.find('.screen-timeline-item-info input[type=left]').val(pobj.mymeta.left);
            }
        },
        changePobjInfo: function () {
            var injq = $(this);
            var selection = util.selection(injq);
            var tp = injq.attr('type');
            var val = parseInt(injq.val());

            var apobj = selection.find('.screen-layout-stack-item.active .screen-mx-lypobj');
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
            selection.find('.screen-layout-scale-menu-back').css(lyPos);
            selection.find('.screen-layout-scale-menu-btns').css({
                top: lyPos.top + lyPos.height + 5,
                left: lyPos.left + (lyPos.width - 200)
            });
            selection.find('.screen-layout-scale-menu').css({
                width: lyPos.width / 2,
                height: lyPos.height / 2,
                top: lyPos.top + lyPos.height / 2,
                left: lyPos.left + lyPos.width / 2
            });

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
            var lypobj = selection.find('.screen-layout-stack-item.active .screen-mx-lypobj');
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
        zrealtime: function (opt, arg0, arg1, arg2, arg3, arg4) {
            var selection = this;
            // 检查有效选区
            if (selection.size() == 0)
                return selection;
            // 命令模式
            if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.zrealtime: don't support command '" + opt + "'";
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
            // 返回支持链式赋值
            return selection;
        }
    });
})
(window.jQuery);
