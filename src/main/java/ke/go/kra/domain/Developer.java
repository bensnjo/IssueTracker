package ke.go.kra.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The Developer entity.
 */
@Schema(description = "The Developer entity.")
@Table("developer")
public class Developer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("staff_no")
    private String staffNo;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("full_name")
    private String fullName;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("phone_number")
    private String phoneNumber;

    @Transient
    //@JsonIgnoreProperties(value = { "developers" }, allowSetters = true)
    private Set<Expertise> expertise = new HashSet<>();

    @Transient
    //@JsonIgnoreProperties(value = { "developers" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Developer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffNo() {
        return this.staffNo;
    }

    public Developer staffNo(String staffNo) {
        this.setStaffNo(staffNo);
        return this;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Developer fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public Developer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Developer phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Expertise> getExpertise() {
        return this.expertise;
    }

    public void setExpertise(Set<Expertise> expertise) {
        this.expertise = expertise;
    }

    public Developer expertise(Set<Expertise> expertise) {
        this.setExpertise(expertise);
        return this;
    }

    public Developer addExpertise(Expertise expertise) {
        this.expertise.add(expertise);
        expertise.getDevelopers().add(this);
        return this;
    }

    public Developer removeExpertise(Expertise expertise) {
        this.expertise.remove(expertise);
        expertise.getDevelopers().remove(this);
        return this;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeDeveloper(this));
        }
        if (products != null) {
            products.forEach(i -> i.addDeveloper(this));
        }
        this.products = products;
    }

    public Developer products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Developer addProduct(Product product) {
        this.products.add(product);
        product.getDevelopers().add(this);
        return this;
    }

    public Developer removeProduct(Product product) {
        this.products.remove(product);
        product.getDevelopers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Developer)) {
            return false;
        }
        return id != null && id.equals(((Developer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Developer{" +
            "id=" + getId() +
            ", staffNo='" + getStaffNo() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            "}";
    }
}
