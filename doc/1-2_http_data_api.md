标准Http数据接口
================

所有数据返回结果都是JSON格式

    // http返回值
    {
        ok          : true | false,             // 标示当前操作成功与否
        errCode     : 'err.file.noexists',      // 错误码
        msg         : '文件不存在',             // 本地化后的错误信息
        data        : object | array            // 数据内容
    }

一般来说, 操作成功, 直接根据data属性进行后续操作, 操作失败, 查看errCode或msg


