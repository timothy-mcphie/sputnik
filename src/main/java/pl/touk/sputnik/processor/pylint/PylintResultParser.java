package pl.touk.sputnik.processor.pylint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.pylint.json.PylintMessage;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PylintResultParser implements ExternalProcessResultParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PYLINT_ERROR = "error";
    private static final String PYLINT_FATAL = "fatal";
    private static final String PYLINT_WARNING = "warning";
    private static final String PYLINT_CONVENTION = "convention";
    private static final String PYLINT_REFACTOR = "refactor";

    @Override
    public List<Violation> parse(String pylintOutput) {
        if (StringUtils.isEmpty(pylintOutput)) {
            return Collections.emptyList();
        }
        List<Violation> violations = new ArrayList<>();
	System.out.println(pylintOutput);
	System.out.println("Adding violation");
        Violation violation = new Violation(" ", 1, pylintOutput, Severity.INFO);
        violations.add(violation);
        return violations;
    }

    private String removeHeaderFromPylintOutput(String pylintOutput) {
        return pylintOutput.replace("No config file found, using default configuration", "");
    }

    private String formatViolationMessageFromPylint(PylintMessage message) {
        return message.getMessage() + " [" + message.getSymbol() + "]";
    }

}
