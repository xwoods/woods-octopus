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

$(document).ready(function () {
    $z.initMsg();
});