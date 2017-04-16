package pl.touk.sputnik.processor.pit;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class PitProcessorFactory implements ReviewProcessorFactory<PitProcessor> {
    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.PIT_ENABLED));
    }

    @Override
    public PitProcessor create(Configuration configuration) {
        return new PitProcessor(configuration);
    }
}
