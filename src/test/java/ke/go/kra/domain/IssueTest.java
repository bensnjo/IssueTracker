package ke.go.kra.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ke.go.kra.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IssueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Issue.class);
        Issue issue1 = new Issue();
        issue1.setId(1L);
        Issue issue2 = new Issue();
        issue2.setId(issue1.getId());
        assertThat(issue1).isEqualTo(issue2);
        issue2.setId(2L);
        assertThat(issue1).isNotEqualTo(issue2);
        issue1.setId(null);
        assertThat(issue1).isNotEqualTo(issue2);
    }
}
