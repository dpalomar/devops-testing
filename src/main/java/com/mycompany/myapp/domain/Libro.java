package com.mycompany.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Libro.
 */
@Entity
@Table(name = "libro")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "paginas")
    private Integer paginas;

    @NotNull
    @Column(name = "isbn", nullable = false)
    private String isbn;

    @ManyToOne
    private Autor autor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Libro titulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getPaginas() {
        return paginas;
    }

    public Libro paginas(Integer paginas) {
        this.paginas = paginas;
        return this;
    }

    public void setPaginas(Integer paginas) {
        this.paginas = paginas;
    }

    public String getIsbn() {
        return isbn;
    }

    public Libro isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Autor getAutor() {
        return autor;
    }

    public Libro autor(Autor autor) {
        this.autor = autor;
        return this;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Libro libro = (Libro) o;
        if (libro.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, libro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Libro{" +
            "id=" + id +
            ", titulo='" + titulo + "'" +
            ", paginas='" + paginas + "'" +
            ", isbn='" + isbn + "'" +
            '}';
    }
}
