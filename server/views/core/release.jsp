<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/_setup.jsp" %>
<div class="module-content module-release">
<div class="feture-list">
    <div class="feture-title">新功能列表</div>
    <ul>
        <li>
            "矩阵-屏幕制作", 讲素材摆按照播放要求进行摆放与设置(大小, 跨屏)
        </li>
        <li>
            "问与答", 支持问题分类, 方便统计或完成后续工作
        </li>
        <li>
            "新功能列表", 支持用户投票, 优先完成大家觉得最需要的功能
        </li>
    </ul>
</div>
<div class="release-log">
<div class="release-date">2014-11-16</div>
<div class="release-note">
    <ul>
        <li>
            "文件系统"加强
            <ol>
                <li>图片与视频支持预览</li>
                <li>视频支持播放预览</li>
                <li>视频支持转换为 n x n 模式</li>
                <li>文本或代码文件支持高亮显示+行号显示</li>
                <li>删除, 重命名, 制作副本</li>
            </ol>
        </li>
        <li>
            "下载服务"功能实现
            <ol>
                <li>存放各种盒子需要下载的文件</li>
                <li>通过url, http://octopus.danoolive.com/dw?fnm=文件名</li>
            </ol>
        </li>
        <li>
            "矩阵-素材转换服务", 对已上传素材进行二次转换(比如将视频按照3x5屏幕进行再转换), 即"文件系统"增强部分
        </li>
        <li>
            "矩阵-播放器配置", 配置盒子ip, 布局等
        </li>
    </ul>
</div>
<div class="release-date">2014-09-23</div>
<div class="release-note">
    <ul>
        <li>
            "文件上传功能"实现
            <ol>
                <li>可以上传图片,视频,压缩包,office相关文件等</li>
                <li>图片自动生成缩略图</li>
            </ol>
        </li>
        <li>
            "云存储"功能实现
            <ol>
                <li>分为"我的网盘"与"公共网盘"两个模块</li>
                <li>"我的网盘"只有用户自己具有访问全选</li>
                <li>"公共网盘"为域所有, 加入该域的用户都可以进行读写操作</li>
                <li>网盘类似本地磁盘操作, 可以建立文件夹分类, 系统本身也会根据文件类进行分类</li>
                <li>网盘支持grid与列表两种查看模式</li>
            </ol>
        </li>
        <li>
            "矩阵-素材管理"
            <ol>
                <li>使用方式等同于网盘操作</li>
                <li>矩阵会有特殊的 "文件操作" 按钮, 比如"按照矩阵设置进行视频转换"</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-09-22</div>
<div class="release-note">
    <ul>
        <li>
            "用户头像更新"上线
            <ol>
                <li>点击屏幕右上角头像, 上传一张图片(jpg, png, gif)即可替换头像</li>
            </ol>
        </li>
        <li>
            "聊天" 文件传输(图片, 视频, 文档等等)
            <ol>
                <li>直接将文件拖入聊天窗口即可, 暂时仅仅仅支持图片,office文件与压缩包</li>
            </ol>
        </li>
        <li>
            "播放器comment" 支持文件传输(图片, 视频, 文档等等)
            <ol>
                <li>直接将文件拖入聊天窗口即可, 暂时仅仅仅支持图片,office文件与压缩包</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-09-17</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>在小屏幕上(宽度1000px以内) monitor界面不再显示错行</li>
                <li>monitor的tab切换改为自动缩小, 为monitor提供更多空间</li>
            </ol>
        </li>
        <li>
            "邀请注册"模块实现, 方便让一个新用户完成注册并加入到对应的域中
        </li>
        <li>
            "单域monitor"功能实现, 方便用户查看自己的域的monitor信息
        </li>
    </ul>
</div>
<div class="release-date">2014-09-15</div>
<div class="release-note">
    <ul>
        <li>
            "更新日志"模块更新
            <ol>
                <li>内容分成2栏, "发布日志" 与 "新功能列表"</li>
                <li>"新功能列表" 为接下来要实现的功能, 部分功能点来自"问与答"中用户提出的问题</li>
            </ol>
        </li>
        <li>
            "聊天功能"模块更新
            <ol>
                <li>不刷新页面, 当前聊天信息不会消失</li>
                <li>可以查看历史聊天记录</li>
            </ol>
        </li>
        <li>
            "问与答"模块更新
            <ol>
                <li>显示每个问题的 "回复"数</li>
            </ol>
        </li>
        <li>
            "线上监控"模块更新
            <ol>
                <li>修正了某个域少盒子的问题, 是由于一个盒子被移动了, 在多个域绑定造成的</li>
            </ol>
        </li>
        <li>
            整体代码架构更新
            <ol>
                <li>route.js通过后台配置读取,不再配置在前端代码目录中</li>
                <li>views, rs目录更新位置, 更加合理, 方便开发与debug</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-09-04</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>后台数据获取方式更新</li>
            </ol>
        </li>
        <li>
            "登陆注册"模块更新
            <ol>
                <li>更新登陆方式为 "电子邮箱" + "密码" 进行登陆</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-08-28</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>备注单独一列显示</li>
                <li>调整备注框位置到坐下,防止被聊天功能遮挡</li>
                <li>添加重点关注功能(显示指定某些box)</li>
            </ol>
        </li>
        <li>
            "聊天功能"模块更新
            <ol>
                <li>新消息出现, 伴有声音提示</li>
                <li>新消息出现, 浏览器标题栏会有提示</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-08-27</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>监控数据根据准确,与服务器误差缩短为1分钟以内</li>
                <li>显示每个域的概括信息</li>
                <li>自动保存更加完善, 包括排序信息</li>
            </ol>
        </li>
        <li>
            "聊天功能"上线
            <ol>
                <li>可以查询好友的在线状态</li>
                <li>可以与好友进行聊天或留言</li>
            </ol>
        </li>
        <li>
            布局添加了侧边栏功能
            <ol>
                <li>侧边栏可以随时打开或隐藏</li>
                <li>目前侧边栏只有聊天功能, 稍后将添加其他模块</li>
            </ol>
        </li>
        <li>
            布局默认启用"mini"模式, 可以显示更多内容
        </li>
    </ul>
</div>
<div class="release-date">2014-08-18</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>修正3G信息, 最后心跳时间的显示问题</li>
            </ol>
        </li>
        <li>
            "截图监控"模块上线
            <ol>
                <li>查看某个盒子的一天内的截图, 通过截图变化, 判断盒子是否正常运行</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-08-17</div>
<div class="release-note">
    <ul>
        <li>
            "线上监控"模块更新
            <ol>
                <li>支持自动保存/加载配置(列设置, 查询条件, 分页, 服务器与域选择等信息)</li>
                <li>支持定时刷新</li>
                <li>支持下载当前查询数据</li>
                <li>备注会自动标明有多少条</li>
            </ol>
        </li>
    </ul>
</div>
<div class="release-date">2014-08-12</div>
<div class="release-note">
    <ul>
        <li>
            "更新日志"模块上线, 记录每次更新了哪些内容
        </li>
        <li>
            "问与搭"模块上线
        </li>
        <li>
            "线上监控"添加了备注功能并修正了部分显示bug
        </li>
        <li>
            "注册用户"模块上线
        </li>
        <li>
            "用户管理"模块上线(仅仅管理域可用)
        </li>
        <li>
            "域管理"模块上线(仅仅管理域可用)
        </li>
    </ul>
</div>
</div>
</div>