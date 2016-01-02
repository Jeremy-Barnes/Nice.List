/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    var page = new App();
    ko.applyBindings(page);
});

class App {
    public user: KnockoutObservable<UserModel>;
    public status: KnockoutObservable<AppStatus> = ko.observable(AppStatus.Landing);
    public passwordConfirm: KnockoutObservable<string> = ko.observable("");
    public passwordsMatch: KnockoutComputed<boolean>;


    constructor() {
        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(() => { return this.user().password() == this.passwordConfirm();}, this);
    }//ctor

    public getData() {
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "getAll",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            crossDomain: true
        };
        var that = this;
        jQuery.ajax(settings).then(function (o) {
            var items = o[0];
            for (let i = 0; i < o.length; i++) {
                let item: User = o[i];
            }
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });

    }

    public submitData() {
        var testUser: User = {
            userID : 1,
            userName : "Test Name",
            firstName : "Jill",
            lastName : "Smith",
            city : "Anytown",
            state : "IL",
            country: "USA",
            postcode: "74114",
            emailAddress: "test@email.com",
            password: "guest"
        };
        var parameters = JSON.stringify(testUser);

        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "createUser",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        jQuery.ajax(settings).fail(function (request: JQueryXHR) {
            alert(request);
        });


    }

    public getSignUp() {
        this.status(AppStatus.SignUp);
    }
}

enum AppStatus {
    SignUp, Landing, Account, ViewUser
}