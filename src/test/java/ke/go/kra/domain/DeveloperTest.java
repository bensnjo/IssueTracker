package ke.go.kra.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ke.go.kra.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeveloperTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Developer.class);
        Developer developer1 = new Developer();
        developer1.setId(1L);
        Developer developer2 = new Developer();
        developer2.setId(developer1.getId());
        assertThat(developer1).isEqualTo(developer2);
        developer2.setId(2L);
        assertThat(developer1).isNotEqualTo(developer2);
        developer1.setId(null);
        assertThat(developer1).isNotEqualTo(developer2);
    }
}
