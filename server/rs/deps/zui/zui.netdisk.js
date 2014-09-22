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
            html += '            <ul class="netdisk-toolbar-btns default-btns-upload">';
            html += '                <li>';
            html += '                    <span class="fa fa-upload fa-lg"></span>';
            html += '                    上传';
            html += '                </li>';
            html += '            </ul>';
            html += '            <ul class="netdisk-toolbar-btns default-btns-newfolder">';
            html += '                <li>';
            html += '                    <span class="fa fa-folder-o fa-lg"></span>';
            html += '                    新建文件夹';
            html += '                </li>';
            html += '            </ul>';
            html += '            <ul class="netdisk-toolbar-btns select-btns">';
            html += '                <li class="single file-download">';
            html += '                    <span class="fa fa-download fa-lg"></span>';
            html += '                    下载';
            html += '                </li>';
            html += '                <li class="single multi file-delete">';
            html += '                    <span class="fa fa-trash fa-lg"></span>';
            html += '                    删除';
            html += '                </li>';
            html += '                <li class="single file-share">';
            html += '                    <span class="fa fa-share-alt fa-lg"></span>';
            html += '                    分享';
            html += '                </li>';
            html += '                <li class="single file-rename">';
            html += '                    <span class="fa fa-pencil-square-o fa-lg"></span>';
            html += '                    重命名';
            html += '                </li>';
            html += '                <li class="multi file-move">';
            html += '                    <span class="fa fa-arrows-alt fa-lg"></span>';
            html += '                    移动';
            html += '                </li>';
            html += '            </ul>';
            html += '            <ul class="netdisk-toolbar-btns list-mode-switch">';
            html += '                <li class="active" mode="grid">';
            html += '                    <span class="fa fa-th-large fa-lg"></span>';
            html += '                </li>';
            html += '                <li mode="list">';
            html += '                    <span class="fa fa-th-list fa-lg"></span>';
            html += '                </li>';
            html += '            </ul>';
            html += '        </div>';
            html += '        <div class="netdisk-crumbs">';
            html += '            <ul>';
            html += '               <li module="' + opt.root.module + '" mkey="' + opt.root.moduleKey + '" pid="' + opt.root.pid + '">全部文件</li>';
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
            html += '        <div class="netdisk-list">';
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
            // FIXME 这样写太不对啦
            if (window.myConf.hasPreview(doc.type)) {
                html += '         <img class="file-type" src="/doc/preview/' + doc.id + '">';
            } else {
                html += '         <div class="file-type zui-icon-64 ' + doc.type + '"></div>';
            }
            html += '         <div class="file-type-list zui-icon-24 ' + doc.type + '"></div>';
            html += '         <div class="file-nm">' + doc.name + '</div>';
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
            var module = croot.attr('module') == 'undefined' ? '' : croot.attr('module');
            var mkey = croot.attr('mkey') == 'undefined' ? '' : croot.attr('mkey');
            var pid = croot.attr('pid') == 'undefined' ? '' : croot.attr('pid');
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

            // 上传文件
            selection.delegate('.default-btns-upload', 'click', function () {
                var selection = util.selection(this);
                var dm = data.dm(selection);
                $.upload({
                    title: "上传文件",
                    width: "90%",
                    height: "80%",
                    upload: {
                        multi: true,
                        type: ['jpg', 'jpeg', 'png', 'gif', 'mp4', 'avi', 'mov', 'mkv', 'zip', 'tar', 'rar', 'txt', 'xls', 'xlsx', 'doc', 'docx', 'ppt', 'pptx'],
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
                            xhr.setRequestHeader('moduleKey', dm.moduleKey);
                            xhr.setRequestHeader('pid', dm.pid);
                            xhr.setRequestHeader("fnm", "" + encodeURI(file.name));
                            xhr.setRequestHeader('isPrivate', "true");
                            xhr.send(file);
                        },
                        finishUpload: function () {
                            $.masker('close');
                            data.refresh(selection);
                        }
                    }
                });
            });

            // 打开文件
            selection.delegate('.file-nm', 'click', function () {
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
                    if (doc.cate == "image") {
                        $.masker({
                            title: "图片名称: " + doc.name,
                            closeBtn: true,
                            width: "80%",
                            height: "80%",
                            body: function () {
                                var html = '';
                                html += '<div class="open-file-container">'
                                html += '   <img class="open-file" src="/doc/bin/read?docId=' + doc.id + '" >';
                                html += '</div>'
                                return html;
                            }
                        });
                        return;
                    }
                    // 预览
                    alert('暂时还不支持打开预览, 请下载到本地查看.')
                }
            });

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
            var snum = nlist.find('input[type=checkbox]:checked').length;
            if (snum == 0) {
                selectBtns.removeClass('multi').removeClass('single');
            }
            else if (snum == 1) {
                selectBtns.removeClass('multi').addClass('single');
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
                dlist.push($(ele).data(DOC_ITEM));
            });
            return dlist;
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