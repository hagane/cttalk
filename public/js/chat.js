var app = angular.module('chat', []);

app.service('ChatService', function () {
    var chats = {}
    var onSelectListeners = []

    this.getChats = function () {
        return chats
    }

    this.get = function (token) {
        return chat.token
    }

    this.put = function (token, name) {
        var chat = {
            "token" : token,
            "name" : name,
            "selected" : false,
            "unread" : 0,
            "messages" : []
        }
        chats[chat.token] = chat
    }

    this.unread = function (token, unread) {
        var chat = chats[token]
        if (chat) {
            chat.unread = unread
        }
    }

    this.update = function (token, messages) {
        var chat = chats[token]
        if (chat) {
            messages.forEach(chat.messages.push)
        }
    }

    this.select = function (token) {
        for (var key in chats) {
            chats[key].selected = false
        }
        var selected = chats[token]
        if (selected) {
            selected.selected = true
            onSelectListeners.forEach(function (listener) {
                listener(selected)
            })
        }
    }

    this.onSelect = function(listener) {
        onSelectListeners.push(listener)
    }
})

app.controller('RosterController', function ($scope, $http, ChatService) {
    var roster = this;

    $scope.chats = ChatService.getChats()

    function getUnreadMessages(token) {
        var req = $http.get("/api/unread/"+token)
            req.success(
                function(data, status, headers, config) {
                    ChatService.unread(token, data)
                }
            )
            req.error(
                function(data, status, headers, config) {
                    console.log("Failed updating chat [%s]: %s", chat.token, status)
                }
            )
    }

    var req = $http.get("/api/chats")
        req.success(
            function(data, status, headers, config) {
                for(var i in data) {
                    var chat = data[i]
                    ChatService.put(chat.token, chat.name)
                    getUnreadMessages(chat.token)
                }
            }
        )

    $scope.select = function (selectedChat) {
        ChatService.select(selectedChat.token)
    };
});

app.controller('ChatboxController', function ($scope, $http, ChatService) {
    var chatbox = this;

    ChatService.onSelect(function(selected) {
        $scope.chat = selected
        selected.unread = 0
    })

    $scope.post = function() {
        if (chatbox.text) {
            $scope.chat.messages.push({"sender" : chatbox.sender, "text" : chatbox.text})
            chatbox.text = ""

            var message = {
                "token" : $scope.chat.token,
                "sender" : chatbox.sender,
                "text" : chatbox.text
            }
            var req = $http.post("/api/send", message)
            req.success(function(data, status, headers, config) {})
        }
    }
});