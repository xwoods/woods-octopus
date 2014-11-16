/**
 * 控件介绍
 *
 * 替换zpreview         ->      控件名称
 *
 */
(function ($) {

    var codeHighLight = {
        'py': "python",
        'sh': "bash",
        'go': "go",
        'java': "java",
        'js': 'javascript',
        'html': 'markup',
        'svg': 'markup',
        'xml': 'markup',
        'txt': 'markup',
        'text': 'markup',
        'rb': 'ruby'
    };


    function showDoc(mdiv, doc) {
        var meta = $z.util.str2json(doc.meta);
        var dshow = mdiv.find('.zpreview-doc-show');
        var dsz = selSize(dshow);
        var html = '';
        if ("image" == doc.cate) {
            var pp = selPos(dsz.width, dsz.height, meta.width, meta.height);
            html += '<img src="/doc/bin/read?docId=' + doc.id + '"';
            html += ' style="display: block; position: absolute; width: ' + pp.w + 'px; height: ' + pp.h + 'px; top: ' + pp.top + 'px; left: ' + pp.left + 'px;"'
            html += '>';
        }
        else if ("video" == doc.cate) {
            if (doc.transDone == true) {
                var pp = selPos(dsz.width, dsz.height, meta.previewWidth, meta.previewHeight);
                html += '<video id="' + doc.id + '" class="video-js vjs-default-skin vjs-controls-enabled vjs-has-started vjs-user-active vjs-playing"';
                html += '   style="display: block; position: absolute; width: ' + pp.w + 'px; height: ' + pp.h + 'px; top: ' + pp.top + 'px; left: ' + pp.left + 'px;"';
                html += '   controls preload="auto" width="' + pp.w + '" height="' + pp.h + '"';
                html += '   poster="/doc/preview-video-poster/' + doc.id + '"';
                html += '   data-setup="{}">';
                html += '   <source src="/doc/preview-video/' + doc.id + '" type="video/mp4" />';
                html += '</video>';
            } else {
                // 未转换完毕
                var pp = selPos(dsz.width, dsz.height, meta.thumbWidth, meta.thumbHeight);
                html += '<img src="/doc/preview/' + doc.id + '"';
                html += ' style="display: block; position: absolute; width: ' + pp.w + 'px; height: ' + pp.h + 'px; top: ' + pp.top + 'px; left: ' + pp.left + 'px;"';
                html += '>';
            }
        }
        //else if ("text" == doc.cate) {
        //    html += '<textarea value="loading" class="zpreview-doc-text"';
        //    html += ' style="display: block; position: absolute; width: ' + dsz.width + 'px; height: ' + dsz.height + 'px; top: ' + 0 + 'px; left: ' + 0 + 'px;"';
        //    html += '></textarea>';
        //}
        else if ("text" == doc.cate || "code" == doc.cate) {
            html += '<pre data-start="1" class="zpreview-doc-code line-numbers"';
            html += ' style="display: block; position: absolute; width: ' + dsz.width + 'px; height: ' + dsz.height + 'px; top: ' + 0 + 'px; left: ' + 0 + 'px;"';
            html += '><code class="language-' + ("text" == doc.cate ? codeHighLight['text'] : codeHighLight[doc.type]) + '"></code></pre>';
        }
        else {
            var pp = selPos(dsz.width, dsz.height, 64, 64);
            html += '<div class="zui-icon-64 ' + doc.type + '"';
            html += ' style="display: block; position: absolute; width: ' + pp.w + 'px; height: ' + pp.h + 'px; top: ' + pp.top + 'px; left: ' + pp.left + 'px;"';
            html += '></div>';
        }
        dshow.empty();
        dshow.append(html);

        // 加载内容
        if ("text" == doc.cate || "code" == doc.cate) {
            $z.http.getText('/doc/txt/read', {'docId': doc.id}, function (content) {
                dshow.find('.zpreview-doc-code code').append(content);
                Prism.highlightAll();
            });
        }
    }

    function showMeta(mdiv, doc) {

    }

    function selSize(div) {
        return {
            'width': div.width(),
            'height': div.height()
        };
    }

    function selPos(cw, ch, ow, oh) {
        var hp = '';
        var oR = ow / oh;
        var nR = cw / ch;
        var nw, nh, x, y;
        // 原图太宽
        if (oR > nR) {
            nw = Math.min(ow, cw);
            nh = nw / oR;
        }
        // 原图太长
        else if (oR < nR) {
            nh = Math.min(oh, ch);
            nw = nh * oR;
        }
        // 比例相同
        else {
            nw = Math.min(ow, cw);
            nh = nw;
        }
        x = ( cw - nw ) / 2
        y = ( ch - nh) / 2
        return {
            'w': nw,
            'h': nh,
            'top': y,
            'left': x
        };
    }

    //_________________________________
    $.extend({
        zpreview: function (opt, arg0, arg1) {
            // 初始化模式
            if (typeof opt == "object") {
                var doc = opt.doc;
                // 初始化目录
                $.masker({
                    closeBtn: true,
                    width: "95%",
                    height: "80%",
                    body: function () {
                        var html = '';
                        html += '<div class="zpreview-title">';
                        html += '   <span class="pre-docname">' + doc.name + "." + doc.type + '</span>';
                        html += '   <a class="pre-download" href="/doc/bin/read?docId=' + doc.id + '">下载</a>';
                        html += '</div>';
                        html += '<div class="zpreview-body">';
                        html += '<div class="zpreview-body-container">';
                        html += '   <div class="zpreview-doc">';
                        html += '       <div class="zpreview-doc-show">';
                        html += '       </div>';
                        html += '   </div>';
                        html += '   <div class="zpreview-prop">';
                        html += '       <div class="zpreview-prop-main">';
                        html += '           <table>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">编号</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.id + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">分类</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.cate + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">类型</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.type + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">文件大小</td>';
                        html += '                   <td class="zpreview-prop-val">' + $z.util.sizeText(doc.size) + '(' + doc.size + 'bytes)</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">创建者</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.createUser + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">创建时间</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.createTime + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">最后修改者</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.modifyUser + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">最后修改时间</td>';
                        html += '                   <td class="zpreview-prop-val">' + doc.modifyTime + '</td>';
                        html += '               </tr>';
                        html += '               <tr>';
                        html += '                   <td class="zpreview-prop-key">备注</td>';
                        html += '                   <td class="zpreview-prop-val">';
                        html += '                       <input value="' + ($z.util.isBlank(doc.remark) ? '' : doc.remark) + '">';
                        html += '                   </td>';
                        html += '               </tr>';
                        //html += '               <tr>';
                        //html += '                   <td class="zpreview-prop-key">sha1</td>';
                        //html += '                   <td class="zpreview-prop-val">' + doc.sha1 + '</td>';
                        //html += '               </tr>';
                        html += '           </table>';
                        html += '       </div>';
                        html += '       <div class="zpreview-prop-other">';
                        html += '           <table>';
                        html += '           </table>';
                        html += '       </div>';
                        html += '   </div>';
                        html += '</div>';
                        html += '</div>';
                        return html;
                    },
                    afterDomReady: function (mdiv) {
                        showDoc(mdiv, doc);
                        showMeta(mdiv, doc);
                    }
                });
                // 加载数据

            }
            return;
        }
    });
})
(window.jQuery);