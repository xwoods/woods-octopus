/**
 * Created by pw on 14-7-16.
 */

var tmpl_li = '';
tmpl_li += '<li>';
tmpl_li += '    <a href="#{{url}}">';
tmpl_li += '        <i class="fa {{icon}} fa-lg"></i>';
tmpl_li += '        <span class="module-title">{{name}}</span>';
tmpl_li += '    </a>';
tmpl_li += '</li>';

var tmpl_li_sub = '';
tmpl_li_sub += '<li class="has-sub">';
tmpl_li_sub += '    <a href="javascript:void(0)">';
tmpl_li_sub += '        <i class="fa {{icon}} fa-lg"></i>';
tmpl_li_sub += '        <span class="module-title">{{name}}</span>';
tmpl_li_sub += '        <i class="fa fa-lg subnav-switch"></i>';
tmpl_li_sub += '    </a>';
tmpl_li_sub += '    <ul class="subnav-list">';
tmpl_li_sub += '        {{#each subNav}}';
tmpl_li_sub += '        <li>';
tmpl_li_sub += '            <a href="#{{url}}">';
tmpl_li_sub += '                <span>{{name}}</span>';
tmpl_li_sub += '            </a>';
tmpl_li_sub += '        </li>';
tmpl_li_sub += '        {{/each}}';
tmpl_li_sub += '    </ul>';
tmpl_li_sub += '</li>';

function handlebarsHtml(tmpl, content) {
    var template = Handlebars.compile(tmpl);
    return template(content);
}

function navHtml(nc) {
    var nhtml = '';
    for (var i = 0; i < nc.length; i++) {
        nhtml += handlebarsHtml(nc[i].subNav ? tmpl_li_sub : tmpl_li, nc[i]);
    }
    return nhtml;
}

function navConf(nc) {
    for (var i = 0; i < nc.length; i++) {
        var ni = nc[i];
        ni.name = $z.msg(ni.name);
        navUrlNameMap["" + ni.url] = ni.name;
        if (ni.subNav) {
            $.each(ni.subNav, function (i, ele) {
                ele.name = $z.msg(ele.name);
                navUrlNameMap["" + ele.url] = ele.name;
            });
        }
    }
}

window.namap = {};

var navUrlNameMap = {};

var tmpl_crumb_li = '<li><span>{{value}}</span></li>';

function crumbHtml(route) {
    var rlist = [];
    var rs = route.substr(1).split('/');
    var lastPath = "/";
    for (var i = 0; i < rs.length; i++) {
        var r = {key: lastPath + rs[i]};
        r.value = navUrlNameMap[r.key];
        rlist.push(r);
        lastPath += rs[i] + "/";
    }
    var chtml = '';
    for (var i = 0; i < rlist.length; i++) {
        chtml += handlebarsHtml(tmpl_crumb_li, rlist[i]);
    }
    return chtml;
}

var previewTypeMap = {
    'jpg': true,
    'jpeg': true,
    'png': true,
    'gif': true
};

var uploadTypeMap = {
    'jpg': true,
    'jpeg': true,
    'png': true,
    'gif': true,
    'doc': true,
    'xls': true,
    'ppt': true,
    'zip': true
};


if (!window.myConf) {
    window.myConf = {
        'domain': '',
        'user': '',
        'alertAudio': '',
        'friends': [],
        'friendsNameMap': {},
        'friendsOnline': {},
        'friendsName': '',
        'friendsChat': {},
        // 部分控制页面的元素
        'isDebug': false,
        'mini': true,
        hasPreview: function (type) {
            return previewTypeMap[type] == true;
        },
        canUpload: function (type) {
            return uploadTypeMap[type] == true;
        }
    }
}

