/**
 * Created by pw on 14-7-16.
 */
function LoginCtrl($scope) {
    $scope.login = function () {
        var lf = {
            'domain': $scope.domain,
            'name': $scope.name,
            'password': $scope.password
        };
        console.log("login by : " + JSON.stringify(lf));
        if (_.isEmpty(lf.domain) || _.isEmpty(lf.name) || _.isEmpty(lf.password)) {
            alert("请输入完整的登陆信息后再尝试登陆");
            return;
        }
        $z.http.post("/user/login", lf, function (re) {
            window.location.href = "/browser";
        });
    }
}

function RegCtrl($scope) {

    var rcontent = $('.reg-content');
    var rname = rcontent.find('input[name=regname]');
    var ralias = rcontent.find('input[name=regalias]');
    var rpassword = rcontent.find('input[name=regpassword]');
    var ric = rcontent.find('input[name=reginviteCode]');

    rname.on('change', function () {
        var val = rname.val();
        if ($z.util.isBlank(val)) {
            rname.parent().removeClass('ing').removeClass('ok').removeClass('fail');
            return;
        }
        var repName = new RegExp("^[0-9a-zA-Z]{2,20}$");
        if (repName.test(val)) {
            $z.http.get("/user/checkExist/name/" + val, function (re) {
                if (re.data) {
                    rname.parent().removeClass('ing').removeClass('ok').addClass('fail');
                } else {
                    rname.parent().removeClass('ing').removeClass('fail').addClass('ok');
                }
            });
        } else {
            rname.parent().removeClass('ok').removeClass('ing').addClass('fail');
        }
    });

    ralias.on('change', function () {
        var val = ralias.val();
        if ($z.util.isBlank(val)) {
            ralias.parent().removeClass('ing').removeClass('ok').removeClass('fail');
            return;
        }
        var repName = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9]){2,10}$");
        if (repName.test(val)) {
            $z.http.get("/user/checkExist/alias/" + val, function (re) {
                if (re.data) {
                    ralias.parent().removeClass('ing').removeClass('ok').addClass('fail');
                } else {
                    ralias.parent().removeClass('ing').removeClass('fail').addClass('ok');
                }
            });
        } else {
            ralias.parent().removeClass('ok').removeClass('ing').addClass('fail');
        }
    });

    rpassword.on('change', function () {
        var val = rpassword.val();
        if ($z.util.isBlank(val)) {
            rpassword.parent().removeClass('ing').removeClass('ok').removeClass('fail');
            return;
        }
        var repName = new RegExp("^[a-zA-Z0-9_@]{6,20}$");
        if (repName.test(val)) {
            rpassword.parent().removeClass('ing').removeClass('fail').addClass('ok');
        } else {
            rpassword.parent().removeClass('ok').removeClass('ing').addClass('fail');
        }
    });

    ric.on('change', function () {
        var val = ric.val();
        if ($z.util.isBlank(val)) {
            ric.parent().removeClass('ing').removeClass('ok').removeClass('fail');
            return;
        }
        ric.parent().removeClass('ing').removeClass('fail').addClass('ok');
    });


    var btnReg = rcontent.find('.reg-submit');
    btnReg.on('click', function () {
        if (!rname.parent().hasClass('ok')) {
            alert('用户名为空或者信息不正确');
            return;
        }
        if (!ralias.parent().hasClass('ok')) {
            alert('昵称为空或者信息不正确');
            return;
        }
        if (!rpassword.parent().hasClass('ok')) {
            alert('密码为空或者信息不正确');
            return;
        }
        if (!ric.parent().hasClass('ok')) {
            alert('注册码为空或者信息不正确');
            return;
        }
        $z.http.post("/user/register", {
            'name': rname.val(),
            'alias': ralias.val(),
            'password': rpassword.val(),
            'ic': ric.val()
        }, function (re) {
            alert($z.msg(re.msg));
            rname.val('');
            rname.parent().removeClass('ok');
            ralias.val('');
            ralias.parent().removeClass('ok');
            rpassword.val('');
            rpassword.parent().removeClass('ok');
            ric.val('');
            ric.parent().removeClass('ok');

            $('.switch-form.login').click();
        });
    });
}

$(document).ready(function () {
    $z.initMsg();

    var lcontent = $('.login-content');
    var rcontent = $('.reg-content');

    $('.switch-form').on('click', function () {
        if ($(this).hasClass('reg')) {
            lcontent.hide();
            rcontent.show();
        } else {
            rcontent.hide();
            lcontent.show();
        }
    })

//    var showTopLogin = parseInt(lcontent.css('margin-top'));
//    var showTopReg = parseInt(rcontent.css('margin-top'));
//
//    rcontent.css('margin-top', (showTopReg * -3) + "px");
//
//    console.log('login-form-top : ' + showTopLogin);
//    console.log('  reg-form-top : ' + showTopReg);
//
//    $('.switch-form').on('click', function () {
//        if ($(this).hasClass('reg')) {
//            lcontent.animate({'margin-top': (showTopLogin * -3) + "px"}, 200, function () {
//                rcontent.animate({'margin-top': showTopReg + "px"}, 200, function () {
//                });
//            });
//        } else {
//            rcontent.animate({'margin-top': (showTopReg * -3) + "px"}, 200, function () {
//                lcontent.animate({'margin-top': showTopLogin + "px"}, 200, function () {
//                });
//            });
//        }
//    })

});