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
    public lines: KnockoutObservableArray<UserModel>;

    constructor() {
        this.lines = ko.observableArray<UserModel>();
        var that = this;
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
                that.lines.push(ko.toJS(item));
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

}