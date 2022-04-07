package ke.go.kra.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ke.go.kra.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExpertiseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expertise.class);
        Expertise expertise1 = new Expertise();
        expertise1.setId(1L);
        Expertise expertise2 = new Expertise();
        expertise2.setId(expertise1.getId());
        assertThat(expertise1).isEqualTo(expertise2);
        expertise2.setId(2L);
        assertThat(expertise1).isNotEqualTo(expertise2);
        expertise1.setId(null);
        assertThat(expertise1).isNotEqualTo(expertise2);
    }
}
