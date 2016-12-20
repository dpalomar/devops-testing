(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('LibroDetailController', LibroDetailController);

    LibroDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Libro', 'Autor'];

    function LibroDetailController($scope, $rootScope, $stateParams, previousState, entity, Libro, Autor) {
        var vm = this;

        vm.libro = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryApp:libroUpdate', function(event, result) {
            vm.libro = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