$(document).ready(function () {

    // 我的配置
    window.myConf.domain = $(document.body).attr('dmnNm');
    window.myConf.user = $(document.body).attr('userNm');
    window.myConf.alertAudio = $(document.body).attr('alertAudio');

    window.myConf.friendsNameMap[window.myConf.user] = window.myConf.user;

    if (window.myConf.isDebug) {
        var blr = $("#before-load-ready");
        blr.remove();
    }

    $z.initMsg();

    $z.http.get('/user/na', function (re) {
        window.namap = re.data;
        console.log("Map Name-Alias Ready");
    });


    // 调整.main-sidebar的高度
    var mainSidebar = $('.main-sidebar');

    // 调整.container的min-height
    var containerJq = $('.container');
    var winHeight = $z.browser.winsz().height;
    var headerHeight = $('.header').outerHeight();
    var footerHeight = $('.footer').outerHeight();
    console.log("win : " + winHeight + ", header : " + headerHeight + ", footer : " + footerHeight);
    containerJq.css({
        'min-height': winHeight - headerHeight - footerHeight
    });


    // 基本信息
    var dmnNm = $(document.body).attr('dmnNm');

    var mainContainer = $('.main-container');

    // 侧边导航栏
    var navListJq = $('.nav-list');
    var crumbJq = $('.crumb');

    mainContainer.delegate('.main-nav .nav-list a', 'click', function () {
        var clkA = $(this);
        var clkLi = clkA.parent();
        var clkUl = clkLi.parent();
        var hasSub = clkLi.hasClass('has-sub');
        var isSub = clkUl.hasClass('subnav-list');

        // 非mini模式
        if (!mainContainer.hasClass('mini')) {
            // 一级餐单, 那就把其他的关闭
            if (!isSub) {
                clkLi.siblings('.has-sub').removeClass('open');
            }
            if (hasSub) {
                clkLi.toggleClass('open');
            }
        }

        if (!clkLi.hasClass('active')) {
            if (!isSub) {
                if (!hasSub) {
                    navListJq.find('li.active').removeClass('active');
                    clkLi.addClass('active');
                }
            } else {
                navListJq.find('li.active').removeClass('active');
                clkUl.parent().addClass('active');
                clkLi.addClass('active');
            }
        }

        // 切换页面信息
        var route = clkA.attr('href').substr(1);
        if (!hasSub) {
            var ch = crumbHtml(route);
            crumbJq.find('li:not(.crumb-first)').remove();
            crumbJq.append(ch);
        }
    });

    // 切换mini
    var navMenu = $('.nav-menu');
    navMenu.delegate(".nav-mini-toogle", 'click', function () {
        navListJq.find('.has-sub.open').removeClass('open');
        var tbtn = $(this);
        if (tbtn.hasClass("mode-mini")) {
            tbtn.removeClass('mode-mini');
            tbtn.removeClass('fa-angle-double-right');
            tbtn.addClass('fa-angle-double-left');
            mainContainer.removeClass("mini");
        } else {
            tbtn.addClass('mode-mini');
            tbtn.removeClass('fa-angle-double-left');
            tbtn.addClass('fa-angle-double-right');
            mainContainer.addClass("mini");
        }
    });

    function afterLoadReady() {
        var blr = $("#before-load-ready");
        if (!window.myConf.isDebug && blr.length > 0) {
            setTimeout(function () {
                // blr.animate({top: -1 * blr.height()}, 300, function(){
                blr.animate({opacity: 0}, 300, function () {
                    blr.remove();
                });
            }, 1200);
        }
    }

    // 加载后台nav配置
    $z.http.get("/ui/nav/get/" + dmnNm, function (re) {
        // 加载nav
        var nc = re.data;
        navConf(nc);
        navListJq.append(navHtml(nc));
        // 判断url中的#后面的参数
        var route = $z.http.urlAfter();
        if (route) {
            var clkA = navListJq.find('a[href="#' + route + '"]');
            console.log("find route : " + route + ", has " + clkA.length);
            if (clkA.length > 0 && !clkA.parent().hasClass('has-sub')) {
                clkA.find('span').click();
            } else {
                navListJq.find('a').first().find('span').click();
            }
        } else {
            navListJq.find('a').first().find('span').click();
        }
        // 去掉loading
        afterLoadReady();
    });

    $(document.body).find('#logout').on('click', function () {
        $z.http.get('/user/logout', function () {
            window.location.href = "/login";
        });
    });

    if (window.myConf.mini) {
        navMenu.find(".nav-mini-toogle").click();
    }
});

