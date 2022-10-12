package ke.go.kra.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Expertise.
 */
@Table("expertise")
public class Expertise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(max = 50)
    @Column("name")
    private String name;

    @Size(max = 255)
    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "expertise", "products" }, allowSetters = true)
    private Set<Developer> developers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Expertise id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Expertise name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Expertise description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Developer> getDevelopers() {
        return this.developers;
    }

    public void setDevelopers(Set<Developer> developers) {
        if (this.developers != null) {
            this.developers.forEach(i -> i.removeExpertise(this));
        }
        if (developers != null) {
            developers.forEach(i -> i.addExpertise(this));
        }
        this.developers = developers;
    }

    public Expertise developers(Set<Developer> developers) {
        this.setDevelopers(developers);
        return this;
    }

    public Expertise addDeveloper(Developer developer) {
        this.developers.add(developer);
        developer.getExpertise().add(this);
        return this;
    }

    public Expertise removeDeveloper(Developer developer) {
        this.developers.remove(developer);
        developer.getExpertise().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expertise)) {
            return false;
        }
        return id != null && id.equals(((Expertise) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Expertise{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
