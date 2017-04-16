package pl.touk.sputnik.processor.pit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.review.Comment;

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
        try {
            List<Comment> Comments = new ArrayList<>();
            Comment comment = new Comment(0, pitOutput);
            Comments.add(Comment);
            return violations;
        } catch (IOException e) {
            throw new PitException("Error when appending...", e);
        }
    }

}
