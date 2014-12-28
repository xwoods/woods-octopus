/**
 * 控件介绍
 *
 * 替换masker         ->      控件名称
 *
 */
(function ($) {
    var OPT_NAME = "masker_option";
    var SEL_CLASS = ".masker";
    var SEL_CLASS_NM = "masker";
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
        init: function (opt) {
            var html = '';
            html += '<div class="masker">';
            html += '   <div class="masker-bg">';
            if (opt.title) {
                html += '   <div class="masker-title">' + opt.title + '</div>';
            }
            html += '       <div class="masker-fg">';
            html += '           <div class="masker-body">';
            if (opt.body) {
                html += opt.body();
            }
            html += '           </div>';
            html += '       </div>';
            html += '       <div class="masker-btns">';
            if (opt.btns) {
                for (var i = 0; i < opt.btns.length; i++) {
                    html += '<div class="' + opt.btns[i].clz + '">' + opt.btns[i].label + '</div>';
                }
            }
            if (opt.closeBtn) {
                html += '<div class="btn-close">关闭</div>';
            }
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
            var mdiv = $(html);
            mdiv.appendTo(document.body);
            mdiv.find('.masker-bg').children().css({'position': "fixed"});
            return mdiv;
        }
    };
    // _________________________________
    var data = {
        init: function (opt) {

        }
    };
    // _________________________________
    var events = {
        unbind: function (selection) {
            selection.undelegate();
        },
        bind: function (selection, opt) {
            selection.delegate('.btn-close', 'click', function () {
                $.masker('close');
            });
            if (opt.btns) {
                for (var i = 0; i < opt.btns.length; i++) {
                    var bset = opt.btns[i];
                    selection.delegate('.' + bset.clz, bset.event.type, function () {
                        bset.event.handle(selection);
                    });
                }
            }
        }
    };
    // _________________________________
    var layout = {
        size: function (us, ws) {
            us = "" + us;
            if (us.indexOf('%') != -1) {
                var per = parseInt(us);
                us = (ws * per / 100);
            }
            return parseInt(us);
        },
        resize: function (mdiv) {
            var opt = util.opt(mdiv);
            var winsz = $z.browser.winsz();
            var fh = opt.height ? layout.size(opt.height, winsz.height) : winsz.height - 200;
            var fw = opt.width ? layout.size(opt.width, winsz.width) : winsz.width;

            mdiv.css({
                'height': winsz.height,
                'width': winsz.width
            });
            mdiv.find('.masker-bg').css({
                'height': winsz.height,
                'width': winsz.width
            });

            mdiv.find('.masker-title').css({
                'width': fw,
                'top': (winsz.height - fh) / 2 - 50,
                'left': (winsz.width - fw) / 2
            });

            mdiv.find('.masker-fg').css({
                'height': fh,
                'width': fw,
                'top': (winsz.height - fh) / 2,
                'left': (winsz.width - fw) / 2
            });

            mdiv.find('.masker-btns').css({
                'width': fw,
                'top': (winsz.height - fh) / 2 + fh,
                'left': (winsz.width - fw) / 2
            });
        }
    };
// _________________________________
    var commands = {
        close: function () {
            var mdiv = this;
            var opt = util.opt(mdiv);

            if (opt.beforeClose) {
                opt.beforeClose();
            }

            mdiv.remove();

            // 如果是多层makser
            var otherMasker = $('.masker.masker-back');

            if (otherMasker.length > 0) {
                $(otherMasker[otherMasker.length - 1]).removeClass('masker-back');
            } else {
                $(document.body).children().removeClass('masker-back');
            }


            if (opt.afterClose) {
                opt.afterClose();
            }
        },
        get: function () {
            return this;
        },
        resize: function () {
            layout.resize(this);
        },
        addCloseBtn: function () {
            var mdiv = this;
            mdiv.find('.masker-btns').append('<div class="btn-close">关闭</div>');
        }
    };
// _________________________________
    $.extend({
        masker: function (opt, arg0, arg1) {
            // 初始化模式
            if (typeof opt == "object") {
                // 其他都变暗
                $(document.body).children().addClass('masker-back');
                // 初始化
                var mdiv = dom.init(opt);
                // 记录当前opt
                util.setOpt(mdiv, opt)
                // 调整大小
                layout.resize(mdiv);
                // 绑定事件
                events.bind(mdiv, opt);

                if (opt.afterDomReady) {
                    opt.afterDomReady(mdiv);
                }
            }
            // 命令模式
            else if (opt && (typeof opt == "string")) {
                if ("function" != typeof commands[opt])
                    throw "$.fn.masker: don't support command '" + opt + "'";
                var mdiv = $(document.body).children(".masker").last();
                if (mdiv.length > 0) {
                    var re = commands[opt].apply(mdiv, [arg0, arg1]);
                    if (re != null || re != undefined) {
                        return re;
                    }
                }
            }
            // 返回支持链式赋值
            return this;
        }
    });
})
(window.jQuery);