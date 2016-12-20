package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Autor.
 */
@Entity
@Table(name = "autor")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Autor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "nacimiento")
    private LocalDate nacimiento;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @OneToMany(mappedBy = "autor")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Libro> libros = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Autor nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getNacimiento() {
        return nacimiento;
    }

    public Autor nacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
        return this;
    }

    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public Autor nacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
        return this;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Set<Libro> getLibros() {
        return libros;
    }

    public Autor libros(Set<Libro> libros) {
        this.libros = libros;
        return this;
    }

    public Autor addLibro(Libro libro) {
        libros.add(libro);
        libro.setAutor(this);
        return this;
    }

    public Autor removeLibro(Libro libro) {
        libros.remove(libro);
        libro.setAutor(null);
        return this;
    }

    public void setLibros(Set<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Autor autor = (Autor) o;
        if (autor.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, autor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Autor{" +
            "id=" + id +
            ", nombre='" + nombre + "'" +
            ", nacimiento='" + nacimiento + "'" +
            ", nacionalidad='" + nacionalidad + "'" +
            '}';
    }
}