// 控制module跳转
var coreRoute = [
    {
        url: '/me',
        page: '/core/me.jsp',
        ctrl: 'MeCtl'
    },
    {
        url: '/users',
        page: '/core/users.jsp',
        ctrl: 'UsersCtl'
    },
    {
        url: '/invite',
        page: '/core/invite.jsp',
        ctrl: 'InviteCtl'
    },
    {
        url: '/setting',
        page: '/core/setting.jsp',
        ctrl: 'SettingCtl'
    },
    {
        url: '/domains',
        page: '/core/domains.jsp',
        ctrl: 'DomainsCtl'
    },
    {
        url: '/issue',
        page: '/core/issue.jsp',
        ctrl: 'IssueCtl'
    },
    {
        url: '/releaseNote',
        page: '/core/release.jsp',
        ctrl: 'ReleaseCtl'
    },
    {
        url: '/ext/sql',
        page: '/ext/sql.jsp',
        ctrl: 'SqlCtl'
    },
    {
        url: '/notImpl',
        page: '/core/notImpl.jsp',
        ctrl: 'NoImplCtl'
    },
    {
        url: '/netdisk4me',
        page: '/core/netdisk4me.jsp',
        ctrl: 'ND4UserCtl'
    },
    {
        url: '/netdisk4domain',
        page: '/core/netdisk4domain.jsp',
        ctrl: 'ND4DomainCtl'
    },
];

var mainApp = angular.module('mainApp', ['ngRoute']);

mainApp.controller('NoImplCtl', function ($scope) {
});

