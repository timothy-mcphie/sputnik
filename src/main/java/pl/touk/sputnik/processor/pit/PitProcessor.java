package pl.touk.sputnik.processor.pit;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.filter.PythonFilter;

import java.io.File;

@Slf4j
class PitProcessor extends ProcessorRunningExternalProcess {


    private PitExecutor pitExecutor;
    private PitResultParser pitResultParser;

    public PitProcessor(Configuration configuration) {
        pitExecutor = new PitExecutor(configuration.getProperty(GeneralOption.PIT_FILTER));
        pitResultParser = new PitResultParser();
    }

    @Override
    @NotNull
    //We override the process to get a list of files and return a review result
    public ReviewResult process(@NotNull Review review) {
        ReviewResult result = new ReviewResult();
        for (Violation violation : getParser().parse(processFileAndDumpOutput(""))) {
                result.add(violation);
            }
    }

    @NotNull
    @Override
    public String getName() {
        return "Pit";
    }

    @Override
    public FileFilter getReviewFileFilter() {
        return new PythonFilter();
//shouldn't matter -> only trying to force python script to return a large comment, not to be put on actual reviewfiles.
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return pitResultParser;
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return pitExecutor.runPitScript();
    }
}
