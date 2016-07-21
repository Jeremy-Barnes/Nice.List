class ServiceMethods {
    static baseURL: string = "http://52.32.150.194:8080/api/nice/"; //http://localhost:8080/api/nice/
    static selectorValidator: string[];

    public static doAjax(functionName: string, functionService: string, parameters: any): JQueryPromise<any> {
        var param = JSON.stringify(parameters);
        var settings: JQueryAjaxSettings = {
            url: ServiceMethods.baseURL + functionService + "/" + functionName,
            type: "POST",
            crossDomain: true,
            data: param,
            contentType: "application/json",
            headers: {
                SelectorValidator: ServiceMethods.selectorValidator ? ServiceMethods.selectorValidator[0] + ':' + ServiceMethods.selectorValidator[1] : null,
            },
            success: (json, status, args) => {
                if (args.getResponseHeader("SelectorValidator")) {
                    ServiceMethods.selectorValidator = args.getResponseHeader("SelectorValidator").split(":");
                }
            }
        };
        return jQuery.ajax(settings);       
    }

    public static createUser(user: UserModel): JQueryPromise<User> {
        return ServiceMethods.doAjax("createUser", "users", ko.toJS(user));
    }

    public static getUserFromToken(selector: string, validator: string): JQueryPromise<User>{
        return ServiceMethods.doAjax("getUserFromToken", "users", { selector: selector, validator: validator });
    }

    public static changeUserInformation(user: UserModel): JQueryPromise<User> {
        var dat = new FormData();
        dat.append("user", JSON.stringify(ko.toJS(user)));
        dat.append("file", (<any>jQuery("#file")[0]).files[0]);

        var settings: JQueryAjaxSettings = {
            url: ServiceMethods.baseURL + "users/changeUserInformation",
            type: "POST",
            contentType: false,
            processData: false,
            dataType: "json",
            data: dat,
            crossDomain: true
        };
        return jQuery.ajax(settings);
    }

    public static logIn(user: UserModel): JQueryPromise<User> {
        return ServiceMethods.doAjax("getUserFromLogin", "users", ko.mapping.toJS(user));
    }

    public static addFriend(user: UserModel, friendEmailAddress: string): JQueryPromise<void> {
        var parameters = {};
        parameters["user"] = ko.mapping.toJS(user);
        parameters["requestedEmailAddress"] = friendEmailAddress;
        return ServiceMethods.doAjax("createFriendship", "friends", parameters);
    }

    public static respondToFriendRequest(acceptedRequest: boolean, requester: UserModel, user: UserModel): JQueryPromise<void> {
        var req: Friendship = {
            friendshipID: -1,
            accepted: acceptedRequest,
            requestedUserID: user.userID(),
            requesterUserID: requester.userID()
        }
        return ServiceMethods.doAjax("respondToFriendRequest", "friends", req);
    }

    public static addWishListItem(item: WishListItemModel): JQueryPromise<WishListItem> {
        item.price(0);
        return ServiceMethods.doAjax("addListItem", "wishlist", ko.mapping.toJS(item));
    }

    public static updateWishListItem(editWishListItem: WishListItemModel): JQueryPromise<WishListItem> {
        return ServiceMethods.doAjax("editListItem", "wishlist", ko.mapping.toJS(editWishListItem));
    }

    public static getUserWishList(requester: UserModel, friend: UserModel): JQueryPromise<WishListItem[]> {
        var req: Friendship = {
            accepted: true,
            friendshipID: -1,
            requesterUserID: requester.userID(),
            requestedUserID: friend.userID(),
        }
        return ServiceMethods.doAjax("getUserWishList", "wishlist", req);
    }
}