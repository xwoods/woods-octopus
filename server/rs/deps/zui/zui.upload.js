/**
 * 控件介绍
 *
 * 替换upload         ->      控件名称
 *
 */
(function ($) {
    var UP_FILE = "upload-file";

    function majorName(name) {
        if (name.lastIndexOf('.') != -1) {
            return name.substr(0, name.lastIndexOf('.'));
        }
        return name;
    }

    function suffixName(name) {
        if (name.lastIndexOf('.') != -1) {
            return name.substr(name.lastIndexOf('.') + 1);
        }
        return null;
    }

    function sizeText(size, unit) {
        if (typeof size != "number") size = size * 1;
        if ("M" == unit) {
            var g = size / 1000;
            if (g > 1) return Math.ceil(g * 10) / 10 + " GB";
            return size + "MB";
        }
        if ("K" == unit) {
            var m = size / 1000;
            var g = m / 1000;
            if (g > 1) return Math.ceil(g * 10) / 10 + " GB";
            if (m > 1) return Math.ceil(m * 10) / 10 + " MB";
            return Math.ceil(k) + " KB";
        }
        var k = size / 1000;
        if (k > 1) {
            var m = k / 1000;
            var g = m / 1000;
            if (g > 1) return Math.ceil(g * 10) / 10 + " GB";
            if (m > 1) return Math.ceil(m * 10) / 10 + " MB";
            return Math.ceil(k) + " KB";
        }
        return size + " B";
    }

    // _________________________________
    $.extend({
        upload: function (opt, arg0, arg1) {
            // 初始化模式
            if (typeof opt == "object") {
                opt.upload.unum = 0;
                opt.upload.tpmap = {};
                if (opt.upload.type) {
                    for (var i = 0; i < opt.upload.type.length; i++) {
                        opt.upload.tpmap[opt.upload.type[i]] = true;
                    }
                }
                opt.upload.uploading = false;
                $.masker({
                    title: opt.title,
                    width: opt.width,
                    height: opt.height,
                    closeBtn: true,
                    btns: [
                        {
                            clz: 'btn-upload-file',
                            label: "上传",
                            event: {
                                type: 'click',
                                handle: function (sele) {
                                    if (!opt.upload.uploading) {
                                        uploadFile2Server();
                                    }
                                }
                            }
                        }
                    ],
                    body: function () {
                        var html = '';
                        html += '<div class="upload-file-form">'
                        html += '   <div class="upload-file-tip">';
                        html += '       <div>把文件<b>拖拽到这里</b>, 然后点上传即可</div>'
                        html += '   </div>'
                        html += '   <div class="upload-file-list-container">';
                        html += '       <ul class="upload-file-list ' + (opt.upload.num === 1 ? "single" : "") + '"></ul>';
                        html += '   </div>';
                        html += '</div>'
                        return html;
                    }
                });
                var mdiv = $.masker('get');
                var dropArea = $(".upload-file-form", mdiv);
                var upArea = $(".upload-file-list", mdiv);

                function uploadFile2Server() {
                    var ufiles = upArea.children().not(".uploading").not(".uploaded");
                    if (ufiles.length <= 0) {
                        opt.upload.uploading = false;
                        if (opt.upload.finishUpload) {
                            opt.upload.finishUpload();
                        }
                        return;
                    }
                    var cup = ufiles.first();
                    var cprocess = cup.find('.file-uploading-process');
                    var ctip = cup.find('.file-uploading-tip');
                    var file = cup.data(UP_FILE);
                    cup.addClass('uploading');
                    opt.upload.doUpload(file, mdiv, function (e) {
                        var p = parseInt(e.loaded * 10000 / e.total) / 100 + "%";
                        ctip.html(p);
                        cprocess.css('width', p);
                    }, function () {
                        cup.removeClass('uploading');
                        cup.addClass('uploaded');
                        // 继续下一个
                        uploadFile2Server();
                    });
                }

                function uploadFileHtml(file) {
                    var upH = '';
                    upH += '<li fnm="' + file.name + '" fsz="' + file.size + '">';
                    upH += '    <div class="file-type zui-icon-64 ' + suffixName(file.name) + '" ></div>';
                    upH += '    <div class="file-size">' + sizeText(file.size) + '</div>';
                    upH += '    <div class="file-nm">' + file.name + '</div>';
                    upH += '    <div class="file-del fa fa-lg fa-times"></div>';
                    upH += '    <div class="file-uploading">';
                    upH += '        <div class="file-uploading-process"></div>';
                    upH += '    </div>';
                    upH += '    <div class="file-uploading-tip">0%</div>'
                    upH += '</li>';
                    return upH;
                }

                function addUploadFile(file) {
                    if (checkUploadFile(file)) {
                        if (opt.upload.num != undefined) {
                            if (opt.upload.unum < opt.upload.num) {
                                $(uploadFileHtml(file)).appendTo(upArea).data(UP_FILE, file);
                                opt.upload.unum++;
                            }
                        } else {
                            $(uploadFileHtml(file)).appendTo(upArea).data(UP_FILE, file);
                        }
                    }
                }

                function checkUploadFile(file) {
                    var fnmS = suffixName(file.name);
                    if (fnmS) {
                        if (opt.upload.type) {
                            return opt.upload.tpmap[fnmS] === true;
                        }
                        return true;
                    }
                    return false;
                }

                var events = {
                    dragOver: function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        dropArea.addClass("dragover");
                    },
                    dragLeave: function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        dropArea.removeClass("dragover");
                    },
                    dropFile: function (e) {
                        dropArea.removeClass("dragover");
                        e.stopPropagation();
                        e.preventDefault();
                        if (e.dataTransfer) {
                            var tfiles = e.dataTransfer.files;
                            if (opt.upload.multi) {
                                for (var i = 0; i < tfiles.length; i++) {
                                    addUploadFile(tfiles[i]);
                                }
                            } else {
                                addUploadFile(tfiles[0]);
                            }
                        }
                        return this;
                    },
                    fileDel: function () {
                        $(this).parent().remove();
                        opt.upload.unum--;
                    }
                };
                mdiv.delegate(".upload-file-form", "dragover", events.dragOver);
                mdiv.delegate(".upload-file-form", "dragleave", events.dragLeave);
                mdiv.delegate(".file-del", "click", events.fileDel);
                dropArea[0].addEventListener("drop", events.dropFile, false);
            }
            // 返回支持链式赋值
            return this;
        }
    });
})(window.jQuery);