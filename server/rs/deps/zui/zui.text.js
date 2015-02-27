/**
 * 控件介绍
 *
 * 替换ztext         ->      控件名称
 *
 */
(function ($) {
    var OPT_NAME = "ztext_option";
    var SEL_CLASS = ".ztext";
    var SEL_CLASS_NM = "ztext";

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
        }
    };
    // _________________________________
    var dom = {
        init: function (selection) {
            var opt = util.opt(selection);
            var doc = opt.doc;
            var html = '';
            html += '<div class="edit-title-bar">';
            html += '   <div class="edit-doc-name">' + doc.name + "." + doc.type + '</div>';
            html += '   <div class="edit-btn close-masker">关闭</div>';
            html += '   <div class="edit-btn screen-save">保存</div>';
            html += '</div>';
            html += '<div class="edit-body ztext">';
            html += '   <textarea placeholder="无内容..." ></textarea>';
            html += '</div>';
            selection.append(html);
        }
    };
    // _________________________________
    var data = {
        init: function (selection) {
            var opt = util.opt(selection);

            $z.http.getText('/doc/txt/read', {
                'docId': opt.doc.id
            }, function (text) {
                selection.find('.ztext textarea').val(text);
            });
        }
    };
// _________________________________
    var events = {
        unbind: function (selection) {
            selection.undelegate();
        },
        bind: function (selection) {
            var opt = util.opt(selection);
            selection.delegate('.screen-save', 'click', function () {
                var tcontent = selection.find('.ztext textarea').val();
                $z.http.post('/doc/txt/write', {
                    'docId': opt.doc.id,
                    'content': tcontent
                }, function (re) {
                    alert('保存成功');
                });
            });

            selection.delegate('.close-masker', 'click', function () {
                $.masker('close');
            });
        }
    };
// _________________________________
    var layout = {
        resize: function (selection) {
        }
    };
// _________________________________
    var commands = {
        depose: function () {
        }
    };
// _________________________________
    $.fn.extend({
        ztext: function (opt, arg0, arg1, arg2, arg3, arg4) {
            var selection = this;
            // 检查有效选区
            if (selection.size() == 0)
                return selection;
            // 命令模式
            if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.ztext: don't support command '" + opt + "'";
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
