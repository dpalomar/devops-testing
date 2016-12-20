package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.LibraryApp;

import com.mycompany.myapp.domain.Libro;
import com.mycompany.myapp.repository.LibroRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LibroResource REST controller.
 *
 * @see LibroResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryApp.class)
public class LibroResourceIntTest {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final Integer DEFAULT_PAGINAS = 1;
    private static final Integer UPDATED_PAGINAS = 2;

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    @Inject
    private LibroRepository libroRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restLibroMockMvc;

    private Libro libro;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LibroResource libroResource = new LibroResource();
        ReflectionTestUtils.setField(libroResource, "libroRepository", libroRepository);
        this.restLibroMockMvc = MockMvcBuilders.standaloneSetup(libroResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Libro createEntity(EntityManager em) {
        Libro libro = new Libro()
                .titulo(DEFAULT_TITULO)
                .paginas(DEFAULT_PAGINAS)
                .isbn(DEFAULT_ISBN);
        return libro;
    }

    @Before
    public void initTest() {
        libro = createEntity(em);
    }

    @Test
    @Transactional
    public void createLibro() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro

        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isCreated());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate + 1);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testLibro.getPaginas()).isEqualTo(DEFAULT_PAGINAS);
        assertThat(testLibro.getIsbn()).isEqualTo(DEFAULT_ISBN);
    }

    @Test
    @Transactional
    public void createLibroWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = libroRepository.findAll().size();

        // Create the Libro with an existing ID
        Libro existingLibro = new Libro();
        existingLibro.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingLibro)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTituloIsRequired() throws Exception {
        int databaseSizeBeforeTest = libroRepository.findAll().size();
        // set the field null
        libro.setTitulo(null);

        // Create the Libro, which fails.

        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsbnIsRequired() throws Exception {
        int databaseSizeBeforeTest = libroRepository.findAll().size();
        // set the field null
        libro.setIsbn(null);

        // Create the Libro, which fails.

        restLibroMockMvc.perform(post("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isBadRequest());

        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLibros() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);

        // Get all the libroList
        restLibroMockMvc.perform(get("/api/libros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(libro.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO.toString())))
            .andExpect(jsonPath("$.[*].paginas").value(hasItem(DEFAULT_PAGINAS)))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN.toString())));
    }

    @Test
    @Transactional
    public void getLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);

        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", libro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(libro.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO.toString()))
            .andExpect(jsonPath("$.paginas").value(DEFAULT_PAGINAS))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLibro() throws Exception {
        // Get the libro
        restLibroMockMvc.perform(get("/api/libros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);
        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Update the libro
        Libro updatedLibro = libroRepository.findOne(libro.getId());
        updatedLibro
                .titulo(UPDATED_TITULO)
                .paginas(UPDATED_PAGINAS)
                .isbn(UPDATED_ISBN);

        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLibro)))
            .andExpect(status().isOk());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate);
        Libro testLibro = libroList.get(libroList.size() - 1);
        assertThat(testLibro.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testLibro.getPaginas()).isEqualTo(UPDATED_PAGINAS);
        assertThat(testLibro.getIsbn()).isEqualTo(UPDATED_ISBN);
    }

    @Test
    @Transactional
    public void updateNonExistingLibro() throws Exception {
        int databaseSizeBeforeUpdate = libroRepository.findAll().size();

        // Create the Libro

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLibroMockMvc.perform(put("/api/libros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(libro)))
            .andExpect(status().isCreated());

        // Validate the Libro in the database
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLibro() throws Exception {
        // Initialize the database
        libroRepository.saveAndFlush(libro);
        int databaseSizeBeforeDelete = libroRepository.findAll().size();

        // Get the libro
        restLibroMockMvc.perform(delete("/api/libros/{id}", libro.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Libro> libroList = libroRepository.findAll();
        assertThat(libroList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
