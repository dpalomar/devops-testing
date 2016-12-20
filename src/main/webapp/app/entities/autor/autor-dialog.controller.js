(function() {
    'use strict';

    angular
        .module('libraryApp')
        .controller('AutorDialogController', AutorDialogController);

    AutorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Autor', 'Libro'];

    function AutorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Autor, Libro) {
        var vm = this;

        vm.autor = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.libros = Libro.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.autor.id !== null) {
                Autor.update(vm.autor, onSaveSuccess, onSaveError);
            } else {
                Autor.save(vm.autor, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('libraryApp:autorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.nacimiento = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
