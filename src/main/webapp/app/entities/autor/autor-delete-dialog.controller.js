(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('AutorDeleteController',AutorDeleteController);

    AutorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Autor'];

    function AutorDeleteController($uibModalInstance, entity, Autor) {
        var vm = this;

        vm.autor = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Autor.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
