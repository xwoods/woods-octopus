/**
 * Created by pw on 14-8-11.
 */

function IssueCtl($scope) {

    var module = $('.module-issue');
    var containerHeight = parseInt(module.parents(".container").css('min-height'));

    var issueTitleJq = module.find('.issue-title');
    var issueCommitJq = module.find('.issue-commit');
    var issueListJq = module.find('.issue-list');
    var issueContentJq = module.find('.issue-content');
    var issueSubmitJq = module.find('.issue-submit');

    issueListJq.css({'height': containerHeight - issueTitleJq.outerHeight() - issueCommitJq.outerHeight() - 105});

    var dmnNm = $("body").attr('dmnNm');
    var userNm = $("body").attr('userNm');

    if (dmnNm == "admin") {
        // 管理域查看所有的
        dmnNm = null;
    }

    function refreshIssueList() {
        $z.http.post("/issue/query", {
            "orderby": 'createTime',
            "asc": false,
            "domain": dmnNm
        }, function (re) {
            issueListJq.empty();
            var ilist = re.data.list;
            var uhtml = '';
            for (var i = 0; i < ilist.length; i++) {
                var issue = ilist[i];
                uhtml += '<li class="' + (issue.createUser == userNm ? "my-issue" : "") + '">';
                uhtml += '    <div class="ct-user">';
                uhtml += '        <img src="/user/face/' + issue.createUser + '">';
                uhtml += '        <p>' + issue.createUser + '</p>';
                uhtml += '    </div>';
                uhtml += '    <div class="ct-issue">';
                uhtml += '        <span>' + issue.createTime + '</span>';
                uhtml += '        <p>' + $z.util.replaceAll(issue.content, "\n", "<br>") + '</p>';
                uhtml += '        <em issueId="' + issue.id + '">查看回复</em>';
                uhtml += '    </div>';
                uhtml += '</li>';
            }
            issueListJq.append(uhtml);
        });
    }

    function refreshIssueReplyList(issueId) {
        $z.http.get("/issue/reply/list", {
            'issueId': issueId
        }, function (re) {
            issueListJq.find('li.ct-user-right').remove();
            var ilist = re.data;
            var uhtml = '';
            for (var i = 0; i < ilist.length; i++) {
                var issueReply = ilist[i];
                uhtml += '<li class="ct-user-right ' + (issueReply.createUser == userNm ? "my-issue" : "") + '">';
                uhtml += '    <div class="ct-user">';
                uhtml += '        <img src="/user/face/' + issueReply.createUser + '">';
                uhtml += '        <p>' + issueReply.createUser + '</p>';
                uhtml += '    </div>';
                uhtml += '    <div class="ct-issue">';
                uhtml += '        <span>' + issueReply.createTime + '</span>';
                uhtml += '        <p>' + $z.util.replaceAll(issueReply.content, "\n", "<br>") + '</p>';
                uhtml += '    </div>';
                uhtml += '</li>';
            }
            issueListJq.append(uhtml);
        })
    }

    var isReply = false;
    var issueId = 0;

    issueListJq.delegate('em', 'click', function () {
        var emJq = $(this);
        issueId = emJq.attr('issueId');
        var cli = emJq.parent().parent();
        var cul = cli.parent();
        if (emJq.hasClass('reply')) {
            // 收起状态
            emJq.html('查看回复');
            emJq.removeClass('reply');
            cli.removeClass('reply');
            cul.removeClass('check-reply');
            isReply = false;
            issueSubmitJq.html('发 布');
            issueListJq.find('li.ct-user-right').remove();
            issueId = 0;
        } else {
            // 打开
            emJq.html('收起回复');
            emJq.addClass('reply');
            cli.addClass('reply');
            cul.addClass('check-reply');
            isReply = true;
            issueSubmitJq.html('回 复');
            // 添加reply
            refreshIssueReplyList(issueId);
        }
    });

    issueCommitJq.delegate('.issue-submit', 'click', function () {
        var cval = issueContentJq.val();
        if ($z.util.isBlank(cval)) {
            alert('亲爱的, 写点什么再点发布吧');
            return;
        }
        if (issueSubmitJq.hasClass('ing')) {
            alert('发布中, 请耐心等待...');
            return;
        }
        issueSubmitJq.addClass('ing');
        issueSubmitJq.html('提交中...');

        if (isReply) {
            $z.http.post('/issue/reply/add', {'content': cval, 'issueId': issueId}, function () {
                issueContentJq.val('');
                issueSubmitJq.removeClass('ing');
                issueSubmitJq.html("回 复");
                refreshIssueReplyList(issueId);
            });
        } else {
            $z.http.post('/issue/add', {'content': cval}, function () {
                issueContentJq.val('');
                issueSubmitJq.removeClass('ing');
                issueSubmitJq.html('发 布');
                refreshIssueList();
            });
        }
    });


    refreshIssueList();
}