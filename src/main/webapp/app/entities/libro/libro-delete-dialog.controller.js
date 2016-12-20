(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('LibroDeleteController',LibroDeleteController);

    LibroDeleteController.$inject = ['$uibModalInstance', 'entity', 'Libro'];

    function LibroDeleteController($uibModalInstance, entity, Libro) {
        var vm = this;

        vm.libro = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Libro.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
