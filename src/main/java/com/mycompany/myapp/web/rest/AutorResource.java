package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.Autor;

import com.mycompany.myapp.repository.AutorRepository;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Autor.
 */
@RestController
@RequestMapping("/api")
public class AutorResource {

    private final Logger log = LoggerFactory.getLogger(AutorResource.class);
        
    @Inject
    private AutorRepository autorRepository;

    /**
     * POST  /autors : Create a new autor.
     *
     * @param autor the autor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new autor, or with status 400 (Bad Request) if the autor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/autors")
    @Timed
    public ResponseEntity<Autor> createAutor(@Valid @RequestBody Autor autor) throws URISyntaxException {
        log.debug("REST request to save Autor : {}", autor);
        if (autor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("autor", "idexists", "A new autor cannot already have an ID")).body(null);
        }
        Autor result = autorRepository.save(autor);
        return ResponseEntity.created(new URI("/api/autors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("autor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /autors : Updates an existing autor.
     *
     * @param autor the autor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated autor,
     * or with status 400 (Bad Request) if the autor is not valid,
     * or with status 500 (Internal Server Error) if the autor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/autors")
    @Timed
    public ResponseEntity<Autor> updateAutor(@Valid @RequestBody Autor autor) throws URISyntaxException {
        log.debug("REST request to update Autor : {}", autor);
        if (autor.getId() == null) {
            return createAutor(autor);
        }
        Autor result = autorRepository.save(autor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("autor", autor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /autors : get all the autors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of autors in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/autors")
    @Timed
    public ResponseEntity<List<Autor>> getAllAutors(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Autors");
        Page<Autor> page = autorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/autors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /autors/:id : get the "id" autor.
     *
     * @param id the id of the autor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the autor, or with status 404 (Not Found)
     */
    @GetMapping("/autors/{id}")
    @Timed
    public ResponseEntity<Autor> getAutor(@PathVariable Long id) {
        log.debug("REST request to get Autor : {}", id);
        Autor autor = autorRepository.findOne(id);
        return Optional.ofNullable(autor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /autors/:id : delete the "id" autor.
     *
     * @param id the id of the autor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/autors/{id}")
    @Timed
    public ResponseEntity<Void> deleteAutor(@PathVariable Long id) {
        log.debug("REST request to delete Autor : {}", id);
        autorRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("autor", id.toString())).build();
    }

}
