(function() {
    'use strict';

    angular
        .module('libraryApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('autor', {
            parent: 'entity',
            url: '/autor?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'libraryApp.autor.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/autor/autors.html',
                    controller: 'AutorController',
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
                    $translatePartialLoader.addPart('autor');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('autor-detail', {
            parent: 'entity',
            url: '/autor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'libraryApp.autor.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/autor/autor-detail.html',
                    controller: 'AutorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('autor');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Autor', function($stateParams, Autor) {
                    return Autor.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'autor',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('autor-detail.edit', {
            parent: 'autor-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/autor/autor-dialog.html',
                    controller: 'AutorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Autor', function(Autor) {
                            return Autor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('autor.new', {
            parent: 'autor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/autor/autor-dialog.html',
                    controller: 'AutorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nombre: null,
                                nacimiento: null,
                                nacionalidad: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('autor', null, { reload: 'autor' });
                }, function() {
                    $state.go('autor');
                });
            }]
        })
        .state('autor.edit', {
            parent: 'autor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/autor/autor-dialog.html',
                    controller: 'AutorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Autor', function(Autor) {
                            return Autor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('autor', null, { reload: 'autor' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('autor.delete', {
            parent: 'autor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/autor/autor-delete-dialog.html',
                    controller: 'AutorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Autor', function(Autor) {
                            return Autor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('autor', null, { reload: 'autor' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
