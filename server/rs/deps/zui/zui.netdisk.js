/**
 * 控件介绍
 *
 * 替换netdisk         ->      控件名称
 *
 */
(function ($) {
    var OPT_NAME = "netdisk_option";
    var SEL_CLASS = ".netdisk";
    var SEL_CLASS_NM = "netdisk";
    var DOC_ITEM = "doc-item";
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
            // TODO
            if (!opt.mode) {
                opt.mode = 'write'
            }
            if (!opt.view) {
                opt.view = 'grid';
            }
            if (!opt.uploadType) {
                opt.uploadType = ['jpg', 'jpeg', 'png', 'gif', 'mp4', 'avi', 'mov', 'mkv', 'mpg', 'mpeg', 'wmv', 'txt', 'sh', 'py', 'go', 'zip', 'tar', 'gz', '7z', 'rar', 'rb', 'json', 'js', 'java', 'c', 'cpp', 'xls', 'xlsx', 'doc', 'docx', 'ppt', 'pptx']
            }
            if (opt.isPrivate == undefined) {
                opt.isPrivate = true;
            }
            var defaultSwitchs = {
                'createFile': false,
                'createFolder': true,
                'rename': true,
                'upload': true,
                'download': true,
                'share': false,
                'delete': true,
                'move': false,
                'copy': false,
                'trans': false,
                'add2Screen': false
            };
            if (opt.switchs) {
                opt.switchs = $.extend({}, defaultSwitchs, opt.switchs);
            } else {
                opt.switchs = defaultSwitchs;
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
        }
    };
    // _________________________________
    var dom = {
        init: function (selection) {
            // 生成html
            dom._initHtml(selection);
        },
        _initHtml: function (selection) {
            var opt = util.opt(selection);
            var html = '';
            html += ' 	<div class="netdisk">';
            html += '        <div class="netdisk-toolbar">';
            if (opt.mode == 'write') {
                if (opt.switchs.upload) {
                    html += '            <ul class="netdisk-toolbar-btns default-btns-upload">';
                    html += '                <li>';
                    html += '                    <span class="fa fa-upload fa-lg"></span>';
                    html += '                    上传';
                    html += '                </li>';
                    html += '            </ul>';
                }
                if (opt.switchs.createFile) {
                    html += '            <ul class="netdisk-toolbar-btns default-btns-create">';
                    html += '                <li>';
                    html += '                    <span class="fa fa-file-o fa-lg"></span>';
                    html += '                    新建';
                    html += '                </li>';
                    html += '            </ul>';
                }
                if (!opt.dwservice) {
                    if (opt.switchs.createFolder) {
                        html += '            <ul class="netdisk-toolbar-btns default-btns-newfolder">';
                        html += '                <li>';
                        html += '                    <span class="fa fa-folder-o fa-lg"></span>';
                        html += '                    新建文件夹';
                        html += '                </li>';
                        html += '            </ul>';
                    }
                }
            }
            html += '            <ul class="netdisk-toolbar-btns select-btns">';
            if (opt.switchs.download) {
                html += '                <li class="single file-download">';
                html += '                    <span class="fa fa-download fa-lg"></span>';
                html += '                    下载';
                html += '                </li>';
            }
            if (opt.mode == 'write') {
                if (opt.switchs.delete) {
                    html += '                <li class="single multi file-delete">';
                    html += '                    <span class="fa fa-trash fa-lg"></span>';
                    html += '                    删除';
                    html += '                </li>';
                }
                if (opt.switchs.rename) {
                    html += '                <li class="single file-rename">';
                    html += '                    <span class="fa fa-pencil-square-o fa-lg"></span>';
                    html += '                    重命名';
                    html += '                </li>';
                }
                if (!opt.dwservice) {
                    if (opt.switchs.share) {
                        html += '                <li class="single file-share">';
                        html += '                    <span class="fa fa-share-alt fa-lg"></span>';
                        html += '                    分享';
                        html += '                </li>';
                    }
                    if (opt.switchs.move) {
                        html += '                <li class="multi single file-move">';
                        html += '                    <span class="fa fa-arrows fa-lg"></span>';
                        html += '                    移动到';
                        html += '                </li>';
                    }
                    if (opt.switchs.copy) {
                        html += '                <li class="single file-copy">';
                        html += '                    <span class="fa fa-copy fa-lg"></span>';
                        html += '                    制作副本';
                        html += '                </li>';
                    }
                    if (opt.switchs.trans) {
                        html += '                <li class="single file-trans">';
                        html += '                    <span class="fa fa-exchange fa-lg"></span>';
                        html += '                    转换为';
                        html += '                </li>';
                    }
                    if (opt.switchs.add2Screen) {
                        html += '                <li class="single multi file-add2screen">';
                        html += '                    <span class="fa fa-plus-circle fa-lg"></span>';
                        html += '                    添加到屏幕';
                        html += '                </li>';
                    }
                }
            }
            html += '            </ul>';
            if (opt.dwservice) {
                html += '<span class="dwservice-url">';
                html += 'http://' + window.location.host + '/dw?fnm=' + '<em>文件名</em>';
                html += '</span>';
            }
            html += '            <ul class="netdisk-toolbar-btns list-mode-switch">';
            html += '                <li mode="grid" class="' + (opt.view == 'grid' ? "active" : "") + '">';
            html += '                    <span class="fa fa-th-large fa-lg"></span>';
            html += '                </li>';
            html += '                <li mode="list" class="' + (opt.view != 'grid' ? "active" : "") + '">';
            html += '                    <span class="fa fa-th-list fa-lg"></span>';
            html += '                </li>';
            html += '            </ul>';
            html += '        </div>';
            html += '        <div class="netdisk-crumbs">';
            html += '            <ul>';
            html += '               <li module="' + opt.root.module + '" mkey="' + (opt.root.moduleKey ? opt.root.moduleKey : "") + '" pid="' + (opt.root.pid ? opt.root.pid : "") + '">根目录</li>';
            html += '            </ul>';
            html += '            <div class="netdisk-list-num">共 <span>0</span> 个文件</div>';
            html += '        </div>';
            html += '        <div class="netdisk-list-toolbar">';
            if (opt.multisel) {
                html += '            <input type="checkbox" class="check-all-file">';
            }
            html += '            <ul class="cate-filter">';
            html += '                   <li class="fa fa-folder-o fa-lg" cate="folder"></li>';
            html += '                   <li class="fa fa-file-image-o fa-lg" cate="image"></li>';
            html += '                   <li class="fa fa-file-video-o fa-lg" cate="video"></li>';
            html += '                   <li class="fa fa-file-audio-o fa-lg" cate="audio"></li>';
            html += '                   <li class="fa fa-file-word-o fa-lg" cate="office"></li>';
            html += '                   <li class="fa fa-file-code-o fa-lg" cate="code"></li>';
            html += '            </ul>';
            html += '        </div>';
            html += '        <div class="netdisk-list ' + (opt.view == 'list' ? "list-view" : "") + '">';
            html += '            <ul>';
            html += '            </ul>';
            html += '        </div>';
            html += '        <div class="netdisk-loading"><span class="fa fa-refresh fa-spin-05s fa-1x"></span> 努力加载中...</div>';
            html += '    </div>';
            selection.empty().append(html);
        },
        updateListItem: function (ulJq, dlist) {
            for (var i = 0; i < dlist.length; i++) {
                var lhJq = $(dom.listItemHtml(dlist[i]));
                lhJq.appendTo(ulJq).data(DOC_ITEM, dlist[i]);
            }
        },
        listItemHtml: function (doc) {
            var html = '';
            html += '    <li class="file-cate-' + doc.cate + '" docId="' + doc.id + '">';
            html += '         <input type="checkbox" class="list-chk">';
            //html += '   <div class="file-icons" >';
            if (doc.hasPreview) {
                html += '         <img class="file-type" src="/doc/preview/' + doc.id + '?' + new Date().getTime() + '">';
                if (doc.hasTrans && 'video' == doc.cate) {
                    var mi = $z.util.str2json(doc.meta);
                    if (!doc.transDone) {
                        html += '   <div class="file-tip video-trans-stat"><span class="fa fa-refresh fa-lg"></span></div>';
                    }
                    if (mi.transCutX > 1 && mi.transCutY > 1) {
                        html += '   <div class="file-tip video-cutAs">' + mi.transCutX + "x" + mi.transCutY + '</div>';
                    }
                    html += '</img>';
                }
            } else {
                html += '         <div class="file-type zui-icon-64 ' + doc.type + '"></div>';
            }
            //html += '   <div>';
            // TODO doc.hasTrans
            html += '         <div class="file-type-list zui-icon-24 ' + doc.type + '"></div>';
            html += '         <div class="file-nm" fnm="' + doc.name + '"><span>';
            var fnm = doc.name;
            if (fnm.length > 25) {
                fnm = fnm.substr(0, 18) + "..." + fnm.substr(fnm.length - 4);
            }
            if (doc.cate == "folder") {
                html += fnm
            } else {
                html += fnm + '.' + doc.type;
            }
            html += '</span></div>';
            html += '         <div class="file-size">' + $z.util.sizeText(doc.size) + '</div>';
            html += '         <div class="file-createTime">' + doc.createTime + '</div>';
            html += '    </li>';
            return html;
        }
    };
    // _________________________________
    var data = {
        init: function (selection) {
            data.refresh(selection);
        },
        dm: function (selection) {
            var croot = selection.find('.netdisk-crumbs ul li').last();
            var module = croot.attr('module');
            var mkey = croot.attr('mkey');
            var pid = croot.attr('pid');
            var cateJq = selection.find('.cate-filter li.active');
            var cate = cateJq.length > 0 ? cateJq.attr('cate') : null;
            return {
                'module': module,
                'moduleKey': mkey,
                'pid': pid,
                'cate': cate
            }
        },
        refresh: function (selection) {
            var opt = util.opt(selection);
            // 准换更新
            var loadingJq = selection.find('.netdisk-loading');
            var ndlistJq = selection.find('.netdisk-list ul');
            var ndlistNum = selection.find('.netdisk-list-num span');
            ndlistNum.html('0');
            ndlistJq.empty();
            loadingJq.show();

            var dm = data.dm(selection);
            data.loadDocument(dm, function (dlist) {
                opt.dlist = dlist;
                // 缓存
                setDocs(dlist);
                // 更新显示
                ndlistNum.html(opt.dlist.length);
                loadingJq.hide();
                dom.updateListItem(ndlistJq, opt.dlist);
                // 所有勾选, 全部取消
                selection.find('.select-btns').removeClass('single').removeClass('multi');
                var call = selection.find('.check-all-file');
                if (call.length != 0) {
                    call.prop('checked', false);
                }
            });
        },
        loadDocument: function (dm, callback) {
            $z.http.post('/doc/list', dm, function (re) {
                callback(re.data);
            });
        }
    };
// _________________________________
    var events = {
        unbind: function (selection) {
            selection.undelegate();
        },
        bind: function (selection) {
            // selection.delegate('', '', events.;

            var nlist = selection.find('.netdisk-list');
            var selectBtns = selection.find('.select-btns');

            // 全选
            selection.delegate('.check-all-file', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var chk = $(this);
                if (chk.prop('checked')) {
                    nlist.find('input[type=checkbox]').prop('checked', true);
                } else {
                    nlist.find('input[type=checkbox]').prop('checked', false);
                }
                events.checkSelBtns(selection);
            });


            // 单选 or 多选
            selection.delegate('.list-chk', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var chk = $(this);
                if (chk.prop('checked')) {
                    if (!opt.multisel) {
                        // 其他选中的取消掉
                        nlist.find('input[type=checkbox]:checked').prop('checked', false);
                        chk.prop('checked', true);
                    }
                } else {
                    var call = selection.find('.check-all-file');
                    if (call.length != 0) {
                        call.prop('checked', false);
                    }
                }
                events.checkSelBtns(selection);
            });


            // 切换列表布局
            selection.delegate('.netdisk-toolbar-btns.list-mode-switch li', 'click', function () {
                var cli = $(this);
                if (!cli.hasClass('active')) {
                    cli.siblings().removeClass('active');
                    cli.addClass('active');
                    if (cli.attr('mode') == 'list') {
                        selection.find('.netdisk-list').addClass('list-view');
                    } else {
                        selection.find('.netdisk-list').removeClass('list-view');
                    }
                }
            });

            // 选中cate
            selection.delegate('.cate-filter li', 'click', function () {
                var selection = util.selection(this);
                var cli = $(this);
                if (!cli.hasClass('active')) {
                    cli.siblings().removeClass('active');
                    cli.addClass('active');
                } else {
                    cli.removeClass('active');
                }
                data.refresh(selection);
            });

            // 新建文件夹
            selection.delegate('.default-btns-newfolder', 'click', function () {
                var selection = util.selection(this);
                var folderNm = prompt("请输入文件夹的名称", "新建文件夹");
                if ($z.util.isBlank(folderNm)) {
                    return;
                }
                var dm = data.dm(selection);
                events.createNewFile(dm, folderNm, 'dir', function (doc) {
                    if (doc != null) {
                        data.refresh(selection);
                    }
                });
            });

            // 新建文件
            selection.delegate('.default-btns-create', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var createType = opt.createType;
                var fileNm = prompt("请输入" + createType[0] + "格式的文件名称", "未命名");
                if ($z.util.isBlank(fileNm)) {
                    return;
                }
                var dm = data.dm(selection);
                events.createNewFile(dm, fileNm, createType[0], function (doc) {
                    if (doc != null) {
                        data.refresh(selection);
                    }
                });
            });

            // 上传文件
            selection.delegate('.default-btns-upload', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var dm = data.dm(selection);
                $.upload({
                    title: "上传文件",
                    width: "90%",
                    height: "80%",
                    upload: {
                        multi: true,
                        type: opt.uploadType,
                        doUpload: function (file, upJq, progress, callback) {
                            var xhr = new XMLHttpRequest();
                            if (!xhr.upload) {
                                alert("XMLHttpRequest object don't support upload for your browser!!!");
                                return;
                            }
                            xhr.upload.addEventListener("progress", function (e) {
                                progress(e);
                            }, false);
                            xhr.onreadystatechange = function (e) {
                                if (xhr.readyState == 4) {
                                    if (xhr.status == 200) {
                                        callback();
                                    } else {
                                        alret('Fail to upload "' + file.name + '"\n\n' + xhr.responseText);
                                    }
                                }
                            };
                            // 准备请求对象头部信息
                            var contentType = "application/x-www-form-urlencoded; charset=utf-8";
                            xhr.open("POST", "/doc/bin/add", true);
                            xhr.setRequestHeader('Content-type', contentType);
                            xhr.setRequestHeader('module', dm.module);
                            xhr.setRequestHeader('moduleKey', encodeURI(dm.moduleKey));
                            xhr.setRequestHeader('pid', dm.pid);
                            xhr.setRequestHeader("fnm", "" + encodeURI(file.name));
                            xhr.setRequestHeader('isPrivate', opt.isPrivate);
                            xhr.send(file);
                        },
                        finishUpload: function () {
                            $.masker('close');
                            data.refresh(selection);
                        }
                    }
                });
            });

            var openFile = function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var doc = $(this).parent().data(DOC_ITEM);
                if (doc.cate == "folder") {
                    // 进入该目录
                    var pul = selection.find('.netdisk-crumbs ul');
                    var nli = $('<li module="" mkey="" pid="' + doc.id + '">' + doc.name + '</li>');
                    nli.appendTo(pul);
                    data.refresh(selection);
                } else {
                    // 预览还是修改
                    // TODO 这里需要大修改
                    if (doc.type == "js" || doc.type == "json" || doc.type == "screen") {
                        $.zedit({
                            'doc': doc
                        });
                    } else {
                        $.zpreview({
                            "doc": doc
                        });
                    }
                }
            };

            // 打开文件
            selection.delegate('.file-nm', 'click', openFile);
            selection.delegate('img.file-type', 'click', openFile);

            // 切换显示目录
            selection.delegate('.netdisk-crumbs ul li', 'click', function () {
                var cli = $(this);
                if (cli.siblings().length == 0) {
                    // 根目录
                    return;
                }
                if (cli.next().length == 0) {
                    // 最后一个, 当前目录
                    return;
                }
                cli.nextAll().remove();
                data.refresh(selection);
            });

            // 分享文件
            selection.delegate('.file-share', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var sfile = selection.find('.netdisk-list input[type=checkbox]:checked').first();
                var doc = sfile.parent().data(DOC_ITEM);
                // TODO
                alert('该功能未完成');
            });

            // 制作副本
            selection.delegate('.file-copy', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var sfile = selection.find('.netdisk-list input[type=checkbox]:checked').first();
                var doc = sfile.parent().data(DOC_ITEM);
                if (doc.readAs == "DIR" || doc.readAs == "CPX") {
                    alert("暂时不支持文件夹")
                    return;
                }
                $z.http.post('/doc/copy', {'docId': doc.id}, function (re) {
                    data.refresh(selection);
                });
            });

            // 添加到屏幕
            selection.delegate('.file-add2screen', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var dflist = selection.find('.netdisk-list input[type=checkbox]:checked');
                var dfarray = [];
                var hasDir = false;
                var notTransDone = false;
                dflist.each(function () {
                    var doc = $(this).parent().data(DOC_ITEM);
                    if (doc.readAs == "DIR" || doc.readAs == "CPX") {
                        hasDir = true;
                        return;
                    }
                    if (doc.hasTrans && 'video' == doc.cate && !doc.transDone) {
                        notTransDone = true;
                        return;
                    }
                    dfarray.push(doc);
                });
                if (notTransDone) {
                    alert("不能添加未转化完成的视频");
                    return;
                }
                if (hasDir) {
                    alert("暂时不支持添加文件夹, 请修改选中项目")
                    return;
                }
                opt.events.add2Screen(dfarray);
                //
                var allchkbtn = selection.find('.check-all-file');
                if (allchkbtn.prop('checked')) {
                    allchkbtn.click();
                } else {
                    allchkbtn.click().click();
                }
            });

            // 转换文件
            selection.delegate('.file-trans', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var sfile = selection.find('.netdisk-list input[type=checkbox]:checked').first();
                var doc = sfile.parent().data(DOC_ITEM);
                if (doc.cate == "video") {
                    var mi = $z.util.str2json(doc.meta);
                    if (mi.transCutX != 1 && mi.transCutY != 1) {
                        alert("抱歉, 只有原版视频支持转换操作");
                        return;
                    }
                    if (doc.transFail) {
                        alert("当前视频转换存在问题, 请检查!");
                        return;
                    }
                    if (!doc.transDone) {
                        alert("当前视频转换正在进行转换, 请稍后在操作");
                        return;
                    }

                    var cutMax = 10;

                    var ow = mi.width;
                    var oh = mi.height;
                    var nw = mi.width;
                    var nh = mi.height;
                    var ncutX = 1;
                    var ncutY = 1;

                    // 进行新的切割
                    $.masker({
                        title: "视频切割",
                        width: 300,
                        height: 300,
                        closeBtn: true,
                        btns: [{
                            clz: 'btn-video-trans',
                            label: "开始转换",
                            event: {
                                type: 'click',
                                handle: function (sele) {
                                    if (ncutX == 1 && ncutY == 1) {
                                        alert('额...没有分割吧, 还是1x1的视频');
                                        return;
                                    }
                                    $z.http.post('/doc/trans/video', {
                                        'docId': doc.id,
                                        'cutX': ncutX,
                                        "cutY": ncutY
                                    }, function (re) {
                                        $.masker('close');
                                        data.refresh(selection);
                                    });
                                }
                            }
                        }],
                        body: function () {
                            var html = '';
                            html += '<div class="trans-form-video">'
                            html += '   <div class="trans-label">分割前</div>';
                            html += '   <div class="trans-info before">';
                            html += '       <div class="trans-cutAs">';
                            html += '           <b>大小</b>';
                            html += '           <input class="cutX" value="' + mi.transCutX + '" disabled> x';
                            html += '           <input class="cutY" value="' + mi.transCutX + '" disabled>';
                            html += '       </div>'
                            html += '       <div class="trans-wh">';
                            html += '           <b>分辨率</b>';
                            html += '           <input class="twidth" value="' + mi.width + '" disabled> x';
                            html += '           <input class="theight" value="' + mi.height + '" disabled>';
                            html += '       </div>'
                            html += '   </div>'
                            html += '   <div class="trans-label ">分割后</div>';
                            html += '   <div class="trans-info after">';
                            html += '       <div class="trans-cutAs">';
                            html += '           <b>大小</b>';
                            html += '           <input class="cutX" value="' + mi.transCutX + '" type="number" min="1" max="' + cutMax + '"> x';
                            html += '           <input class="cutY" value="' + mi.transCutX + '" type="number" min="1" max="' + cutMax + '">';
                            html += '       </div>'
                            html += '       <div class="trans-wh">';
                            html += '           <b>分辨率</b>';
                            html += '           <input class="twidth" value="' + mi.width + '" disabled> x';
                            html += '           <input class="theight" value="' + mi.height + '" disabled>';
                            html += '       </div>'
                            html += '   </div>'
                            html += '</div>'
                            return html;
                        },
                        afterDomReady: function (mdiv) {
                            var atw = mdiv.find('.trans-info.after .twidth');
                            var ath = mdiv.find('.trans-info.after .theight');
                            mdiv.delegate('.trans-info.after .cutX', 'change', function () {
                                var cx = $(this);
                                if (isNaN(parseInt(cx.val()))) {
                                    cx.val(1);
                                }
                                ncutX = parseInt(cx.val());
                                nw = ncutX * ow;
                                atw.val(nw);
                            });
                            mdiv.delegate('.trans-info.after .cutY', 'change', function () {
                                var cy = $(this);
                                if (isNaN(parseInt(cy.val()))) {
                                    cy.val(1);
                                }
                                ncutY = parseInt(cy.val());
                                nh = parseInt(cy.val()) * oh;
                                ath.val(nh);
                            });
                        }
                    });
                } else {
                    alert("抱歉, 目前只有视频类型文件支持转换操作");
                }
            });

            // 重命名文件
            selection.delegate('.file-rename', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                var sfile = selection.find('.netdisk-list input[type=checkbox]:checked').first();
                var doc = sfile.parent().data(DOC_ITEM);

                var oldName = doc.name;
                var newName = prompt("重命名后点击确认", oldName);
                if (!$z.util.isBlank(newName) && oldName != newName) {
                    $z.http.post('/doc/rename', {'docId': doc.id, 'docName': newName}, function (re) {
                        var newName2 = re.data;
                        if (newName2 != newName) {
                            alert("文件 " + newName + " 已经存在, 新名称被修改为 " + newName2);
                        }
                        doc.name = newName2;
                        var fnm = doc.name;
                        sfile.parent().find('.file-nm').prop('fnm', fnm);
                        if (fnm.length > 25) {
                            fnm = fnm.substr(0, 18) + "..." + fnm.substr(fnm.length - 4);
                        }
                        if (doc.cate != "folder") {
                            fnm += '.' + doc.type;
                        }
                        sfile.parent().find('.file-nm span').html(fnm);
                    });
                }
            });

            // 下载文件
            selection.delegate('.file-download', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                // FIXME 暂时仅仅支持单文件下载
                var sfile = selection.find('.netdisk-list input[type=checkbox]:checked').first();
                var doc = sfile.parent().data(DOC_ITEM);
                if (doc.cate == 'folder') {
                    alert('暂时不支持文件夹下载');
                    return;
                }
                window.location.href = "/doc/bin/read?docId=" + doc.id;
            });

            // 删除文件
            selection.delegate('.file-delete', 'click', function () {
                var selection = util.selection(this);
                var opt = util.opt(selection);
                // FIXME 暂时仅仅支持单文件下载
                var dflist = selection.find('.netdisk-list input[type=checkbox]:checked');
                var dfarray = [];
                var dfmap = {};
                dflist.each(function () {
                    var doc = $(this).parent().data(DOC_ITEM);
                    dfarray.push(doc.id);
                    dfmap[doc.id] = doc;
                })
                var sureDelete = confirm("确定要删除 " + dfarray.length + " 个文件吗?");
                if (sureDelete) {
                    $.masker({
                        title: "删除文件",
                        width: "70%",
                        height: "70%",
                        body: function () {
                            var html = '';
                            html += '<div class="netdisk-delete-list">'
                            html += '<div class="netdisk-delete-list-log">' + addLog("开始删除以下文件:") + "</div>";
                            html += '<div class="netdisk-delete-list-log"></div>';
                            for (var i = 0; i < dfarray.length; i++) {
                                html += '<div class="netdisk-delete-list-log">' + addLog((i + 1) + ". " + dfmap[dfarray[i]].name) + "</div>";
                            }
                            html += '<div class="netdisk-delete-list-log"></div>';
                            html += '<div class="netdisk-delete-list-log">' + addLog("努力删除中, 请耐心等待....") + "</div>";
                            html += '<div class="netdisk-delete-list-log"></div>';
                            html += '</div>'
                            return html;
                        }
                    });
                    var mdiv = $.masker('get');
                    var dllog = mdiv.find('.netdisk-delete-list');
                    $z.http.post('/doc/delete', {'docId': dfarray.join(',')}, function (re) {
                        var smap = re.data;
                        for (var i = 0; i < dfarray.length; i++) {
                            var delSucc = smap[dfarray[i]];
                            var delDoc = dfmap[dfarray[i]];
                            var html = '';
                            html += '<div class="netdisk-delete-list-log">';
                            html += addLog('<b>' + (delSucc ? "成功" : "失败") + "</b> " + (i + 1) + ". " + delDoc.name);
                            html += '</div>';
                            dllog.append(html);
                        }
                        $.masker('addCloseBtn');
                        data.refresh(selection);
                    });
                }
            });

            function addLog(log) {
                var dt = new Date();
                var ymd = "" + dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDay();
                var hms = "" + dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
                return ymd + " " + hms + " " + log;
            }
        },
        createNewFile: function (dm, fnm, ftp, callback) {
            $z.http.post('/doc/create', $.extend(dm, {
                'fnm': fnm,
                'ftp': ftp
            }), function (re) {
                callback(re.data);
            });
        },
        // 检查选中情况, 显示不同的按钮
        checkSelBtns: function (selection) {
            var opt = util.opt(selection);
            var nlist = selection.find('.netdisk-list');
            var selectBtns = selection.find('.select-btns');
            var dwurl = selection.find('.dwservice-url em');
            var slit = nlist.find('input[type=checkbox]:checked');
            var snum = slit.length;
            if (snum == 0) {
                selectBtns.removeClass('multi').removeClass('single');
                dwurl.html('文件名');
            }
            else if (snum == 1) {
                selectBtns.removeClass('multi').addClass('single');
                if (opt.dwservice) {
                    var dwDoc = slit.first().parent().data(DOC_ITEM);
                    var dwName = dwDoc.name;
                    if (dwDoc.type) {
                        dwName += '.' + dwDoc.type;
                    }
                    dwurl.html(dwName);
                }
            }
            else if (snum > 1) {
                selectBtns.removeClass('single').addClass('multi');
            }

            // 勾选中全选按钮
            if (opt.dlist.length == snum && snum > 0) {
                var call = selection.find('.check-all-file');
                if (call.length != 0) {
                    call.prop('checked', true);
                }
            }
        }
    };
