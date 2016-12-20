(function() {
    'use strict';

    angular
        .module('libraryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('libro', {
            parent: 'entity',
            url: '/libro?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'libraryApp.libro.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/libro/libros.html',
                    controller: 'LibroController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('libro');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('libro-detail', {
            parent: 'entity',
            url: '/libro/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'libraryApp.libro.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/libro/libro-detail.html',
                    controller: 'LibroDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('libro');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Libro', function($stateParams, Libro) {
                    return Libro.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'libro',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('libro-detail.edit', {
            parent: 'libro-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/libro/libro-dialog.html',
                    controller: 'LibroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Libro', function(Libro) {
                            return Libro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('libro.new', {
            parent: 'libro',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/libro/libro-dialog.html',
                    controller: 'LibroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                titulo: null,
                                paginas: null,
                                isbn: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('libro', null, { reload: 'libro' });
                }, function() {
                    $state.go('libro');
                });
            }]
        })
        .state('libro.edit', {
            parent: 'libro',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/libro/libro-dialog.html',
                    controller: 'LibroDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Libro', function(Libro) {
                            return Libro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('libro', null, { reload: 'libro' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('libro.delete', {
            parent: 'libro',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/libro/libro-delete-dialog.html',
                    controller: 'LibroDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Libro', function(Libro) {
                            return Libro.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('libro', null, { reload: 'libro' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
