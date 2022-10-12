package ke.go.kra.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.*;
import ke.go.kra.domain.enumeration.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Issue.
 */
@Table("issue")
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 10)
    @Column("defect_number")
    private String defectNumber;

    @Size(max = 255)
    @Column("description")
    private String description;

    @Size(max = 10)
    @Column("version")
    private String version;

    @NotNull(message = "must not be null")
    @Column("status")
    private Status status;

    @NotNull(message = "must not be null")
    @Column("date_identified")
    private LocalDate dateIdentified;

    @Column("date_closed")
    private LocalDate dateClosed;

    @Size(max = 1000)
    @Column("comments")
    private String comments;

    @Transient
    private Category category;

    @Transient
    @JsonIgnoreProperties(value = { "developers" }, allowSetters = true)
    private Product product;

    @Transient
    @JsonIgnoreProperties(value = { "expertise", "products" }, allowSetters = true)
    private Developer assignee;

    @Transient
    private Priority priority;

    @Transient
    private Department department;

    @Column("category_id")
    private Long categoryId;

    @Column("product_id")
    private Long productId;

    @Column("assignee_id")
    private Long assigneeId;

    @Column("priority_id")
    private Long priorityId;

    @Column("department_id")
    private Long departmentId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Issue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefectNumber() {
        return this.defectNumber;
    }

    public Issue defectNumber(String defectNumber) {
        this.setDefectNumber(defectNumber);
        return this;
    }

    public void setDefectNumber(String defectNumber) {
        this.defectNumber = defectNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public Issue description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return this.version;
    }

    public Issue version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Status getStatus() {
        return this.status;
    }

    public Issue status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDateIdentified() {
        return this.dateIdentified;
    }

    public Issue dateIdentified(LocalDate dateIdentified) {
        this.setDateIdentified(dateIdentified);
        return this;
    }

    public void setDateIdentified(LocalDate dateIdentified) {
        this.dateIdentified = dateIdentified;
    }

    public LocalDate getDateClosed() {
        return this.dateClosed;
    }

    public Issue dateClosed(LocalDate dateClosed) {
        this.setDateClosed(dateClosed);
        return this;
    }

    public void setDateClosed(LocalDate dateClosed) {
        this.dateClosed = dateClosed;
    }

    public String getComments() {
        return this.comments;
    }

    public Issue comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public Issue category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public Issue product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Developer getAssignee() {
        return this.assignee;
    }

    public void setAssignee(Developer developer) {
        this.assignee = developer;
        this.assigneeId = developer != null ? developer.getId() : null;
    }

    public Issue assignee(Developer developer) {
        this.setAssignee(developer);
        return this;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        this.priorityId = priority != null ? priority.getId() : null;
    }

    public Issue priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        this.departmentId = department != null ? department.getId() : null;
    }

    public Issue department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    public Long getAssigneeId() {
        return this.assigneeId;
    }

    public void setAssigneeId(Long developer) {
        this.assigneeId = developer;
    }

    public Long getPriorityId() {
        return this.priorityId;
    }

    public void setPriorityId(Long priority) {
        this.priorityId = priority;
    }

    public Long getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Long department) {
        this.departmentId = department;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Issue)) {
            return false;
        }
        return id != null && id.equals(((Issue) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Issue{" +
            "id=" + getId() +
            ", defectNumber='" + getDefectNumber() + "'" +
            ", description='" + getDescription() + "'" +
            ", version='" + getVersion() + "'" +
            ", status='" + getStatus() + "'" +
            ", dateIdentified='" + getDateIdentified() + "'" +
            ", dateClosed='" + getDateClosed() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
