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
    var LY_INDEX = 10;

    function getIndex() {
        return LY_INDEX++;
    }

    function resetIndex() {
        LY_INDEX = 10;
    }

    function stopPlay(callback) {
        $z.http.post("/matrix/play/stop", {
            'mc': 'default'
        }, function (re) {
            if (callback) {
                callback();
            }
        });
    }

    // 实时播放命令的对象
    var RT = {
        ws: null,
        wsInter: null,
        ping: function () {
            RT.wsInter = setInterval(function () {
                if (RT.ws == null) {
                    clearInterval(RT.wsInter);
                    RT.wsInter = null;
                } else {
                    RT.send('#PING');
                }
            }, 10 * 1000);
        },
        init: function () {
            var host = window.location.host;
            var ws = new WebSocket("ws://" + host + "/matrix/realtime/cmd");
            ws.onopen = function (e) {
            };
            ws.onmessage = function (e) {
            };
            ws.onclose = function (e) {
                RT.ws = null;
            };
            ws.onerror = function (e) {
                RT.ws = null;
            };
            RT.ws = ws;
        },
        close: function () {
            if (RT.ws != null) {
                RT.ws.close();
            }
            RT.ws = null;
            if (RT.wsInter != null) {
                clearInterval(RT.wsInter);
            }
            RT.wsInter = null;
        },
        send: function (smg) {
            if (RT.ws != null) {
                RT.ws.send(smg);
                console.debug("send : " + smg);
            }
        },
        playLayer: function (cindex, docId, style) {
            RT.send(RTCmd('play', {
                'doc': docId,
                'style': style,
                'zIndex': parseInt(cindex)
            }));
        },
        playVisible: function (cindex, docId, mxconf) {
            RT.send(RTCmd('play', {
                'doc': docId,
                'style': {
                    'width': mxconf.sizeX * mxconf.revWidth,
                    'height': mxconf.sizeY * mxconf.revHeight,
                    'top': 0,
                    'left': 0
                },
                'zIndex': parseInt(cindex),
                'visible': false
            }));
        },
        stopLayer: function (cindex) {
            if (cindex == undefined) {
                RT.send(RTCmd('data', {
                    "type": "stop",
                    "by": "all"
                }));
            } else {
                RT.send(RTCmd('data', {
                    "type": "stop",
                    "by": "z_index",
                    "params": {"ids": [cindex]}
                }));
            }
        },
        unlockLayer: function (cindex, docId, style, mxconf) {
            RT.stopLayer(cindex);
            RT.playVisible(cindex, docId, mxconf);
            RT.moveLayer(cindex, style);
        },
        lockLayer: function (cindex, docId, style) {
            RT.stopLayer(cindex);
            RT.playLayer(cindex, docId, {
                "height": style.height,
                "left": style.left,
                "top": style.top,
                "width": style.width
            });
        },
        moving: false,
        moveInfo: {
            width: 0,
            height: 0,
            top: 0,
            left: 0,
            zIndex: 10
        },
        moveDuration: 20,
        moveLayer: function (cindex, style) {
            if (!RT.moving) {
                setTimeout(function () {
                    RT.send(RTCmd('data', {
                        type: "update",
                        target: "z_index",
                        params: {
                            "type": "move",
                            "height": RT.moveInfo.height,
                            "left": RT.moveInfo.left,
                            "top": RT.moveInfo.top,
                            "width": RT.moveInfo.width,
                            zIndex: RT.moveInfo.zIndex
                        }
                    }));
                    RT.moving = false;
                }, RT.moveDuration);
                RT.moving = true;
            }

            RT.moveInfo.zIndex = parseInt(cindex);
            RT.moveInfo.width = style.width
            RT.moveInfo.height = style.height
            RT.moveInfo.top = style.top
            RT.moveInfo.left = style.left
        }
    };

    function RTCmd(tp, data) {
        return $z.util.json2str({
            'type': tp,
            'data': data
        });
    }

    window.RT = RT;

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
            opt.zoomNum = 1;
            if (opt.ask == undefined) {
                opt.ask = true;
            }
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
        },
        zoomIn: function (oldpos) {
            return {
                'width': oldpos.width * 2,
                'height': oldpos.height * 2,
                'top': oldpos.top - oldpos.height / 2,
                'left': oldpos.left - oldpos.width / 2
            };
        },
        zoomOut: function (oldpos) {
            return {
                'width': oldpos.width / 2,
                'height': oldpos.height / 2,
                'top': oldpos.top + oldpos.height / 4,
                'left': oldpos.left + oldpos.width / 4
            };
        },
        oldpos: function (jq) {
            var style = jq[0].style;
            return {
                'width': parseFloat(style.width),
                'height': parseFloat(style.height),
                'top': parseFloat(style.top),
                'left': parseFloat(style.left)
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
            // html += '   <div class="edit-btn screen-save">保存</div>';
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
            html += '                           <input class="screen-mx-item-width" type="width" value="0" oldval="0" disabled />';
            html += '                           <span>高度</span>';
            html += '                           <input class="screen-mx-item-height" type="height" value="0" oldval="0" disabled />';
            html += '                           <span>X</span>';
            html += '                           <input class="screen-mx-item-left" type="left" value="0" oldval="0" disabled />';
            html += '                           <span>Y</span>';
            html += '                           <input class="screen-mx-item-top" type="top" value="0" oldval="0" disabled />';
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
        load: function (selection) {
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
                        "layers": []
                    };
                }
                // 按照screen进行加载
                var doclist = [];
                for (var i = 0; i < screen.layers.length; i++) {
                    var ly = screen.layers[i];
                    var doc = getDoc(ly.docId);
                    var mi = $z.util.str2json(doc.meta);
                    mi.width = ly.style.width;
                    mi.height = ly.style.height;
                    mi.top = ly.style.top;
                    mi.left = ly.style.left;
                    doc.meta = $z.util.json2str(mi);
                    doclist.push(doc);
                }
                events.addPobj2TimelineAndLayout(selection, doclist);
            });
        },
        export: function (selection) {
            var layers = [];
            var lis = selection.find('.screen-lys-list li');
            lis.each(function (i, ele) {
                var li = $(ele);
                var ly = {};
                ly.docId = li.find('.screen-mx-pobj').attr('docid');
                var cindex = li.attr('cindex');
                var mpobj = selection.find('.screen-layout-stack-item.screen-mx-ly-' + cindex + ' .screen-mx-lypobj');
                if (mpobj.length > 0) {
                    var pobj = mpobj.data('POBJ');
                    ly.style = {
                        'width': pobj.mymeta.width,
                        'height': pobj.mymeta.height,
                        'top': pobj.mymeta.top,
                        'left': pobj.mymeta.left
                    };
                }
                layers.push(ly);
            });
            return layers;
        },
        save: function (selection) {
            var opt = util.opt(selection);
            var screen = {
                'layers': data.export(selection)
            };
            $z.http.post('/doc/txt/write', {
                'docId': opt.doc.id,
                'content': $z.util.json2str(screen)
            }, function (re) {
                //alert('保存成功');
            });
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

            var opt = util.opt(selection);

            // 保存
            selection.delegate('.screen-save', 'click', function () {
                var sbtn = $(this);
                var selection = util.selection(sbtn);
                data.save(selection);
            });

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
                data.save(selection);
                RT.close();
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

                var ips = lockBtn.parent().find('input');

                var opt = util.opt(selection);
                var mxpobj = selection.find('.screen-lys-list li.active .screen-mx-pobj');

                var cindex = mxpobj.parent().attr('cindex');
                var docId = mxpobj.attr('docid');
                var style = mxpobj.data('POBJ').mymeta;
                var mxconf = opt.mxconf;

                if (lockBtn.hasClass('off')) {
                    // 所上
                    lypobj.removeClass('unlock');
                    lypobj.resizable("disable").draggable('disable');
                    lockBtn.removeClass('off');
                    lockBtn.html('ON')

                    selection.find('.screen-lys').show();
                    selection.find('.screen-material').show();
                    ips.attr('disabled', 'disabled');

                    RT.lockLayer(cindex, docId, style, mxconf);
                } else {
                    // 解锁
                    lypobj.addClass('unlock');
                    lypobj.resizable("enable").draggable('enable');
                    lockBtn.addClass('off');
                    lockBtn.html('OFF');

                    selection.find('.screen-lys').hide();
                    selection.find('.screen-material').hide();
                    ips.attr('disabled', null);

                    RT.unlockLayer(cindex, docId, style, mxconf);
                }
            });

            // 缩放窗口

            selection.delegate('.screen-layout-scale-menu-btns .fa', 'click', events.zoomLayout);

        },
        zoomLayout: function () {
            var zbtn = $(this);
            var selection = util.selection(zbtn);
            var opt = util.opt(selection);
            var zoom = selection.find('.scale-zoom input');
            var z = parseInt(zoom.val());
            var goBig = !zbtn.hasClass('fa-search-minus');
            z = z + (zbtn.hasClass('fa-search-minus') ? -1 : 1);
            if (z < 5 && z > 0) {
                zoom.val(z);
                // 调整小窗口
                var zwin = selection.find('.screen-layout-scale-menu-cube');
                if (goBig) {
                    zwin.css(util.zoomOut(util.oldpos(zwin)));
                } else {
                    zwin.css(util.zoomIn(util.oldpos(zwin)));
                }
                // 调整大窗口
                var lypobs = selection.find('.screen-mx-lypobj');
                lypobs.each(function (i, ele) {
                    var ly = $(ele);
                    if (goBig) {
                        ly.css(util.zoomIn(util.oldpos(ly)));
                    } else {
                        ly.css(util.zoomOut(util.oldpos(ly)));
                    }
                });
                opt.zoomNum = z;
            } else {
                alert('抱歉, 超出了缩放范围');
            }
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
            var lastCindex = 0;
            for (var i = 0; i < doclist.length; i++) {
                var cindex = events.addLayer(selection);
                lastCindex = cindex;
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
                            var pobj = ui.helper.data('POBJ');
                            var width = Math.floor(ui.size.width / opt.layout.scaleX);
                            var height = Math.floor(ui.size.height / opt.layout.scaleY);
                            lyinfoW.val(width);
                            lyinfoH.val(height);
                            pobj.mymeta.width = width;
                            pobj.mymeta.height = height;
                            RT.moveLayer(cindex, pobj.mymeta);
                        },
                        stop: function (event, ui) {
                            var pobj = ui.helper.data('POBJ');
                            var width = Math.floor(ui.size.width / opt.layout.scaleX);
                            var height = Math.floor(ui.size.height / opt.layout.scaleY);
                            lyinfoW.val(width);
                            lyinfoH.val(height);
                            pobj.mymeta.width = width;
                            pobj.mymeta.height = height;
                            RT.moveLayer(cindex, pobj.mymeta);
                        }
                    })
                    .draggable({
                        start: function (event, ui) {
                            ui.helper.addClass('moving');
                        },
                        stop: function (event, ui) {
                            ui.helper.removeClass('moving');
                            var pobj = ui.helper.data('POBJ');
                            var left = Math.floor(ui.position.left / opt.layout.scaleX);
                            var top = Math.floor(ui.position.top / opt.layout.scaleY);
                            lyinfoX.val(left);
                            lyinfoY.val(top);
                            pobj.mymeta.top = top;
                            pobj.mymeta.left = left;
                            RT.moveLayer(cindex, pobj.mymeta);
                        },
                        drag: function (event, ui) {
                            var pobj = ui.helper.data('POBJ');
                            var left = Math.floor(ui.position.left / opt.layout.scaleX);
                            var top = Math.floor(ui.position.top / opt.layout.scaleY);
                            lyinfoX.val(left);
                            lyinfoY.val(top);
                            pobj.mymeta.top = top;
                            pobj.mymeta.left = left;
                            RT.moveLayer(cindex, pobj.mymeta);
                        }
                    }).resizable('disable').draggable('disable');


                // 播放
                RT.playLayer(cindex, doc.id, {
                    "height": mi.height,
                    "left": mi.left,
                    "top": mi.top,
                    "width": mi.width
                });
            }

            selection.find('li.screen-mx-ly-' + lastCindex).click();

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

            // 发送命令
            RT.stopLayer();
            resetIndex();
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

            // 发送命令
            RT.stopLayer(cindex);
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
            var pobj = apobj.data('POBJ');
            pobj.mymeta[tp] = val;

            layout.resetCurrentLypobj(selection);

            var mxpobj = selection.find('.screen-lys-list li.active .screen-mx-pobj');
            var cindex = mxpobj.parent().attr('cindex');

            // 也要调用move
            RT.moveLayer(cindex, pobj.mymeta);
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
            selection.find('.screen-layout-scale-menu-cube').css({
                width: lyPos.width / 2,
                height: lyPos.height / 2,
                top: 0,
                left: 0
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

            // screen-lys-list
            var lysList = selection.find('.screen-lys-list');

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

            if (opt.ask) {
                if (confirm('是否进入实时编辑模式')) {
                    stopPlay(function () {
                        RT.init();
                        RT.ping();
                        data.load(selection);
                    });
                } else {
                    // 初始化doc
                    data.load(selection);
                }
            } else {
                stopPlay(function () {
                    RT.init();
                    RT.ping();
                    data.load(selection);
                });

            }
            // 返回支持链式赋值
            return selection;
        }
    });
})
(window.jQuery);
