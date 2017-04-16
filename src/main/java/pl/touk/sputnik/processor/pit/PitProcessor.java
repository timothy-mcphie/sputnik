package pl.touk.sputnik.processor.pit;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.tools.externalprocess.ExternalProcessResultParser;
import pl.touk.sputnik.processor.tools.externalprocess.ProcessorRunningExternalProcess;

import java.io.File;

class PitProcessor extends ProcessorRunningExternalProcess {


    private PitExecutor pitExecutor;
    private PitResultParser pitResultParser;

    PitProcessor(Configuration configuration) {
        pitExecutor = new PitExecutor(configuration.getProperty(GeneralOption.PIT_FILTER));
        pitResultParser = new PitResultParser();
    }

    @NotNull
    @Override
    public String getName() {
        return "Pit";
    }

    @Override
    public ExternalProcessResultParser getParser() {
        return pitResultParser;
    }

    @Override
    public String processFileAndDumpOutput(File fileToReview) {
        return pitExecutor.runOnFile(fileToReview.getAbsolutePath());
    }
}
