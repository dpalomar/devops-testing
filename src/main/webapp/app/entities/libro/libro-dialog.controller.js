(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('LibroDialogController', LibroDialogController);

    LibroDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Libro', 'Autor'];

    function LibroDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Libro, Autor) {
        var vm = this;

        vm.libro = entity;
        vm.clear = clear;
        vm.save = save;
        vm.autors = Autor.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.libro.id !== null) {
                Libro.update(vm.libro, onSaveSuccess, onSaveError);
            } else {
                Libro.save(vm.libro, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('libraryApp:libroUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
