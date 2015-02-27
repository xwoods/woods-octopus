(function ($) {
    var editPlugins = {
        'js': {
            bodyHtml: function (doc) {
                var html = '';
                html += '<div class="edit-title-bar">';
                html += '</div>';
                html += '<div class="edit-body">';
                html += '</div>';
                return html;
            }
        },
        'screen': {
            bodyHtml: function (doc) {
                var html = '';
                html += '<div class="edit-title-bar">';
                html += '   <div class="edit-doc-name">' + doc.name + "." + doc.type + '</div>';
                html += '   <div class="edit-btn screen-sourcecode">查看源码</div>';
                html += '   <div class="edit-btn screen-save">保存</div>';
                html += '</div>';
                html += '<div class="edit-body">';
                html += '   <div class="edit-container">'
                html += '       <div class="screen-layout">';
                html += '           <div class="screen-main">';
                html += '               <div class="screen-dashboard-menu">';
                html += '                   <span class="screen-mx-label">矩阵大小</span>';
                html += '                   <span class="screen-mx-attr">';
                html += '                       <input class="screen-mx-size-x" value="1"> x <input class="screen-mx-size-y" value="1">';
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
                html += '                       <input class="screen-mx-layout-assist" type="checkbox">';
                html += '                   </span>';
                html += '               </div>';
                html += '               <div class="screen-dashboard">';
                html += '                   <div class="screen-canvas">';
                html += '                   </div>';
                html += '               </div>';
                html += '               <div class="screen-timeline">';
                html += '               </div>';
                html += '           </div>';
                html += '           <div class="screen-lys">';
                html += '               <div class="screen-lys-btn-add">';
                html += '                   <i class="fa fa-plus-circle fa-1x"></i> 新建层';
                html += '               </div>';
                html += '               <ul class="screen-lys-list">';
                html += '                   <li>';
                html += '                       <div class="ly-del"></div>';
                html += '                       <div class="ly-name">测试一层</div>';
                html += '                   </li>';
                html += '                   <li class="active">';
                html += '                       <div class="ly-del"></div>';
                html += '                       <div class="ly-name">测试2层</div>';
                html += '                   </li>';
                html += '                   <li>';
                html += '                       <div class="ly-del"></div>';
                html += '                       <div class="ly-name">测试3层</div>';
                html += '                   </li>';
                html += '               </ul>';
                html += '           </div>';
                html += '           <div class="screen-material">';
                html += '           </div>';
                html += '       </div>';
                html += '       <div class="source-layout">';
                html += '       </div>';
                html += '   </div>';
                html += '</div>';
                return html;
            },
            domReady: function (mdiv, doc) {
                var material = mdiv.find('.screen-material');
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
                            // 添加到当前的时间线中
                            alert('选中' + doclist.length + "个素材");
                        }
                    }
                });
                material.netdisk('listHeight');
                mdiv.find('.screen-lys-list').sortable({
                    start: function () {
                    },
                    stop: function (e, ui) {
                    }
                });
            },
            resize: function (mdiv) {
                var scrnMain = mdiv.find('.screen-main');
                var scrnDash = mdiv.find('.screen-dashboard');
                var scrnTimeline = mdiv.find('.screen-timeline');
                var scrnDashMenu = mdiv.find('.screen-dashboard-menu');
                scrnDash.css({
                    'height': (scrnMain.height() - scrnDashMenu.height() - scrnTimeline.height())
                });
            }
        }
    };

    //_________________________________
    $.extend({
        zedit: function (opt, arg0, arg1) {
            // 初始化模式
            if (typeof opt == "object") {
                var doc = opt.doc;
                // 初始化目录
                $.masker({
                    closeBtn: false,
                    width: "98%",
                    height: "98%",
                    body: function () {
                        return "";
                    },
                    afterDomReady: function (mdiv) {
                        var selection = mdiv.find('.masker-body');
                        // screen
                        if (doc.type == 'screen') {
                            selection.zscreen({
                                'doc': doc
                            });
                            return
                        }
                        // 文本格式
                        if (doc.cate == 'text' || doc.cate == 'code') {
                            selection.ztext({
                                'doc': doc
                            });
                            return
                        }
                        // 其他
                        alert('其他暂时没有实现');
                    }
                });
            }
            return;
        }
    });
})
(window.jQuery);