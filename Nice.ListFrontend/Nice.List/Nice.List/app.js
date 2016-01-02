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
    } //ctor
    App.prototype.getData = function () {
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "getAll",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            crossDomain: true
        };
        var that = this;
        jQuery.ajax(settings).then(function (o) {
            var items = o[0];
            for (var i = 0; i < o.length; i++) {
                var item = o[i];
            }
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.submitData = function () {
        var testUser = {
            userID: 1,
            userName: "Test Name",
            firstName: "Jill",
            lastName: "Smith",
            city: "Anytown",
            state: "IL",
            country: "USA",
            postcode: "74114",
            emailAddress: "test@email.com",
            password: "guest"
        };
        var parameters = JSON.stringify(testUser);
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "createUser",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        jQuery.ajax(settings).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.getSignUp = function () {
        this.status(AppStatus.SignUp);
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