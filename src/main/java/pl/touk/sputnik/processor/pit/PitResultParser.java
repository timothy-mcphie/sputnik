package pl.touk.sputnik.processor.pit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.Severity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PitResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Violation> parse(String pitOutput) {
        if (StringUtils.isEmpty(pitOutput)) {
            return Collections.emptyList();
        }
	List<Violation> violations = new ArrayList<>();
	Violation violation = new Violation("", 0, pitOutput, Severity.ERROR);
	violations.add(violation);
	return violations;
    }

}
