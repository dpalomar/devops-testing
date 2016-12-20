package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.LibraryApp;

import com.mycompany.myapp.domain.Autor;
import com.mycompany.myapp.repository.AutorRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AutorResource REST controller.
 *
 * @see AutorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryApp.class)
public class AutorResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NACIONALIDAD = "AAAAAAAAAA";
    private static final String UPDATED_NACIONALIDAD = "BBBBBBBBBB";

    @Inject
    private AutorRepository autorRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAutorMockMvc;

    private Autor autor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AutorResource autorResource = new AutorResource();
        ReflectionTestUtils.setField(autorResource, "autorRepository", autorRepository);
        this.restAutorMockMvc = MockMvcBuilders.standaloneSetup(autorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Autor createEntity(EntityManager em) {
        Autor autor = new Autor()
                .nombre(DEFAULT_NOMBRE)
                .nacimiento(DEFAULT_NACIMIENTO)
                .nacionalidad(DEFAULT_NACIONALIDAD);
        return autor;
    }

    @Before
    public void initTest() {
        autor = createEntity(em);
    }

    @Test
    @Transactional
    public void createAutor() throws Exception {
        int databaseSizeBeforeCreate = autorRepository.findAll().size();

        // Create the Autor

        restAutorMockMvc.perform(post("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isCreated());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeCreate + 1);
        Autor testAutor = autorList.get(autorList.size() - 1);
        assertThat(testAutor.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testAutor.getNacimiento()).isEqualTo(DEFAULT_NACIMIENTO);
        assertThat(testAutor.getNacionalidad()).isEqualTo(DEFAULT_NACIONALIDAD);
    }

    @Test
    @Transactional
    public void createAutorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = autorRepository.findAll().size();

        // Create the Autor with an existing ID
        Autor existingAutor = new Autor();
        existingAutor.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAutorMockMvc.perform(post("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingAutor)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = autorRepository.findAll().size();
        // set the field null
        autor.setNombre(null);

        // Create the Autor, which fails.

        restAutorMockMvc.perform(post("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isBadRequest());

        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAutors() throws Exception {
        // Initialize the database
        autorRepository.saveAndFlush(autor);

        // Get all the autorList
        restAutorMockMvc.perform(get("/api/autors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(autor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].nacimiento").value(hasItem(DEFAULT_NACIMIENTO.toString())))
            .andExpect(jsonPath("$.[*].nacionalidad").value(hasItem(DEFAULT_NACIONALIDAD.toString())));
    }

    @Test
    @Transactional
    public void getAutor() throws Exception {
        // Initialize the database
        autorRepository.saveAndFlush(autor);

        // Get the autor
        restAutorMockMvc.perform(get("/api/autors/{id}", autor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(autor.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.nacimiento").value(DEFAULT_NACIMIENTO.toString()))
            .andExpect(jsonPath("$.nacionalidad").value(DEFAULT_NACIONALIDAD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAutor() throws Exception {
        // Get the autor
        restAutorMockMvc.perform(get("/api/autors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAutor() throws Exception {
        // Initialize the database
        autorRepository.saveAndFlush(autor);
        int databaseSizeBeforeUpdate = autorRepository.findAll().size();

        // Update the autor
        Autor updatedAutor = autorRepository.findOne(autor.getId());
        updatedAutor
                .nombre(UPDATED_NOMBRE)
                .nacimiento(UPDATED_NACIMIENTO)
                .nacionalidad(UPDATED_NACIONALIDAD);

        restAutorMockMvc.perform(put("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAutor)))
            .andExpect(status().isOk());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeUpdate);
        Autor testAutor = autorList.get(autorList.size() - 1);
        assertThat(testAutor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testAutor.getNacimiento()).isEqualTo(UPDATED_NACIMIENTO);
        assertThat(testAutor.getNacionalidad()).isEqualTo(UPDATED_NACIONALIDAD);
    }

    @Test
    @Transactional
    public void updateNonExistingAutor() throws Exception {
        int databaseSizeBeforeUpdate = autorRepository.findAll().size();

        // Create the Autor

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAutorMockMvc.perform(put("/api/autors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(autor)))
            .andExpect(status().isCreated());

        // Validate the Autor in the database
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAutor() throws Exception {
        // Initialize the database
        autorRepository.saveAndFlush(autor);
        int databaseSizeBeforeDelete = autorRepository.findAll().size();

        // Get the autor
        restAutorMockMvc.perform(delete("/api/autors/{id}", autor.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Autor> autorList = autorRepository.findAll();
        assertThat(autorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
