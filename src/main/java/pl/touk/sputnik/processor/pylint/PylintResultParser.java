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
        Violation violation = new Violation(" ", 1, pylintOutput, PYLINT_ERROR);
        violations.add(violation);
        return violations;
    }

    private String removeHeaderFromPylintOutput(String pylintOutput) {
        return pylintOutput.replace("No config file found, using default configuration", "");
    }

    private String formatViolationMessageFromPylint(PylintMessage message) {
        return message.getMessage() + " [" + message.getSymbol() + "]";
    }

    private Severity pylintMessageTypeToSeverity(String message, String messageType) {
        if (PYLINT_ERROR.equals(messageType)) {
            return Severity.ERROR;
        } else if (PYLINT_WARNING.equals(messageType)) {
            return Severity.WARNING;
        } else if (PYLINT_CONVENTION.equals(messageType) || PYLINT_REFACTOR.equals(messageType)) {
            return Severity.INFO;
        } else if (PYLINT_FATAL.equals(messageType)) {
            throw new PylintException("Fatal error from pylint (" + message + ")");
        } else {
            throw new PylintException("Unknown message type returned by pylint (type = " + messageType + ")");
        }
    }
}
