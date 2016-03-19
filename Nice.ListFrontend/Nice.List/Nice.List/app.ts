/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />

var page: App;

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    page = new App();
    ko.applyBindings(page);
});


class App {

    /******** App status tracking *******/
    public status: KnockoutObservable<AppStatus> = ko.observable(AppStatus.Landing);
    public friendAddStatus: KnockoutObservable<FriendAddStatus> = ko.observable(FriendAddStatus.Waiting);
    public loadedHTML: boolean[] = new Array<boolean>();

    /******** Active user data ******/
    public user: KnockoutObservable<UserModel> = ko.observable(new UserModel());
    public topFriends: KnockoutObservableArray<UserModel> = ko.observableArray<UserModel>(null);

    /******** Form Field Bind Vars *******/
    public friendEmailAddress: KnockoutObservable<string> = ko.observable("");
    public editWishListItem: KnockoutObservable<WishListItemModel> = ko.observable(new WishListItemModel());
    public passwordConfirm: KnockoutObservable<string> = ko.observable("");
    public passwordsMatch: KnockoutComputed<boolean>;
    public wishUser: KnockoutObservable<UserModel> = ko.observable(new UserModel());

    constructor() {
        this.passwordsMatch = ko.pureComputed(() => {
            var passwordCo = this.passwordConfirm();
            return this.user().password() == this.passwordConfirm();
        }, this);

        ko.computed(() => {
            var user = this.user();
            if (user != null && user.friends() != null) {
                this.topFriends.removeAll();
                var max = this.user().friends().length < 5 ? this.user().friends().length : 5;
                for (let i = 0; i < max; i++) {
                    this.topFriends.push(user.friends()[i]);
                }
            }
        }, this);

        ko.computed(() => {
            if (this.status() != AppStatus.Landing && !this.loadedHTML[this.status().toString()]) {
                this.loadedHTML[this.status().toString()] = true;
                this.getTemplateHTML(AppStatus[this.status()]);
            }
        }, this);
        this.initUser();

        jQuery('#add-friend').on('hidden.bs.modal', (e) => { this.friendAddStatus(FriendAddStatus.Waiting); this.friendEmailAddress(""); }); //I'm not happy about this, either

    }//ctor

    private initUser(): void {

        let siteCookie: string = this.findCookie();
        if (siteCookie == null) { //no user or too many users
            //TODO kill these cookies
            this.status(AppStatus.Landing);
        } else {
            let selectorValidator = siteCookie.split(":");
            this.getUser(selectorValidator[0], selectorValidator[1]);
        }
    }

    private findCookie(): string {

        let usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1];
        } else {
            return null;
        }
    }

    public getUser(selector: string, validator: string) {
        var self = this;
        ServiceMethods.getUserFromToken(selector, validator).then(function (o: User) {
            self.user(ko.mapping.fromJS(o));
            self.wishUser(self.user());
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public createUser() {
        var self = this;
        ServiceMethods.createUser(this.user()).then(function (updatedUser: User) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.passwordConfirm("");
            (<any>$("#sign-up")).modal('hide'); 
            self.status(AppStatus.Account);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public submitAccountChanges() {
        var self = this;
        ServiceMethods.changeUserInformation(this.user()).then(function (updatedUser: User) {
            var us = ko.mapping.fromJS(updatedUser); 
            self.user(us);
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public logIn() {
        var self = this;
        ServiceMethods.logIn(this.user()).then(function (o: User) {
            self.user(ko.mapping.fromJS(o));
            self.wishUser(self.user());
            (<any>$("#log-in")).modal('hide'); 
           if (o.firstName.length && o.lastName.length) {
                self.status(AppStatus.Home);
            } else {
                self.status(AppStatus.Account);
            }
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public addFriend() {
        var self = this;
        ServiceMethods.addFriend(this.user(), this.friendEmailAddress()).then(function () {
            self.friendAddStatus(FriendAddStatus.Success);
        }).fail(function (request: JQueryXHR) {
            self.friendAddStatus(FriendAddStatus.Failure);
        });
    }

    public acceptFriendRequest(newFriend: UserModel) {
        this.respondToFriendRequest(true, newFriend);
    }

    public rejectFriendRequest(snubbed: UserModel) {
        this.respondToFriendRequest(false, snubbed);
    }

    private respondToFriendRequest(acceptedRequest: boolean, requester: UserModel) {
        var self = this;
        ServiceMethods.respondToFriendRequest(acceptedRequest, requester, this.user()).then(function () {
            var deletedIndex = self.user().requestsToReview.remove(requester)
            if (acceptedRequest) {
                self.user().friends.push(requester);
            }
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public addWishListItem() {
        this.editWishListItem().price(0);
        var self = this;
        ServiceMethods.addWishListItem(this.editWishListItem()).then(function (item: WishListItem) {
            self.user().wishList.push(ko.mapping.fromJS(item));
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request: JQueryXHR) {
            self.editWishListItem(new WishListItemModel());
            alert(request);
        });
    }

    public editItem(item: WishListItemModel) {
        this.editWishListItem(item);
        (<any>$("#edit-item")).modal('show'); 
    }

    public updateWishListItem() {
        if (this.editWishListItem().price(0) == null) this.editWishListItem().price(0);
        var self = this;
        ServiceMethods.updateWishListItem(this.editWishListItem()).then(function (item: WishListItem) {
            if (!(self.editWishListItem().wishListItemID() > 0)) {
                self.user().wishList.push(ko.mapping.fromJS(item));
            }
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request: JQueryXHR) {
            self.editWishListItem(new WishListItemModel());
            alert(request);
        });
    }

    public markWishListItemAsBought(item: WishListItemModel) {
        item.isBought(true);
        item.purchaserUserID(this.user().userID());
        this.editWishListItem(item);
        this.updateWishListItem();
    }

    public buyWishListItem(item: WishListItemModel) {
        this.markWishListItemAsBought(item);
        window.open(item.URL());
    }

    public selectFriend(friend: UserModel) {
        var self = this;
        ServiceMethods.getUserWishList(this.user(), friend).then(function (item: WishListItem[]) {
            if (!(self.editWishListItem().wishListItemID() > 0)) {
                self.wishUser().wishList(ko.mapping.fromJS(item)());
            }
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
        this.wishUser(friend);
        this.status(AppStatus.Home);
    }

    private getTemplateHTML(page: String): JQueryPromise<void> {
        var opts: JQueryAjaxSettings = {
            url: location.origin + "/Views/" + page + ".htm",
            type: "GET",
            dataType: "html",
            contentType: "text/html"
        };
        var self = this;
        return jQuery.ajax(opts).done((html: any) => {
            jQuery("#bindDIV").append(html);
            ko.applyBindings(self, jQuery("#" + jQuery(html)[0].id)[0]); 
        });
    }
}

enum AppStatus {
    Home, Account, Landing, Friends, FriendRequests
}

enum FriendAddStatus {
    Success, Failure, Waiting
}