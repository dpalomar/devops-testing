package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Autor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Autor entity.
 */
@SuppressWarnings("unused")
public interface AutorRepository extends JpaRepository<Autor,Long> {

}
