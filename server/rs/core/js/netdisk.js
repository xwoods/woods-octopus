/**
 * Created by pw on 14-9-22.
 */

function ND4UserCtl($scope) {

    var module = $('.module-netdisk4me');
    var ndJq = module.find('.netdisk-container');
    ndJq.netdisk({
        root: {
            module: 'users',
            moduleKey: window.myConf.user
        }
    });

}

function ND4DomainCtl($scope) {

    var module = $('.module-netdisk4domain');
    var ndJq = module.find('.netdisk-container');
    ndJq.netdisk({
        root: {
            module: 'domains',
            moduleKey: window.myConf.domain
        }
    });
}