mainApp.controller('MyFriendsCtrl', function ($scope) {

    // ===================== chat
    var mainContainer = $('.main-container');
    var headFriends = $('.header .header-module .myfriends');
    var headModule = headFriends.parents('.header-module');
    var mainSidebar = $('.main-sidebar');
    var chatList = mainSidebar.find('.chat-list');
    var chatContainer = mainSidebar.find('.chat-container');
    var noChat = mainSidebar.find('.no-chat');

    function sidebarResize() {
        var wh = $z.browser.winsz().height;
        mainSidebar.css('height', wh - 47);
        mainSidebar.find('.chat-container .chat-content').css('height', wh - 47 - 200);
    }

    sidebarResize();

    window.onresize = function () {
        sidebarResize();
        $.masker('resize');
    }

    // 切换sidebar
    mainSidebar.delegate(".main-sidebar-switch i", 'click', function () {
        var sbtn = $(this);
        if (sbtn.hasClass('mode-sidebar')) {
            sbtn.removeClass('mode-sidebar');
            sbtn.removeClass('fa-angle-double-right');
            sbtn.addClass('fa-angle-double-left');
            mainContainer.removeClass("sidebar");
        } else {
            sbtn.addClass('mode-sidebar');
            sbtn.removeClass('fa-angle-double-left');
            sbtn.addClass('fa-angle-double-right');
            mainContainer.addClass("sidebar");
        }
    });

    function addNewChat(chatMember) {
        if (!noChat.hasClass('hdn')) {
            noChat.addClass('hdn')
        }
        var chatId = chatMember.chatId;
        var chatTitle = chatMember.chatAlias;
        var chatUser = chatMember.toUser;
        var chatItem = chatList.find('#chat-' + chatId);

        if (chatItem.length <= 0) {
            var html = '';
            html += '<li id="chat-' + chatId + '" chatId="' + chatId + '">';
            html += '   <i class="fa fa-1x fa-times-circle"></i>';
            html += '   <div class="chat-thumb">';
            // FIXME 暂时仅仅添加聊天人的头像
            html += '   <img src="/user/face/' + chatUser + '">';
            html += '   </div>';
            html += '   </div>';
            html += '   <div class="chat-user">' + chatUser + '</div>';
            html += '   <div class="chat-unread">0</div>';
            html += '</li>'
            chatList.append(html);
            var chtml = '';
            chtml += '<div id="chat-container-' + chatId + '" chatId="' + chatId + '">';
            chtml += '  <div class="chat-header">' + chatTitle + '</div>';
            chtml += '  <div class="chat-content">';
            chtml += '      <div class="chat-readmore" chatId="' + chatId + '">加载更多历史信息...</div>';
            chtml += '  </div>';
            chtml += '  <div class="chat-footer">';
            chtml += '      <div class="chat-menu-bar">';
            chtml += '      </div>';
            chtml += '      <div class="chat-submit">';
            chtml += '          <textarea placeholder="写点什么吧...."></textarea>';
            chtml += '          <span chatId="' + chatId + '">发送</span>';
            chtml += '      </div>';
            chtml += '  </div>';
            chtml += '  <div class="chat-upload">';
            chtml += '      <span>0%</span>';
            chtml += '  </div>';
            chtml += '</div>';
            chatContainer.append(chtml);
            // 调整高度
            sidebarResize();
            // 最新的切换
            chatList.children().last().click();
        } else {
            // 切换
            chatItem.click();
        }
    }

    var isGetChatMsg = false;

    function getChatMsg(chatId) {
        if (!isGetChatMsg) {
            isGetChatMsg = true;
            $z.http.get('/chat/msg/get', {
                'chatId': chatId
            }, function (re) {
                if (re.data) {
                    var chlist = re.data;
                    var chBody = chatContainer.find('#chat-container-' + chatId + ' .chat-content');
                    // 拿到的新的数据
                    for (var i = 0; i < chlist.length; i++) {
                        var ch = chlist[i];
                        var html = '';
                        html += '<div class="' + (ch.user == window.myConf.user ? "me" : "") + '">';
                        html += '   <span class="name">' + window.myConf.friendsNameMap[ch.user] + "</span>";
                        html += '   <span class="time">' + ch.createTime + "</span>";
                        if (ch.content[0] == "*" && ch.content[ch.content.length - 1] == "*") {
                            // 文件
                            var finfo = ch.content.substr(1, ch.content.length - 2).split(',');
                            var ftype = finfo[0];
                            var fname = finfo[1];
                            var docId = finfo[2];
                            html += '<div class="file-preview">';
                            if (window.myConf.hasPreview(ftype)) {
                                html += '<img src="/doc/preview/' + docId + '" class="file-preview-img" docId="' + docId + '">';
                            } else {
                                html += '<div class="file-type zui-file-icon ' + ftype + '"></div>'
                                html += '<div class="file-nm"><a href="/doc/bin/read?docId=' + docId + '" >' + fname + '.' + ftype + '</a></div>'
                            }
                            html += '<div>';
                        } else {
                            html += '   <p>' + $z.util.replaceAll(ch.content, "\n", "<br>") + "</p>";
                        }
                        html += '</div>';
                        chBody.append(html);
                    }
                    chBody[0].scrollTop = chBody[0].scrollHeight;
                    // 调整侧边栏
                    chatList.find('#chat-' + chatId).removeClass('hasUnread');
                    // 调整顶部栏
                }
                isGetChatMsg = false;
            });
        }
    }

    function getUnread() {
        if ($.isEmptyObject(window.myConf.friendsChat)) {
            return;
        }
        $z.http.get('/chat/unread/check', function (re) {
            if (!re.data) {
                re.data = {};
            }
            var totalUnread = 0;
            for (var unm in window.myConf.friendsChat) {
                var cm = window.myConf.friendsChat[unm];
                var cid = cm.chatId;
                var cun = re.data[cid];
                if (cun == null) {
                    cun = 0;
                }
                totalUnread += cun;
                // 顶部
                var finfo = headFriends.find('.friend-chat-' + cid);
                var fur = finfo.find('.friend-unread');
                fur.html(cun);
                if (cun > 0) {
                    fur.removeClass('hdn');
                } else {
                    fur.addClass('hdn');
                }
                // 侧边栏
                var citem = chatList.find('#chat-' + cid);
                citem.find('.chat-unread').html(cun);
                if (cun > 0) {
                    citem.addClass('hasUnread');
                } else {
                    citem.removeClass('hasUnread');
                }
                // 长查询
                if (cid == currentChat) {
                    currentChatUnread = cun;
                }
            }
            if (totalUnread > 0) {
                headModule.addClass('hasCheck');
                headModule.find('.check-list-tip').html(totalUnread);
            } else {
                headModule.removeClass('hasCheck');
            }
            lastUnread = totalUnread;
            if (lastUnread == 0) {
                document.title = "八爪鱼";
            }
        });
    }


    chatContainer.on('dragover', function (e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).addClass('dragover');
    });

    chatContainer.on('dragleave', function (e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).removeClass('dragover');
    });

    chatContainer[0].addEventListener("drop", function (e) {
        chatContainer.removeClass('dragover');
        e.stopPropagation();
        e.preventDefault();
        var activeChat = chatContainer.find('div.active');
        var upro = activeChat.find('.chat-upload span');
        var chatId = activeChat.attr('chatId');
        if (e.dataTransfer) {
            var tf = e.dataTransfer.files[0];
            var fnmS = function (name) {
                if (name.lastIndexOf('.') != -1) {
                    return name.substr(name.lastIndexOf('.') + 1);
                }
                return null;
            }(tf.name);
            if (fnmS != null) {
                if (window.myConf.canUpload(fnmS)) {
                    chatContainer.addClass('uploading');
                    var xhr = new XMLHttpRequest();
                    xhr.upload.addEventListener("progress", function (e) {
                        var p = parseInt(e.loaded * 10000 / e.total) / 100 + "%";
                        upro.html(p);
                    }, false);
                    xhr.onreadystatechange = function (e) {
                        if (xhr.readyState == 4) {
                            if (xhr.status == 200) {
                                var re = $z.util.str2json(xhr.responseText);
                                var doc = re.data;
                                // 模拟发送一个特殊信息
                                var msg = "*" + doc.type + "," + doc.name + "," + doc.id + "*";
                                var mtxt = activeChat.find('.chat-submit textarea');
                                mtxt.val(msg);
                                var mbtn = activeChat.find('.chat-submit span');
                                mbtn.click();
                                // 取消状态
                                upro.html('0%');
                                chatContainer.removeClass('uploading');
                            }
                        }
                    };
                    // 准备请求对象头部信息
                    var contentType = "application/x-www-form-urlencoded; charset=utf-8";
                    xhr.open("POST", "/doc/bin/add", true);
                    xhr.setRequestHeader('Content-type', contentType);
                    xhr.setRequestHeader('DOC_M', "chat");
                    xhr.setRequestHeader('DOC_MKEY', chatId);
                    xhr.setRequestHeader("DOC_FNM", "" + encodeURI(tf.name));
                    xhr.setRequestHeader('DOC_RPIVATE', "false");
                    xhr.send(tf);
                }
            }
        }
    }, false);


    // 加载历史信息
    chatContainer.delegate('.chat-readmore', 'click', function () {
        var rmBtn = $(this);
        if (rmBtn.hasClass('reading') || rmBtn.hasClass('readdone')) {
            return;
        } else {
            rmBtn.addClass('reading');
            rmBtn.html("努力加载中....");

            var time = '';
            var chatId = rmBtn.attr('chatId');

            var oldMsg = rmBtn.next();
            if (oldMsg.length > 0) {
                time = oldMsg.find('.time').html();
            }

            $z.http.post('/chat/msg/history', {
                'chatId': chatId,
                'time': time
            }, function (re) {
                if (re.data) {
                    var chlist = re.data;
                    // 拿到的新的数据
                    for (var i = chlist.length - 1; i >= 0; i--) {
                        var ch = chlist[i];
                        var html = '';
                        html += '<div class="' + (ch.user == window.myConf.user ? "me" : "") + '">';
                        html += '   <span class="name">' + window.myConf.friendsNameMap[ch.user] + "</span>";
                        html += '   <span class="time">' + ch.createTime + "</span>";
                        if (ch.content[0] == "*" && ch.content[ch.content.length - 1] == "*") {
                            // 文件
                            var finfo = ch.content.substr(1, ch.content.length - 2).split(',');
                            var ftype = finfo[0];
                            var fname = finfo[1];
                            var docId = finfo[2];
                            html += '<div class="file-preview">';
                            if (window.myConf.hasPreview(ftype)) {
                                html += '<img src="/doc/preview/' + docId + '" class="file-preview-img" docId="' + docId + '">';
                            } else {
                                html += '<div class="file-type zui-file-icon ' + ftype + '"></div>'
                                html += '<div class="file-nm"><a href="/doc/bin/read?docId=' + docId + '" >' + fname + '.' + ftype + '</a></div>'
                            }
                            html += '<div>';
                        } else {
                            html += '   <p>' + $z.util.replaceAll(ch.content, "\n", "<br>") + "</p>";
                        }
                        html += '</div>';
                        rmBtn.after(html);
                    }
                    if (chlist.length > 0) {
                        rmBtn.html("加载更多历史信息...");
                    } else {
                        rmBtn.addClass('readdone');
                        rmBtn.html("没有更多的历史信息了");
                    }
                }
                rmBtn.removeClass('reading');
            });
        }
    });

    // 发送
    chatContainer.delegate('.chat-footer .chat-submit span', 'click', function () {
        var sbtn = $(this);
        var stxt = sbtn.prev();
        var chatId = sbtn.attr('chatId');
        var content = stxt.val();
        stxt.val('');
        if (!$z.util.isBlank(content)) {
            // 发送消息
            $z.http.post('/chat/msg/send', {
                'chatId': chatId,
                'content': content
            }, function (re) {
                // 获取当前队列的消息, 也包括自己的
                getChatMsg(chatId);
            });
        }
    });

    // 激活chat
    chatList.delegate('li', 'click', function () {
        var cli = $(this);
        var chatId = cli.attr('chatId');
        if (!cli.hasClass('active')) {
            cli.siblings().removeClass('active');
            cli.addClass('active');
            var ccontainer = chatContainer.find('#chat-container-' + cli.attr('chatId'));
            ccontainer.siblings().removeClass('active').addClass('hdn');
            ccontainer.addClass('active').removeClass('hdn');
            var chBody = chatContainer.find('.chat-content');
            chBody[0].scrollTop = chBody[0].scrollHeight;
        }
        // 加载对应的数据
        getChatMsg(chatId);

        currentChat = chatId;
    });

    // 删除chat
    chatList.delegate('li > i.fa', 'click', function (e) {
        e.stopPropagation();
        var cli = $(this).parent();
        var chatId = cli.attr('chatId');

        // 隐藏
        chatContainer.find('#chat-container-' + chatId).removeClass('active').addClass('hdn');

        var cprev = cli.prev();
        var cnext = cli.next();
        if (cprev.length > 0) {
            cprev.click();
        } else if (cnext.length > 0) {
            cnext.click();
        } else {
            noChat.removeClass('hdn');
            currentChat = 0;
        }

        // 删除
        cli.remove();
    });

    $scope.users = [];

    $scope.chatWithFriend = function (name) {
        var cm = window.myConf.friendsChat[name];
        var swbtn = mainSidebar.find('.main-sidebar-switch i');
        if (!swbtn.hasClass('mode-sidebar')) {
            swbtn.click();
        }
        addNewChat(cm)
    }

    $scope.myFriendsChat = function () {
        $z.http.get("/chat/friends", {
            'friends': window.myConf.friendsName
        }, function (re) {
            window.myConf.friendsChat = re.data;
            for (var i = 0; i < $scope.users.length; i++) {
                var su = $scope.users[i];
                var cm = window.myConf.friendsChat[su.name];
                su.chatId = cm.chatId;
            }
            $scope.$apply();
        });
    }

    $scope.myFriends = function () {
        $z.http.get("/user/friends", function (re) {
            var nfriends = re.data;
            if (nfriends.length != window.myConf.friends.length) {
                console.log("Load Friend List, At " + new Date());
                window.myConf.friends = nfriends;
                var fnms = [];
                for (var i = 0; i < nfriends.length; i++) {
                    fnms.push(nfriends[i].name);
                    window.myConf.friendsNameMap[nfriends[i].name] = nfriends[i].name;
                }
                window.myConf.friendsName = fnms.join(',');
                $scope.users = window.myConf.friends;
                $scope.myFriendsChat();
                $scope.myFriendsOnline();
            }
        });
    }

    $scope.myFriends();

    // 15分钟
    setInterval($scope.myFriends, 600000);

    $scope.myFriendsOnline = function () {
        $z.http.post("/user/friends/online", {
            'friends': window.myConf.friendsName
        }, function (re) {
            console.log("Check Friend Online, At " + new Date());
            var fonlineMap = re.data;
            for (var i = 0; i < window.myConf.friends.length; i++) {
                var fri = window.myConf.friends[i];
                fri.isOnline = fonlineMap[fri.name];
            }
            $scope.users = window.myConf.friends;
            $scope.$apply();
        });
    }
    //  20s
    setInterval($scope.myFriendsOnline, 20000);

    // 长ping
    function ping() {
        $z.http.get("/user/ping", function (re) {
            console.log("Ping Server, At " + new Date());
        });
    }

    ping();
    // 30s
    setInterval(ping, 30000);

    getUnread();

    // 启动一个轮训机制
    var lastUnread = 0;
    $z.http.cometES({
        url: "/chat/unread/longcheck",
        onChange: function (respTxt, opt) {
            var unreadNum = parseInt(respTxt);
            if (!isNaN(unreadNum)) {
                if (lastUnread < unreadNum) {
                    playAlertAudio();
                    document.title = " 您有 " + unreadNum + " 条新消息未读";
                }
                // 说明是数字, 那就尝试读取吧
                if (lastUnread !== unreadNum) {
                    getUnread();
                }
            }
        },
        onFinish: function () {
        }
    });

    var currentChat = 0;
    var currentChatUnread = 0;
    setInterval(function () {
        if (currentChat != 0 && currentChatUnread > 0) {
            // 判断一下当前chat的unread数量
            getChatMsg(currentChat);
        }
    }, 1000);

    // 播放提示音
    var isPlayAlertAudio = false;

    function playAlertAudio() {
        if (!isPlayAlertAudio) {
            isPlayAlertAudio = true;
            var vod = new Audio(window.myConf.alertAudio);
            vod.play();
            isPlayAlertAudio = false;
        }
    };

    // 上传头像
    $(document.body).delegate('.hm-user .user-face', 'click', function () {
        var uface = $(this);
        $.upload({
            title: "上传头像",
            width: 400,
            height: 300,
            upload: {
                num: 1,
                type: ['jpg', 'jpeg', 'png', 'gif'],
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
                    xhr.open("POST", "/user/upload/face/" + window.myConf.user, true);
                    xhr.setRequestHeader('Content-type', contentType)
                    xhr.send(file);
                },
                afterUpload: function () {

                },
                finishUpload: function () {
                    $.masker('close');
                    // 重新加载头像
                    var fp = uface.parent();
                    uface.remove();
                    fp.prepend('<img src="/user/face/' + window.myConf.user + "?" + new Date() + '" class="user-face">');
                }
            }
        });
    });
});

mainApp.config(['$routeProvider', function ($routeProvider) {
    var routeList = [].concat(coreRoute).concat(appRoute);
    for (var i = 0; i < routeList.length; i++) {
        var route = routeList[i];
        console.log("Reg Route : " + route.url);
        $routeProvider.when(route.url, {
            templateUrl: "views" + route.page,
            controller: route.ctrl
        });
    }
    $routeProvider.otherwise({redirectTo: '/notImpl'});
}]);