// _________________________________
    var layout = {
        resize: function (selection) {
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
        seleFiles: function () {
            var selection = this;
            var opt = util.opt(selection);
            var sfiles = selection.find('.netdisk-list input[type=checkbox]:checked');
            var dlist = [];
            sfiles.each(function (i, ele) {
                dlist.push($(ele).parent().data(DOC_ITEM));
            });
            return dlist;
        },
        refresh: function () {
            data.refresh(this);
        },
        reload: function (module, mkey, pid, name) {
            var selection = this;
            var cul = selection.find('.netdisk-crumbs ul');
            cul.empty();
            if ($z.util.isBlank(name)) {
                name = "全部文件"
            }
            cul.append('<li module="' + module + '" mkey="' + (mkey ? mkey : "") + '" pid="' + (pid ? pid : "") + '">' + name + '</li>');
            data.refresh(selection);
        },
        listHeight: function () {
            var sel = this;
            var maxHeight = sel.height();
            sel.find('.netdisk-list').css('height', maxHeight - 90).css('overflow-y', 'auto');
        }
    };
// _________________________________
    $.fn.extend({
        netdisk: function (opt, arg0, arg1, arg2, arg3, arg4) {
            var selection = this;
            // 检查有效选区
            if (selection.size() == 0)
                return selection;
            // 命令模式
            if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.netdisk: don't support command '" + opt + "'";
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
