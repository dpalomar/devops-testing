(function() {
    'use strict';
    angular
        .module('libraryApp')
        .factory('Libro', Libro);

    Libro.$inject = ['$resource'];

    function Libro ($resource) {
        var resourceUrl =  'api/libros/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
