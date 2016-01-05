/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />
$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    var page = new App();
    ko.applyBindings(page);
});
var App = (function () {
    function App() {
        var _this = this;
        this.status = ko.observable(AppStatus.Landing);
        this.passwordConfirm = ko.observable("");
        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(function () { return _this.user().password() == _this.passwordConfirm(); }, this);
        this.initUser();
    } //ctor
    App.prototype.initUser = function () {
        var siteCookie = this.findCookie();
        if (siteCookie == null) {
            //TODO kill these cookies
            this.status(AppStatus.SignUp);
        }
        else {
            var selectorValidator = siteCookie.split(":");
            this.getUser(selectorValidator[0], selectorValidator[1]);
        }
    };
    App.prototype.getUser = function (selector, validator) {
        var valid = { selector: selector, validator: validator };
        var parameters = JSON.stringify(valid);
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "getUserFromToken",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (o) {
            self.user(ko.toJS(o));
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.findCookie = function () {
        var usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1];
        }
        else {
            return null;
        }
    };
    return App;
})();
var AppStatus;
(function (AppStatus) {
    AppStatus[AppStatus["SignUp"] = 0] = "SignUp";
    AppStatus[AppStatus["Landing"] = 1] = "Landing";
    AppStatus[AppStatus["Account"] = 2] = "Account";
    AppStatus[AppStatus["ViewUser"] = 3] = "ViewUser";
})(AppStatus || (AppStatus = {}));
//# sourceMappingURL=app.js.map