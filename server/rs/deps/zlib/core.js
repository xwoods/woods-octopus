(function (window) {

    window._$z = window.$z;
    window.$z = {};

    // 判断是否已经有人用过$z这个名字了
    $z.conflicted = (_$z !== undefined);

    // 判断是否有console对象可用
    $z.hasConsole = (console !== undefined);
    $z.hasTrim = ("trim".trim !== undefined);

    $z.__msg__ = {};
    $z.initMsg = function () {
        var msgJq = $("#__msg__");
        var mkvList = msgJq.children();
        mkvList.each(function (i, ele) {
            var mkv = $(ele);
            $z.__msg__[mkv.attr('key')] = mkv.html();
        });
    }
    $z.msg = function (key) {
        var msg = $z.__msg__[key];
        if ($z.util.isBlank(msg)) {
            return key;
        } else {
            return msg;
        }
    }

    // 生成对应的命名空间
    $z.makePackage = function (pkg) {
        var plist = pkg.split(".");
        var cpkg = $z;
        for (var i = 0; i < plist.length; i++) {
            var pnm = plist[i];
            if (cpkg[pnm] == undefined) {
                cpkg[pnm] = {};
            }
            cpkg = cpkg[pnm];
        }
        return cpkg;
    };

    var util = $z.makePackage("util");

    // ====================================== 对象, 方法

    util.isEmpty = function (obj) {
        if (obj == null || obj == undefined) {
            return true;
        }
        var name;
        for (name in obj) {
            return false;
        }
        return true;
    };

    util.isFunction = function (fn) {
        if (fn == null || fn == undefined) {
            return false;
        }
        return typeof fn === 'function';
    };

    // ====================================== 字符串相关

    util.isBlank = function (str) {
        if (str == null || str == undefined) {
            return true;
        } else if (typeof str == 'string' && util.trim(str) == "") {
            return true;
        }
        return false;
    };

    util.trim = function (str) {
        if (str == null || str == undefined) {
            return '';
        }
        if ($z.hasTrim) {
            return str.trim();
        } else {
            // 使用正则去掉前后的空格
            return str.replace(/(^\s*)|(\s*$)/g, "");
        }
    };

    // ====================================== json 转换

    util.toJson = function (obj) {
        return JSON.stringify(obj);
    };

    util.fromJson = function (str) {
        // eval的方法, 会执行里面的js代码, 比较有危险性, 可能会被注入
        // return eval("(" + str + ")");
        return JSON.parse(str);
    };

    util.json2str = function (json) {
        return util.toJson(json);
    };

    util.str2json = function (str) {
        return util.fromJson(str);
    };

    util.replaceAll = function (str, reallyDo, replaceWith, ignoreCase) {
        if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
            return str.replace(new RegExp(reallyDo, (ignoreCase ? "gi" : "g")), replaceWith);
        } else {
            return str.replace(reallyDo, replaceWith);
        }
    };

    util.timeText = function (ut) {
        if (ut <= 0) {
            return "";
        }
        if (ut > 0 && ut < 1000) {
            return "1s";
        }
        if (ut >= 1000 && ut < 60000) { // 1m内
            return parseInt(ut / 1000) + "s";
        }
        if (ut >= 1000 && ut < 60000) { // 1h内
            var tm = parseInt(ut / 1000 / 60);
            return tm + "m" + util.timeText(ut - tm * 1000 * 60);
        }
        if (ut >= 60000) { //
            var th = parseInt(ut / 1000 / 60 / 60);
            return th + "h" + util.timeText(ut - th * 1000 * 60 * 60);
        }
    }

    util.sizeText = function (size, unit) {
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

})(window);