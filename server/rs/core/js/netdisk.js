/**
 * Created by pw on 14-9-22.
 */

function ND4UserCtl($scope) {

    var module = $('.module-netdisk4me');

    var ndJq = module.find('.netdisk-list');
    var listModeJq = module.find('.netdisk-toolbar-btns.list-btns');

    listModeJq.delegate('li', 'click', function () {
        var cli = $(this);
        if (!cli.hasClass('active')) {
            cli.siblings().removeClass('active');
            cli.addClass('active');
            if (cli.attr('mode') == 'list') {
                ndJq.addClass('list-view');
            } else {
                ndJq.removeClass('list-view');
            }
        }
    });
}

function ND4DomainCtl($scope) {

}