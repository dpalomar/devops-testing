(function() {
    'use strict';
    angular
        .module('libraryApp')
        .factory('Autor', Autor);

    Autor.$inject = ['$resource', 'DateUtils'];

    function Autor ($resource, DateUtils) {
        var resourceUrl =  'api/autors/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.nacimiento = DateUtils.convertLocalDateFromServer(data.nacimiento);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.nacimiento = DateUtils.convertLocalDateToServer(copy.nacimiento);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.nacimiento = DateUtils.convertLocalDateToServer(copy.nacimiento);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
