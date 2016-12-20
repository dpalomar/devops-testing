(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('AutorDetailController', AutorDetailController);

    AutorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Autor', 'Libro'];

    function AutorDetailController($scope, $rootScope, $stateParams, previousState, entity, Autor, Libro) {
        var vm = this;

        vm.autor = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryApp:autorUpdate', function(event, result) {
            vm.autor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
